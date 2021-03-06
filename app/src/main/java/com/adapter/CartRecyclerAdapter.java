package com.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.ecommarceapp.R;
import com.general.files.GeneralFunctions;
import com.squareup.picasso.Picasso;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shroff on 11-Nov-17.
 */

public class CartRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<HashMap<String, String>> list;
    Context mContext;
    public GeneralFunctions generalFunc;

    private OnItemClickListener mItemClickListener;

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ITEM = 2;
    public static final int TYPE_FOOTER = 3;

    boolean isFooterEnabled = false;
    View footerView;

    FooterViewHolder footerHolder;

    public CartRecyclerAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int btn_type, int position);

        void onQTYChangeList(View v, int quantity, int position);

        void onMoveToWishClickList(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_page_category_name, parent, false);
            v.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            return new HeaderViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cartlist_item, parent, false);
            return new ViewHolder(view);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ViewHolder) {
            final HashMap<String, String> item = list.get(position);
            final ViewHolder viewHolder = (ViewHolder) holder;


            viewHolder.itemNameTxtView.setText(Html.fromHtml(item.get("name")));
            viewHolder.itemPriceTxtView.setText(item.get("price"));
//            viewHolder.itemQuantityTxtView.setText("QTY: " + item.get("quantity"));
            buildQTYData(viewHolder.qtySpinner, viewHolder.qtyArea, GeneralFunctions.parseInt(1, item.get("quantity")));
            Picasso.with(mContext)
                    .load(item.get("image"))
                    .into(viewHolder.itemImgView);

            viewHolder.removeItemArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClickList(view, 0, position);
                    }
                }
            });
            viewHolder.contentArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClickList(view, -1, position);
                    }
                }
            });
            viewHolder.editItemArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onMoveToWishClickList(view, position);
                    }
                }
            });


            final int[] iCurrentSelection = {viewHolder.qtySpinner.getSelectedItemPosition()};
            viewHolder.qtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (iCurrentSelection[0] != i) {
                        // Your code here
                        if (mItemClickListener != null) {
                            mItemClickListener.onQTYChangeList(view, viewHolder.qtySpinner.getSelectedItemPosition() + 1, position);
                        }
                    }
                    iCurrentSelection[0] = i;
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });

        } else if (holder instanceof HeaderViewHolder) {
            final HashMap<String, String> item = list.get(position);
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.categoryNameTxtView.setText(Html.fromHtml(item.get("name")));
//            Utils.printLog("CCN","::"+item.get("name"));
        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            this.footerHolder = footerHolder;
        }
    }

    public void buildQTYData(AppCompatSpinner spinner, View qtyArea, int currentQTY) {
        ArrayList<String> qtyData = new ArrayList<>();

        int qtySelectedPosition = 0;
        for (int i = 1; i < 6; i++) {
            qtyData.add("QTY: " + i);
            if (currentQTY == i) {
                qtySelectedPosition = qtyData.size() - 1;
            }
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, qtyData);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(qtySelectedPosition);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(mContext, 5), Utils.dipToPixels(mContext, 1), Color.parseColor("#DEDEDE"), spinner);
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public MTextView itemNameTxtView;
        public MTextView itemPriceTxtView;
        public AppCompatSpinner qtySpinner;
        public AppCompatImageView itemImgView;
        public View removeItemArea;
        public View editItemArea;
        public View contentArea;
        public View qtyArea;

        public ViewHolder(View view) {
            super(view);

            itemNameTxtView = (MTextView) view.findViewById(R.id.itemNameTxtView);
            itemPriceTxtView = (MTextView) view.findViewById(R.id.itemPriceTxtView);
            qtySpinner = (AppCompatSpinner) view.findViewById(R.id.qtySpinner);
            itemImgView = (AppCompatImageView) view.findViewById(R.id.itemImgView);
            removeItemArea = view.findViewById(R.id.removeItemArea);
            editItemArea = view.findViewById(R.id.editItemArea);
            contentArea = view.findViewById(R.id.contentArea);
            qtyArea = view.findViewById(R.id.qtyArea);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;

        public FooterViewHolder(View itemView) {
            super(itemView);

            progressArea = (LinearLayout) itemView;

        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        MTextView categoryNameTxtView;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            categoryNameTxtView = (MTextView) itemView.findViewById(R.id.categoryNameTxtView);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position) && isFooterEnabled == true) {
            return TYPE_FOOTER;
        } else if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == list.size();
    }

    private boolean isPositionHeader(int position) {
        return list.get(position).get("TYPE").equals("" + TYPE_HEADER);
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (isFooterEnabled == true) {
            return list.size() + 1;
        } else {
            return list.size();
        }

    }

    public void addFooterView() {
        Utils.printLog("Footer", "added");
        this.isFooterEnabled = true;
        notifyDataSetChanged();
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.VISIBLE);
    }

    public void removeFooterView() {
        Utils.printLog("Footer", "removed");
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.GONE);
//        footerHolder.progressArea.setPadding(0, -1 * footerView.getHeight(), 0, 0);
    }
}
