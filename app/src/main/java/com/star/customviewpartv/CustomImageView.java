package com.star.customviewpartv;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

public class CustomImageView extends ImageView {

    public static final int IMAGE_SCALE_TYPE_FILLXY = 0;
    public static final int IMAGE_SCALE_TYPE_CENTER = 1;

    private String mTitleText;
    private int mTitleTextColor;
    private int mTitleTextSize;
    private Bitmap mImage;
    private int mImageScaleType;

    private Paint mPaint;
    private Rect mTextBounds;
    private Rect mImageBounds;

    private TextPaint mTextPaint;

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CustomImageView, defStyleAttr, 0);

        for (int i = 0; i < typedArray.getIndexCount(); i++) {

            int attr = typedArray.getIndex(i);

            switch (attr) {
                case R.styleable.CustomImageView_titleText:
                    mTitleText = typedArray.getString(attr);
                    break;

                case R.styleable.CustomImageView_titleTextColors:
                    mTitleTextColor = typedArray.getColor(attr, Color.BLACK);
                    break;

                case R.styleable.CustomImageView_titleTextSize:
                    mTitleTextSize = typedArray.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
                                    getResources().getDisplayMetrics()));
                    break;

                case R.styleable.CustomImageView_image:
                    mImage = BitmapFactory.decodeResource(getResources(),
                            typedArray.getResourceId(attr, 0));
                    break;

                case R.styleable.CustomImageView_imageScaleType:
                    mImageScaleType = typedArray.getInt(attr, 0);
                    break;

            }
        }

        typedArray.recycle();

        mPaint = new Paint();
        mTextBounds = new Rect();
        mImageBounds = new Rect();

        mPaint.setTextSize(mTitleTextSize);
        mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mTextBounds);

        mTextPaint = new TextPaint(mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            int widthByImage = getPaddingLeft() + mImage.getWidth() + getPaddingRight();
            int widthByText = getPaddingLeft() + mTextBounds.width() + getPaddingRight();

            width = Math.min(Math.max(widthByImage, widthByText), widthSize);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            int heightByImageAndText = getPaddingTop() + mImage.getHeight() +
                    mTextBounds.height() + getPaddingBottom();

            height = Math.min(heightByImageAndText, heightSize);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(Color.CYAN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);

        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        mPaint.setColor(mTitleTextColor);
        mPaint.setStyle(Paint.Style.FILL);

        int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int contentHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        if (mTextBounds.width() > contentWidth) {

            String msg = TextUtils.ellipsize(mTitleText, mTextPaint, contentWidth,
                    TextUtils.TruncateAt.END).toString();

            canvas.drawText(msg, getPaddingLeft(), contentHeight + getPaddingTop(), mPaint);
        } else {
            canvas.drawText(mTitleText,
                    getPaddingLeft() + (contentWidth - mTextBounds.width()) / 2,
                    contentHeight + getPaddingTop(), mPaint);
        }

        if (mImageScaleType == IMAGE_SCALE_TYPE_FILLXY) {
            mImageBounds.left = getPaddingLeft();
            mImageBounds.top = getPaddingTop();
            mImageBounds.right = getPaddingLeft() + contentWidth;
            mImageBounds.bottom = getPaddingTop() + contentHeight - mTextBounds.height();
        } else if (mImageScaleType == IMAGE_SCALE_TYPE_CENTER) {
            mImageBounds.left = getPaddingLeft() +
                    Math.max(0, (contentWidth - mImage.getWidth()) / 2);
            mImageBounds.top = getPaddingTop() +
                    Math.max(0, ((contentHeight - mTextBounds.height()) - mImage.getHeight()) / 2);
            mImageBounds.right = Math.min(mImageBounds.left + mImage.getWidth(),
                    getPaddingLeft() + contentWidth);
            mImageBounds.bottom = Math.min(mImageBounds.top + mImage.getWidth(),
                    getPaddingTop() + contentHeight - mTextBounds.height());
        }

        canvas.drawBitmap(mImage, null, mImageBounds, mPaint);
    }
}
