package com.example.easypg.Class;

public class BoardedTenantClass {
    private String tenantname;
    private String tenantphone;
    private String tenantroom;
    private String tenantrentamount;
    private String tenantpgid;

    public BoardedTenantClass(String tenantname, String tenantphone, String tenantroom, String tenantrentamount, String tenantpgid) {
        this.tenantname = tenantname;
        this.tenantphone = tenantphone;
        this.tenantroom = tenantroom;
        this.tenantrentamount = tenantrentamount;
        this.tenantpgid = tenantpgid;
    }
    public BoardedTenantClass(){}

    public String getTenantname() {
        return tenantname;
    }

    public void setTenantname(String tenantname) {
        this.tenantname = tenantname;
    }

    public String getTenantphone() {
        return tenantphone;
    }

    public void setTenantphone(String tenantphone) {
        this.tenantphone = tenantphone;
    }

    public String getTenantroom() {
        return tenantroom;
    }

    public void setTenantroom(String tenantroom) {
        this.tenantroom = tenantroom;
    }

    public String getTenantrentamount() {
        return tenantrentamount;
    }

    public void setTenantrentamount(String tenantrentamount) {
        this.tenantrentamount = tenantrentamount;
    }

    public String getTenantpgid() {
        return tenantpgid;
    }

    public void setTenantpgid(String tenantpgid) {
        this.tenantpgid = tenantpgid;
    }
}
