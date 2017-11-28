package io.bestcloud.ledger;

import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.InvalidProtocolBufferRuntimeException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

public class LedgerUtil {

	private static Properties props = new Properties();

	static {
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProp(String key) {
		return props.getProperty(key);
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
						String payLoadStr = new String(transactionActionInfo.getProposalResponsePayload(), "ascii");
						System.out
								.println(String.format("   Transaction action %d proposal response payload length: %d",
										j, payLoadStr.length()));
						System.out.println(String.format("   Transaction action %d proposal response payload: %s", j,
								printableString(payLoadStr)));

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
									byte[] valueBt=writeList.getValue().toByteArray();
									String valAsString = printableString(
											new String(valueBt, "UTF-8"));
									System.out.println(
											String.format("     Namespace %s write set %d key %s has value,len %s, value  '%s' ",
													namespace, rs, writeList.getKey(),valueBt.length, valAsString));

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

	public static void blockWalker(HFClient client, Channel channel)
			throws InvalidArgumentException, ProposalException, IOException {
		try {
			BlockchainInfo channelInfo = channel.queryBlockchainInfo();
			for (long current = 0; current < channelInfo.getHeight(); current++) {
				System.out.println("*************  analyse block inf ,current " + current);
				BlockInfo returnedBlock = channel.queryBlockByNumber(current);
				getBlockInf(client, returnedBlock);
			}
		} catch (InvalidProtocolBufferRuntimeException e) {
			throw e.getCause();
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
