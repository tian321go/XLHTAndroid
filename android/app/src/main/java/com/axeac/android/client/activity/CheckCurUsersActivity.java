package com.axeac.android.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.axeac.android.client.R;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.adapters.ChooseGridAdapter;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.utils.DeviceMessage;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * 用户角色设置界面
 * @author axeac
 * @version 2.3.0.0001
 *
 * */
public class CheckCurUsersActivity extends BaseActivity {

	private Context mContext;

	private TextView curUsersNote;
	private GridView curUsersData;
	private TextView curRolesNote;
	private GridView curRolesData;

	private List<String> usersDataKeys = new ArrayList<String>();
	private List<String> usersDataVals = new ArrayList<String>();
	private Map<String, ArrayList<String>> rolesDataKeys = new HashMap<String, ArrayList<String>>();
	private Map<String, ArrayList<String>> rolesDataVals = new HashMap<String, ArrayList<String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTransStatus(CheckCurUsersActivity.this);
		this.setContentView(R.layout.axeac_common_layout_normal);
		mContext = this;
		setTitle(R.string.settings_curusers);

		FrameLayout layout = (FrameLayout) this.findViewById(R.id.settings_layout_center);
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		LinearLayout convertView = (LinearLayout) mInflater.inflate(R.layout.userinfo_seletor, null);
		layout.addView(convertView);
		curUsersNote = (TextView) convertView.findViewById(R.id.curusers_users_note);
		curUsersData = (GridView) convertView.findViewById(R.id.curusers_users_data);
		curRolesNote = (TextView) convertView.findViewById(R.id.curusers_roles_note);
		curRolesData = (GridView) convertView.findViewById(R.id.curusers_roles_data);

		RelativeLayout saved = (RelativeLayout) this.findViewById(R.id.menu_item_first);
		saved.setVisibility(View.VISIBLE);
		saved.setOnClickListener(mSaveBtnClickListener());
		ImageView savedBtn = (ImageView) this.findViewById(R.id.menu_item_first_btn);
		savedBtn.setImageResource(R.drawable.btn_saved);
		TextView savedText = (TextView) this.findViewById(R.id.menu_item_first_text);
		savedText.setText(R.string.axeac_msg_confirm);

