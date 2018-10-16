package utils;

import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;



public class SampleUser implements User, Serializable {

    private String name;
    private Set<String> roles;
    private String account;
    private String affiliation;
    private String organization;
    private String enrollmentSecret;
    String mspId;
    Enrollment enrollment = null; //need access in test env.



    private transient SampleStore keyValStore;
    private String keyValStoreName;

    public SampleUser(String name, String org, SampleStore fs) {
        this.name = name;
        this.keyValStore = fs;
        this.organization = org;
        this.keyValStoreName = toKeyValStoreName(this.name, org);
        String memberStr = keyValStore.getValue(keyValStoreName);
        if (null == memberStr) {
            saveState();
        } else {
            restoreState();
        }

    }

    void saveState() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            keyValStore.setValue(keyValStoreName, Hex.toHexString(bos.toByteArray()));
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    SampleUser restoreState() {
        String memberStr = keyValStore.getValue(keyValStoreName);
        if (null != memberStr) {
            // The user was found in the key value store, so restore the
            // state.
            byte[] serialized = Hex.decode(memberStr);
            ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
            try {
                ObjectInputStream ois = new ObjectInputStream(bis);
                SampleUser state = (SampleUser) ois.readObject();
                if (state != null) {
                    this.name = state.name;
                    this.roles = state.roles;
                    this.account = state.account;
                    this.affiliation = state.affiliation;
                    this.organization = state.organization;
                    this.enrollmentSecret = state.enrollmentSecret;
                    this.enrollment = state.enrollment;
                    this.mspId = state.mspId;
                    return this;
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("Could not restore state of member %s", this.name), e);
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    @Override
    public String getAccount() {
        return this.account;
    }

    @Override
    public String getAffiliation() {
        return this.affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return this.enrollment;
    }

    @Override
    public String getMspId() {
        return this.mspId;
    }

    public static String toKeyValStoreName(String name, String org) {
        return "user." + name + org;
    }

    public boolean isEnrolled() {
        return this.enrollment != null;
    }

    public void setEnrollment(Enrollment enrollment) {

        this.enrollment = enrollment;
        saveState();

    }

    public void setEnrollmentSecret(String enrollmentSecret) {
        this.enrollmentSecret = enrollmentSecret;
        saveState();
    }

    public String getEnrollmentSecret() {
        return this.enrollmentSecret;
    }

    public void setMspId(String mspID) {
        this.mspId = mspID;
        saveState();

    }


}
