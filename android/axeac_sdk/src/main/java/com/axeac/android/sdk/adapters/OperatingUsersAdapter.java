package com.axeac.android.sdk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axeac.android.sdk.R;

public class OperatingUsersAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<String> list;
	private List<String> idlist;
	private Map<Integer, ViewHolder> itemViewMap = new HashMap<Integer, ViewHolder>();

	public List<String> getChooseList() {
		return chooseList;
	}

	public List<String> getIdlist() {
		return idlist;
	}

	private List<String> chooseList = new ArrayList<>();

	public OperatingUsersAdapter(Context mContext, List<String> list,List<String> idList) {
		mInflater = LayoutInflater.from(mContext);
		this.list = list;
		this.idlist = idList;
	}

	public void addChoose(List<String> o){
		chooseList.addAll(o);
		notifyDataSetChanged();
	}


	
	public Map<Integer, ViewHolder> getItemViewMap() {
		return itemViewMap;
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
		ViewHolder holder = null;
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
		if (chooseList.contains(list.get(position))) {
			holder.img.setVisibility(View.VISIBLE);
		} else {
			holder.img.setVisibility(View.INVISIBLE);
		}
		itemViewMap.put(position, holder);
		return convertView;
	}

	public final class ViewHolder {
		public TextView text;
		public ImageView img;
	}
}