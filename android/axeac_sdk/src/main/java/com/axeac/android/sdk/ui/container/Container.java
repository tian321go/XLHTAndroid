package com.axeac.android.sdk.ui.container;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Container
 * <br>容器
 * @author axeac
 * @version 1.0.0
 */
public class Container extends Component {

	protected String layout;
	protected String scroll;
	protected boolean clear = false;

	protected KHMAP5Layout layoutContainer;
	protected Map<String,Component> childs = new LinkedHashMap<String,Component>();
	protected ArrayList<String> removeComps = new ArrayList<String>();

	public Container(Activity ctx) {
		super(ctx);
		this.layoutContainer = new BoxLayoutY(this.ctx);
		this.returnable = false;
	}

	public void setChilds(Map<String,Component> childs) {
		this.childs = childs;
	}

	/**
	 * 设置布局
	 * @param layout
	 * 字符串
	 * */
	public void setLayout(String layout) {
		if (layout != null && !"".equals(layout)) {
			if ("BoxLayoutY".toUpperCase().equals(layout.toUpperCase())) {
				this.layoutContainer = new BoxLayoutY(this.ctx);
			} else if("BoxLayoutX".toUpperCase().equals(layout.toUpperCase())) {
				this.layoutContainer = new BoxLayoutX(this.ctx);
			}
		}
		this.layout = layout;
	}

	public void setScroll(String scroll) {
		this.scroll = scroll;
	}

	/**
	 * 获得移除的组件
	 * @return
	 * 装有组件的list集合
	 * */
	public ArrayList<String> getRemoveComps() {
		return removeComps;
	}

	/**
	 * 设置移除的组件集合
	 * @param removeComps
	 * 移除组件集合
	 * */
	public void setRemoveComps(ArrayList<String> removeComps) {
		this.removeComps = removeComps;
	}

	/**
	 * 对clear赋值（true或false）
	 * @param clear
	 * clear 值为true或false
	 * */
	public void setClear(boolean clear) {
		this.clear = clear;
	}

	/**
	 * 判断容器是否已清空
	 * @return
	 * clear值为true，代表已清空，false代表未清空
	 * */
	public boolean isClear() {
		return clear;
	}

	/**
	 * describe:According to the layout attribute, rearranged
	 * 根据layout属性，重绘组件
	 */
	@Override
	public void repaint() {
		this.clear();
		for(String key : this.childs.keySet())
			this.layoutContainer.addViewIn(this.childs.get(key));
	}

	/**
	 * 清空容器
	 * */
	public void clear(){
		this.layoutContainer.removeAll();
	}

	/**
	 * describe:The format is c.add. Component ID = true / false, true for the return list
	 * 描述：格式为c.add.组件ID = true/flase，true为返回列表
	 * @return
	 */
	public void add(String compId, boolean isReturn) {
		if(StaticObject.ComponentMap.get(compId) != null && StaticObject.ComponentMap.get(compId).addable){
			childs.put(compId,StaticObject.ComponentMap.get(compId));
			StaticObject.ComponentMap.get(compId).addable = false;
			if (isReturn) {
				StaticObject.ReturnComponentMap.put(compId, StaticObject.ComponentMap.get(compId));
			}
		}
	}

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		String[] compIds = childs.keySet().toArray(new String[0]);
		for (int i = 0; i < compIds.length; i++) {
			this.layoutContainer.addViewIn(StaticObject.ComponentMap.get(compIds[i]));
		}
		int r = Integer.parseInt(bgColor.substring(0, 3));
		int g = Integer.parseInt(bgColor.substring(3, 6));
		int b = Integer.parseInt(bgColor.substring(6, 9));
		this.layoutContainer.getLayout().setBackgroundColor(Color.rgb(r, g, b));
		if (this.bgImage != null && !"".equals(bgImage)) {
			BitmapDrawable draw;
			try {
				draw = new BitmapDrawable(this.ctx.getResources(),BitmapFactory.decodeStream(this.ctx.getResources().getAssets().open(bgImage + ".png")));
				this.layoutContainer.getLayout().setBackgroundDrawable(draw);
			} catch (IOException e) {
			}
		}
		this.layoutContainer.getLayout().getBackground().setAlpha((int)(255 * ((float)this.alpha/100)));
		this.layoutContainer.getLayout().setPadding(0, 10, 0, 0);
	}

	/**
	 * describe:The parameter is the component to be removed
	 *
	 * 移除指定组件
	 * @param compId
	 * compId 组件id
	 */
	public void remove(String compId) {
		removeComps.add(compId);
	}

	/**
	 * 移除所有组件
	 *
	 */
	public void removeComp(String compId) {
		this.layoutContainer.removeAll(StaticObject.ComponentMap.get(compId).getView());
	}

	@Override
	public View getView() {
		return this.layoutContainer.getLayout();
	}

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public void starting() {
		this.buildable = false;
	}

	@Override
	public void end() {
		this.buildable = true;
	}

}