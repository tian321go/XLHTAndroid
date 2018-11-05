package com.axeac.android.sdk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.adapters.FileSelectorAdapter;
import com.axeac.android.sdk.ui.FileSelector;
import com.axeac.android.sdk.utils.FileUtils;
/**
 * describe:select file activity
 * <br>选择文件的activity
 * @author axeac
 * @version 1.0.0
 * */
public class FileSelectorActivity extends BaseActivity {

	private Context mContext;

	private GridView mGridView;

	private RelativeLayout rootPathLayout;
	private RelativeLayout pervPathLayout;
//	private ImageButton backPage;
	/**
	 * 文件路径
	 * */
	private String rootPath = "/";
	/**
	 * 文件路径
	 * */
	private String path = "/";
	/**
	 * 是否为文件夹标志
	 * <br>默认值为true
	 * */
	private boolean filter = true;

	/**
	 * FileSelectorAdapter对象
	 * */
	private FileSelectorAdapter mAdapter;
	/**
	 * 存储文件地址的list集合
	 * */
	private List<String> filePaths = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTransStatus(FileSelectorActivity.this);
		this.setContentView(R.layout.axeac_common_layout_normal);
		mContext = this;

		FrameLayout layout = (FrameLayout) this.findViewById(R.id.settings_layout_center);
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		RelativeLayout convertView = (RelativeLayout) mInflater.inflate(R.layout.axeac_fileselector, null);
		layout.addView(convertView);

		rootPathLayout = (RelativeLayout) this.findViewById(R.id.menu_item_first);
		rootPathLayout.setVisibility(View.VISIBLE);
		rootPathLayout.setOnClickListener(mRootPathClickListener());
		ImageView rootPathBtn = (ImageView) this.findViewById(R.id.menu_item_first_btn);
		rootPathBtn.setImageResource(R.drawable.index1);
		TextView rootPathText = (TextView) this.findViewById(R.id.menu_item_first_text);
		rootPathText.setText(R.string.axeac_msg_rootpath);
		pervPathLayout = (RelativeLayout) this.findViewById(R.id.menu_item_second);
		pervPathLayout.setVisibility(View.VISIBLE);
		pervPathLayout.setOnClickListener(mPervPathClickListener());
		ImageView pervPathBtn = (ImageView) this.findViewById(R.id.menu_item_second_btn);
		pervPathBtn.setImageResource(R.drawable.index1);
		TextView pervPathText = (TextView) this.findViewById(R.id.menu_item_second_text);
		pervPathText.setText(R.string.axeac_msg_pervpath);

		mGridView = (GridView) convertView.findViewById(R.id.fileselector_list);
		mGridView.setOnItemClickListener(mItemClickListener);
		Intent intent = this.getIntent();
		if (intent != null) {
			path = intent.getStringExtra("PATH");
			filter = intent.getBooleanExtra("FILTER", true);
		}
		if (path == null || path.equals("/")) {
			path = FileUtils.getSDCardPath();
		}
		rootPath = path;
		rootPathLayout.setVisibility(View.GONE);
		pervPathLayout.setVisibility(View.GONE);
		obtainFileList(path);
		backPage.setOnClickListener(mBackBtnClickListener());
	}

	// describe:Display all files in the path with GrideView
	/**
	 * 网格视图显示某路径下的所有文件
	 * @param path
	 * file path
	 * 文件路径
	 * */
	private void obtainFileList(String path) {
		filePaths.clear();
		this.path = path;
		File f = new File(path);
		File[] files = null;
		if (filter) {
			files = f.listFiles(fileFilter);
		} else {
			files = f.listFiles();
		}
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (!file.getAbsolutePath().equals("/mnt") && !file.getAbsolutePath().equals("/sdcard")) {
					if (file.canRead()) {
						filePaths.add(file.getPath());
					}
				}
			}
		}
		mAdapter = new FileSelectorAdapter(mContext, filePaths);
		mGridView.setAdapter(mAdapter);
	}

	/**
	 * 子条目点击事件
	 * */
	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			rootPathLayout.setVisibility(View.VISIBLE);
			pervPathLayout.setVisibility(View.VISIBLE);
			File file = new File(filePaths.get(position));
			if (file.isDirectory()) {
				obtainFileList(filePaths.get(position));
			} else {
				if (mAdapter.getIsCheckSelected().get(position)) {
					mAdapter.getItemViewMap().get(position).filecheck.setVisibility(View.GONE);
					mAdapter.getIsCheckSelected().put(position, false);
				} else {
					for (int i = 0; i < mAdapter.getIsCheckSelected().size(); i++) {
						if (i == position) {
							mAdapter.getItemViewMap().get(position).filecheck.setVisibility(View.VISIBLE);
							mAdapter.getIsCheckSelected().put(position, true);
						} else {
							if (mAdapter.getItemViewMap().get(i) != null) {
								mAdapter.getItemViewMap().get(i).filecheck.setVisibility(View.GONE);
							}
							mAdapter.getIsCheckSelected().put(i, false);
						}
					}
				}
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	private View.OnClickListener mPervPathClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File file = new File(path);
				if (file.getParent().equals(rootPath)) {
					rootPathLayout.setVisibility(View.GONE);
					pervPathLayout.setVisibility(View.GONE);
					obtainFileList(rootPath);
				} else {
					obtainFileList(file.getParent());
				}
			}
		};
	}

	private View.OnClickListener mRootPathClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rootPathLayout.setVisibility(View.GONE);
				pervPathLayout.setVisibility(View.GONE);
				obtainFileList(rootPath);
			}
		};
	}

	/**
	 * 过滤文件
	 * */
	private FileFilter fileFilter = new FileFilter() {

		private String [] filters = {".txt", ".doc"};

		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			}
			for (String filter : filters) {
				if (file.getName().toLowerCase().endsWith(filter)) {
					return true;
				}
			}
			return false;
		}
	};

	private View.OnClickListener mSaveBtnClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int index = -1;
				for (int i = 0; i < mAdapter.getIsCheckSelected().size(); i++) {
					if (mAdapter.getIsCheckSelected().get(i)) {
						index = i;
					}
				}
				if (index > -1) {
					Handler handler = FileSelector.handlerMap.get(FileSelector.curPosition);
					if (handler != null) {
						Message msg = new Message();
						msg.what = FileSelector.FILE_WITH_DATA;
						msg.obj = path + "/" + mAdapter.getItemViewMap().get(index).filename.getText().toString();
						handler.sendMessage(msg);
					}
					backFuc();
				} else {
					Toast.makeText(mContext, R.string.axeac_msg_choice_file_nil, Toast.LENGTH_SHORT).show();
					backFuc();
				}
			}
		};
	}

	private View.OnClickListener mBackBtnClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backFuc();
			}
		};
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backFuc();
		}
		return false;
	}

	private void backFuc() {
		this.finish();
	}
}