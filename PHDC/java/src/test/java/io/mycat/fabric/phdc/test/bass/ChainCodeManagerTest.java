package io.mycat.fabric.phdc.test.bass;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InfoException;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import io.mycat.fabric.phdc.bass.ChaincodeExecutor;
import io.mycat.fabric.phdc.bass.FabricUser;

public class ChainCodeManagerTest {

	private static final long waitTime = 6000;
	private static final Log logger = LogFactory.getLog(ChainCodeManagerTest.class);
	private static String UICChannelName = "uic";
	private static String HDCChannelName = "hdc";
	private static String DRCChannelName = "drc";
	private static String userName = "HOUser1";
	private static String secret = "mycat2018";
	private static String UICCName = "uicc";
	private static String HDCCName = "hdcc";
	private static String DRCCName = "drcc";
	private static String chaincodeVersion = "1.0.1";
	Channel uic;
	Channel hdc;
	Channel drc;
	HFClient client;
	ChaincodeExecutor uicExecutor;
	ChaincodeExecutor hdcExecutor;
	ChaincodeExecutor drcExecutor;

	@Test
	public void testInit() throws InterruptedException, ExecutionException, TimeoutException {
		try {
			File f = ResourceUtils.getFile("classpath:fabric-config/connection-profile-standard.yaml");
			System.out.println(f.exists());
			NetworkConfig networkConfig = NetworkConfig.fromYamlFile(f);
			NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();
			NetworkConfig.CAInfo caInfo = clientOrg.getCertificateAuthorities().get(0);

			FabricUser user = getFabricUser(clientOrg, caInfo);

			HFClient client = HFClient.createNewInstance();
			client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
			client.setUserContext(user);

			uic = client.loadChannelFromConfig(UICChannelName, networkConfig);
			hdc = client.loadChannelFromConfig(HDCChannelName, networkConfig);
			drc = client.loadChannelFromConfig(DRCChannelName, networkConfig);

			uic.initialize();
			hdc.initialize();
			drc.initialize();

			uicExecutor = new ChaincodeExecutor(UICCName, chaincodeVersion);
			/*hdcExecutor = new ChaincodeExecutor(HDCCName, chaincodeVersion);
			drcExecutor = new ChaincodeExecutor(DRCCName, chaincodeVersion);*/
			uic.registerBlockListener(blockEvent -> {
				logger.info(String.format("Receive block event (number %s) from %s", blockEvent.getBlockNumber(),
						blockEvent.getPeer()));
			});
			hdc.registerBlockListener(blockEvent -> {
				logger.info(String.format("Receive block event (number %s) from %s", blockEvent.getBlockNumber(),
						blockEvent.getPeer()));
			});
			drc.registerBlockListener(blockEvent -> {
				logger.info(String.format("Receive block event (number %s) from %s", blockEvent.getBlockNumber(),
						blockEvent.getPeer()));
			});
			printChannelInfo(client, uic);
			printChannelInfo(client, hdc);
			printChannelInfo(client, drc);

			// executeChaincode(client, uic); executeChaincode(client, hdc);
			// executeChaincode(client, drc);

			uicExecutor = new ChaincodeExecutor(UICCName, chaincodeVersion);
			hdcExecutor = new ChaincodeExecutor(HDCCName, chaincodeVersion);
			drcExecutor = new ChaincodeExecutor(DRCCName, chaincodeVersion);
			
			String resp = uicExecutor.executeTransaction(client, uic, false, "getHO");
			System.out.println(resp);
			logger.info("Shutdown channel.");
			uic.shutdown(false);
			hdc.shutdown(false);
			drc.shutdown(false);

		} catch (InvalidArgumentException | NetworkConfigurationException
				| org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException | InfoException | EnrollmentException
				| CryptoException | IllegalAccessException | InstantiationException | ClassNotFoundException
				| NoSuchMethodException | InvocationTargetException | TransactionException | ProposalException
				| IOException e) {
			e.printStackTrace();
		}
	}

	
	@Test
	public void testOp() throws InterruptedException, ExecutionException, TimeoutException {
		try {
			File f = ResourceUtils.getFile("classpath:fabric-config/houser-profile-standard.yaml");
			System.out.println(f.exists());
			NetworkConfig networkConfig = NetworkConfig.fromYamlFile(f);
			NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();
			NetworkConfig.CAInfo caInfo = clientOrg.getCertificateAuthorities().get(0);

			FabricUser user = getFabricUser(clientOrg, caInfo);

			HFClient client = HFClient.createNewInstance();
			client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
			client.setUserContext(user);

			hdc = client.loadChannelFromConfig(HDCChannelName, networkConfig);

			hdc.initialize();

			hdcExecutor = new ChaincodeExecutor(HDCCName, chaincodeVersion);
			hdc.registerBlockListener(blockEvent -> {
				logger.info(String.format("Receive block event (number %s) from %s", blockEvent.getBlockNumber(),
						blockEvent.getPeer()));
			});
			printChannelInfo(client, hdc);

			// executeChaincode(client, uic); executeChaincode(client, hdc);
			// executeChaincode(client, drc);

			String resp;
//			resp = hdcExecutor.executeTransaction(client, hdc, true, "putData", "{\"UserID\":\"1000\", \"DepartID\":\"1000\", \"ItemID\":\"1000\", \"Result\":\"1000\", \"Doctor\":\"1000\"}");
//			System.out.println(resp);
			resp = hdcExecutor.executeTransaction(client, hdc, false, "getData", "{\"DepartID\":\"2345\", \"ItemID\":\"2345\", \"Date\":\"2018/09/02\"}");
			System.out.println(resp);
//			resp = hdcExecutor.executeTransaction(client, hdc, false, "getDeparts", "{\"Sex\":\"1\", \"Age\":\"30\"}");
//			System.out.println(resp);
			logger.info("Shutdown channel.");
			hdc.shutdown(false);

		} catch (InvalidArgumentException | NetworkConfigurationException
				| org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException | InfoException | EnrollmentException
				| CryptoException | IllegalAccessException | InstantiationException | ClassNotFoundException
				| NoSuchMethodException | InvocationTargetException | TransactionException | ProposalException
				| IOException e) {
			e.printStackTrace();
		}
	}
	