		Intent intent = this.getIntent();
		if (intent != null) {
			execute(intent.getStringExtra(StaticObject.CURUSERSDATA));
		} else {
			backFuc();
		}
	}

	private void execute(String data) {
		if (data != null && !data.equals("")) {
			parseData(data);
		}
		if (usersDataKeys.size() <= 0) {
			return;
		}
		curUsersNote.setVisibility(View.VISIBLE);
		curUsersData.setVisibility(View.VISIBLE);
		boolean[] checkUsers = new boolean[usersDataKeys.size()];
		checkUsers[0] = true;
		ChooseGridAdapter usersAdapter = new ChooseGridAdapter(mContext, usersDataVals, checkUsers, "");
		curUsersData.setAdapter(usersAdapter);
		curUsersData.setOnItemClickListener(usersOnItemClickListener(usersAdapter));
		DeviceMessage.getParams().put("MEIP_CURRENT_USER", usersDataKeys.get(0));
		for (int i = 0; i < usersAdapter.getIsCheckSelected().size(); i++) {
			if (usersAdapter.getIsCheckSelected().get(i)) {
				List<String> rolesKeys = rolesDataKeys.get(usersDataKeys.get(i));
				if (rolesKeys.size() <= 0) {
					curRolesNote.setVisibility(View.GONE);
					curRolesData.setVisibility(View.GONE);
					DeviceMessage.getParams().remove("MEIP_CURRENT_ROLE");
					return;
				}
				curRolesNote.setVisibility(View.VISIBLE);
				curRolesData.setVisibility(View.VISIBLE);
				boolean[] checkRoles = new boolean[rolesKeys.size()];
				checkRoles[0] = true;
				ChooseGridAdapter rolesAdapter = new ChooseGridAdapter(mContext, rolesDataVals.get(usersDataKeys.get(i)), checkRoles, "");
				curRolesData.setAdapter(rolesAdapter);
				curRolesData.setOnItemClickListener(rolesOnItemClickListener(rolesAdapter, i));
				DeviceMessage.getParams().put("MEIP_CURRENT_ROLE", rolesDataKeys.get(usersDataKeys.get(i)).get(0));
				return;
			}
		}
	}

	private AdapterView.OnItemClickListener usersOnItemClickListener(final ChooseGridAdapter usersAdapter) {
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				for (int i = 0; i < usersAdapter.getIsCheckSelected().size(); i++) {
					if (i == position) {
						usersAdapter.getItemViewMap().get(position).text.setBackgroundResource(R.drawable.axeac_label_choice_enable);
						usersAdapter.getItemViewMap().get(position).img.setVisibility(View.VISIBLE);
						usersAdapter.getIsCheckSelected().put(position, true);
						DeviceMessage.getParams().put("MEIP_CURRENT_USER", usersDataKeys.get(position));
					} else {
						usersAdapter.getItemViewMap().get(i).text.setBackgroundResource(R.drawable.axeac_label_choice_disable);
						usersAdapter.getItemViewMap().get(i).img.setVisibility(View.INVISIBLE);
						usersAdapter.getIsCheckSelected().put(i, false);
					}
				}
				if (usersAdapter.getIsCheckSelected().get(position)) {
					List<String> rolesKeys = rolesDataKeys.get(usersDataKeys.get(position));
					if (rolesKeys.size() <= 0) {
						curRolesNote.setVisibility(View.GONE);
						curRolesData.setVisibility(View.GONE);
						DeviceMessage.getParams().remove("MEIP_CURRENT_ROLE");
						return;
					}
					curRolesNote.setVisibility(View.VISIBLE);
					curRolesData.setVisibility(View.VISIBLE);
					boolean[] checkRoles = new boolean[rolesKeys.size()];
					checkRoles[0] = true;
					ChooseGridAdapter rolesAdapter = new ChooseGridAdapter(mContext, rolesDataVals.get(usersDataKeys.get(position)), checkRoles, "");
					curRolesData.setAdapter(rolesAdapter);
					curRolesData.setOnItemClickListener(rolesOnItemClickListener(rolesAdapter, position));
					DeviceMessage.getParams().put("MEIP_CURRENT_ROLE", rolesDataKeys.get(usersDataKeys.get(position)).get(0));
				}
				usersAdapter.notifyDataSetChanged();
			}
		};
	}

	private AdapterView.OnItemClickListener rolesOnItemClickListener(final ChooseGridAdapter rolesAdapter, final int pos) {
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				for (int i = 0; i < rolesAdapter.getIsCheckSelected().size(); i++) {
					if (i == position) {
						rolesAdapter.getItemViewMap().get(position).text.setBackgroundResource(R.drawable.axeac_label_choice_enable);
						rolesAdapter.getItemViewMap().get(position).img.setVisibility(View.VISIBLE);
						rolesAdapter.getIsCheckSelected().put(position, true);
						DeviceMessage.getParams().put("MEIP_CURRENT_ROLE", rolesDataKeys.get(usersDataKeys.get(pos)).get(position));
					} else {
						rolesAdapter.getItemViewMap().get(i).text.setBackgroundResource(R.drawable.axeac_label_choice_disable);
						rolesAdapter.getItemViewMap().get(i).img.setVisibility(View.INVISIBLE);
						rolesAdapter.getIsCheckSelected().put(i, false);
					}
				}
				rolesAdapter.notifyDataSetChanged();
			}
		};
	}

	private void parseData(String data) {
		usersDataKeys.clear();
		usersDataVals.clear();
		rolesDataKeys.clear();
		rolesDataVals.clear();

		Property property = new Property(data);
		Vector<String> keys = property.keys();

		usersDataKeys.add(StaticObject.read.getString(StaticObject.CUR_USERNAME, ""));
		usersDataVals.add(mContext.getString(R.string.curusers_curuser));
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.elementAt(i);
			String val = (String) property.getProperty(key);
			if (key.trim().toLowerCase().startsWith("user")) {
				String[] userData = StringUtil.split(val, "||");
				if (userData.length == 2) {
					usersDataKeys.add(userData[0]);
					usersDataVals.add(userData[1]);
				}
			}
		}

		for (int i = 0; i < usersDataKeys.size(); i++) {
			String userId = usersDataKeys.get(i);
			ArrayList<String> rolesKeys = new ArrayList<String>();
			ArrayList<String> rolesVals = new ArrayList<String>();
			for (int j = 0; j < keys.size(); j++) {
				String key = (String) keys.elementAt(j);
				String val = (String) property.getProperty(key);
				if (key.trim().toLowerCase().startsWith(userId + ".role")) {
					String[] roleData = StringUtil.split(val, "||");
					if (roleData.length == 2) {
						rolesKeys.add(roleData[0]);
						rolesVals.add(roleData[1]);
					}
				}
			}
			rolesDataKeys.put(userId, rolesKeys);
			rolesDataVals.put(userId, rolesVals);
		}
	}

	private View.OnClickListener mSaveBtnClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(StaticObject.BUTTON_ACTION);
				intent.putExtra("name", "curUsersSavedButton");
				LocalBroadcastManager
						.getInstance(CheckCurUsersActivity.this).sendBroadcast(intent);
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