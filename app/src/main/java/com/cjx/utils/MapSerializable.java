package com.cjx.utils;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by CJX on 2016-8-10.
 */
public class MapSerializable implements Serializable {

    private Map<String,String> contactsMap;

    public Map<String, String> getContactsMap() {
        return contactsMap;
    }

    public void setContactsMap(Map<String, String> contactsMap) {
        this.contactsMap = contactsMap;
    }
}
