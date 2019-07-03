package com.stpl.pg4me.Class;

public class NotOnboardedTenantsClass {
    private String name;
    private String phone;
    private String room;
    private String rentamount;

    public NotOnboardedTenantsClass(String name, String phone, String room, String rentamount) {
        this.name = name;
        this.phone = phone;
        this.room = room;
        this.rentamount = rentamount;
    }
    public NotOnboardedTenantsClass(){}

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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRentamount() {
        return rentamount;
    }

    public void setRentamount(String rentamount) {
        this.rentamount = rentamount;
    }
}
