package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.FileSelectorActivity;
import com.axeac.android.sdk.tools.Base64Coding;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.FileUtils;

/**
 * describe:File selection
 * 文件选择
 * @author axeac
 * @version 1.0.0
 */
public class FileSelector extends LabelComponent {

	public static final int FILE_WITH_DATA = 3003;

	private EditText textField;

	/**
	 * 负责将position值赋值给curPosition的中间值
	 * */
	private int pos = 0;

	/**
	 * 标记handler位置的position
	 * */
	public static int position = 0;

	/**
	 * 标记handler位置的position
	 * */
	public static int curPosition = 0;

	/**
	 * 存储handler的Map集合
	 * */
	public static Map<Integer, Handler> handlerMap = new HashMap<Integer, Handler>();

	/**
	 * 文件路径
	 * */
	private String path = "/";

	/**
	 * 过滤器，*.txt||*.doc
	 * */
	private boolean filter = true;

	public FileSelector(Activity ctx) {
		super(ctx);
		this.returnable = true;
		RelativeLayout valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text, null);
		textField = (EditText) valLayout.findViewById(R.id.label_text_single);
		textField.setClickable(true);
		textField.setInputType(InputType.TYPE_NULL);
		this.view = valLayout;
		handlerMap.put(++position, mHandler);
		pos = position;
	}

	/**
	 * 设置文件路径
	 * @param path
	 * 文件路径
	 * */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 设置是否过滤文件
	 * @param filter
	 * 可选值 true|false
	 * */
	public void setFilter(String filter) {
		this.filter = Boolean.parseBoolean(filter);
	}

	private View.OnTouchListener listener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				curPosition = pos;
				chooseFile();
			}
			return true;
		}
	};

	/**
	 * 选择文件
	 * */
	private void chooseFile() {
		if (FileUtils.checkSDCard()) {
			Intent intent = new Intent(ctx, FileSelectorActivity.class);
			intent.putExtra("PATH", path);
			intent.putExtra("FILTER", filter);
			ctx.startActivityForResult(intent, FILE_WITH_DATA);
		} else {
			textField.setText("");
			Toast.makeText(ctx, R.string.axeac_msg_sdcard_noexist, Toast.LENGTH_SHORT).show();
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FILE_WITH_DATA:
				chooseWithData((String) msg.obj);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 显示选择文件路径
	 * @param filePath
	 * 文件路径
	 * */
	private void chooseWithData(String filePath) {
		if (filePath.equals("")) {
			textField.setText("");
		} else {
			textField.setText(filePath);
		}
	}

	/**
	 * 返回文件数据
	 * @return
	 * 文件数据
	 * */
	@SuppressWarnings("unused")
	private String getReturnValue() {
		String data = "";
		File file = new File(textField.getText().toString());
		try {
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
				byte[] temp = new byte[1024];
				int size = 0;
				while ((size = in.read(temp)) != -1) {
					out.write(temp, 0, size);
				}
				in.close();
				byte[] content = out.toByteArray();
				out.close();
				data = Base64Coding.encode(content);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		if(!this.visiable) return;
		if (!readOnly) {
			textField.setOnTouchListener(listener);
		}
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
						textField.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
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
							textField.setTextColor(Color.rgb(r, g, b));
						}
					}
				}
			}
		}
		if (familyName == null || "".equals(familyName)) {
			textField.setTypeface(Typeface.defaultFromStyle(style));
		} else {
			textField.setTypeface(Typeface.create(familyName, style));
		}
	}

	@Override
	public View getView() {
		return super.getView();
	}
	
	@Override
	public String getValue() {
		return null;
	}

	@Override
	public void repaint() {

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