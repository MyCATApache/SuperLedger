package io.mycat.fabric.phdc.bass;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import io.mycat.fabric.phdc.bass.dto.BaseResp;
import io.mycat.fabric.phdc.exception.BuzException;

/**
 * 操作HDC CHANNEL 类
 * @author xu
 */
@Component("hDCChainCodeManager")
public class HDCChainCodeManager extends ChainCodeManager{
	
	
	public HDCChainCodeManager() throws InvalidArgumentException, InfoException, EnrollmentException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, NetworkConfigurationException, IOException, CryptoException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, TransactionException {
		//URL url=ChainCodeManager.class.getClassLoader().getResource("houser-profile-standard.yaml");
		InputStream configStream = ChainCodeManager.class.getClassLoader().getResourceAsStream("fabric-config/houser-profile-standard.yaml");
		channelName = "hdc";//暂时写死
		userName = "HOUser1";
		secret = "mycat2018";
		cCName = "hdcc";
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
	
	/**
	 * 存储数据
	 * @return
	 * @throws BuzException 
	 */
	public BaseResp putData(String... args) throws BuzException {
		try {
			
			chaincodeExecutor = new ChaincodeExecutor(cCName, chaincodeVersion);
			String resp = chaincodeExecutor.executeTransaction(client, channel, true, "putData",args);
			return JSON.parseObject(resp, BaseResp.class);
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException 
				| ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException
				| TimeoutException e) {
			e.printStackTrace();
			throw new BuzException("获取失败");
		}
	}
	
	
	public String testGetHO() throws BuzException {
		String resp;
		try {
			chaincodeExecutor = new ChaincodeExecutor(cCName, chaincodeVersion);
			resp = chaincodeExecutor.executeTransaction(client, channel, false, "getDeparts");
			return resp;
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException
				| ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException
				| TimeoutException e) {
			e.printStackTrace();
			throw new BuzException("获取失败");
		}
		
	}
	
	public static void main(String[] args) throws InvalidArgumentException, InfoException, EnrollmentException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, NetworkConfigurationException, CryptoException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, TransactionException, IOException, ProposalException, InterruptedException, ExecutionException, TimeoutException, BuzException {
		HDCChainCodeManager manager =new HDCChainCodeManager();
		System.out.println(manager.testGetHO());
	}
	
}
