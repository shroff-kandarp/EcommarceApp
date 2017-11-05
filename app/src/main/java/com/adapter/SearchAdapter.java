package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecommarceapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shroff on 20-Aug-17.
 */

public class SearchAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> list_item;
    Context mContext;

    public SearchAdapter(ArrayList<HashMap<String, String>> list_item, Context mContext) {
        this.list_item = list_item;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list_item.size();
    }

    @Override
    public Object getItem(int i) {
        return list_item.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.search_list_item, null);
        }

        ImageView menuIcon = (ImageView) view.findViewById(R.id.iv_icon);
        TextView menuTitleTxt = (TextView) view.findViewById(R.id.tv_str);
        menuIcon.setImageResource(R.mipmap.ic_search);
//        menuIcon.setImageResource(Integer.parseInt(list_item.get(i)[0]));
        menuTitleTxt.setText(list_item.get(i).get("name"));
        return view;
    }
}
