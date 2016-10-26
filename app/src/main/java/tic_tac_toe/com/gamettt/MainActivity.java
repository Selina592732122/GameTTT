package tic_tac_toe.com.gamettt;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import tic_tac_toe.com.gamettt.view.SettingDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private GameView mChess;
	private TextView mTvColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mChess = (GameView) findViewById(R.id.chess);
		findViewById(R.id.btnBack).setOnClickListener(this);
		findViewById(R.id.btnReset).setOnClickListener(this);
		findViewById(R.id.btnRedo).setOnClickListener(this);
		findViewById(R.id.btnSave).setOnClickListener(this);
		findViewById(R.id.btnLoad).setOnClickListener(this);
		findViewById(R.id.btnSetting).setOnClickListener(this);
		mTvColor = (TextView) findViewById(R.id.tvCircleColor);
		mChess.setOnCurrentColorListener(new OnCurrentColorListener() {
			@Override
			public void onCurrentColor(String color) {
				mTvColor.setText(color);
			}
		});
		mChess.setOnResultListener(new OnResultListener() {
			@Override
			public void onComplete(String result) {
				new AlertDialog.Builder(MainActivity.this).setTitle("提示")
						.setMessage(result)
						.setPositiveButton("重玩", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								reset();
							}
						}).setNegativeButton("取消", null).show();
			}

			@Override
			public void onError(String msg) {
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				btnBack();
				break;
			case R.id.btnReset:
				reset();
				break;
			case R.id.btnRedo:
				btnRedo();
				break;
			case R.id.btnSave:
				btnSave();
				break;
			case R.id.btnLoad:
				btnLoad();
				break;
			case R.id.btnSetting:
				btnSetting();
				break;
		}
	}

	private void btnSetting() {
		SettingDialog dialog = new SettingDialog(this);
		dialog.setResultClickListener(new SettingDialog.OnResultClickListener() {
			@Override
			public void onResult(int count, int max) {
				mChess.setChessboard(count, max);
			}
		});
		dialog.show();
	}

	//加载保存的状态
	private void btnLoad() {
		mChess.load();
	}

	//保存
	private void btnSave() {
		mChess.save();
	}

	//前进
	private void btnRedo() {
		mChess.redo();
	}

	//后退
	private void btnBack() {
		mChess.undo();
	}

	private void reset() {
		mChess.reset();
	}
}
