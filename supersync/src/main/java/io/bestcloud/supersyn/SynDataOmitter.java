package io.bestcloud.supersyn;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

import io.bestcloud.ledger.LedgerClient;
import io.bestcloud.ledger.LedgerUtil;

public class SynDataOmitter {
	private LedgerClient client;
	private ChaincodeID chaincodeID;

	public SynDataOmitter() {

	}

	public void start() throws Exception {
		client = new LedgerClient();
		String mychaincodeName = LedgerUtil.getProp("chaincodeName");
		String mychaincodeVersion = LedgerUtil.getProp("chaincodeVersion");
		chaincodeID = ChaincodeID.newBuilder().setName(mychaincodeName).setVersion(mychaincodeVersion).build();

	}

	public String commitSynDataTx(byte[] synData) throws Exception {
		TransactionProposalRequest txProposal = client.getHFClient().newTransactionProposalRequest();
		txProposal.setArgBytes(new byte[][] { "dbsync".getBytes(), synData });
		txProposal.setFcn("invoke");
		txProposal.setChaincodeID(chaincodeID);
		return LedgerUtil.commitTransaction(txProposal, client.getChannel());
	}

	public static void main(String[] args) throws Exception {
		SynDataOmitter omitter = new SynDataOmitter();
		omitter.start();
		byte[] data = ("insert into mytable values (" + System.currentTimeMillis() % 1000 + ")").getBytes();
		String transactionId = omitter.commitSynDataTx(data);
		System.out.println("success commited syn data transaction " + transactionId);

	}

}
