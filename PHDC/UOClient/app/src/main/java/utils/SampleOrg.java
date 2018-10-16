package utils;

import org.hyperledger.fabric.sdk.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class SampleOrg {

    final String name;
    final String mspid;
    private String domainName;
    private String caLocation;
    private String caName;
    private SampleUser admin;
    private SampleUser peerAdmin;

    Map<String, User> userMap = new HashMap<>();
    Map<String, String> peerLocations = new HashMap<>();
    Map<String, String> ordererLocations = new HashMap<>();
    Map<String, String> eventHubLocations = new HashMap<>();

    private Properties caProperties = null;

    public SampleOrg(String name, String mspid) {
        this.name = name;
        this.mspid = mspid;
    }

    public void addPeerLocation(String name, String location) {

        peerLocations.put(name, location);
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
    public String getDomainName() {
        return this.domainName;
    }
    public void addOrdererLocation(String name, String location) {

        ordererLocations.put(name, location);
    }

    public void addEventHubLocation(String name, String location) {

        eventHubLocations.put(name, location);
    }

    public void setCALocation(String caLocation) {
        this.caLocation = caLocation;
    }

    public void setCAName(String caName) {
        this.caName = caName;
    }

    public void setCAProperties(Properties caProperties) {
        this.caProperties = caProperties;
    }

    public String getCALocation() {
        return this.caLocation;
    }

    public Properties getCAProperties() {
        return this.caProperties;
    }
    public void addUser(SampleUser user) {
        this.userMap.put(user.getName(), user);
    }

    public User getUser(String name) {
        return this.userMap.get(name);
    }

    public SampleUser getAdmin() {
        return this.admin;
    }

    public void setAdmin(SampleUser admin) {
        this.admin = admin;
    }
    public String getMSPID() {
        return this.mspid;
    }

    public SampleUser getPeerAdmin() {
        System.out.println("Getting peer admin for "+this.mspid+ " with name: "+peerAdmin.getMspId());
        return this.peerAdmin;
    }

    public void setPeerAdmin(SampleUser peerAdmin) {

        System.out.println("Setting peer admin for "+this.mspid+ " with name: "+peerAdmin.getMspId());
        this.peerAdmin = peerAdmin;
    }

    public String getName() {
        return name;
    }

    public Set<String> getOrdererNames() {

        return Collections.unmodifiableSet(ordererLocations.keySet());
    }

    public String getOrdererLocation(String name) {
        return ordererLocations.get(name);

    }

    public Set<String> getPeerNames() {

        return Collections.unmodifiableSet(peerLocations.keySet());
    }
    public String getPeerLocation(String name) {
        return peerLocations.get(name);

    }

}