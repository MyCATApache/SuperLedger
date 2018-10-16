package io.mycat.fabric.phdc.bass;

import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

public class FabricUser implements User {

    private String name;
    private String organization;
    private String mspId;
    private String account;
    private Set<String> roles;
    Enrollment enrollment;
    private String affiliation;


    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public String getName() {
        return this.name;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return this.roles;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAffiliation() {
        return this.affiliation;
    }

    public Enrollment getEnrollment() {
        return this.enrollment;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getMspId() {
        return this.mspId;
    }
}
