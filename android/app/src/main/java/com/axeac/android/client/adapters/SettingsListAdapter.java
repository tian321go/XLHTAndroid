package com.axeac.android.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.axeac.android.client.R;

/**
 * 设置选择列表适配器
 * @author axeac
 * @version 2.3.0.0001
 * */
public class SettingsListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private String[] labelList;
	
	public SettingsListAdapter(Context ctx, String[] labelList) {
		this.mInflater = LayoutInflater.from(ctx);
		this.labelList = labelList;
	}
	
	@Override
	public int getCount() {
		return labelList.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.settings_item, null);
			holder.settingsItemLabel = (TextView) convertView.findViewById(R.id.settings_item_label);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.settingsItemLabel.setText(labelList[position]);
		return convertView;
	}
	
	private class ViewHolder {
		TextView settingsItemLabel;
	}
}