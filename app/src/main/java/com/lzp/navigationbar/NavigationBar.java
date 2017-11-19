package com.lzp.navigationbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by p_panzhliu on 2017/10/10.
 */

public class NavigationBar extends View {

    private static String COLOR_CUR_SUCC_BG = "#3AA3F9";
    private static String COLOR_WAIT_BG = "#F4F4F4";
    private static String COLOR_PASSSED_BG = "#3AA3F9";
    private static String COLOR_CUR_ERROR_BG = "#F76160";

    private static String COLOR_CUR_TXT = "#FFFFFF";
    private static String COLOR_WAIT_TXT = "#61b0ff";
    private static String COLOR_PASSED_TXT = "#80ffffff";

    private static final int DEFAULT_STEP_COUNTS = 5;
    private static final int DEFAULT_DIVIDER_WIDTH = 10;

    private Paint mTxtPaint, mBgPaint, mDividerPaint, mCurrentDividerPaint;
    private int mCount = 0;
    private Step mCureStep;
    private int mStepCount = DEFAULT_STEP_COUNTS;

    public NavigationBar(Context context) {
        this(context, null);
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26, getContext().getResources().getDisplayMetrics());
        mTxtPaint.setTextSize(size);
        mTxtPaint.setTextAlign(Paint.Align.CENTER);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(Color.parseColor(COLOR_CUR_SUCC_BG));
        mBgPaint.setStyle(Paint.Style.FILL);

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(Color.parseColor("#80ffffff"));
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setStrokeWidth(DEFAULT_DIVIDER_WIDTH);

        mCurrentDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurrentDividerPaint.setStyle(Paint.Style.FILL);
        mCurrentDividerPaint.setColor(Color.parseColor(COLOR_CUR_SUCC_BG));
    }

    public void next(Step step) {
        if (mCount >= mStepCount) {
            return;
        }
        mCureStep = step;
        mCount++;
        invalidate();
    }

    public void updateState(boolean success) {
        if (mCureStep != null) {
            mCureStep.success = success;
            invalidate();
        }
    }

    public boolean isEnd() {
        return mCount == mStepCount;
    }

    public void reset() {
        mCount = 0;
        mCureStep = null;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //如果没有开始，全部绘制等待的背景
        if (mCount == 0) {
            mBgPaint.setColor(Color.parseColor(COLOR_WAIT_BG));
            canvas.drawRect(0, 0, getWidth(), getHeight(), mBgPaint);
            return;
        }

        int left = drawPassed(canvas);
        left = drawCurrent(canvas, left);
        drawWait(canvas, left);
        drawDivider(canvas);
        drawText(canvas);
    }

    private int drawPassed(Canvas canvas) {
        mBgPaint.setColor(Color.parseColor(COLOR_PASSSED_BG));
        int passed = mCount - 1;//已通过的个数
        int left = 0, top = 0;
        int right = getWidth() / 10;
        int bottom = getHeight();
        int centerY = getHeight() / 2;

        Path path = new Path();
        for (int i = 0; i < passed; i++) {
            path.moveTo(left, top);
            path.lineTo(right, top);
            path.lineTo(right, bottom);
            path.lineTo(left, bottom);
            canvas.drawPath(path, mBgPaint);
            left = right;
            right += getWidth() / 10;
        }
        return left;
    }

    private int drawCurrent(Canvas canvas, int left) {
        if (mCureStep.success)
            mBgPaint.setColor(Color.parseColor(COLOR_CUR_SUCC_BG));
        else
            mBgPaint.setColor(Color.parseColor(COLOR_CUR_ERROR_BG));
        int width = ((10 - (mStepCount - 1)) * getWidth()) / 10;
        int right = left + width;
        int top = 0;
        int bottom = getHeight();
        canvas.drawRect(left, top, right, bottom, mBgPaint);
        return right;
    }

    private void drawWait(Canvas canvas, int left) {
        mBgPaint.setColor(Color.parseColor(COLOR_WAIT_BG));
        int right = getWidth();
        int top = 0;
        int bottom = getHeight();
        canvas.drawRect(left, top, right, bottom, mBgPaint);
    }

    private void drawDivider(Canvas canvas) {
        int passed = mCount - 1;//已通过的个数
        int left = 0, top = 0;
        int bottom = getHeight();
        int centerX = 0;
        int centerY = getHeight() / 2;

        Path path = new Path();

        //passed
        for (int i = 0; i < passed; i++) {
            left += getWidth() / 10;
            //算上分割线的宽度
            if (i == 0) {
                left -= DEFAULT_DIVIDER_WIDTH;
            }
            centerX = left + 30;
            path.reset();
            path.moveTo(left, top);
            path.lineTo(centerX, centerY);
            path.lineTo(left, bottom);
            if (i == passed - 1 && !mCureStep.success) {//最后一个,并且当前step状态是error，先不绘制divider，需要单独处理
                continue;
            }
            canvas.drawPath(path, mDividerPaint);
        }
        if (passed > 0) {//上面画分割线的时候减去了分割线的宽度，现在要恢复回来。不然的话，画current的分割线时会错位
            left += DEFAULT_DIVIDER_WIDTH;
        }
        if (passed > 0 && !mCureStep.success) {//如果当前step的状态是error，需要改变diver绘制，先绘制一个蓝色填充的三角，然后在绘制divider。
            mCurrentDividerPaint.setColor(Color.parseColor(COLOR_CUR_SUCC_BG));
            centerX = left + 30;
            path.reset();
            path.moveTo(left, top);
            path.lineTo(centerX, centerY);
            path.lineTo(left, bottom);
            canvas.drawPath(path, mCurrentDividerPaint);
            canvas.drawPath(path, mDividerPaint);
        }

        //current
        if (mCureStep.success)
            mCurrentDividerPaint.setColor(Color.parseColor(COLOR_CUR_SUCC_BG));
        else
            mCurrentDividerPaint.setColor(Color.parseColor(COLOR_CUR_ERROR_BG));

        int curWidth = ((10 - (mStepCount - 1)) * getWidth()) / 10;
        left += curWidth;
        centerX = left + 30;
        path.reset();
        path.moveTo(left, top);
        path.lineTo(centerX, centerY);
        path.lineTo(left, bottom);
        canvas.drawPath(path, mCurrentDividerPaint);

        //wait
        int waited = mStepCount - mCount;
        for (int i = 0; i < waited - 1; i++) {
            left += getWidth() / 10;
            centerX = left + 30;
            path.reset();
            path.moveTo(left, top);
            path.lineTo(centerX, centerY);
            path.lineTo(left, bottom);
            canvas.drawPath(path, mDividerPaint);
        }
    }

    private void drawText(Canvas canvas) {
        int passed = mCount - 1;//已通过的个数
        int left = 0, top = 0;
        int right = getWidth() / 10;
        int bottom = getHeight();
        int centerX = 0;
        int centerY = getHeight() / 2;

        Rect rect = new Rect();

        //passed
        mTxtPaint.setColor(Color.parseColor(COLOR_PASSED_TXT));
        for (int i = 0; i < passed; i++) {
            String text = String.valueOf(i + 1);

            mTxtPaint.getTextBounds(text, 0, text.length(), rect);
            int x = left + (right - left - rect.width()) / 2;
            int y = top + (bottom - top) / 2;
            //第一个只有右边有分割线，其他的两边都有
            if (i == 0)
                x += 15;
            else
                x += 30;
            y += rect.height() / 2;
            canvas.drawText(text, x, y, mTxtPaint);

            left = right;
            right += getWidth() / 10;
        }

        //current
        right = left + ((10 - (mStepCount - 1)) * getWidth()) / 10;
        mTxtPaint.setColor(Color.parseColor(COLOR_CUR_TXT));
        String text = mCount + " " + mCureStep.desc;
        mTxtPaint.getTextBounds(text, 0, 1, rect);//以数字的大小为标准，否则会出现不是居中的情况
        float x = left + (right - left - rect.width()) / 2;
        float y = top + (bottom - top) / 2;
        if (mCount == 1 || mCount == mStepCount)
            x += 15;
        else
            x += 30;
        y += rect.height() / 2;

        canvas.drawText(text, x, y, mTxtPaint);

        //wait
        mTxtPaint.setColor(Color.parseColor(COLOR_WAIT_TXT));
        left = right;
        right += getWidth() / 10;
        int waited = mStepCount - mCount;
        for (int i = 0; i < waited; i++) {
            text = (mCount + i + 1) + "";
            mTxtPaint.getTextBounds(text, 0, text.length(), rect);
            x = left + (right - left - rect.width()) / 2;
            y = top + (bottom - top) / 2;
            if (i == waited - 1)
                x += 15 + DEFAULT_DIVIDER_WIDTH;
            else
                x += 30 + DEFAULT_DIVIDER_WIDTH;
            y += rect.height() / 2;
            canvas.drawText(text, x, y, mTxtPaint);

            left = right;
            right += getWidth() / 10;
        }
    }

    public static class Step {
        private final String desc;
        private boolean success;

        public Step(String desc, boolean success) {
            this.desc = desc;
            this.success = success;
        }
    }
}
