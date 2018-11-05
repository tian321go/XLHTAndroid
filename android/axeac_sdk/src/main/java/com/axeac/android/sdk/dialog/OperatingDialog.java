package com.axeac.android.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.axeac.android.sdk.R;
/**
 * describe:Custom Dialog
 * <br>自定义操作按钮Dialog
 * @author axeac
 * @version 1.0.0
 */
public class OperatingDialog extends Dialog {

	public OperatingDialog(Context ctx) {
		super(ctx);
	}

	public OperatingDialog(Context ctx, int theme) {
		super(ctx, theme);
	}

	public static class Builder {

		private Context ctx;
		private boolean flag;
		
		private ListView aidsListView;
		private ListView usersListView;
		private ListView chooseListView;

		private EditText sreachTxt;
		private ImageView sreachBtn;

		public ImageView getSreachBtn() {
			return sreachBtn;
		}

		public EditText getSreachTxt() {
			return sreachTxt;
		}

		private OnClickListener posBtnClickListener;
		private OnClickListener negBtnClickListener;

		public Builder(Context ctx) {
			this.ctx = ctx;
		}

		public Builder setPositiveButton(OnClickListener listener) {
			this.posBtnClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(OnClickListener listener) {
			this.negBtnClickListener = listener;
			return this;
		}
		
		public Builder setCancelable(boolean flag) {
			this.flag = flag;
			return this;
		}
		
		public ListView getListViewForAids() {
			return aidsListView;
		}
		
		public ListView getListViewForUsers() {
			return usersListView;
		}
		public ListView getListViewForChoose() {
			return chooseListView;
		}

		public OperatingDialog create() {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final OperatingDialog dialog = new OperatingDialog(ctx, R.style.Dialog);
			View layout = inflater.inflate(R.layout.axeac_operating_dialog, null);
			dialog.setCancelable(flag);
			dialog.setContentView(layout);
			Window window = dialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			window.setGravity(Gravity.CENTER);
			lp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			window.setAttributes(lp);
			aidsListView = (ListView) layout.findViewById(R.id.operating_aids_list);
			usersListView = (ListView) layout.findViewById(R.id.operating_users_list);
			chooseListView = (ListView) layout.findViewById(R.id.listview_choosed);
			sreachTxt = (EditText)layout.findViewById(R.id.searchTxt);
			sreachBtn = (ImageView)layout.findViewById(R.id.searchBtn);
			((Button) layout.findViewById(R.id.operating_dialog_posbtn)).setText(R.string.axeac_msg_over);
			((Button) layout.findViewById(R.id.operating_dialog_negbtn)).setText(R.string.axeac_msg_cancel);
			if (posBtnClickListener != null) {
				layout.findViewById(R.id.operating_dialog_posbtn).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						posBtnClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
					}
				});
			}
			if (negBtnClickListener != null) {
				layout.findViewById(R.id.operating_dialog_negbtn).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						negBtnClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
					}
				});
			}
			return dialog;
		}
	}
}