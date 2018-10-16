package utils;
import android.content.Context;
import android.content.ContextWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class TestConfig {

    private static Context context;
    private static final String DEFAULT_CONFIG = "config.properties";
    private static TestConfig config;
    private static final Properties sdkProperties = new Properties();

    private static final String PROPBASE = "org.hyperledger.fabric.sdktest.";
    private static final String PROPOSALWAITTIME = PROPBASE + "ProposalWaitTime";
    private static final String INVOKEWAITTIME = PROPBASE + "InvokeWaitTime";
    private static final String INTEGRATIONTESTSTLS = PROPBASE + "integrationtests.tls";
    private static final String INTEGRATIONTESTS_ORG = PROPBASE + "integrationTests.org.";

    private final boolean runningTLS;
    private final boolean runningFabricCATLS;
    private final boolean runningFabricTLS;

    private static final Pattern orgPat = Pattern.compile("^" + Pattern.quote(INTEGRATIONTESTS_ORG) + "([^\\.]+)\\.mspid$");
    private static final HashMap<String, SampleOrg> sampleOrgs = new HashMap<>();

    private TestConfig() {


        try {

            InputStream inputstream = context.getAssets().open(DEFAULT_CONFIG);
            sdkProperties.load(inputstream);

        } catch (FileNotFoundException f) {

            f.printStackTrace();

        } catch(IOException i){

            i.printStackTrace();
        }
        finally {

            //runningTLS = null != sdkProperties.getProperty(INTEGRATIONTESTSTLS, null);
            runningTLS = false;
            System.out.println("Running tls: "+runningTLS);
            runningFabricCATLS = runningTLS;
            runningFabricTLS = runningTLS;
            System.out.println("Populating sample orgs list:");
            for (Map.Entry<Object, Object> x : sdkProperties.entrySet()) {
                final String key = x.getKey() + "";
                final String val = x.getValue() + "";

                if (key.startsWith(INTEGRATIONTESTS_ORG)) {

                    Matcher match = orgPat.matcher(key);

                    if (match.matches() && match.groupCount() == 1) {
                        String orgName = match.group(1).trim();

                        System.out.println("Key: "+orgName + " Value: "+val.trim());
                        sampleOrgs.put(orgName, new SampleOrg(orgName, val.trim()));

                    }
                }
            }
            System.out.println("Populating Each Org:");
            for (Map.Entry<String, SampleOrg> org : sampleOrgs.entrySet()) {
                final SampleOrg sampleOrg = org.getValue();
                final String orgName = org.getKey();

                String peerNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".peer_locations");
                String[] ps = peerNames.split("[ \t]*,[ \t]*");

                for (String peer : ps) {
                    String[] nl = peer.split("[ \t]*@[ \t]*");
                    sampleOrg.addPeerLocation(nl[0], grpcTLSify(nl[1]));
                }

                final String domainName = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".domname");

                sampleOrg.setDomainName(domainName);

                String ordererNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".orderer_locations");
                ps = ordererNames.split("[ \t]*,[ \t]*");
                for (String peer : ps) {
                    String[] nl = peer.split("[ \t]*@[ \t]*");
                    System.out.println("Orderernames: "+nl[1]);
                    sampleOrg.addOrdererLocation(nl[0], grpcTLSify(nl[1]));
                }

                String eventHubNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".eventhub_locations");
                ps = eventHubNames.split("[ \t]*,[ \t]*");
                for (String peer : ps) {
                    if (peer.length() > 0) {
                        String[] nl = peer.split("[ \t]*@[ \t]*");
                        sampleOrg.addEventHubLocation(nl[0], grpcTLSify(nl[1]));
                    }
                }

                sampleOrg.setCALocation(httpTLSify(sdkProperties.getProperty((INTEGRATIONTESTS_ORG + orgName + ".ca_location"))));

                sampleOrg.setCAName(sdkProperties.getProperty((INTEGRATIONTESTS_ORG + orgName + ".caName")));

                System.out.println("runnignFabricCATLS: "+runningFabricCATLS);

                if (runningFabricCATLS) {

                    String cert = sdkProperties.getProperty((INTEGRATIONTESTS_ORG + orgName + ".catlsCert"));
                    try{

                        InputStream is = context.getAssets().open(cert);
                        try {
                            FileOutputStream outputStream = context.openFileOutput("ca.org1.example.com-cert.pem", Context.MODE_PRIVATE);
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            //read from is to buffer
                            while((bytesRead = is.read(buffer)) !=-1){
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            is.close();
                            //flush OutputStream to write any buffered data to file
                            outputStream.flush();
                            outputStream.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        File f = new File(context.getFilesDir(),"ca.org1.example.com-cert.pem");
                        System.out.println("(FabricCa Cert) File exsits: "+f.exists());

                        Properties properties = new Properties();
                        properties.setProperty("pemFile", f.getAbsolutePath());
                        properties.setProperty("allowAllHostNames", "true"); //testing environment only NOT FOR PRODUCTION!
                        sampleOrg.setCAProperties(properties);

                    }catch(IOException e){
                        System.out.println("Error opening file");
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public String getTestChannelPath() {

        return "src/test/fixture/sdkintegration/e2e-2Orgs/channel";

    }

    private String grpcTLSify(String location) {
        location = location.trim();
        /*Exception e = Utils.checkGrpcUrl(location);
        if (e != null) {
            throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
        }*/
        return runningFabricTLS ?location.replaceFirst("^grpc://", " grpcs://") : location;

    }

    private String httpTLSify(String location) {
        location = location.trim();

        return runningFabricCATLS ?
                location.replaceFirst("^http://", "https://") : location;
    }

    public static TestConfig getConfig(Context c) {
        context=c;
        if (null == config) {
            config = new TestConfig();
        }
        return config;

    }

    public SampleOrg getIntegrationTestsSampleOrg(String name) {

        return sampleOrgs.get(name);
    }

    public Properties getOrdererProperties(String name) {

        return getEndPointProperties("orderer", name);

    }

    private Properties getEndPointProperties(final String type, final String name) {

        final String domainName = getDomainName(name);

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("newartifacts", Context.MODE_PRIVATE);
        //needs to handle peer certificate for tls
//        File cert = new File(directory.getAbsolutePath() + File.separator + "tlsca.example.com-cert.pem");
        File cert = new File(directory.getAbsolutePath() + File.separator + "ca.ho.example.com-cert.pem");
        if (!cert.exists()) {
            throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", name,
                    cert.getAbsolutePath()));
        }

        Properties ret = new Properties();
        ret.setProperty("pemFile", cert.getAbsolutePath());
        ret.setProperty("trustServerCertificate", "true");
        ret.setProperty("hostnameOverride", name);
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");

        return ret;
    }

    private String getDomainName(final String name) {
        int dot = name.indexOf(".");
        if (-1 == dot) {
            return null;
        } else {
            return name.substring(dot + 1);
        }

    }
    public Properties getPeerProperties(String name) {

        final String domainName = getDomainName(name);

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("newartifacts", Context.MODE_PRIVATE);
        //needs to handle peer certificate for tls
//        File cert = new File(directory.getAbsolutePath() + File.separator + "ca.ho.example.com-cert.pem");
        File cert = new File(directory.getAbsolutePath() + File.separator + "tlsca.example.com-cert.pem");
        if (!cert.exists()) {
            throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", name,
                    cert.getAbsolutePath()));
        }

        Properties ret = new Properties();
        ret.setProperty("pemFile", cert.getAbsolutePath());
        ret.setProperty("trustServerCertificate", "true");
        ret.setProperty("hostnameOverride", name);
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");

        return ret;

    }
    public long getProposalWaitTime() {
        return Integer.parseInt(getProperty(PROPOSALWAITTIME));
    }
    private String getProperty(String property) {

        String ret = sdkProperties.getProperty(property);
        return ret;
    }
    public int getTransactionWaitTime() {
        return Integer.parseInt(getProperty(INVOKEWAITTIME));
    }

}
