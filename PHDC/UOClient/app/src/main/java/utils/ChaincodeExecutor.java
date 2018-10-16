package utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ChaincodeExecutor {
    private static final Log logger = LogFactory.getLog(ChaincodeExecutor.class);

    private String chaincodeName;
    private String version;
    private ChaincodeID ccId;
    private long waitTime = 6000;

    public ChaincodeExecutor(String chaincodeName, String version) {
        this.chaincodeName = chaincodeName;
        this.version = version;

        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder()
                .setName(chaincodeName)
                .setVersion(version);
        ccId = chaincodeIDBuilder.build();
    }

    public String getChaincodeName() {
        return chaincodeName;
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public String executeTransaction(HFClient client, Channel channel, boolean invoke, String func, String... args) throws InvalidArgumentException, ProposalException, UnsupportedEncodingException, InterruptedException, ExecutionException, TimeoutException {
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(ccId);
        transactionProposalRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
        System.out.println(func+" : "+args[0]);

        transactionProposalRequest.setFcn(func);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setProposalWaitTime(waitTime);


        List<ProposalResponse> successful = new LinkedList();
        List<ProposalResponse> failed = new LinkedList();
        String ret = "";
        String fail_ret = "";

        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        for (ProposalResponse response : transactionPropResp) {

            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                String payload = new String(response.getChaincodeActionResponsePayload());
                System.out.println(String.format("[√] Got success response from peer %s => payload: %s", response.getPeer().getName(), payload));
                successful.add(response);
                ret = payload;
            } else {
                String status = response.getStatus().toString();
                String msg = response.getMessage();
                System.out.println(String.format("[×] Got failed response from peer %s => %s: %s ", response.getPeer().getName(), status, msg));
                failed.add(response);
                fail_ret = msg;
            }
        }

        if (ret.equals("")) ret = fail_ret;
        final String lambda_ret = ret;

        if (invoke) {
            System.out.println("Sending transaction to orderers...");
            channel.sendTransaction(successful).thenApply(transactionEvent -> {
                System.out.println("Orderer response: txid" + transactionEvent.getTransactionID());
                return lambda_ret;
            }).exceptionally(e -> {
                e.printStackTrace();
                return lambda_ret;
            }).get(waitTime, TimeUnit.SECONDS);
        }
        return ret;
    }
}
