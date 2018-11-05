package com.axeac.android.sdk.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.DensityUtil;
/**
 * 列表选择网格显示视图适配器
 * @author axeac
 * @version 1.0.0
 * */
public class ChooseGridAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	/**
	 * 网格item高度
	 * */
	private int height;
	/**
	 * 存储网格item文字的list集合
	 * */
	private List<String> list;
	/**
	 * 字体
	 * */
	private String font;
	private Map<Integer, ViewHolder> itemViewMap = new HashMap<Integer, ViewHolder>();
	private Map<Integer, Boolean> isCheckSelected = new HashMap<Integer, Boolean>();

	public ChooseGridAdapter(Context mContext, List<String> list, boolean[] selectedItems, String font) {
		mInflater = LayoutInflater.from(mContext);
		height = DensityUtil.dip2px(mContext, 35);
		this.list = list;
		this.font = font;
		if (list != null && list.size() > 0) {
			isCheckSelected.clear();
			for (int i = 0; i < list.size(); i++) {
				if (selectedItems[i]) {
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
			convertView = mInflater.inflate(R.layout.axeac_label_choice, null);
			convertView.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.FILL_PARENT, height));
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.label_choice_text);
			holder.img = (ImageView) convertView.findViewById(R.id.label_choice_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text.setText(list.get(position));
		String familyName = null;
		int style = Typeface.NORMAL;
		if (this.font != null && !"".equals(this.font)) {
			if (this.font.indexOf(";") != -1) {
				String[] strs = this.font.split(";");
				for (String str : strs) {
					if (str.startsWith("size")) {
						int index = str.indexOf(":");
						if (index == -1)
							continue;
						String s = str.substring(index + 1).trim();
						holder.text.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
					} else if(str.startsWith("family")) {
						int index = str.indexOf(":");
						if(index == -1)
							continue;
						familyName = str.substring(index + 1).trim();
					} else if(str.startsWith("style")) {
						int index = str.indexOf(":");
						if (index == -1)
							continue;
						String s = str.substring(index + 1).trim();
						if ("bold".equals(s)){
							style = Typeface.BOLD;
						} else if("italic".equals(s)) {
							style = Typeface.ITALIC;
						} else {
							if (s.indexOf(",") != -1) {
								if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
									style = Typeface.BOLD_ITALIC;
								}
								if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
									style = Typeface.BOLD_ITALIC;
								}
							}
						}
					} else if(str.startsWith("color")) {
						int index = str.indexOf(":");
						if (index == -1)
							continue;
						String s = str.substring(index + 1).trim();
						if (CommonUtil.validRGBColor(s)) {
							int r = Integer.parseInt(s.substring(0, 3));
							int g = Integer.parseInt(s.substring(3, 6));
							int b = Integer.parseInt(s.substring(6, 9));
							holder.text.setTextColor(Color.rgb(r, g, b));
						}
					}
				}
			}
		}
		if (familyName == null || "".equals(familyName)) {
			holder.text.setTypeface(Typeface.defaultFromStyle(style));
		} else {
			holder.text.setTypeface(Typeface.create(familyName, style));
		}
		if (isCheckSelected.get(position)) {
			holder.text.setBackgroundResource(R.drawable.axeac_label_choice_enable);
			holder.img.setVisibility(View.VISIBLE);
		} else {
			holder.text.setBackgroundResource(R.drawable.axeac_label_choice_disable);
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