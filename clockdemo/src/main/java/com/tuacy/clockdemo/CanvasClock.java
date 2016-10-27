package com.tuacy.clockdemo;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.tuacy.clockdemo.utils.DensityUtils;

import java.util.Calendar;

public class CanvasClock extends View {

	private static final int DEFAULT_WIDTH_DP  = 400;
	private static final int DEFAULT_HEIGHT_DP = 400;

	/**
	 * 时钟的半径
	 */
	private int   mRadius;
	/**
	 *
	 */
	private float mProportion;
	private Paint mOuterCirclePaint;
	private Paint mTextPaint;
	private Rect  mTextRect;
	private Paint mTickPaint;
	private Paint mHourPaint;
	private Paint mMinutePaint;
	private Paint mSecondPaint;
	private Paint mCenterPaint;

	public CanvasClock(Context context) {
		this(context, null);
	}

	public CanvasClock(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CanvasClock(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
//		if (attrs != null) {
//			TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.CanvasClock);
//			mRadius = (int) styled.getDimension(R.styleable.CanvasClock_clock_radius, 0);
//			styled.recycle();
//		}
		mProportion = 1;

		mOuterCirclePaint = new Paint();
		mOuterCirclePaint.setAntiAlias(true);
		mOuterCirclePaint.setStrokeWidth(10 * mProportion);
		mOuterCirclePaint.setColor(Color.BLACK);
		mOuterCirclePaint.setStyle(Paint.Style.STROKE);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(DensityUtils.sp2px(getContext(), 14 * mProportion));
		mTextPaint.setTextAlign(Paint.Align.CENTER);

		mTextRect = new Rect();

		mTickPaint = new Paint();
		mTickPaint.setAntiAlias(true);
		mTickPaint.setColor(Color.RED);
		mTickPaint.setStrokeWidth(8 * mProportion);

		mHourPaint = new Paint();
		mHourPaint.setAntiAlias(true);
		mHourPaint.setStrokeCap(Paint.Cap.ROUND);
		mHourPaint.setStrokeWidth(10 * mProportion);

		mMinutePaint = new Paint();
		mMinutePaint.setAntiAlias(true);
		mMinutePaint.setStrokeWidth(7 * mProportion);

		mSecondPaint = new Paint();
		mSecondPaint.setAntiAlias(true);
		mSecondPaint.setColor(Color.RED);
		mSecondPaint.setStyle(Paint.Style.FILL);

		mCenterPaint = new Paint();
		mCenterPaint.setAntiAlias(true);
		mCenterPaint.setStrokeWidth(10 * mProportion);
		mCenterPaint.setColor(Color.WHITE);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width, height;
		width = measureDimension(DEFAULT_WIDTH_DP, widthMeasureSpec);
		height = measureDimension(DEFAULT_HEIGHT_DP, heightMeasureSpec);
		width = height = Math.min(width, height);
		mRadius = width / 2;
		mProportion = mRadius / 200.0f;
		setMeasuredDimension(width, height);
	}

	protected int measureDimension(int defaultSize, int measureSpec) {

		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			//1. layout给出了确定的值，比如：100dp
			//2. layout使用的是match_parent，但父控件的size已经可以确定了，比如设置的是具体的值或者match_parent
			result = specSize; //建议：result直接使用确定值
		} else if (specMode == MeasureSpec.AT_MOST) {
			//1. layout使用的是wrap_content
			//2. layout使用的是match_parent,但父控件使用的是确定的值或者wrap_content
			result = Math.min(defaultSize, specSize); //建议：result不能大于specSize
		} else {
			//UNSPECIFIED,没有任何限制，所以可以设置任何大小
			//多半出现在自定义的父控件的情况下，期望由自控件自行决定大小
			result = defaultSize;
		}

		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawClock(canvas);
		postInvalidateDelayed(1000);
	}

	private void drawOuterCircle(Canvas canvas) {
		canvas.drawCircle(0, 0, mRadius - 10 / 2.0f, mOuterCirclePaint);
	}

	private void drawClockText(Canvas canvas) {
		int clockHours[] = {3,
							4,
							5,
							6,
							7,
							8,
							9,
							10,
							11,
							12,
							1,
							2};
		int textRadius = (int) (mRadius - 40 * mProportion);
		for (int index = 0; index < clockHours.length; index++) {
			float pointX = (float) (textRadius * Math.cos(Math.toRadians(30 * index)));
			float pointY = (float) (textRadius * Math.sin(Math.toRadians(30 * index)));
			String text = clockHours[index] + "";
			mTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
			canvas.drawText(clockHours[index] + "", pointX, pointY + mTextRect.height() / 2, mTextPaint);
		}
	}

	private void drawTick(Canvas canvas) {
		for (int index = 0; index < 60; index++) {
			canvas.save();
			int angle = 360 / 60 * index;
			canvas.rotate(angle);
			if (index % 5 == 0) {
				mTickPaint.setColor(Color.BLACK);
			} else {
				mTickPaint.setColor(Color.GRAY);
			}
			canvas.drawPoint(0, -(mRadius - 20 * mProportion), mTickPaint);
			canvas.restore();

		}
	}

	private void drawHourPointer(Canvas canvas, int hour, int minute) {
		canvas.save();
		canvas.rotate(360 / 12 * hour + 30 / 60.0f * minute);
		canvas.drawLine(0, -(mRadius - 80 * mProportion), 0, 20 * mProportion, mHourPaint);
		canvas.restore();
	}

	private void drawMinutePointer(Canvas canvas, int minute, int second) {
		canvas.save();
		canvas.rotate(360 / 60 * minute + 6 / 60.0f * second);
		canvas.drawLine(0, -(mRadius - 70 * mProportion), 0, 40 * mProportion, mMinutePaint);
		canvas.restore();
	}

	private void drawSecondPointer(Canvas canvas, int second) {
		canvas.save();
		canvas.rotate(360 / 60 * second);
		Path path = new Path();
		path.moveTo(0, -(mRadius - 40 * mProportion));
		path.lineTo(5 * mProportion, 60 * mProportion);
		path.lineTo(-5 * mProportion, 60 * mProportion);
		path.close();
		canvas.drawPath(path, mSecondPaint);
		canvas.restore();
	}

	private void drawCenterPoint(Canvas canvas) {
		canvas.drawPoint(0, 0, mCenterPaint);
	}

	private void drawClock(Canvas canvas) {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		canvas.save();
		canvas.translate(mRadius, mRadius);
		// 画外部的圆圈
		drawOuterCircle(canvas);
		// 画时钟上面的数字
		drawClockText(canvas);
		// 画刻度
		drawTick(canvas);
		// 画时钟指针
		drawHourPointer(canvas, hour, minute);
		// 画分钟指针
		drawMinutePointer(canvas, minute, second);
		// 画秒钟指针
		drawSecondPointer(canvas, second);
		// 画中心小白点
		drawCenterPoint(canvas);
		canvas.restore();
	}
}
