package tic_tac_toe.com.gamettt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tic_tac_toe.com.gamettt.bean.Point;
import tic_tac_toe.com.gamettt.bean.Position;

interface OnCurrentColorListener {
	void onCurrentColor(String color);
}

interface OnResultListener {
	void onComplete(String result);

	void onError(String msg);
}

public class GameView extends View {
	private static int COUNT = 3;// 横竖斜方连续个数先满count个，为胜方
	private static int Max = 3;// Max*Max棋盘
	private int mColor;
	private boolean canTouch;
	private String resultMsg;
	private Paint mLinePaint;
	private Position[][] mScore;
	private float mChildWidth;
	private float mChildHeight;
	private Paint mCirclePaint;
	private int mChessPieces;// -1黑，1白，0初始化
	private ArrayList<Position> mPositionList = new ArrayList<>();//棋盘共下的子
	private ArrayList<Position> mBlackPositionList = new ArrayList<>();//棋盘下的黑子
	private ArrayList<Position> mWhitePositionList = new ArrayList<>();//棋盘共下的白子
	private ArrayList<Position> mPositionUndoList = new ArrayList<>();//存储悔棋的步骤
	//	private ArrayList<Position> mSuccessPositionList = new ArrayList<>();
	private ArrayList<Point> mSuccessPointList = new ArrayList<>();

