package com.axeac.android.sdk.ui.container;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.utils.StaticObject;
/**
 * describe:Group Container
 *
 * 组容器
 * <br>以一组的形式显示信息，组容器外边缘为圆角矩形。
 */
public class GroupContainer extends Container {

	/**
	 * 组的标题栏文字，当设置后起作用
	 * */
	private String title = "";
	/**
	 * 标题栏的图标最左边
	 * */
	private String icon = "";
	
	private RelativeLayout layout;
	private TextView groupTitle;
	private View groupline;
	private LinearLayout groupContent;
	
	public GroupContainer(Activity ctx) {
		super(ctx);
		layout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_group_container, null);
		groupTitle = (TextView) layout.findViewById(R.id.group_title);
		groupline = (View) layout.findViewById(R.id.group_line);
		groupContent = (LinearLayout) layout.findViewById(R.id.group_content);
		bgColor = "237237237";
	}

	/**
	 * 对title赋值（标题栏文字）
	 * @param title
	 * title（标题栏文字）
	 * */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 对icon赋值（标题栏图标）
	 * @param icon
	 * icon（标题栏图标）
	 * */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public void setLayout(String layout) {
	}

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		if (title.equals("")) {
			groupTitle.setVisibility(View.GONE);
			groupline.setVisibility(View.VISIBLE);
			groupContent.setBackgroundResource(R.drawable.axeac_group_container_all);
		} else {
			groupTitle.setVisibility(View.VISIBLE);
			groupTitle.setText(title);
			groupline.setVisibility(View.GONE);
			groupContent.setBackgroundResource(R.drawable.axeac_group_container_content);
		}
		if (icon != null && !"".equals(icon)) {
			Drawable draw;
			try {
				draw = new BitmapDrawable(ctx.getResources(),BitmapFactory.decodeStream(ctx.getResources().getAssets().open("opicon/" + icon + ".png")));
				draw.setBounds(0, 0, 32, 32);
				groupTitle.setCompoundDrawables(draw, null, null, null);
			} catch (IOException e) {
				try {
					draw = new BitmapDrawable(ctx.getResources(),BitmapFactory.decodeStream(ctx.getResources().getAssets().open("opicon/qita.png")));
					draw.setBounds(0, 0, 32, 32);
					groupTitle.setCompoundDrawables(draw, null, null, null);
				} catch (IOException ee) {
				}
			}
		}
		String[] compIds = childs.keySet().toArray(new String[0]);
		for (int i = 0; i < compIds.length; i++) {
			this.layoutContainer.addViewIn(StaticObject.ComponentMap.get(compIds[i]));
		}
		GradientDrawable bggd = (GradientDrawable) groupContent.getBackground();
		int r = Integer.parseInt(bgColor.substring(0, 3));
		int g = Integer.parseInt(bgColor.substring(3, 6));
		int b = Integer.parseInt(bgColor.substring(6, 9));
		bggd.setColor(Color.rgb(r, g, b));
		if (this.bgImage != null && !"".equals(bgImage)) {
			BitmapDrawable draw;
			try {
				draw = new BitmapDrawable(this.ctx.getResources(),BitmapFactory.decodeStream(this.ctx.getResources().getAssets().open(bgImage + ".png")));
				groupContent.setBackgroundDrawable(draw);
			} catch (IOException e) {
			}
		}
		bggd.setAlpha((int)(255 * ((float)this.alpha/100)));
		groupContent.addView(this.layoutContainer.getLayout());
	}

	@Override
	public View getView() {
		return layout;
	}
}