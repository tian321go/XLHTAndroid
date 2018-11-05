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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.axeac.android.sdk.R;
/**
 * describe:Custom Dialog
 * <br>自定义Dialog
 * @author axeac
 * @version 1.0.0
 * */
public class CustomDialog extends Dialog {

	public CustomDialog(Context ctx) {
		super(ctx);
	}

	public CustomDialog(Context ctx, int theme) {
		super(ctx, theme);
	}

	public static class Builder {

		private Context ctx;
		private String title;
		private String message;
		private boolean showMessage;
		private String posBtnText;
		private String neuBtnText;
		private String negBtnText;
		private View contentView;
		private boolean cancelable;
		private boolean canceledOnTouchOutside;

		private OnClickListener posBtnClickListener,
				neuBtnClickListener, negBtnClickListener;

		public Builder(Context ctx) {
			this.ctx = ctx;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setTitle(int title) {
			this.title = ctx.getString(title);
			return this;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setShowMessage(boolean showMessage){
			this.showMessage = showMessage;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = ctx.getString(message);
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setPositiveButton(int resId,
				OnClickListener listener) {
			this.posBtnText = ctx.getString(resId);
			this.posBtnClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String text,
				OnClickListener listener) {
			this.posBtnText = text;
			this.posBtnClickListener = listener;
			return this;
		}

		public Builder setNeutralButton(int resId,
				OnClickListener listener) {
			this.neuBtnText = ctx.getString(resId);
			this.neuBtnClickListener = listener;
			return this;
		}

		public Builder setNeutralButton(String text,
				OnClickListener listener) {
			this.neuBtnText = text;
			this.neuBtnClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int resId,
				OnClickListener listener) {
			this.negBtnText = ctx.getString(resId);
			this.negBtnClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String text,
				OnClickListener listener) {
			this.negBtnText = text;
			this.negBtnClickListener = listener;
			return this;
		}
		
		public Builder setCancelable(boolean cancelable) {
			this.cancelable = cancelable;
			return this;
		}
		
		public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
			this.canceledOnTouchOutside = canceledOnTouchOutside;
			return this;
		}
		
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomDialog dialog = new CustomDialog(ctx, R.style.Dialog);
			View layout = inflater.inflate(R.layout.axeac_alert_dialog, null);
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
			dialog.setContentView(layout);
			Window window = dialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			window.setGravity(Gravity.CENTER);
			lp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			window.setAttributes(lp);
			((TextView) layout.findViewById(R.id.alert_dialog_title)).setText(title);
			if (posBtnText != null) {
				((Button) layout.findViewById(R.id.alert_dialog_posbtn)).setText(posBtnText);
				if (posBtnClickListener != null) {
					layout.findViewById(R.id.alert_dialog_posbtn).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							posBtnClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			} else {
				layout.findViewById(R.id.alert_dialog_posbtn).setVisibility(View.GONE);
			}
			if (neuBtnText != null) {
				((Button) layout.findViewById(R.id.alert_dialog_neubtn)).setText(neuBtnText);
				if (neuBtnClickListener != null) {
					layout.findViewById(R.id.alert_dialog_neubtn).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							neuBtnClickListener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
						}
					});
				}
			} else {
				layout.findViewById(R.id.alert_dialog_neubtn).setVisibility(View.GONE);
			}
			if (negBtnText != null) {
				((Button) layout.findViewById(R.id.alert_dialog_negbtn)).setText(negBtnText);
				if (negBtnClickListener != null) {
					layout.findViewById(R.id.alert_dialog_negbtn).setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							negBtnClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
			} else {
				layout.findViewById(R.id.alert_dialog_negbtn).setVisibility(View.GONE);
			}
			if (!showMessage&&contentView == null){
				((LinearLayout) layout.findViewById(R.id.alert_dialog_view)).setVisibility(View.GONE);
			}
			if (message == null) {
				((TextView) layout.findViewById(R.id.alert_dialog_message)).setVisibility(View.GONE);
			}
			if (message != null) {
				((TextView) layout.findViewById(R.id.alert_dialog_message)).setText(message);
			} else if (contentView != null) {
				((LinearLayout) layout.findViewById(R.id.alert_dialog_view)).removeAllViews();
				((LinearLayout) layout.findViewById(R.id.alert_dialog_view)).addView(contentView);
			}
			if(negBtnText == null && posBtnText == null && neuBtnText == null)
				layout.findViewById(R.id.alert_dialog_line).setVisibility(View.GONE);
			return dialog;
		}
	}
}