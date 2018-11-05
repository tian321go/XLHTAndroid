package com.axeac.android.sdk.ui.base;

import com.axeac.android.sdk.utils.CommonUtil;

/**
 * 文字设置类，包括文字大小、字体、是否粗体、斜体及文字颜色
 * @author axeac
 * @version 1.0.0
 * */
public class Font {
	/**
	 * 包含文字属性的字符串
	 * <br>例：size:14px;family:Arial;style=bold,italic;color:255255255;
	 * */
	private String source;
	/**
	 * 字体  例：Arial
	 * */
	private String family;
	/**
	 * 文字尺寸，默认值：12
	 * */
	private int fontSize = 12;
	/**
	 * 是否粗体，默认值：false
	 * */
	private boolean bold = false;
	/**
	 * 是否斜体，默认值：false
	 * */
	private boolean italic = false;

	/**
	 * 文字颜色，默认值：android.graphics.Color.BLACK
	 * */
	private int color = android.graphics.Color.BLACK;

	// size:14px;family:Arial;style=bold,italic;color:255255255;
	// size:14px;family:宋体;style=bold,italic;color:255255255;
	/**
	 * 传入字符串source，对其赋值
	 * @param source
	 * 文字属性字符串
	 * <br>如：size:14px;family:Arial;style=bold,italic;color:255255255;
	 * */
	public Font(String source) {
		if (source != null) {
			this.source = source;
			init();
		}
	}

	/**
	 * 根据source对family，fontSize，bold，italic，color进行初始化
	 * */
	private void init() {
		try {
			String[] ar = source.trim().split(";");
			for (String str : ar) {
				str = str.trim().toLowerCase();
				int pos = 0;
				if (str.startsWith("size")) {
					str = str.replace("px", "");
					pos = str.indexOf(":");
					if (pos != -1) {
						fontSize = Integer.parseInt(str.substring(pos + 1));
					}
				} else if (str.startsWith("color")) {
					pos = str.indexOf(":");
					if (pos != -1) {
						this.color = CommonUtil
								.getColor(str.substring(pos + 1));
					}
				} else if (str.startsWith("style")) {
					pos = str.indexOf(":");
					if (pos != -1) {
						str = str.substring(pos + 1);
						if (str.indexOf("bold") != -1) {
							this.bold = true;
						}
						if (str.indexOf("italic") != -1) {
							this.italic = true;
						}
					}
				}
			}
		} catch (Throwable e) {

		}
	}

	/**
	 *  返回source（包含文字各种属性的字符串）
	 *  @return
	 *  source（包含文字各种属性的字符串）
	 * */
	public String getSource() {
		return source;
	}

	/**
	 * 返回family（字体）
	 * @return
	 * family（字体）
	 * */
	public String getFamily() {
		return family;
	}

	/**
	 * 对family赋值（字体）
	 * @param family
	 * family（字体）
	 * */
	public void setFamily(String family) {
		this.family = family;
	}

	/**
	 * 返回fontSize（文字尺寸）
	 * @return
	 * fontSize（文字尺寸）
	 * */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * 对fontSize赋值（文字尺寸）
	 * @param fontSize
	 * fontSize（文字尺寸）
	 * */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * 返回bold（是否粗体）
	 * @return
	 * bold（是否粗体）
	 * */
	public boolean isBold() {
		return bold;
	}

	/**
	 * 对bold赋值（是否粗体）
	 * @param bold
	 * bold（是否粗体）
	 * */
	public void setBold(boolean bold) {
		this.bold = bold;
	}

	/**
	 * 返回italic（是否斜体）
	 * @return
	 * italic（是否斜体）
	 * */
	public boolean isItalic() {
		return italic;
	}

	/**
	 * 对italic赋值（是否斜体）
	 * @param italic
	 * italic（是否斜体）
	 * */
	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	/**
	 * 返回color（文字颜色）
	 * @return
	 * color（文字颜色）
	 * */
	public int getColor() {
		return color;
	}

	/**
	 * 对color赋值（文字颜色）
	 * @param color
	 * color（文字颜色）
	 * */
	public void setColor(int color) {
		this.color = color;
	}

}