	private OnCurrentColorListener onCurrentColorListener;
	private OnResultListener onResultListener;
	private int m;//闪动次数
	private int mCircleSize;
	private Handler circleHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				canTouch = false;
				mCircleSize = (Integer) msg.obj;
				invalidate();
			} else if (msg.what == 2) {
				onResultListener.onComplete(resultMsg);
			}
		}
	};

	public GameView(Context context) {
		super(context);
		init();
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		//初始化棋盘
		canTouch = true;
		m = 0;
		mPositionList.clear();
		mBlackPositionList.clear();
		mWhitePositionList.clear();
		mSuccessPointList.clear();
		mPositionUndoList.clear();
		mScore = new Position[Max][Max];
		for (int i = 0; i < mScore.length; i++) {
			for (int j = 0; j < mScore[i].length; j++) {
				mScore[i][j] = new Position(0, 0, 0, 0, null);
			}
		}

		mLinePaint = new Paint();
		mLinePaint.setColor(Color.BLACK);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStrokeCap(Paint.Cap.ROUND);
		mLinePaint.setStrokeWidth(6);

		mCirclePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		exchangeChess();
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		mChildWidth = width / Max;
		mChildHeight = height / Max;

		//竖
		for (int i = 1; i < Max; i++) {
			canvas.drawLine(mChildWidth * i, 0, mChildWidth * i, height, mLinePaint);
		}
		//横
		for (int i = 1; i < Max; i++) {
			canvas.drawLine(0, mChildHeight * i, width, mChildHeight * i, mLinePaint);
		}
		//画圆
		for (int i = 0; i < mPositionList.size(); i++) {
			Position position = mPositionList.get(i);
			mCirclePaint.setColor(position.getColor());
			canvas.drawCircle(position.getX(), position.getY(), 40, mCirclePaint);
		}

		if (mSuccessPointList.size() != COUNT) {
			return;
		}
		for (int i = 0; i < mSuccessPointList.size(); i++) {
			Position position = getPosition(mSuccessPointList.get(i));
			mCirclePaint.setColor(mColor);
			canvas.drawCircle(position.getX(), position.getY(), mCircleSize, mCirclePaint);
		}
	}

	/**
	 * 计算输赢
	 */
	private void calculator(List<Position> myChessPosition, String color) {
		mSuccessPointList.clear();
		if (myChessPosition.size() < COUNT) {
			return;
		}
		if (color.equals("黑子")) {
			mColor = Color.BLACK;
		} else {
			mColor = Color.WHITE;
		}
		List<Point> myPointList = new ArrayList<>();
		Point temPoint = new Point(0, 0);
		for (int i = 0; i < myChessPosition.size(); i++) {
			myPointList.add(myChessPosition.get(i).getPoint());
		}
		int count = 0;
		Point point = myPointList.get(myPointList.size() - 1);//获取最后下的棋子的位置
		int x = point.getX();
		int y = point.getY();
		temPoint.setX(x).setY(y);
		//横
		while (myPointList.contains(temPoint)) {
			mSuccessPointList.add(new Point(temPoint.getX(), temPoint.getY()));
			count++;
			int tempX = temPoint.getX();
			if (!(tempX < Max) || count >= COUNT) {
				break;
			}
			temPoint.setX(tempX + 1);
		}
		if (count == COUNT) {
			resultMsg = "本局结束，" + color + "胜！！！";
			new Thread(new CircleThread()).start();
			return;
		}
		temPoint.setX(x).setY(y);
		mSuccessPointList.remove(0);
		count--;//重复算了x,y这点
		while (myPointList.contains(temPoint)) {
			mSuccessPointList.add(new Point(temPoint.getX(), temPoint.getY()));
			count++;
			int tempX = temPoint.getX();
			if (tempX < 1 || count >= COUNT) {
				break;
			}
			temPoint.setX(tempX - 1);
		}
		if (count == COUNT) {
			resultMsg = "本局结束，" + color + "胜！！！";
			new Thread(new CircleThread()).start();
			return;
		}
		//竖
		mSuccessPointList.clear();
		count = 0;
		temPoint.setX(x).setY(y);
		while (myPointList.contains(temPoint)) {
			mSuccessPointList.add(new Point(temPoint.getX(), temPoint.getY()));
			count++;
			int tempY = temPoint.getY();
			Log.d("横+", "count=" + count + ",tempY=" + tempY);
			if (!(tempY < Max) || count >= COUNT) {
				break;
			}
			temPoint.setY(tempY + 1);
		}
		if (count == COUNT) {
			resultMsg = "本局结束，" + color + "胜！！！";
			new Thread(new CircleThread()).start();
			return;
		}
		mSuccessPointList.remove(0);
		temPoint.setX(x).setY(y);
		count--;//重复算了x,y这点
		while (myPointList.contains(temPoint)) {
			mSuccessPointList.add(new Point(temPoint.getX(), temPoint.getY()));
			count++;
			int tempY = temPoint.getY();
			Log.d("横-", "count=" + count + ",tempY=" + tempY);
			if (tempY < 1 || count >= COUNT) {
				break;
			}
			temPoint.setY(tempY - 1);
		}
		if (count == COUNT) {
			resultMsg = "本局结束，" + color + "胜！！！";
			new Thread(new CircleThread()).start();
			return;
		}
		//正斜向 /
		mSuccessPointList.clear();
		count = 0;
		temPoint.setX(x).setY(y);
		while (myPointList.contains(temPoint)) {
			mSuccessPointList.add(new Point(temPoint.getX(), temPoint.getY()));
			count++;
			int tempX = temPoint.getX();
			int tempY = temPoint.getY();
			Log.d("横+", "count=" + count + ",tempY=" + tempY);
			if (tempY < 1 || !(tempX < Max) || count >= COUNT) {
				break;
			}
			temPoint.setX(tempX + 1).setY(tempY - 1);
		}
		if (count == COUNT) {
			resultMsg = "本局结束，" + color + "胜！！！";
			new Thread(new CircleThread()).start();
			return;
		}
		mSuccessPointList.remove(0);
		temPoint.setX(x).setY(y);
		count--;
		while (myPointList.contains(temPoint)) {
			mSuccessPointList.add(new Point(temPoint.getX(), temPoint.getY()));
			count++;
			int tempX = temPoint.getX();
			int tempY = temPoint.getY();
			Log.d("横+", "count=" + count + ",tempY=" + tempY);
			if (tempX < 1 || !(tempY < Max) || count >= COUNT) {
				break;
			}
			temPoint.setX(tempX - 1).setY(tempY + 1);
		}
		if (count == COUNT) {
			resultMsg = "本局结束，" + color + "胜！！！";
			new Thread(new CircleThread()).start();
			return;
		}
		//反斜向 \
		mSuccessPointList.clear();
		count = 0;
		temPoint.setX(x).setY(y);
		while (myPointList.contains(temPoint)) {
			mSuccessPointList.add(new Point(temPoint.getX(), temPoint.getY()));
			count++;
			int tempX = temPoint.getX();
			int tempY = temPoint.getY();
			Log.d("横+", "count=" + count + ",tempY=" + tempY);
			if (!(tempY < Max) || !(tempX < Max) || count >= COUNT) {
				break;
			}
			temPoint.setX(tempX + 1).setY(tempY + 1);
		}
		if (count == COUNT) {
			resultMsg = "本局结束，" + color + "胜！！！";
			new Thread(new CircleThread()).start();
			return;
		}
		mSuccessPointList.remove(0);
		temPoint.setX(x).setY(y);
		count--;
		while (myPointList.contains(temPoint)) {
			mSuccessPointList.add(new Point(temPoint.getX(), temPoint.getY()));
			count++;
			int tempX = temPoint.getX();
			int tempY = temPoint.getY();
			Log.d("横+", "count=" + count + ",tempY=" + tempY);
			if (tempX < 1 || tempY < 1 || count >= COUNT) {
				break;
			}
			temPoint.setX(tempX - 1).setY(tempY - 1);
		}
		if (count == COUNT) {
			resultMsg = "本局结束，" + color + "胜！！！";
			new Thread(new CircleThread()).start();
			return;
		}
		if (mPositionList.size() == Max * Max) {
			resultMsg = "本局结束，平局！！！";
			mSuccessPointList.clear();
			onResultListener.onComplete(resultMsg);
		}
		//		int sum1 = Math.abs(mScore[0][0].getScore() + mScore[0][1].getScore() + mScore[0][2].getScore());
		//		int sum2 = Math.abs(mScore[1][0].getScore() + mScore[1][1].getScore() + mScore[1][2].getScore());
		//		int sum3 = Math.abs(mScore[2][0].getScore() + mScore[2][1].getScore() + mScore[2][2].getScore());
		//		int sum4 = Math.abs(mScore[0][0].getScore() + mScore[1][0].getScore() + mScore[2][0].getScore());
		//		int sum5 = Math.abs(mScore[0][1].getScore() + mScore[1][1].getScore() + mScore[2][1].getScore());
		//		int sum6 = Math.abs(mScore[0][2].getScore() + mScore[1][2].getScore() + mScore[2][2].getScore());
		//		int sum7 = Math.abs(mScore[0][0].getScore() + mScore[1][1].getScore() + mScore[2][2].getScore());
		//		int sum8 = Math.abs(mScore[0][2].getScore() + mScore[1][1].getScore() + mScore[2][0].getScore());
		//
		//		if (sum1 == 3) {
		//			mSuccessPositionList.add(mScore[0][0]);
		//			mSuccessPositionList.add(mScore[0][1]);
		//			mSuccessPositionList.add(mScore[0][2]);
		//		} else if (sum2 == 3) {
		//			mSuccessPositionList.add(mScore[1][0]);
		//			mSuccessPositionList.add(mScore[1][1]);
		//			mSuccessPositionList.add(mScore[1][2]);
		//		} else if (sum3 == 3) {
		//			mSuccessPositionList.add(mScore[2][0]);
		//			mSuccessPositionList.add(mScore[2][1]);
		//			mSuccessPositionList.add(mScore[2][2]);
		//		} else if (sum4 == 3) {
		//			mSuccessPositionList.add(mScore[0][0]);
		//			mSuccessPositionList.add(mScore[1][0]);
		//			mSuccessPositionList.add(mScore[2][0]);
		//		} else if (sum5 == 3) {
		//			mSuccessPositionList.add(mScore[0][1]);
		//			mSuccessPositionList.add(mScore[1][1]);
		//			mSuccessPositionList.add(mScore[2][1]);
		//		} else if (sum6 == 3) {
		//			mSuccessPositionList.add(mScore[0][2]);
		//			mSuccessPositionList.add(mScore[1][2]);
		//			mSuccessPositionList.add(mScore[2][2]);
		//		} else if (sum7 == 3) {
		//			mSuccessPositionList.add(mScore[0][0]);
		//			mSuccessPositionList.add(mScore[1][1]);
		//			mSuccessPositionList.add(mScore[2][2]);
		//		} else if (sum8 == 3) {
		//			mSuccessPositionList.add(mScore[0][2]);
		//			mSuccessPositionList.add(mScore[1][1]);
		//			mSuccessPositionList.add(mScore[2][0]);
		//		}
		//
		//
		//		if (sum1 == 3 || sum2 == 3 || sum3 == 3 || sum4 == 3 || sum5 == 3 || sum6 == 3 || sum7 == 3 || sum8 == 3) {
		//			if (sum1 == 3 || sum4 == 3 || sum7 == 3) {
		//				if (mScore[0][0].getColor() == Color.WHITE) {
		//					color = "白子";
		//				} else {
		//					color = "黑子";
		//				}
		//			} else if (sum2 == 3) {
		//				if (mScore[1][0].getColor() == Color.WHITE) {
		//					color = "白子";
		//				} else {
		//					color = "黑子";
		//				}
		//			} else if (sum3 == 3) {
		//				if (mScore[2][0].getColor() == Color.WHITE) {
		//					color = "白子";
		//				} else {
		//					color = "黑子";
		//				}
		//			} else if (sum5 == 3) {
		//				if (mScore[0][1].getColor() == Color.WHITE) {
		//					color = "白子";
		//				} else {
		//					color = "黑子";
		//				}
		//			} else if (sum6 == 3 || sum8 == 3) {
		//				if (mScore[0][2].getColor() == Color.WHITE) {
		//					color = "白子";
		//				} else {
		//					color = "黑子";
		//				}
		//			}
		//			resultMsg = "本局结束，" + color + "胜！！！";
		//			new Thread(new CircleThread()).start();
		//		} else {
		//			if (mPositionList.size() == 9) {
		//				resultMsg = "本局结束，平局！！！";
		//				onResultListener.onComplete(resultMsg);
		//			}
		//		}
		//
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (canTouch && event.getAction() == MotionEvent.ACTION_DOWN) {
			mPositionUndoList.clear();//一旦重新下棋子，不能redo
			Point point = getPoint(event);
			if (mScore[point.getY()][point.getX()].getScore() == 0) {
				Position position = getPosition(point);
				mPositionList.add(position);
				mScore[point.getY()][point.getX()] = position;
				if (mChessPieces == -1) {
					mBlackPositionList.add(position);
					calculator(mBlackPositionList, "黑子");
				} else if (mChessPieces == 1) {
					mWhitePositionList.add(position);
					calculator(mWhitePositionList, "白子");
				}
				postInvalidate();
				exchangeChess();
				event.setAction(MotionEvent.ACTION_CANCEL);
			} else {
				//有棋子
				onResultListener.onError("当前位置有棋子，换个位置！");
			}
		}


		return super.onTouchEvent(event);
	}

	/**
	 * 换棋手
	 */
	private void exchangeChess() {
		if (mChessPieces == -1) {
			mChessPieces = 1;
			mCirclePaint.setColor(Color.WHITE);
			if (onCurrentColorListener != null)
				onCurrentColorListener.onCurrentColor("白子");
		} else {
			mChessPieces = -1;
			mCirclePaint.setColor(Color.BLACK);
			if (onCurrentColorListener != null)
				onCurrentColorListener.onCurrentColor("黑子");
		}
	}

	/**
	 * 获取下棋子的位置(坐标)
	 *
	 * @param point
	 */
	private Position getPosition(Point point) {
		float x = 0, y = 0;

		for (int i = 0; i < Max; i++) {
			if (point.getX() == i) {
				x = i * mChildWidth + mChildWidth / 2;
			}
		}

		for (int i = 0; i < Max; i++) {
			if (point.getY() == i) {
				y = i * mChildHeight + mChildHeight / 2;
			}
		}
		Position position = null;
		if (mChessPieces == -1) {
			position = new Position(x, y, Color.BLACK, mChessPieces, point);
		} else {
			position = new Position(x, y, Color.WHITE, mChessPieces, point);
		}

		return position;
	}

	/**
	 * 获取下棋子的位置(数组下标)
	 *
	 * @param event
	 */
	private Point getPoint(MotionEvent event) {
		float fx = event.getX();
		float fy = event.getY();
		int x = 0, y = 0;
		for (int i = 0; i < Max; i++) {
			if (fx < mChildWidth * (i + 1)) {
				x = i;
				break;
			}
		}
		for (int i = 0; i < Max; i++) {
			if (fy < mChildHeight * (i + 1)) {
				y = i;
				break;
			}
		}

		Point point = new Point(x, y);
		return point;
	}

	public void setOnCurrentColorListener(OnCurrentColorListener onCurrentColorListener) {
		this.onCurrentColorListener = onCurrentColorListener;
		exchangeChess();
	}

	public void setOnResultListener(OnResultListener onResultListener) {
		this.onResultListener = onResultListener;
	}

	/**
	 * 重置
	 */
	public void reset() {
		init();
		invalidate();
	}

	/**
	 * 退一步
	 */
	public void undo() {
		if (mPositionList.size() == 0) {
			return;
		}
		mSuccessPointList.clear();
		m = 0;
		canTouch = true;
		Position position = mPositionList.get(mPositionList.size() - 1);
		if (position.getColor() == Color.WHITE) {
			mWhitePositionList.remove(mWhitePositionList.size() - 1);
		} else {
			mBlackPositionList.remove(mBlackPositionList.size() - 1);
		}
		//保存住去掉的步骤
		mPositionUndoList.add(position);
		mPositionList.remove(mPositionList.size() - 1);
		exchangeChess();
		mScore[position.getPoint().getY()][position.getPoint().getX()] = new Position(0, 0, 0, 0, null);
		postInvalidate();
	}

	/**
	 * 进一步
	 */
	public void redo() {
		if (mPositionUndoList.size() == 0) {
			return;
		}
		Position position = mPositionUndoList.get(mPositionUndoList.size() - 1);
		if (position.getColor() == Color.WHITE) {
			mWhitePositionList.add(position);
		} else {
			mBlackPositionList.add(position);
		}
		mPositionList.add(position);
		mScore[position.getPoint().getY()][position.getPoint().getX()] = position;
		mPositionUndoList.remove(mPositionUndoList.size() - 1);
		exchangeChess();
		postInvalidate();
		if (position.getColor() == Color.WHITE) {
			calculator(mWhitePositionList, "白子");
		} else {
			calculator(mBlackPositionList, "黑子");
		}
	}

	/**
	 * 保存棋盘
	 */
	public void save() {
		if (mPositionList.size() == 0) {
			return;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("mScore", mScore);
		map.put("mPositionList", mPositionList);
		map.put("mBlackPositionList", mBlackPositionList);
		map.put("mWhitePositionList", mWhitePositionList);

		File filePath = new File(BaseApplication.instance.getCacheDir(), "gameTTT");
		FileSaveHandler.saveObject(filePath.getPath(), map);

		reset();
	}

	/**
	 * 加载保存的棋盘
	 */
	public void load() {
		File filePath = new File(BaseApplication.instance.getCacheDir(), "gameTTT");
		Map<String, Object> map = (Map<String, Object>) FileSaveHandler.readObject(filePath.getPath());

		mScore = (Position[][]) map.get("mScore");
		mPositionList = (ArrayList<Position>) map.get("mPositionList");
		mBlackPositionList = (ArrayList<Position>) map.get("mBlackPositionList");
		mWhitePositionList = (ArrayList<Position>) map.get("mWhitePositionList");

		if (mPositionList.size() == 0) {
			return;
		}
		Position position = mPositionList.get(mPositionList.size() - 1);
		if (position.getColor() == Color.WHITE) {
			calculator(mWhitePositionList, "白子");
		} else {
			calculator(mBlackPositionList, "黑子");
		}
		postInvalidate();
	}

	/**
	 * 设置棋盘
	 *
	 * @param count
	 * @param max
	 */
	public void setChessboard(int count, int max) {
		COUNT = count;
		Max = max;
		reset();
	}
	
	private class CircleThread implements Runnable {

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(200);
					m++;
					Message msg = new Message();
					if (m > 6) {
						msg.what = 2;
						circleHandler.sendMessage(msg);
						return;
					}
					msg.what = 1;
					if (m % 2 == 0) {
						msg.obj = 45;
					} else {
						msg.obj = 40;
					}

					circleHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
}