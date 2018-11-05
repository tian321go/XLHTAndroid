package com.axeac.android.sdk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.axeac.android.sdk.R;

public class OperatingChooseAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<String> idlist = new ArrayList<>();
    private List<String> list = new ArrayList<>();

    public List<String> getIdlist() {
        return idlist;
    }

    public List<String> getList() {
        return list;
    }


    public OperatingChooseAdapter(Context mContext) {
        mInflater = LayoutInflater.from(mContext);
    }

    public void add(String o,String id) {
        list.add(o);
        idlist.add(id);
        notifyDataSetChanged();
    }
    public void remove(String o,String id) {
        list.remove(o);
        idlist.remove(id);
        notifyDataSetChanged();
    }

    public void clear(){
        list.clear();
        idlist.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.axeac_operating_users_item, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.operating_user_item_text);
            holder.img = (ImageView) convertView.findViewById(R.id.operating_user_item_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(list.get(position));
        return convertView;
    }

    public final class ViewHolder {
        public TextView text;
        public ImageView img;
    }
}