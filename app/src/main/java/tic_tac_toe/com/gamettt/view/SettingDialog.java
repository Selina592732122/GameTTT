package tic_tac_toe.com.gamettt.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import tic_tac_toe.com.gamettt.R;

public class SettingDialog extends Dialog {
	private OnResultClickListener onResultClickListener;
	private Context context;

	public SettingDialog(Context context) {
		super(context);
		this.context = context;
	}

	public SettingDialog(Context context, int themeResId) {
		super(context, themeResId);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	public void setResultClickListener(OnResultClickListener onResultClickListener) {
		this.onResultClickListener = onResultClickListener;
	}

	private void init() {
		setContentView(R.layout.dialog_setting);
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		final EditText etCount = (EditText) findViewById(R.id.etCount);
		final EditText etMax = (EditText) findViewById(R.id.etMax);
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					int count = Integer.parseInt(etCount.getText().toString());
					int max = Integer.parseInt(etMax.getText().toString());
					if (count > 5 && count < 3) {
						Toast.makeText(context, "棋子数最大5,最小3", Toast.LENGTH_SHORT).show();
						return;
					}
					if (max > 10) {
						Toast.makeText(context, "棋盘大小最大10*10", Toast.LENGTH_SHORT).show();
						return;
					}
					if (count <= max) {
						onResultClickListener.onResult(count, max);
						dismiss();
					} else {
						Toast.makeText(context, "棋子数大于棋盘大小", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					Toast.makeText(context, "格式有误", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public interface OnResultClickListener {
		void onResult(int count, int max);
	}
}
