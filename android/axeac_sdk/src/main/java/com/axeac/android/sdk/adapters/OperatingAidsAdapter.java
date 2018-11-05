package com.axeac.android.sdk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axeac.android.sdk.R;

public class OperatingAidsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<String> list;
	private Map<Integer, ViewHolder> itemViewMap = new HashMap<Integer, ViewHolder>();
	private Map<Integer, Boolean> isCheckSelected = new HashMap<Integer, Boolean>();

	public OperatingAidsAdapter(Context mContext, List<String> list) {
		mInflater = LayoutInflater.from(mContext);
		this.list = list;
		if (list != null && list.size() > 0) {
			isCheckSelected.clear();
			for (int i = 0; i < list.size(); i++) {
				if (i == 0) {
					isCheckSelected.put(i, true);
				} else {
					isCheckSelected.put(i, false);
				}
			}
		}
	}
	
	public Map<Integer, ViewHolder> getItemViewMap() {
		return itemViewMap;
	}
	
	public Map<Integer, Boolean> getIsCheckSelected() {
		return isCheckSelected;
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
			convertView = mInflater.inflate(R.layout.axeac_operating_aids_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.operating_aids_item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text.setText(list.get(position));
		if (isCheckSelected.get(position)) {
			holder.text.setBackgroundResource(R.drawable.axeac_operating_choice);
		} else {
			holder.text.setBackgroundResource(R.color.white);
		}
		itemViewMap.put(position, holder);
		return convertView;
	}

	public final class ViewHolder {
		public TextView text;
	}
}