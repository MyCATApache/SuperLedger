package io.mycat.fabric.phdc.bass;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InfoException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import io.mycat.fabric.phdc.bass.dto.InviteRespDto;
import io.mycat.fabric.phdc.exception.BuzException;

@Component("dRCChainCodeManager")
public class DRCChainCodeManager extends ChainCodeManager{
	
	private static Logger log = LoggerFactory.getLogger(DRCChainCodeManager.class);
	
	public DRCChainCodeManager() throws InvalidArgumentException, InfoException, EnrollmentException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, NetworkConfigurationException, IOException, CryptoException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, TransactionException {
		InputStream configStream = ChainCodeManager.class.getClassLoader().getResourceAsStream("fabric-config/duo-profile-standard.yaml");
		//Resource fileRource = new ClassPathResource("fabric-config/duo-profile-standard.yaml");
		channelName = "drc";
		userName = "DUOUser1";
		secret = "mycat2018";
		cCName = "drcc";
		//System.out.println(f.exists());
		NetworkConfig networkConfig = NetworkConfig.fromYamlStream(configStream);
		NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();
		NetworkConfig.CAInfo caInfo = clientOrg.getCertificateAuthorities().get(0);

		FabricUser user = getFabricUser(clientOrg, caInfo);
		
		client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
		client.setUserContext(user);

		channel = client.loadChannelFromConfig(channelName, networkConfig);
		channel.initialize();
		
	}
	
	public String testGetHO() throws BuzException {
		String resp;
		try {
			chaincodeExecutor = new ChaincodeExecutor(cCName, chaincodeVersion);
			resp = chaincodeExecutor.executeTransaction(client, channel, false, "getInviteList");
			return resp;
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException 
				| ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException
				| TimeoutException e) {
			e.printStackTrace();
			throw new BuzException("获取失败");
		}
		
	}
	
	public void invite(Integer integer,String callBack, String dUName, int patternId,String cert,String secret,Consumer<String> applyConsumer,Consumer<String> consumer) throws BuzException {
		String arg = "{\"UserID\":\""+integer+"\", \"Cert\":\""+cert+"\", \"Secret\":\""+secret+"\", \"Callback\":\""+callBack+"\", \"DUName\":\""+dUName+"\", \"PatternID\":\""+patternId+"\"}";
		try {
			channel.registerChaincodeEventListener(Pattern.compile(".*"),
	                Pattern.compile(Pattern.quote("evtsender")),
	                (handle, blockEvent, chaincodeEvent) -> {
	            try{
	                log.info(" chaincodeEventListenerHandle is " + handle);
	                log.info(" testTxId is "+ chaincodeEvent.getTxId());
	                log.info("event_name is " + chaincodeEvent.getEventName());
	                log.info("event_data is " + new String(chaincodeEvent.getPayload(),"UTF-8"));
	                log.info("chaincode_name is " + chaincodeEvent.getChaincodeId());
	                log.info("channel name is " + blockEvent.getChannelId());
	                log.info(" blockNum "+ blockEvent.getBlockNumber());
	                
	                String es = blockEvent.getPeer() != null ? blockEvent.getPeer().getName() : blockEvent.getEventHub().getName();
	                log.info(String.format("RECEIVED Chaincode event with handle: %s, chaincode Id: %s, chaincode event name: %s, "
	                                + "transaction id: %s, event payload: \"%s\", from eventhub: %s",
	                        handle, chaincodeEvent.getChaincodeId(),
	                        chaincodeEvent.getEventName(),
	                        chaincodeEvent.getTxId(),
	                        new String(chaincodeEvent.getPayload()), es));
	                applyConsumer.accept(new String(chaincodeEvent.getPayload(),"UTF-8"));
	            } catch (Exception e) {
	                log.info(String.format("Caught an exception running channel %s", channel.getName()));
	                e.printStackTrace();
	                //fail("Test failed with error : " + e.getMessage());
	            }
	        });

			chaincodeExecutor = new ChaincodeExecutor(cCName, chaincodeVersion);
			chaincodeExecutor.executeTransactionAsyn(client, channel, true, "invite",consumer,arg);
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException 
				| ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException
				| TimeoutException e) {
			throw new BuzException("调用chaincode出错");
		}
		
	}
	
	public static void main(String[] args) throws InvalidArgumentException, InfoException, EnrollmentException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, NetworkConfigurationException, CryptoException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, TransactionException, IOException, ProposalException, InterruptedException, ExecutionException, TimeoutException, BuzException {
		DRCChainCodeManager manager =new DRCChainCodeManager();
		//System.out.println(manager.testGetHO());
		manager.invite(1, "/client/member/invitation/result/list", "XU_O",5678, "Cert", "Secret",(t)->{},(t)->{});
	}
	
}
