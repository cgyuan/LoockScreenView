package com.cgy.lockscreen;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LockScreenView extends View {

	private int bitmapWidth, lineWidth;
	/**
	 * 九宫格
	 */
	private Point[] mPoints = new Point[9];
	private Paint mPaint = new Paint();
	/**
	 * 已选择了的格子
	 */
	private List<Integer> mSelectedList = new ArrayList<Integer>();
	/**
	 * 解锁手势是否正确
	 */
	private boolean isWrong = false;
	private MotionEvent mMoveEvent;
	/**
	 * 解锁钥匙
	 */
	private List<Integer> mLockedList = new ArrayList<Integer>();
	/**
	 * 解锁结果监听器
	 */
	private OnUnLockScreenListener onUnLockScreenListener;
	
	/**
	 * 解锁handler
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(mSelectedList.size() > 0){
				/**
				 * 手势操作完成,第一次操作为设置密码,其它时候为解锁操作
				 */
				onUnLockScreenListener.onFinish(mSelectedList);
			}
			switch (msg.what) {
			case 0:
				onUnLockScreenListener.onFail();
				break;
			case 1:
				onUnLockScreenListener.onSuccess();
				break;
			default:
				break;
			}
			mSelectedList = new ArrayList<Integer>();
			isWrong = false;
			invalidate();
		}
	};
	
	/**
	 * 设置解锁监听器
	 * @param listener
	 */
	public void setOnUnLockScreenListener(OnUnLockScreenListener listener){
		this.onUnLockScreenListener = listener;
	}
	
	
	public LockScreenView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public LockScreenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LockScreenView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 设置锁屏密码
	 * 
	 * @Title: setLockedList
	 * @Description: TODO
	 * @param list
	 *            1~9不重复的数字集合
	 * @return void
	 */
	public void setLockedList(List<Integer> list) {
		mLockedList = list;
	}

	/**
	 * 得到锁屏密码
	 * 
	 * @Title: getLockedList
	 * @Description: TODO
	 * @return
	 * @return List<Integer> 1~9不重复的数字集合
	 */
	public List<Integer> getLockedList() {
		return mLockedList;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(2);
		for (int i = 0; i < mPoints.length; i++) {
			if(mSelectedList.contains(i)){
				if(isWrong) {
					mPaint.setColor(Color.RED);
				}else{
					mPaint.setColor(Color.BLUE);
				}
				/**
				 * 画外圆线条
				 */
				mPaint.setAlpha(255);
				mPaint.setStyle(Paint.Style.STROKE);
				canvas.drawCircle(mPoints[i].x, mPoints[i].y, bitmapWidth, mPaint);
			}else{
				/**
				 * 画内圆与外圆之间,灰色填充
				 */
				mPaint.setColor(Color.LTGRAY);
				mPaint.setAlpha(255);
				mPaint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(mPoints[i].x, mPoints[i].y, bitmapWidth, mPaint);
				mPaint.setAlpha(255);
				mPaint.setColor(Color.GRAY);
			}
			// 画中心小圆
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawCircle(mPoints[i].x, mPoints[i].y, lineWidth, mPaint);
		}
		
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeWidth(lineWidth * 2);
		/**
		 * 选择画笔颜色,解锁正确,则为蓝色
		 * 否则红色
		 */
		if(isWrong){
			mPaint.setColor(Color.RED);
		}else{
			mPaint.setColor(Color.BLUE);
		}
		mPaint.setAlpha(100);
		/**
		 * 画格子与格子之间的直线
		 */
		for (int i = 0; i < mSelectedList.size(); i++) {
			if(i > 0){
				canvas.drawLine(
						mPoints[mSelectedList.get(i - 1)].x, 
						mPoints[mSelectedList.get(i - 1)].y, 
						mPoints[mSelectedList.get(i)].x, 
						mPoints[mSelectedList.get(i)].y, mPaint
				);
			}
		}
		
		if(mSelectedList.size() > 0 && mMoveEvent != null){
			// 直线末端的小圆,即手指当前位置的圈圈
			canvas.drawCircle(mMoveEvent.getX(), mMoveEvent.getY(), lineWidth, mPaint);
			/**
			 * 最后一个格子到当前位置的延伸直线
			 */
			canvas.drawLine(mPoints[mSelectedList.get(mSelectedList.size() - 1)].x, 
							mPoints[mSelectedList.get(mSelectedList.size() - 1)].y, 
							mMoveEvent.getX(), mMoveEvent.getY(), mPaint
			);
		}
	}
	
	/**
	 * 确定每个格子所在坐标
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getWidth();
		int height = getHeight();
		if(width > height){
			bitmapWidth = height / 9;
		}else{
			bitmapWidth = width / 9;
		}
		
		lineWidth = bitmapWidth / 3;
		for (int i = 0; i < mPoints.length; i++) {
			if (i < 3) {
				mPoints[i] = new Point((1 + 2 * i) * width / 6, height / 6);
			} else if (i < 6 && i >= 3) {
				mPoints[i] = new Point((1 + 2 * (i - 3)) * width / 6, 3 * height / 6);
			} else {
				mPoints[i] = new Point((1 + 2 * (i - 6)) * width / 6, 5 * height / 6);
			}
		}
	}
	
	private int type;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isWrong){
			return super.onTouchEvent(event);
		}
		mMoveEvent = MotionEvent.obtain(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isTouchPoint(event);
			break;
		case MotionEvent.ACTION_UP:
			mMoveEvent = null;
			type = -1;
			/**
			 * 密码未设置
			 * 密码为空
			 * 未选择解锁参数
			 */
			if(mLockedList == null || mLockedList.size() ==0 || mSelectedList.size() == 0){
				isWrong = false;
				type = 2;
			}else {
				boolean isSuccess = true;
				if(mLockedList.size() == mSelectedList.size()){
					for (int i = 0; i < mLockedList.size(); i++) {
						if(mLockedList.get(i) != mSelectedList.get(i)){
							isSuccess = false;
							break;
						}
					}
				}else{
					isSuccess = false;
				}
				if(!isSuccess){
					isWrong = true;
					type = 0;
				}else{
					isWrong = false;
					type = 1;
				}
			}
			// 延时发送
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(500);
					} catch (Exception e) {
						e.printStackTrace();
					}
					mHandler.sendEmptyMessage(type);
				}
			}).start();
			break;
		case MotionEvent.ACTION_MOVE:
			isTouchPoint(event);
			break;
		default:
			return super.onTouchEvent(event);
		}
		invalidate();
		return true;
	}
	
	private void isTouchPoint(MotionEvent event){
		for (int i = 0; i < mPoints.length; i++) {
			Point point = mPoints[i];
			if(event.getX() >= point.x - bitmapWidth
					&& event.getX() <= point.x + bitmapWidth
					&& event.getY() >= point.y - bitmapWidth
					&& event.getY() <= point.y + bitmapWidth){
				if (!mSelectedList.contains(i)) {
					
					// 下面这个if里面的操作是为了防止同一直线上跳格画线
					if (mSelectedList.size() > 0) {
						int j = mSelectedList.get(mSelectedList.size() - 1);
						switch (i) {
						case 0:
							if (j == 2) {
								if (!mSelectedList.contains(1)) {
									mSelectedList.add(1);
								}
							} else if (j == 6) {
								if (!mSelectedList.contains(3)) {
									mSelectedList.add(3);
								}
							} else if (j == 8) {
								if (!mSelectedList.contains(4)) {
									mSelectedList.add(4);
								}
							}
							break;
						case 1:
							if (j == 7) {
								if (!mSelectedList.contains(4)) {
									mSelectedList.add(4);
								}
							}
							break;
						case 2:
							if (j == 0) {
								if (!mSelectedList.contains(1)) {
									mSelectedList.add(1);
								}
							} else if (j == 6) {
								if (!mSelectedList.contains(4)) {
									mSelectedList.add(4);
								}
							} else if (j == 8) {
								if (!mSelectedList.contains(5)) {
									mSelectedList.add(5);
								}
							}
							break;
						case 3:
							if (j == 5) {
								if (!mSelectedList.contains(4)) {
									mSelectedList.add(4);
								}
							}
							break;
						case 5:
							if (j == 3) {
								if (!mSelectedList.contains(4)) {
									mSelectedList.add(4);
								}
							}
							break;
						case 6:
							if (j == 0) {
								if (!mSelectedList.contains(3)) {
									mSelectedList.add(3);
								}
							} else if (j == 2) {
								if (!mSelectedList.contains(4)) {
									mSelectedList.add(4);
								}
							} else if (j == 8) {
								if (!mSelectedList.contains(7)) {
									mSelectedList.add(7);
								}
							}
							break;
						case 7:
							if (j == 1) {
								if (!mSelectedList.contains(4)) {
									mSelectedList.add(4);
								}
							}
							break;
						case 8:
							if (j == 0) {
								if (!mSelectedList.contains(4)) {
									mSelectedList.add(4);
								}
							} else if (j == 2) {
								if (!mSelectedList.contains(5)) {
									mSelectedList.add(5);
								}
							} else if (j == 6) {
								if (!mSelectedList.contains(7)) {
									mSelectedList.add(7);
								}
							}
							break;
						}
					}
					mSelectedList.add(i);
				}
				return;
			}
		}
	}
	
	public interface OnUnLockScreenListener {
		/**
		 * 解锁失败
		 * 
		 * @Title: onFail
		 * @Description: TODO
		 * @return void
		 */
		public void onFail();

		/**
		 * 解锁成功
		 * 
		 * @Title: onSuccess
		 * @Description: TODO
		 * @return void
		 */
		public void onSuccess();

		/**
		 * @Title: onFinish
		 * @Description: TODO
		 * @param list
		 *            触摸结束后，被选中点的下标
		 * @return void
		 */
		public void onFinish(List<Integer> list);
	}

}
