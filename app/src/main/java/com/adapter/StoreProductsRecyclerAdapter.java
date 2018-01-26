package com.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ecommarceapp.R;
import com.general.files.GeneralFunctions;
import com.squareup.picasso.Picasso;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tarwindersingh on 26/01/18.
 */

public class StoreProductsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int FADE_DURATION = 1000; // in milliseconds

    private int lastPosition = -1;
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

    public String CURRENT_PRODUCT_DISPLAY_MODE = "GRID";

    public StoreProductsRecyclerAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int position);
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
            return new HeaderViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(CURRENT_PRODUCT_DISPLAY_MODE.equals("GRID") ? R.layout.item_store_product_grid_design : R.layout.item_store_product_list_design, parent, false);
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
            viewHolder.itemDescTxtView.setText(Html.fromHtml(item.get("description")));

            Picasso.with(mContext)
                    .load(item.get("image"))
                    .into(viewHolder.itemImgView);


            viewHolder.contentArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClickList(view, position);
                    }
                }
            });

            setAnimation(holder.itemView, position);

        } else if (holder instanceof HeaderViewHolder) {
            final HashMap<String, String> item = list.get(position);
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.categoryNameTxtView.setText(Html.fromHtml(item.get("name")));
            headerHolder.contentArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClickList(view, position);
                    }
                }
            });
            setFadeAnimation(holder.itemView);
        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            this.footerHolder = footerHolder;
        }


    }


    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public MTextView itemNameTxtView;
        public MTextView itemDescTxtView;
        public MTextView itemPriceTxtView;
        public AppCompatImageView itemImgView;
        public ImageView editProductImgView;
        public ImageView deleteProductImgView;
        public View contentArea;

        public ViewHolder(View view) {
            super(view);

            itemNameTxtView = (MTextView) view.findViewById(R.id.itemNameTxtView);
            itemDescTxtView = (MTextView) view.findViewById(R.id.itemDescTxtView);
            itemPriceTxtView = (MTextView) view.findViewById(R.id.itemPriceTxtView);
            itemImgView = (AppCompatImageView) view.findViewById(R.id.itemImgView);
            editProductImgView = (ImageView) view.findViewById(R.id.editProductImgView);
            deleteProductImgView = (ImageView) view.findViewById(R.id.deleteProductImgView);
            contentArea = view.findViewById(R.id.cardview);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;

        public View contentArea;

        public FooterViewHolder(View itemView) {
            super(itemView);
            contentArea = itemView;
            progressArea = (LinearLayout) itemView;

        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        MTextView categoryNameTxtView;

        public View contentArea;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            contentArea = itemView;
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
        this.isFooterEnabled = true;
        notifyDataSetChanged();
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.VISIBLE);
    }

    public void removeFooterView() {
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.GONE);
//        footerHolder.progressArea.setPadding(0, -1 * footerView.getHeight(), 0, 0);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
//        if (position > lastPosition)
//        {
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
//        }
    }
}
