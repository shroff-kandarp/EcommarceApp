package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

import com.ecommarceapp.R;
import com.models.AllCategoriesParentItem;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shroff on 09-Nov-17.
 */

public class AllCategoriesAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<AllCategoriesParentItem> list;

    public AllCategoriesAdapter(Context mContext, ArrayList<AllCategoriesParentItem> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return list.get(groupPosition).getSubCategoryList().get(childPosititon);
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
        return list.get(groupPosition).getSubCategoryList().size();
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
        AllCategoriesParentItem parentItem = (AllCategoriesParentItem) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_category_all_header, null);
        }

        MTextView lblListHeader = (MTextView) convertView
                .findViewById(R.id.categoryNameTxtView);

        ImageView arrowImgView = (ImageView) convertView.findViewById(R.id.arrowImgView);
        if (parentItem.getSubCategoryList().size() == 0) {
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

   /* ArrayList<AllCategoriesParentItem> list;
    Context mContext;
    public GeneralFunctions generalFunc;

    private OnItemClickListener mItemClickListener;

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ITEM = 2;
    public static final int TYPE_FOOTER = 3;

    boolean isFooterEnabled = false;
    View footerView;

    FooterViewHolder footerHolder;

    public AllCategoriesRecyclerAdapter(Context mContext, ArrayList<AllCategoriesParentItem> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_all_header, parent, false);
            v.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            return new HeaderViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_all_item_design, parent, false);
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
//            Utils.printLog("CCN","::"+item.get("name"));
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
        public AppCompatImageView wishlistImgView;
        public AppCompatImageView itemImgView;
        public View contentArea;

        public ViewHolder(View view) {
            super(view);

            itemNameTxtView = (MTextView) view.findViewById(R.id.itemNameTxtView);
            itemDescTxtView = (MTextView) view.findViewById(R.id.itemDescTxtView);
            itemPriceTxtView = (MTextView) view.findViewById(R.id.itemPriceTxtView);
            itemImgView = (AppCompatImageView) view.findViewById(R.id.itemImgView);
            wishlistImgView = (AppCompatImageView) view.findViewById(R.id.wishlistImgView);
            contentArea = view.findViewById(R.id.cardview);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;
        View contentArea;

        public FooterViewHolder(View itemView) {
            super(itemView);
            contentArea = itemView;
            progressArea = (LinearLayout) itemView;

        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        MTextView categoryNameTxtView;
        View contentArea;
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
    }*/
}
