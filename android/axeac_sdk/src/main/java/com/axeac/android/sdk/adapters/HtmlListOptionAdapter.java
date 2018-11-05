package com.axeac.android.sdk.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import com.axeac.android.sdk.R;
/**
 * html列表视图适配器
 * @author axeac
 * @version 1.0.0
 * */
public class HtmlListOptionAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context ctx;
	/**
	 * 存储名字的list集合
	 * */
	private List<String> nameList;
	/**
	 * 存储图标的list集合
	 * */
	private List<String> iconList;
	
	public HtmlListOptionAdapter(Context ctx, List<String> nameList, List<String> iconList) {
		mInflater = LayoutInflater.from(ctx);
		this.ctx = ctx;
		this.nameList = nameList;
		this.iconList = iconList;
	}
	
	@Override
	public int getCount() {
		return nameList.size();
	}

	@Override
	public Object getItem(int position) {
		return nameList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.axeac_dialog_item_opt, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.dialog_item_text);
			holder.img = (ImageView) convertView.findViewById(R.id.dialog_item_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(nameList.get(pos));
		Bitmap img = null;
		try {
			img = BitmapFactory.decodeStream(ctx.getResources().getAssets().open("opicon/" + iconList.get(pos) + ".png"));
		} catch (IOException e) {
			try {
				img = BitmapFactory.decodeStream(ctx.getResources().getAssets().open("opicon/yiban.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		holder.img.setImageBitmap(img);
		return convertView;
	}

	public final class ViewHolder {
		public TextView name;
		public ImageView img;
	}
}