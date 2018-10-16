package utils;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class SampleStore {

    private String file;
    private final Map<String, SampleUser> members = new HashMap<>();


    public SampleStore(File file) {

        this.file = file.getAbsolutePath();
    }

    public SampleUser getMember(String name, String org) {

        // Try to get the SampleUser state from the cache
        SampleUser sampleUser = members.get(SampleUser.toKeyValStoreName(name, org));
        if (null != sampleUser) {
            return sampleUser;
        }

        // Create the SampleUser and try to restore it's state from the key value store (if found).
        sampleUser = new SampleUser(name, org, this);

        return sampleUser;

    }

    public String getValue(String name) {

        Properties properties = loadProperties();
        return properties.getProperty(name);
    }

    public void setValue(String name, String value) {
        Properties properties = loadProperties();
        try (
                OutputStream output = new FileOutputStream(file)
        ) {
            properties.setProperty(name, value);
            properties.store(output, "");
            output.close();

        } catch (IOException e) {
            System.out.println("Could not save the keyvalue store, reason "+ e.getMessage());
        }
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find the file " + file +" error mesage: "+e.getMessage());

        } catch (IOException e) {
            System.out.println("Could not load keyvalue store from file " + file +" error mesage: "+e.getMessage());
        }
        return properties;
    }

    public SampleUser getMember(String name, String org, String mspId, File privateKeyFile, File certificateFile) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

        try {
            // Try to get the SampleUser state from the cache
            SampleUser sampleUser = members.get(SampleUser.toKeyValStoreName(name, org));
            if (null != sampleUser) {
                return sampleUser;
            }
            System.out.println("SampleUser: "+String.valueOf(sampleUser));

            // Create the SampleUser and try to restore it's state from the key value store (if found).
            sampleUser = new SampleUser(name, org, this);
            sampleUser.setMspId(mspId);

            String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");
            System.out.println("Certificate: "+certificate);

            PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));

            sampleUser.setEnrollment(new SampleStoreEnrollement(privateKey, certificate));

            sampleUser.saveState();
            return sampleUser;

        } catch (IOException e) {
            e.printStackTrace();
            throw e;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            throw e;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw e;
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw e;
        }
    }


    static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {

        System.out.println("Length of the data in getPrivateKeyFromBytes "+ data.length);
        final Reader pemReader = new StringReader(new String(data));

        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }

        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);

        return privateKey;
    }

    static final class SampleStoreEnrollement implements Enrollment, Serializable {

        private static final long serialVersionUID = -2784835212445309006L;
        private final PrivateKey privateKey;
        private final String certificate;

        SampleStoreEnrollement(PrivateKey privateKey, String certificate) {

            this.certificate = certificate;

            this.privateKey = privateKey;
        }

        @Override
        public PrivateKey getKey() {

            return privateKey;
        }

        @Override
        public String getCert() {
            return certificate;
        }

    }

    public void saveChannel(Channel channel) throws IOException, InvalidArgumentException {
       /* try{
            setValue("channel." + channel.getName(), Hex.toHexString(channel.s()));
        }catch(Exception e){
            e.printStackTrace();
        }*/

    }


}