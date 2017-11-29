package io.bestcloud.supersyn;

import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;

import io.bestcloud.ledger.LedgerClient;
import io.bestcloud.ledger.LedgerUtil;

public class SuperSynStarter {
	private LedgerClient client;
	private DataSynLogRecorder logRecorder;
	private DataSynProcess dataSynProcess;
	private String channelName;
	final ReentrantLock lock = new ReentrantLock();
	final Condition condition = lock.newCondition();

	public SuperSynStarter() throws Exception {
		logRecorder = new DataSynLogRecorder(LedgerUtil.getProp("dataSynTxlogFile"));
		dataSynProcess = (DataSynProcess) Class.forName(LedgerUtil.getProp("dataSynProcessor")).newInstance();
	}

	public void start() throws Exception {
		channelName = LedgerUtil.getProp("channelName");
		logRecorder.loadSynLog();
		client = new LedgerClient();
		trySynDataFromBlockHeight();
		// 注册监听事件
		client.getChannel().registerBlockListener(blockEvent -> {
			boolean isIntestedChannel = false;
			for (TransactionEvent transactionEvent : blockEvent.getTransactionEvents()) {
				System.out.println("Received Transation event " + transactionEvent.getChannelId() + " ID: "
						+ transactionEvent.getTransactionID() + " block id " + blockEvent.getBlockNumber());
				isIntestedChannel = (transactionEvent.getChannelId().equals(channelName));
				if (isIntestedChannel) {
					break;
				}
			}
			if (isIntestedChannel) {
				try {
					lock.lock();
					condition.signal();
				} finally {
					lock.unlock();
				}
			}
		});

	}

	public void trySynDataFromBlockHeight() throws Exception {
		long fromHeight = logRecorder.getLastDataSyLogRec().blockID;
		BlockchainInfo channelInfo = client.getChannel().queryBlockchainInfo();
		for (long current = fromHeight; current < channelInfo.getHeight(); current++) {
			System.out.println("*************  analyse block inf ,current " + current);
			BlockInfo returnedBlock = client.getChannel().queryBlockByNumber(current);
			processSyncDataFromBlock(returnedBlock);
		}
	}

	private void processSyncDataFromBlock(BlockInfo returnedBlock) throws Exception {
		DataSyLogRec lastSynRec = logRecorder.getLastDataSyLogRec();
		final long blockNumber = returnedBlock.getBlockNumber();
		System.out.println(String.format("current block number %d has %d envelope count:", blockNumber,
				returnedBlock.getEnvelopeCount()));
		if (blockNumber < lastSynRec.blockID) {
			System.out.println("synchronized already " + blockNumber);
			return;
		}
		int i = 0;
		for (BlockInfo.EnvelopeInfo envelopeInfo : returnedBlock.getEnvelopeInfos()) {
			++i;
			final String channelId = envelopeInfo.getChannelId();
			if (!channelId.equals(this.channelName)) {
				continue;
			}
			String txID = envelopeInfo.getTransactionID();
			System.out.println(String.format("  Transaction number %d has transaction id: %s", i, txID));

			System.out.println(String.format("  Transaction number %d has channel id: %s ,type: %s ", i, channelId,
					envelopeInfo.getType()));

			if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
				BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
				System.out.println(String.format("  Transaction number %d has %d actions", i,
						transactionEnvelopeInfo.getTransactionActionInfoCount()));
				assertEquals(transactionEnvelopeInfo.isValid(), true);
				if (!transactionEnvelopeInfo.isValid()) {
					System.out.println("invalid transaction envelope !!! ,skip ");
					continue;
				}
				int actionNo = 0;
				for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo
						.getTransactionActionInfos()) {
					int chaincodeInpuArgsCount = transactionActionInfo.getChaincodeInputArgsCount();
					if (chaincodeInpuArgsCount == 3
							&& Arrays.equals(transactionActionInfo.getChaincodeInputArgs(0),
									"invoke".getBytes("iso-8859-1"))
							&& Arrays.equals(transactionActionInfo.getChaincodeInputArgs(1),
									"dbsync".getBytes("iso-8859-1"))) {
						System.out.println("received  data synch event  ");
						actionNo++;
						String txSeq = txID + "_" + actionNo;
						byte[] dbsyncData = transactionActionInfo.getChaincodeInputArgs(2);
						lastSynRec = logRecorder.getLastDataSyLogRec();
						if (!lastSynRec.containT(txSeq)) {
							try {
								System.out.println(" begin data sync ,transaction seq " + txSeq);
								dataSynProcess.processSynData(dbsyncData);
								logRecorder.addTXSeq(blockNumber, txSeq);
							} catch (Exception e) {
								System.out.println("synchronize err " + e);
							}
						}
					} else {
						String msg = new String(transactionActionInfo.getChaincodeInputArgs(0));
						System.out.println("not  data synch event ,skip ,args 0 is " + msg);
						continue;
					}

				}
			} else {
				System.out.println("YYYYYYYYYYY Transaction inf " + envelopeInfo);

			}
		}

	}

	public static void main(String[] args) throws Exception {
		SuperSynStarter starter = new SuperSynStarter();
		starter.start();

		while (true) {
			try {
				starter.lock.lock();
				boolean sig=starter.condition.await(300, TimeUnit.SECONDS);
				if(sig)
				{
					System.out.println("be knocked by somebody ,try to work ");
				}
				starter.trySynDataFromBlockHeight();
			} catch (Exception e) {
				System.out.println("sync data err " + e);
			} finally {
				starter.lock.unlock();
			}
		}
	}

}
