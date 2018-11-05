package com.axeac.android.sdk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axeac.android.sdk.R;
/**
 * 文件选择视图适配器
 * @author axeac
 * @version 1.0.0
 * */
public class FileSelectorAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	/**
	 * 存储文件路径的list集合
	 * */
	private List<String> filePaths;
	private Map<Integer, ViewHolder> itemViewMap = new HashMap<Integer, ViewHolder>();
	private Map<Integer, Boolean> isCheckSelected = new HashMap<Integer, Boolean>();
	
	public FileSelectorAdapter(Context ctx, List<String> filePaths) {
		mInflater = LayoutInflater.from(ctx);
		this.filePaths = filePaths;
		if (filePaths != null && filePaths.size() > 0) {
			isCheckSelected.clear();
			for (int i = 0; i < filePaths.size(); i++) {
				isCheckSelected.put(i, false);
			}
		}
	}
	
	public Map<Integer, ViewHolder> getItemViewMap() {
		return itemViewMap;
	}
	
	public Map<Integer, Boolean> getIsCheckSelected() {
		return isCheckSelected;
	}

	public int getCount() {
		return filePaths.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.axeac_fileselector_item, null);
			holder = new ViewHolder();
			holder.fileimg = (ImageView) convertView.findViewById(R.id.fileselector_item_img);
			holder.filename = (TextView) convertView.findViewById(R.id.fileselector_item_name);
			holder.filecheck = (ImageView) convertView.findViewById(R.id.fileselector_item_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		File file = new File(filePaths.get(position).toString());
		holder.filename.setText(file.getName());
		if (file.isDirectory()) {
			holder.filecheck.setVisibility(View.GONE);
		} else {
			if (isCheckSelected.get(position)) {
				holder.filecheck.setVisibility(View.VISIBLE);
			} else {
				holder.filecheck.setVisibility(View.GONE);
			}
		}
		if (file.isDirectory()) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_dir);
		} else if (isKnowFile(file.getName(), "apk")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_apk);
		} else if (isKnowFile(file.getName(), "txt")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_txt);
		} else if (isKnowFile(file.getName(), "doc")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_doc);
		} else if (isKnowFile(file.getName(), "xls")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_xls);
		} else if (isKnowFile(file.getName(), "ppt")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_ppt);
		} else if (isKnowFile(file.getName(), "pdf")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_pdf);
		} else if (isKnowFile(file.getName(), "html")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_html);
		} else if (isKnowFile(file.getName(), "zip")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_zip);
		} else if (isKnowFile(file.getName(), "image")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_picture);
		} else if (isKnowFile(file.getName(), "music")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_music);
		} else if (isKnowFile(file.getName(), "video")) {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_video);
		} else {
			holder.fileimg.setBackgroundResource(R.drawable.axeac_file_icon_unknow);
		}
		itemViewMap.put(position, holder);
		return convertView;
	}
	
	public final class ViewHolder {
		public ImageView fileimg;
		public TextView filename;
		public ImageView filecheck;
	}
	
	private boolean isKnowFile(String filename, String type) {
		String[] types = null;
		if (type.equals("apk")) {
			types = new String[]{".apk"};
		} else if (type.equals("txt")) {
			types = new String[]{".txt"};
		} else if (type.equals("doc")) {
			types = new String[]{".doc", ".docx"};
		} else if (type.equals("xls")) {
			types = new String[]{".xls", ".xlsx"};
		} else if (type.equals("ppt")) {
			types = new String[]{".ppt", ".pptx"};
		} else if (type.equals("pdf")) {
			types = new String[]{".pdf"};
		} else if (type.equals("html")) {
			types = new String[]{".html", ".htm"};
		} else if (type.equals("zip")) {
			types = new String[]{".zip", ".rar", ".jar"};
		} else if (type.equals("image")) {
			types = new String[]{".jpg", ".gif", ".png", ".jpeg", ".bmp"};
		} else if (type.equals("music")) {
			types = new String[]{".wma", ".ra", ".ram", ".mov", ".wav", ".mp3", ".ogg", ".mpc"};
		} else if (type.equals("video")) {
			types = new String[]{".avi", ".rmvb", ".rm", ".asf", ".divx", ".mpg", ".mpeg", ".mpe", ".wmv", ".mp4", ".mkv", ".vob"};
		}
		if (types != null) {
			for (String item : types) {
				if (filename.toLowerCase().endsWith(item)) {
					return true;
				}
			}
		}
		return false;
	}
}