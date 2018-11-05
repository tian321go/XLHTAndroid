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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.tools.LinkedHashtable;
import com.axeac.android.sdk.utils.StaticObject;
/**
 * 网格显示视图适配器
 * @author axeac
 * @version 1.0.0
 * */
public class RectangleGridAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private LinkedHashtable[] navLists;

    public RectangleGridAdapter(Context context, LinkedHashtable[] navLists) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.navLists = navLists;
    }

    @Override
    public int getCount() {
        return navLists.length;
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.axeac_gridmenu_item, null);
//            convertView.setBackground(WaterMarkImage.getDrawable(StaticObject.read.getString(StaticObject.USERNAME,""),240,240,30));
            holder.image = (ImageView) convertView.findViewById(R.id.gridmenu_item_grid_img);
            holder.text = (TextView) convertView.findViewById(R.id.gridmenu_item_grid_text);
            holder.noti = (TextView) convertView.findViewById(R.id.gridmenu_item_grid_noti);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String icon = (String) navLists[position].get("icon");


        Glide.with(mContext)
                .load(StaticObject.getImageUrl("res-img:" + icon))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
//		Bitmap bit = BitmapShared.getImageByDb(icon, mContext);
//		if(bit != null){
//			holder.image.setImageBitmap(bit);
//		}
//		else
//			new GetImgService().execute(holder.image,"res-img:"+ navLists[position].get("icon"));

        holder.text.setText((String) navLists[position].get("id"));
        String count = (String) navLists[position].get("count");
        if (count != null && !"".equals(count) && !"0".equals(count)) {
            holder.noti.setVisibility(View.VISIBLE);
            holder.noti.setText(count);
        } else {
            holder.noti.setVisibility(View.GONE);
            holder.noti.setText("");
        }
        return convertView;
    }

    public final class ViewHolder {
        public ImageView image;
        public TextView text;
        public TextView noti;
    }

}