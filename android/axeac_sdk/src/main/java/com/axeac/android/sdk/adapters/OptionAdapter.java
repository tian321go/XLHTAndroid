package com.axeac.android.sdk.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.utils.StaticObject;
/**
 * 适配器
 * @author axeac
 * @version 1.0.0
 * */
public class OptionAdapter extends BaseAdapter {

	private Activity ctx;
	private List<String> btns;
	private CustomDialog dialog;
	private Object tag;

	public OptionAdapter(Activity ctx, List<String> btns, CustomDialog dialog, Object tag){
		this.ctx = ctx;
		this.btns = btns;
		this.dialog = dialog;
		this.tag = tag;
	}

	@Override
	public int getCount() {
		return btns.size();
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
			convertView = LayoutInflater.from(ctx).inflate(R.layout.axeac_dialog_item_opt, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.dialog_item_img);
			holder.text = (TextView) convertView.findViewById(R.id.dialog_item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String imgName = StringUtil.split(btns.get(position),"||")[2];
		String name = StringUtil.split(btns.get(position),"||")[1];
		Bitmap img = null;
		try {
			img = BitmapFactory.decodeStream(ctx.getResources().getAssets().open("opicon/" + imgName + ".png"));
		} catch (IOException e) {
			try {
				img = BitmapFactory.decodeStream(ctx.getResources().getAssets().open("opicon/yiban.png"));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		holder.img.setImageBitmap(img);
		holder.text.setText(name);
		convertView.setTag(R.string.axeac_key0,btns.get(position));
		convertView.setTag(R.string.axeac_key1,tag);
		convertView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				try {
					String optStr = (String)view.getTag(R.string.axeac_key0);
					List<String> vals = (List<String>)view.getTag(R.string.axeac_key1);
					String[] optStrs = StringUtil.split(optStr,"||");
					if (optStrs.length < 4) {
						return;
					}
					String click = optStrs[3];
					String str = "";
					String vs[] = StringUtil.split(click, ":");
					if(vs.length >= 2){
						String pageid = vs[1];
						String params[];
						if(vs.length >= 3){
							params = StringUtil.split(vs[2], ",");
							for(String param : params){
								String kv[] = StringUtil.split(param, "=");
								if(kv.length >= 2){
									str += kv[0] + "=" + vals.get(Integer.parseInt(kv[1])) + "\r\n";
								}
							}
						}
						if(click.startsWith("PAGE")){
							Intent intent = new Intent(); 
							intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
							intent.putExtra("meip", "MEIP_PAGE=" + pageid + "\r\n" + str);
							LocalBroadcastManager
									.getInstance(ctx).sendBroadcast(intent);
						} else if (click.startsWith("OP")) {
							Intent intent = new Intent(); 
							intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
							intent.putExtra("meip", "MEIP_ACTION=" + pageid + "\r\n" + str);
							LocalBroadcastManager
									.getInstance(ctx).sendBroadcast(intent);
						}
					}
				} catch (Exception e) {
					Toast.makeText(ctx, R.string.axeac_toast_exp_op, Toast.LENGTH_SHORT).show();
				}
			}
		});
		return convertView;
	}

	public final class ViewHolder {
		public ImageView img;
		public TextView text;
	}
}