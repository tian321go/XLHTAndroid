package com.axeac.android.sdk.ui.container;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.ObservableScrollView;
import com.axeac.android.sdk.ui.base.ScrollViewListener;
import com.axeac.android.sdk.ui.base.SyncHorizontalScrollView;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.DensityUtil;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Paging Container
 * 分页签
 * <br>以分页签的形式显示信息，组容器外边缘为圆角矩形。
 * @author axeac
 * @version 1.0.0
 */
public class TabContainer extends Container implements ScrollViewListener{

	/**
	 * TOP\LEFT\RIGHT\BOTTOM，设置浮动图标浮动的位置
	 * <br>默认值为TOP
	 * */
	private String direction = "top";
	/**
	 * 当前设置和返回当前选择的Tab页
	 * <br>默认值：1
	 * */
	private int activeTab = 1;

	private boolean isScroll = false;

	private String title = "";
	/**
	 * 存储标签名字的list集合
	 * */
	private List<String> names = new ArrayList<String>();
	/**
	 * 存储索引位置的list集合
	 * */
	private List<Integer> indexs = new ArrayList<Integer>();
	/**
	 * 存储组件id的list集合
	 * */
	private List<String> compIds = new ArrayList<String>();
	/**
	 * 存储标签视图的list集合
	 * */
	private List<TextView> btns = new ArrayList<TextView>();

	private LinearLayout layout;
//	private LinearLayout navTop;
	private SyncHorizontalScrollView navTop;
	private LinearLayout tabContent;
	private TextView tabtitle;


	private int maxTabCount;

