package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

import com.ecommarceapp.R;
import com.models.AttributesParentItem;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shroff on 02-Feb-18.
 */

public class AttributesRecyclerAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<AttributesParentItem> list;

    public AttributesRecyclerAdapter(Context mContext, ArrayList<AttributesParentItem> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return list.get(groupPosition).getSubAttributeList().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final HashMap<String, String> childText = (HashMap<String, String>) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_category_all_item_design, null);
        }

        MTextView txtListChild = (MTextView) convertView
                .findViewById(R.id.categoryNameTxtView);

        txtListChild.setText(childText.get("name"));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getSubAttributeList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.list.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.list.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        AttributesParentItem parentItem = (AttributesParentItem) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_category_all_header, null);
        }

        MTextView lblListHeader = (MTextView) convertView
                .findViewById(R.id.categoryNameTxtView);

        ImageView arrowImgView = (ImageView) convertView.findViewById(R.id.arrowImgView);
        if (parentItem.getSubAttributeList().size() == 0) {
            arrowImgView.setImageResource(R.mipmap.ic_right_arrow);
        } else {
            if (isExpanded) {
                arrowImgView.setImageResource(R.mipmap.ic_collapse);
            } else {
                arrowImgView.setImageResource(R.mipmap.ic_expand);
            }
        }
//        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(parentItem.getName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}