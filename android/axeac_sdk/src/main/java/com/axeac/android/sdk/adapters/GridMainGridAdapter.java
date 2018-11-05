package com.axeac.android.sdk.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.tools.LinkedHashtable;
import com.axeac.android.sdk.utils.StaticObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2018/11/4.
 */

public class GridMainGridAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater mInflater;

    private List<String[]> dataList;

    public GridMainGridAdapter(Context context,List<String[]> dataList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridMainGridAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new GridMainGridAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.axeac_gridlabel_item, null);
            holder.image = (ImageView) convertView.findViewById(R.id.gridlabel_item_img);
            holder.text = (TextView) convertView.findViewById(R.id.gridlabel_item_text);
            convertView.setTag(holder);
        } else {
            holder = (GridMainGridAdapter.ViewHolder) convertView.getTag();
        }
        String icon = dataList.get(position)[1];
        Glide.with(mContext)
                .load(StaticObject.getImageUrl("res-img:" + icon))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);

        holder.text.setText(dataList.get(position)[0]);
        return convertView;
    }

    public final class ViewHolder {
        public ImageView image;
        public TextView text;
    }

}
