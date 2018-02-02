package com.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shroff on 21-Nov-17.
 */

public class AttributesParentItem {
    String name;
    String attribute_group_id;
    ArrayList<HashMap<String, String>> subAttributeList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttributeGroup_id() {
        return attribute_group_id;
    }

    public void setAttributeGroup_id(String attribute_group_id) {
        this.attribute_group_id = attribute_group_id;
    }

    public ArrayList<HashMap<String, String>> getSubAttributeList() {
        return subAttributeList;
    }

    public void setSubAttributeList(ArrayList<HashMap<String, String>> subAttributeList) {
        this.subAttributeList = subAttributeList;
    }
}
