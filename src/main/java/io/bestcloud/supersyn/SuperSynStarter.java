package io.bestcloud.supersyn;

import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hyperledger.fabric.protos.peer.Query.ChaincodeInfo;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.InvalidProtocolBufferRuntimeException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import io.bestcloud.ledger.LedgerClient;
import io.bestcloud.ledger.LedgerUtil;

public class SuperSynStarter {
	private DataSynLogRecorder logRecorder;
	private DataSynProcess dataSynProcess;
	private String channelName;

	public SuperSynStarter() throws Exception {
		logRecorder = new DataSynLogRecorder(LedgerUtil.getProp("dataSynTxlogFile"));
		dataSynProcess = (DataSynProcess) Class.forName(LedgerUtil.getProp("dataSynProcessor")).newInstance();
	}

	public void start() throws Exception {
		channelName = LedgerUtil.getProp("channelName");
		logRecorder.loadSynLog();
		LedgerClient client = new LedgerClient();
		blockWalker(client.getHFClient(), client.getChannel(), logRecorder.getLastDataSyLogRec().blockID);
		// 注册监听事件
		client.getChannel().registerBlockListener(blockEvent -> new MyBlockListener(client));
	}

	private class MyBlockListener implements BlockListener {
		private final LedgerClient client;
		private final Worker worker;

		public MyBlockListener(LedgerClient client) {
			this.client = client;
			worker = new Worker();
			new Thread(worker).start();//worker启动轮询事件
		}

		@Override
		public void received(BlockEvent blockEvent) {
			//收到监听事件,立刻通知worker
			worker.notity(blockEvent);
		}

		private class Worker implements Runnable {

			private Lock lock;
			private Condition notityContition;
			private AtomicReference<BlockEvent> atomicBlockEvent = new AtomicReference<>();
			public Worker() {
				this.lock = new ReentrantLock();
				this.notityContition = lock.newCondition();
			}

			public void notity(BlockEvent blockEvent){
				lock.lock();
				try {
					atomicBlockEvent.set(blockEvent);
					notityContition.signal();
				}finally {
					lock.unlock();
				}
			}
			@Override
			public void run() {
				while (true) {
					lock.lock();
					try {
						System.out.println("查询是否有 blockchainInfo.");
						blockWalker(client.getHFClient(), client.getChannel(), logRecorder.getLastDataSyLogRec().blockID);
						if(notityContition.await(300, TimeUnit.SECONDS)){
							System.out.println("监听到事件通知");
							for (TransactionEvent transactionEvent : atomicBlockEvent.get().getTransactionEvents()) {
								System.out.println("Received Transation event " + transactionEvent.getChannelId() + " ID: "
										+ transactionEvent.getTransactionID() + " block id " + atomicBlockEvent.get().getBlockNumber());
								processSyncDataFromBlock(atomicBlockEvent.get());

							}
							atomicBlockEvent.set(null);
						} else {
							System.out.println("没有监听到事件通知");
						}
					} catch (InvalidArgumentException e) {
						//TODO
					} catch (IOException e) {
						//TODO
					} catch (ProposalException e) {
						//TODO
					} catch (InterruptedException e) {
						//TODO
					} finally {
						lock.unlock();
					}
				}
			}
		}
	}


	private void blockWalker(HFClient client, Channel channel, long fromHeight)
			throws InvalidArgumentException, ProposalException, IOException {

		BlockchainInfo channelInfo = channel.queryBlockchainInfo();
		for (long current = fromHeight; current < channelInfo.getHeight(); current++) {
			System.out.println("*************  analyse block inf ,current " + current);
			BlockInfo returnedBlock = channel.queryBlockByNumber(current);
			processSyncDataFromBlock(returnedBlock);
		}
	}

	private void processSyncDataFromBlock(BlockInfo returnedBlock) {
		try {
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
							System.out.println("......data synch event  ,value length :" + dbsyncData.length);
							lastSynRec = logRecorder.getLastDataSyLogRec();
							if (!lastSynRec.containT(txSeq)) {
								try {
									System.out.println(" begin data sync ,transaction seq " + txSeq);
									dataSynProcess.processSynData(dbsyncData);
									logRecorder.addTXSeq(blockNumber, txSeq);
								} catch (Exception e) {

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
		} catch (

				Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		new SuperSynStarter().start();
		while (true) {
			Thread.sleep(1000 * 600L);
		}

	}
}