	public TabContainer(Activity ctx) {
		super(ctx);
		layout = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_tab_container, null);
		tabContent = (LinearLayout) layout.findViewById(R.id.tab_content);
		navTop = (SyncHorizontalScrollView) layout.findViewById(R.id.tab_nav_top);
		tabtitle = (TextView) layout.findViewById(R.id.tab_title);
		bgColor = "238238238";
	}

	@Override
	public void setLayout(String layout) {
	}

	/**
	 * 设置浮动图标浮动位置
	 * @param direction
	 * direction 可选值：TOP\LEFT\RIGHT\BOTTOM
	 * */
	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * 设置当前设置和返回当前选择的Tab页
	 * @param activeTab
	 * activeTab 默认值：1
	 * */
	public void setActiveTab(String activeTab) {
		this.activeTab = Integer.parseInt(activeTab);
	}

	public void setIsScroll(String isScroll){
		this.isScroll = Boolean.parseBoolean(isScroll);
	}

	public void setTitle(String title){
		this.title = title;
	}
	/**
	 * 设置标签名称
	 * @param names
	 * names 格式示例：标签1,标签2,标签3
	 * */
	public void setNames(String names) {
		String[] name = StringUtil.split(names, ",");
		for (int i = 0; i < name.length; i++) {
			this.names.add(name[i]);
		}
	}

	@Override
	public void add(String compId, boolean isReturn) {
	}
	/**
	 * 添加索引和组件id
	 * @param compId
	 * 组件id
	 * @param val
	 *
	 * */
	public void add(String compId, String val) {
		if(StaticObject.ComponentMap.get(compId) != null && StaticObject.ComponentMap.get(compId).addable){
			childs.put(compId,StaticObject.ComponentMap.get(compId));
			StaticObject.ComponentMap.get(compId).addable = false;
			String[] vals = StringUtil.split(val, ",");
			if (vals.length == 2) {
				indexs.add(Integer.parseInt(vals[1].trim()));
			} else {
				indexs.add(0);
			}
			compIds.add(compId);
			if (CommonUtil.getBoolean(vals[0])) {
				StaticObject.ReturnComponentMap.put(compId, StaticObject.ComponentMap.get(compId));
			}
		}
	}

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		setTabTitle();
		maxTabCount = showActiveTabNav();
		showActiveTabContent("next");
		int r = Integer.parseInt(bgColor.substring(0, 3));
		int g = Integer.parseInt(bgColor.substring(3, 6));
		int b = Integer.parseInt(bgColor.substring(6, 9));
		tabContent.setBackgroundColor(Color.rgb(r, g, b));
		if (this.bgImage != null && !"".equals(bgImage)) {
			BitmapDrawable draw;
			try {
				draw = new BitmapDrawable(this.ctx.getResources(),BitmapFactory.decodeStream(this.ctx.getResources().getAssets().open(bgImage + ".png")));
				tabContent.setBackgroundDrawable(draw);
			} catch (IOException e) {
			}
		}
		tabContent.getBackground().setAlpha((int)(255 * ((float)this.alpha/100)));
		tabContent.addView(this.layoutContainer.getLayout());
	}

	/**
	 * 返回标签页数
	 * @return
	 * 标签页数
	 * */
	private int showActiveTabNav() {
		if (names.size() > 0) {
			LinearLayout layout = new LinearLayout(ctx);
			layout.setBackgroundColor(Color.WHITE);
			layout.getBackground().setAlpha(180);
			layout.setOrientation(LinearLayout.HORIZONTAL);
			layout.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			layout.setGravity(Gravity.CENTER);
			for (int i = 0; i < names.size(); i++) {
				TextView btn = new TextView(ctx);
				btn.setSingleLine();
				btn.setPadding(0, 10, 0, 10);
				btn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
				if(!isScroll) {
					if (names.size() == 1) {
						btn.setLayoutParams(new ViewGroup.LayoutParams(StaticObject.deviceWidth / 3, ViewGroup.LayoutParams.WRAP_CONTENT));
					} else if (names.size() == 2) {
						btn.setLayoutParams(new ViewGroup.LayoutParams(StaticObject.deviceWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
					} else {
						btn.setLayoutParams(new ViewGroup.LayoutParams(StaticObject.deviceWidth / names.size(), ViewGroup.LayoutParams.WRAP_CONTENT));
					}
				}else{
					if (names.size() != 2) {
						btn.setLayoutParams(new ViewGroup.LayoutParams(StaticObject.deviceWidth / 3 + 5, ViewGroup.LayoutParams.WRAP_CONTENT));
					} else {
						btn.setLayoutParams(new ViewGroup.LayoutParams(StaticObject.deviceWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
					}
				}
				btn.setHeight(DensityUtil.dip2px(ctx, 42));
				btn.setBackgroundResource(R.drawable.axeac_tab_bg_normal);
				btn.setGravity(Gravity.CENTER);
				btn.setText(names.get(i));
				btn.setTextColor(Color.BLACK);
				btn.setTag(i);
				btn.setOnClickListener(listener);
				btns.add(btn);
				layout.addView(btn);
			}
			navTop.addView(layout);
			return names.size();
		}
		return 0;
	}

	private void setTabTitle(){
		if (!"".equals(title)&&null!=title){
			tabtitle.setVisibility(View.VISIBLE);
			tabtitle.setText(title);
		}else{
			tabtitle.setVisibility(View.GONE);
		}
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int index = (Integer) v.getTag() + 1;
			if (activeTab != index) {
				if (index > activeTab) {
					activeTab = index;
					showActiveTabContent("next");
				} else {
					activeTab = index;
					showActiveTabContent("last");
				}
			}
		}
	};

	/**
	 * 显示标签内容
	 * @param o
	 * 可选值：next last
	 * */
	private void showActiveTabContent(String o) {
		for (int i = 0; i < maxTabCount; i++) {
			if (names.size() > 0) {
				btns.get(i).setBackgroundResource(R.drawable.axeac_tab_bg_normal);
				btns.get(i).setTextColor(Color.BLACK);
			}
		}
		if (activeTab > maxTabCount) {
			return;
		}
		if (names.size() > 0) {
			btns.get(activeTab - 1).setBackgroundResource(R.drawable.axeac_tab_bg_selected);
			btns.get(activeTab - 1).setTextColor(Color.WHITE);
		}
		this.layoutContainer.removeAll();
		ctx.getWindow().getDecorView().post(new Runnable() {

			@Override
			public void run() {
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < indexs.size(); i++) {
							if (activeTab == indexs.get(i)) {
								if (!clear) {
									TabContainer.this.layoutContainer.addViewIn(childs.get(compIds.get(i)));
								}
							}
						}
					}
				});
			}
		});
	}

	/**
	 * 显示最后一页标签
	 * */
	public void showLastTab() {
		if (activeTab > 1) {
			activeTab = activeTab - 1;
		} else {
			activeTab = maxTabCount;
		}
		showActiveTabContent("last");
	}

	/**
	 * 显示下一页标签
	 * */
	public void showNextTab() {
		if (activeTab < maxTabCount) {
			activeTab = activeTab + 1;
		} else {
			activeTab = 1;
		}
		showActiveTabContent("next");
	}

	@Override
	public View getView() {
		return layout;
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		if(isScroll){
			navTop.scrollTo(x,y);
		}
	}
}