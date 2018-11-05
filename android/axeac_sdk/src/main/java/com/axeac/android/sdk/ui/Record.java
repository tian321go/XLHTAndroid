package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.utils.FileUtils;

/**
 * describe:Recording component
 * 录制组件
 * @author axeac
 * @version 1.0.0
 */
public class Record extends LabelComponent {

	/**
	 * Log打印信息文字
	 * */
	private static final String LOG = "Test";
	/**
	 * 文件存储地址
	 * */
	private static final String FILE_PATH = FileUtils.KHPATH + "/Record";
	/**
	 * 录制文件名称
	 * */
	private static final String FILE_NAME = "audiorecord";
	/**
	 * 文件后缀
	 * */
	private static final String SUFFIX = ".amr";

	/**
	 * 保存的文件名
	 * <br>默认值为空
	 * */
	private String fileName = "";
	/**
	 * 文件类型  Video\Sound
	 * <br>默认值为video
	 * */
	private String option = "video";

	private LinearLayout layoutView;

	private Button mButton = null;
	private TextView mTextView = null;
	/**
	 * MediaRecorder对象
	 * */
	private MediaRecorder mRecorder = null;
	/**
	 * MediaPlayer对象
	 * */
	private MediaPlayer mPlayer = null;

	/**
	 * 录制状态
	 * */
	private Status status = Status.REC_STOP;

	/**
	 * 枚举状态值
	 * */
	private enum Status {
		REC_START, REC_PAUSE, REC_RESUME, REC_STOP,
		PLAY_START, PLAY_PAUSE, PLAY_RESUME, PLAY_STOP
	}

	/**
	 * Timer对象
	 * */
	private Timer mTimer;
	/**
	 * TimerTask对象
	 * */
	private TimerTask mTimerTast;
	/**
	 * 录制时间
	 * */
	private int recLen = 0;
	/**
	 * 录制时间
	 * */
	private int len = 0;
	/**
	 * 存储因暂停产生的文件的list集合
	 * */
	private ArrayList<String> recFileList = new ArrayList<String>();
	/**
	 * 文件存储地址
	 * */
	private String path;
	/**
	 * File对象
	 * */
	private File recAudioFile;

	public Record(Activity ctx) {
		super(ctx);
		this.returnable = true;
		layoutView = new LinearLayout(ctx);
		layoutView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		layoutView.setOrientation(LinearLayout.HORIZONTAL);
		layoutView.setGravity(Gravity.CENTER_VERTICAL);
		this.view = layoutView;
	}

