package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.ui.base.Component;

/**
 * describe:Player component
 * 播放组件
 * @author axeac
 * @version 1.0.0
 */
public class Player extends Component {

	private View view;
	/**
	 * VideoView对象
	 * */
	private VideoView mVideoView;

	/**
	 * 标题文本
	 * */
	private String title;
	/**
	 * 标题图标
	 * */
	private String icon;
	/**
	 * 要打开的文件地址
	 * */
	private String url;
	/**
	 * 文件类型  Video\Sound
	 * 默认值为video
	 * */
	private String option = "video";
	
	public Player(Activity ctx) {
		super(ctx);
		view =  LayoutInflater.from(ctx).inflate(R.layout.axeac_player,null);
		mVideoView = view.findViewById(R.id.player);
//		mVideoView.setLayoutParams(new ViewGroup.LayoutParams(
//				320, 480));
	}

	/**
	 * 设置标题文本
	 * @param title
	 * 标题文本
	 * */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 设置标题图标
	 * @param icon
	 * 标题图标
	 * */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 设置要播放的文件的地址
	 * @param url
	 * 要播放的文件的地址
	 * */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 设置要播放的文件类型
	 * @param option
	 * 可选值 Video\Sound
	 * */
	public void setOption(String option) {
		this.option = option;
	}

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		WindowManager wm = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);
		int screenwidth = wm.getDefaultDisplay().getWidth();
		int sereenheight = wm.getDefaultDisplay().getHeight();
		if (this.width == "-1" && this.height == -1) {
			mVideoView.setLayoutParams(new LinearLayout.LayoutParams(screenwidth, sereenheight, 1));
		} else {
			if (this.width == "-1") {
				mVideoView.setLayoutParams(new LinearLayout.LayoutParams(screenwidth, this.height, 1));
			} else if (this.height == -1) {
				if (this.width.endsWith("%")) {
					int viewWeight = 100 - (int) Float.parseFloat(this.width.substring(0, this.width.indexOf("%")));
					mVideoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
				} else {
					mVideoView.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), sereenheight));
				}
			} else {
				if (this.width.endsWith("%")) {
					int viewWeight = 100 - (int) Float.parseFloat(this.width.substring(0, this.width.indexOf("%")));
					mVideoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
				} else {
					mVideoView.setLayoutParams(new LinearLayout.LayoutParams(screenwidth, this.height));
				}
			}
		}
		option = "video";
		if (url != null) {
			if (option.equals("sound")) {
				
			} else {
				//				mVideoView.setVideoURI(Uri.parse(url));
				MediaController  mediaController=new MediaController(ctx);
				mVideoView.setMediaController(mediaController);
				mVideoView.setVideoPath(url);
				mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						mVideoView.start();//开始播放视频
					}
				});
			}
		} else {
			Toast.makeText(ctx, R.string.axeac_toast_exp_tovideopath, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public String getValue() {
		return null;
	}

	/**
	 * 返回当前视图
	 * */
	@Override
	public View getView() {
		return view;
	}

	@Override
	public void repaint() {
		
	}

	@Override
	public void starting() {
		
	}

	@Override
	public void end() {
		
	}
}