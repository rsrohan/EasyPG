package com.example.easypg.Class;

public class ManagerDetailsClass {
    private String name;
    private String phone;
    private String Pgname;
    private String pincode;
    private String dateCreated;

    public ManagerDetailsClass(String name2, String phone2, String Pgname2, String pincode2, String dateCreated2) {
        name = name2;
        phone = phone2;
        Pgname = Pgname2;
        pincode = pincode2;
        dateCreated = dateCreated2;
    }
    public ManagerDetailsClass()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPgname() {
        return Pgname;
    }

    public void setPgname(String Pgname) {
        this.Pgname = Pgname;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