	/**
	 * 设置保存的文件名
	 * @param fileName
	 * 保存的文件名
	 * */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 设置文件类型
	 * @param option
	 * 可选值 Video\Sound
	 * */
	public void setOption(String option) {
		this.option = option.trim().toLowerCase();
	}

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		this.option = "sound";
		if (fileName.equals("")) {
			fileName = FILE_NAME;
		}
		if (this.option.equals("sound")) {
			RelativeLayout valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_record, null);
			mButton = (Button) valLayout.findViewById(R.id.record_btn);
			if (!readOnly) {
				mButton.setOnClickListener(listener);
			}
			mTextView = (TextView) valLayout.findViewById(R.id.record_time);
			mTextView.setText(parseLenToTime(0));
			if (!readOnly) {
				mTextView.setOnClickListener(listener);
			}
			layoutView.addView(valLayout);
			mTimer = new Timer(true);
		} else {
			// 录制视频布局
		}
	}

	/**
	 * 接收消息的Handler
	 * */
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mTextView.setText(parseLenToTime(len));
			if (status == Status.PLAY_START || status == Status.PLAY_RESUME) {
				int count = mPlayer.getDuration() / 1000;
				if (mPlayer.getDuration() % 1000 >= 500) {
					count += 1;
				}
				if (len == count) {
					status = Status.PLAY_STOP;
					stopPlaying();
				}
			}
		}
	};

	/**
	 * 本类监听事件
	 * */
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == mButton) {
				if (status == Status.REC_STOP || status == Status.PLAY_STOP) {
					status = Status.REC_START;
					startRecording();
					return;
				}
				if (status == Status.REC_START || status == Status.REC_RESUME ||  status == Status.REC_PAUSE) {
					status = Status.REC_STOP;
					stopRecording();
					return;
				}
				if (status == Status.PLAY_START || status == Status.PLAY_RESUME ||  status == Status.PLAY_PAUSE) {
					status = Status.PLAY_STOP;
					stopPlaying();
					return;
				}
			}
			if (v == mTextView) {
				if (status == Status.REC_START || status == Status.REC_RESUME) {
					status = Status.REC_PAUSE;
					pauseRecording();
					return;
				}
				if (status == Status.REC_PAUSE) {
					status = Status.REC_RESUME;
					resumeRecording();
					return;
				}
				if (status == Status.REC_STOP || status == Status.PLAY_STOP) {
					if (recFileList.size() != 0) {
						status = Status.PLAY_START;
						startPlaying();
					}
					return;
				}
				if (status == Status.PLAY_START || status == Status.PLAY_RESUME) {
					status = Status.PLAY_PAUSE;
					pausePlaying();
					return;
				}
				if (status == Status.PLAY_PAUSE) {
					status = Status.PLAY_RESUME;
					resumePlaying();
					return;
				}
			}
		}
	};

	/**
	 * 开始录制
	 * */
	private void startRecording() {
		System.out.println("----------------StartRecording----------------");
		if (!FileUtils.checkSDCard()) {
			status = Status.REC_STOP;
			Toast.makeText(ctx, R.string.axeac_msg_sdcard_noexist, Toast.LENGTH_SHORT).show();
			return;
		}
		path = FileUtils.getSDCardPath() + FILE_PATH;
		File recAudioDir= new File(path);
		if(!recAudioDir.exists()){
			recAudioDir.mkdirs();
		}
		recFileList.clear();
		recAudioFile = new File(recAudioDir, System.currentTimeMillis() + SUFFIX);
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mRecorder.setOutputFile(recAudioFile.getAbsolutePath());
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener(){
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				cancelTimerTast();
				len = 0;
				mTextView.setText(parseLenToTime(len));
				startTimerTast();
				mRecorder.reset();
			}
		});
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG, "prepare() failed");
		}
		mRecorder.start();
		mButton.setBackgroundResource(R.drawable.axeac_record_stop);
		len = 0;
		mTextView.setText(parseLenToTime(len));
		startTimerTast();
	}

	/**
	 * 暂停录制
	 * */
	private void pauseRecording() {
		System.out.println("----------------PauseRecording----------------");
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		recFileList.add(recAudioFile.getPath());
		cancelTimerTast();
	}

	/**
	 * 重新录制
	 * */
	private void resumeRecording() {
		System.out.println("----------------ResumeRecording----------------");
		recAudioFile = new File(new File(path), System.currentTimeMillis() + SUFFIX);
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mRecorder.setOutputFile(recAudioFile.getAbsolutePath());
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener(){
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				cancelTimerTast();
				mTextView.setText(parseLenToTime(len));
				startTimerTast();
				mRecorder.reset();
			}
		});
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG, "prepare() failed");
		}
		mRecorder.start();
		startTimerTast();
	}

	/**
	 * 停止录制
	 * */
	private void stopRecording() {
		System.out.println("----------------StopRecording----------------");
		recLen = len;
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
			recFileList.add(recAudioFile.getPath());
		}
		mTextView.setText(parseLenToTime(recLen));
		mButton.setBackgroundResource(R.drawable.axeac_record_start);
		cancelTimerTast();
		getInputCollection(recFileList);
	}

	/**
	 * 开始播放
	 * */
	private void startPlaying() {
		System.out.println("----------------StartPlaying----------------");
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(path + "/" + fileName + SUFFIX);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG, "prepare() failed");
		}
		mButton.setBackgroundResource(R.drawable.axeac_record_stop);
		len = 0;
		mTextView.setText(parseLenToTime(len));
		startTimerTast();
	}

	/**
	 * 暂停播放
	 * */
	private void pausePlaying() {
		System.out.println("----------------PausePlaying----------------");
		mPlayer.pause();
		cancelTimerTast();
	}

	/**
	 * 重新播放
	 * */
	private void resumePlaying() {
		System.out.println("----------------ResumePlaying----------------");
		mPlayer.start();
		startTimerTast();
	}

	/**
	 * 停止播放
	 * */
	private void stopPlaying() {
		System.out.println("----------------StopPlaying----------------");
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
		mTextView.setText(parseLenToTime(recLen));
		mButton.setBackgroundResource(R.drawable.axeac_record_start);
		cancelTimerTast();
	}

	/**
	 * 计时器开始计时
	 * */
	private void startTimerTast() {
		mTimerTast = new TimerTask() {
			@Override
			public void run() {
				len++;
				handler.sendEmptyMessage(0);
			}
		};
		mTimer.schedule(mTimerTast, 1000, 1000);
	}

	/**
	 * 取消计时器
	 * */
	private void cancelTimerTast() {
		if (mTimerTast != null) {
			mTimerTast.cancel();
		}
	}

	/**
	 * 合并因暂停产生的多段文件
	 * @param list
	 * 存储因暂停产生的文件的list集合
	 * */
	public void getInputCollection(List<String> list) {
		// 创建音频文件,合并的文件放这里
		File recFile = new File(new File(path), fileName + SUFFIX);
		FileOutputStream fileOutputStream = null;
		if (!recFile.exists()) {
			try {
				recFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fileOutputStream = new FileOutputStream(recFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// list里面为暂停录音 所产生的 几段录音文件的名字，中间几段文件的减去前面的6个字节头文件
		for (int i = 0; i < list.size(); i++) {
			File file = new File((String) list.get(i));
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				byte[] myByte = new byte[fileInputStream.available()];
				// 文件长度
				int length = myByte.length;
				// 头文件
				if (i == 0) {
					while (fileInputStream.read(myByte) != -1) {
						fileOutputStream.write(myByte, 0, length);
					}
				} else { // 之后的文件，去掉头文件就可以了
					while (fileInputStream.read(myByte) != -1) {
						fileOutputStream.write(myByte, 6, length - 6);
					}
				}
				fileOutputStream.flush();
				fileInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 结束后关闭流
		try {
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 合成一个文件后，删除之前暂停录音所保存的零碎合成文件
		deleteListRecord();
	}

	/**
	 * 删除掉录制文件
	 * */
	private void deleteListRecord() {
		for (int i = 0; i < recFileList.size(); i++) {
			File file = new File((String) recFileList.get(i));
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * 将数字转换为分秒
	 * */
	private static String parseLenToTime(int len) {
		int minute = len / 60;
		int second = len % 60;
		String min = minute < 10 ? "0" + minute : "" + minute;
		String sec = second < 10 ? "0" + second : "" + second;
		return min + " : " + sec;
	}

	/**
	 * 返回当前视图
	 * */
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

	}

	@Override
	public void end() {

	}
}