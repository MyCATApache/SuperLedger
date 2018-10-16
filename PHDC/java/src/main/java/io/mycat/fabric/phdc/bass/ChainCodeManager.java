package io.mycat.fabric.phdc.bass;

import java.net.MalformedURLException;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InfoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ChainCodeManager {

	protected Logger logger = LoggerFactory.getLogger("ChainCodeManager");
	
	/*private static final long waitTime = 6000;
    private static String connectionProfilePath;

    private static String UICChannelName = "uic";
    private static String HDCChannelName = "hdc";
    private static String DRCChannelName = "drc";
    private static String userName = "UOUser1";
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
    ChaincodeExecutor drcExecutor;*/
    
	HFClient client;
	
	protected String channelName;
	
	protected String userName;
	
	protected String secret;
	
	protected String cCName;
	
	protected String chaincodeVersion = "1.0.1";
	
	protected Channel channel;
    
	protected ChaincodeExecutor chaincodeExecutor;
	
	protected void lineBreak() {
		logger.info("=============================================================");
	}
	
    protected  FabricUser getFabricUser(NetworkConfig.OrgInfo clientOrg, NetworkConfig.CAInfo caInfo)
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
	
    
    
	/*
    @Bean
    public HFClient hfClient() {
    	File f = ResourceUtils.getFile("classpath:fabric-config/connection-profile-standard.yaml");
		System.out.println(f.exists());
		NetworkConfig networkConfig = NetworkConfig.fromYamlFile(f);
		NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();
		NetworkConfig.CAInfo caInfo = clientOrg.getCertificateAuthorities().get(0);
		
		FabricUser user = getFabricUser(clientOrg, caInfo);

		HFClient client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
		client.setUserContext(user);
    }
    */
}
