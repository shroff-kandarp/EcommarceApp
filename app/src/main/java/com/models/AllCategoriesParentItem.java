package com.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shroff on 21-Nov-17.
 */

public class AllCategoriesParentItem {
    String name;
    String category_id;
    ArrayList<HashMap<String, String>> subCategoryList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public ArrayList<HashMap<String, String>> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(ArrayList<HashMap<String, String>> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }
/*public AllCategoriesParentItem(String name, String category_id, ArrayList<HashMap<String, String>> subCategoryList) {
        this.name = name;
        this.category_id = category_id;
        this.subCategoryList = subCategoryList;
    }*/
}
