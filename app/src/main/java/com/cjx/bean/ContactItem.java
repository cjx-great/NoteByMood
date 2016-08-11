package com.cjx.bean;

/**
 * Created by CJX on 2016-8-10.
 *
 * 区分每一个item的类型(区分divider)及其数据
 */
public class ContactItem {

    private String name = "";
    private String phoneNumber = "";
    // 1 -- divider   2 -- contact
    private int type = 2;

    public ContactItem() {

    }

    public ContactItem(String name,int type) {
        this.name = name;
        this.type = type;
    }

    public ContactItem(String name, String phoneNumber, int type) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