	/*private static void executeChaincode(HFClient client, Channel channel)
			throws ProposalException, InvalidArgumentException, UnsupportedEncodingException, InterruptedException,
			ExecutionException, TimeoutException {
		lineBreak();
		ChaincodeExecutor executer = new ChaincodeExecutor(chaincodeName, chaincodeVersion);

		String newValue = String.valueOf(new Random().nextInt(1000));
		executer.executeTransaction(client, channel, true, "set", "baas", newValue);
		executer.executeTransaction(client, channel, false, "query", "baas");

		lineBreak();
		newValue = String.valueOf(new Random().nextInt(1000));
		executer.executeTransaction(client, channel, true, "set", "baas", newValue);
		executer.executeTransaction(client, channel, false, "query", "baas");

	}*/

	private static void lineBreak() {
		logger.info("=============================================================");
	}

	/*
	 * private static void executeChaincode(HFClient client, Channel channel) throws
	 * ProposalException, InvalidArgumentException, UnsupportedEncodingException,
	 * InterruptedException, ExecutionException, TimeoutException { lineBreak();
	 * ChaincodeExecutor executer = new ChaincodeExecutor(chaincodeName,
	 * chaincodeVersion);
	 * 
	 * String newValue = String.valueOf(new Random().nextInt(1000));
	 * executer.executeTransaction(client, channel, true,"set", "baas", newValue);
	 * executer.executeTransaction(client, channel, false,"query", "baas");
	 * 
	 * lineBreak(); newValue = String.valueOf(new Random().nextInt(1000));
	 * executer.executeTransaction(client, channel, true,"set", "baas", newValue);
	 * executer.executeTransaction(client, channel, false,"query", "baas");
	 * 
	 */
	private static void printChannelInfo(HFClient client, Channel channel)
			throws ProposalException, InvalidArgumentException, IOException {
		lineBreak();
		BlockchainInfo channelInfo = channel.queryBlockchainInfo();

		logger.info("Channel height: " + channelInfo.getHeight());
		for (long current = channelInfo.getHeight() - 1; current > -1; --current) {
			BlockInfo returnedBlock = channel.queryBlockByNumber(current);
			final long blockNumber = returnedBlock.getBlockNumber();

			logger.info(String.format("Block #%d has previous hash id: %s", blockNumber,
					Hex.encodeHexString(returnedBlock.getPreviousHash())));
			logger.info(String.format("Block #%d has data hash: %s", blockNumber,
					Hex.encodeHexString(returnedBlock.getDataHash())));
			logger.info(String.format("Block #%d has calculated block hash is %s", blockNumber,
					Hex.encodeHexString(SDKUtils.calculateBlockHash(client, blockNumber,
							returnedBlock.getPreviousHash(), returnedBlock.getDataHash()))));
		}

	}

	private static FabricUser getFabricUser(NetworkConfig.OrgInfo clientOrg, NetworkConfig.CAInfo caInfo)
			throws MalformedURLException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException,
			InfoException, EnrollmentException {
		HFCAClient hfcaClient = HFCAClient.createNewInstance(caInfo);
		HFCAInfo cainfo = hfcaClient.info();
		lineBreak();
		logger.info("CA name: " + cainfo.getCAName());
		logger.info("CA version: " + cainfo.getVersion());

		// Persistence is not part of SDK.

		logger.info("Going to enroll user: " + userName);
		Enrollment enrollment = hfcaClient.enroll(userName, secret);
		logger.info("Enroll user: " + userName + " successfully.");

		FabricUser user = new FabricUser();
		user.setMspId(clientOrg.getMspId());
		user.setName(userName);
		user.setOrganization(clientOrg.getName());
		user.setEnrollment(enrollment);
		return user;
	}

}
