package io.bestcloud.ledger;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

/**
 * Ledger client for simple use
 * 
 * @author wuzhihui
 *
 */
public class LedgerClient {
	private final String channelName;
	private final Channel channel;
	private final HFClient client;
	private final Peer peer;
	private final Orderer orderer;

	private static Properties getProperties() {
		final Properties grpcProps = new Properties();

		grpcProps.put("grpc.NettyChannelBuilderOption.maxMessageSize", new Integer(1024 * 1024 * 60));
		grpcProps.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", new Integer(1024 * 1024 * 60));
		grpcProps.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] { 5L, TimeUnit.MINUTES });
		grpcProps.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] { 8L, TimeUnit.SECONDS });
		return grpcProps;
	}

	public LedgerClient() throws Exception {
		String orgPrivateKeyFile = LedgerUtil.getProp("orgPrivateKeyFile");
		String orgAdminCertificateFile = LedgerUtil.getProp("orgAdminCertificateFile");

		String kstore = LedgerUtil.getProp("kstore");
		final String domainName = LedgerUtil.getProp("domainName");
		String orgName = LedgerUtil.getProp("orgName");
		String orgMSP = LedgerUtil.getProp("orgMSP");
		String orderAddr = LedgerUtil.getProp("orderAddr");

		String peerName = LedgerUtil.getProp("peerName");
		String peerAddr = LedgerUtil.getProp("peerAddr");
		String peerEventAddr = LedgerUtil.getProp("peerEventAddr");
		channelName = LedgerUtil.getProp("channelName");

		SampleOrg org = new SampleOrg(orgName, orgMSP);
		org.setDomainName(domainName);
		SampleStore sampleStore = new SampleStore(new File(kstore));
		SampleUser peerAdminUser = sampleStore.getMember("Admin", orgName, org.getMSPID(), new File(orgPrivateKeyFile),
				new File(orgAdminCertificateFile));
		org.setPeerAdmin(peerAdminUser);
		// Create instance of client.
		client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
		client.setUserContext(org.getPeerAdmin());

		peer = client.newPeer(peerName, peerAddr, getProperties());
		orderer = client.newOrderer("blockchain-orderer", orderAddr, getProperties());
		channel = client.newChannel(channelName);
		channel.addOrderer(orderer);
		channel.addPeer(peer);

		EventHub eventHub = client.newEventHub(peerName + "EvenHub", peerEventAddr, getProperties());
		channel.addEventHub(eventHub);
		channel.initialize();

	}

	public String getChannelName() {
		return channelName;
	}

	public Channel getChannel() {
		return channel;
	}

	public HFClient getHFClient() {
		return client;
	}

	public Peer getPeer() {
		return peer;
	}

	public Orderer getOrderer() {
		return orderer;
	}

}
