package io.bestcloud.supersyn;

import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.InvalidProtocolBufferRuntimeException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import com.google.protobuf.InvalidProtocolBufferException;

import io.bestcloud.ledger.LedgerClient;
import io.bestcloud.ledger.LedgerUtil;

public class TestMyChainCode {

	public static void main(String[] args) throws Exception {
		LedgerClient client = new LedgerClient();

		String mychaincodeName = LedgerUtil.getProp("chaincodeName");
		String mychaincodeVersion = LedgerUtil.getProp("chaincodeVersion");
		ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(mychaincodeName).setVersion(mychaincodeVersion)
				.build();
		// 批量设置
		batchSetKeyValue(client.getHFClient(), client.getChannel(), chaincodeID);
		// 批量查询
		batchQueryChaincode(client.getHFClient(), client.getChannel(), chaincodeID);
		// queryChaincode(client, channel, chaincodeID);
		// blockWalker(client,channel);
	}

	private static int batchSetKeyValue(HFClient client, Channel channel, ChaincodeID chaincodeID)
			throws ProposalException, InvalidArgumentException, UnsupportedEncodingException,
			InvalidProtocolBufferException, InterruptedException, ExecutionException {
		long startTime = System.currentTimeMillis();
		TransactionProposalRequest txProposal = client.newTransactionProposalRequest();
		// txProposal.setArgs(keyAndValues);
		// byte[] data = new byte[64 ];
		// for (int i = 0; i < data.length; i++) {
		// data[i] = (byte) (45 + i % 45);
		// }
		byte[] data = ("insert into mytable values (" + System.currentTimeMillis() % 1000 + ")").getBytes();
		txProposal.setArgBytes(new byte[][] { "dbsync".getBytes(), data });
		txProposal.setFcn("invoke");
		txProposal.setChaincodeID(chaincodeID);
		int usedTime = 0;
		Collection<ProposalResponse> proposeResponses = channel.sendTransactionProposal(txProposal, channel.getPeers());
		for (ProposalResponse proposalResponse : proposeResponses) {
			if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
				fail("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: "
						+ proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage()
						+ ". Was verified : " + proposalResponse.isVerified());
			} else {
				assertEquals(200, proposalResponse.getChaincodeActionResponseStatus()); // Chaincode's
				CompletableFuture<TransactionEvent> eventFuture = channel.sendTransaction(proposeResponses);
				// System.out.println("waiting orderer return ");
				TransactionEvent transactionEvent = eventFuture.get();
				usedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
				assertTrue(transactionEvent.isValid()); // must be valid to be
				System.out.println(String.format("Finished transaction with transaction id %s",
						transactionEvent.getTransactionID()));

			}
		}
		return usedTime;
	}

	private static void batchQueryChaincode(HFClient client, Channel channel, ChaincodeID chaincodeID)
			throws UnsupportedEncodingException {
		QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
		queryByChaincodeRequest.setArgs(new String[] { "key1", "key2", "key3", "key4" });
		queryByChaincodeRequest.setFcn("batchQuery");
		queryByChaincodeRequest.setChaincodeID(chaincodeID);

		Collection<ProposalResponse> queryProposals;

		try {
			queryProposals = channel.queryByChaincode(queryByChaincodeRequest);
		} catch (Exception e) {
			throw new CompletionException(e);
		}

		for (ProposalResponse proposalResponse : queryProposals) {
			if (!proposalResponse.isVerified() || proposalResponse.getStatus() != Status.SUCCESS) {
				fail("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: "
						+ proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage()
						+ ". Was verified : " + proposalResponse.isVerified());
			} else {
				String payload = proposalResponse.getProposalResponse().getResponse().getPayload()
						.toString("iso-8859-1");
				System.out.println("result:" + payload);
			}
		}
	}

	public static void blockWalker(HFClient client, Channel channel)
			throws InvalidArgumentException, ProposalException, IOException {
		try {
			BlockchainInfo channelInfo = channel.queryBlockchainInfo();
			for (long current = channelInfo.getHeight() - 1; current > -1; --current) {
				System.out.println("*************  analyse block inf ,current " + current);
				BlockInfo returnedBlock = channel.queryBlockByNumber(current);
				getBlockInf(client, returnedBlock);
			}
		} catch (InvalidProtocolBufferRuntimeException e) {
			throw e.getCause();
		}
	}

	public static void getBlockInf(HFClient client, BlockInfo returnedBlock) {
		try {
			final long blockNumber = returnedBlock.getBlockNumber();
			System.out.println(String.format("current block number %d has data hash: %s", blockNumber,
					Hex.encodeHexString(returnedBlock.getDataHash())));
			System.out.println(String.format("current block number %d has previous hash id: %s", blockNumber,
					Hex.encodeHexString(returnedBlock.getPreviousHash())));
			System.out.println(String.format("current block number %d has calculated block hash is %s", blockNumber,
					Hex.encodeHexString(SDKUtils.calculateBlockHash(client, blockNumber,
							returnedBlock.getPreviousHash(), returnedBlock.getDataHash()))));
			final int envelopeCount = returnedBlock.getEnvelopeCount();
			System.out.println(String.format("current block number %d has %d envelope count:", blockNumber,
					returnedBlock.getEnvelopeCount()));
			int i = 0;
			for (BlockInfo.EnvelopeInfo envelopeInfo : returnedBlock.getEnvelopeInfos()) {
				++i;
				System.out.println(String.format("  Transaction number %d has transaction id: %s", i,
						envelopeInfo.getTransactionID()));
				final String channelId = envelopeInfo.getChannelId();
				System.out.println(String.format("  Transaction number %d has channel id: %s", i, channelId));
				System.out.println(String.format("  Transaction number %d has epoch: %d", i, envelopeInfo.getEpoch()));
				System.out.println(
						String.format("  Transaction number %d has transaction timestamp: %tB %<te,  %<tY  %<tT %<Tp",
								i, envelopeInfo.getTimestamp()));
				System.out.println(
						String.format("  Transaction number %d has type id: %s", i, "" + envelopeInfo.getType()));

				if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
					BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
					System.out.println(String.format("  Transaction number %d has %d actions", i,
							transactionEnvelopeInfo.getTransactionActionInfoCount()));
					System.out.println(
							String.format("  Transaction number %d isValid %b", i, transactionEnvelopeInfo.isValid()));
					assertEquals(transactionEnvelopeInfo.isValid(), true);
					System.out.println(String.format("  Transaction number %d validation code %d", i,
							transactionEnvelopeInfo.getValidationCode()));
					assertEquals(0, transactionEnvelopeInfo.getValidationCode());
					int j = 0;
					for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo
							.getTransactionActionInfos()) {
						++j;
						System.out.println(String.format("   Transaction action %d has response status %d", j,
								transactionActionInfo.getResponseStatus()));
						assertEquals(200, transactionActionInfo.getResponseStatus());
						System.out.println(String.format(
								"   Transaction action %d has response message bytes as string: %s", j,
								printableString(new String(transactionActionInfo.getResponseMessageBytes(), "UTF-8"))));
						System.out.println(String.format("   Transaction action %d has %d endorsements", j,
								transactionActionInfo.getEndorsementsCount()));

						for (int n = 0; n < transactionActionInfo.getEndorsementsCount(); ++n) {
							BlockInfo.EndorserInfo endorserInfo = transactionActionInfo.getEndorsementInfo(n);
							System.out.println(String.format("Endorser %d signature: %s", n,
									Hex.encodeHexString(endorserInfo.getSignature())));
							// System.out.println(String.format("Endorser %d
							// endorser: %s", n,
							// new String(endorserInfo.getEndorser(),
							// "UTF-8")));
						}
						System.out.println(String.format("   Transaction action %d has %d chaincode input arguments", j,
								transactionActionInfo.getChaincodeInputArgsCount()));
						for (int z = 0; z < transactionActionInfo.getChaincodeInputArgsCount(); ++z) {
							System.out.println(String.format(
									"     Transaction action %d has chaincode input argument %d is: %s", j, z,
									printableString(
											new String(transactionActionInfo.getChaincodeInputArgs(z), "UTF-8"))));
						}
						System.out.println(String.format("   Transaction action %d proposal response status: %d", j,
								transactionActionInfo.getProposalResponseStatus()));
						System.out.println(String.format("   Transaction action %d proposal response payload: %s", j,
								printableString(new String(transactionActionInfo.getProposalResponsePayload()))));

						TxReadWriteSetInfo rwsetInfo = transactionActionInfo.getTxReadWriteSet();
						if (null != rwsetInfo) {
							System.out
									.println(String.format("   Transaction action %d has %d name space read write sets",
											j, rwsetInfo.getNsRwsetCount()));

							for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : rwsetInfo.getNsRwsetInfos()) {
								final String namespace = nsRwsetInfo.getNamespace();
								KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();

								int rs = -1;
								for (KvRwset.KVRead readList : rws.getReadsList()) {
									rs++;

									System.out.println(String.format(
											"     Namespace %s read set %d key %s  version [%d:%d]", namespace, rs,
											readList.getKey(), readList.getVersion().getBlockNum(),
											readList.getVersion().getTxNum()));
								}

								rs = -1;
								for (KvRwset.KVWrite writeList : rws.getWritesList()) {
									rs++;
									String valAsString = printableString(
											new String(writeList.getValue().toByteArray(), "UTF-8"));

									System.out.println(
											String.format("     Namespace %s write set %d key %s has value '%s' ",
													namespace, rs, writeList.getKey(), valAsString));

								}
							}
						}
					}
				} else {
					System.out.println("YYYYYYYYYYY Transaction inf " + envelopeInfo);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String printableString(final String string) {
		int maxLogStringLength = 64;
		if (string == null || string.length() == 0) {
			return string;
		}

		String ret = string.replaceAll("[^\\p{Print}]", "?");

		ret = ret.substring(0, Math.min(ret.length(), maxLogStringLength))
				+ (ret.length() > maxLogStringLength ? "..." : "");

		return ret;

	}
}
