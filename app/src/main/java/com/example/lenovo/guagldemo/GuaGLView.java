package com.example.lenovo.guagldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lenovo on 2016/6/14.
 */
public class GuaGLView extends View {
    private Paint mPaint, mTPaint;
    private Rect mbound = new Rect();
    private Bitmap mBitmap;
    private Path mPath;
    private Bitmap mHongbao;
    private Canvas mCanvas;
    private boolean mComplete = false;
    int x;
    int y ;
    int downx;
    int downy;
    int percent;
    int distanceX = x - downx;
    int distanceY = x - downy;
    int alpha=500;

    public GuaGLView(Context context) {
        super(context, null);
        // TODO Auto-generated constructor stub
    }

    public GuaGLView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    private Context mContext;

    public GuaGLView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;
        //初始化画笔
        initPaint();
        //初始化图片资源
        setUpBitmap();

    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(30);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // mPaint.setColor(0xaa0000ff);

        mTPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTPaint.setStyle(Paint.Style.FILL);
        mTPaint.setStrokeCap(Paint.Cap.ROUND);
        mTPaint.setDither(true);
        mTPaint.setColor(0xaaff0000);
        mTPaint.setTextSize(50);

        mPath = new Path();
      //  measureText();

    }

    private void setUpBitmap() {
        //先绘制dst  再设置xfermode  最后绘制src
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.aa);

        mHongbao = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mHongbao);
        mCanvas.drawColor(Color.GRAY);
       // mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mHongbao.getWidth(),
                    MeasureSpec.AT_MOST);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    mHongbao.getHeight(), MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

      //  RectF rectF=new RectF();
      //  canvas.drawBitmap(pBitmap,getWidth() / 2 - mbound.width() / 2, getHeight()
       //                / 2 + mbound.height() / 2, mTPaint);
        canvas.drawText("中奖500元",getWidth() / 2,getHeight()/2, mTPaint);
        if (mComplete){
            Log.e("draw","end");
           // while(alpha>0){
           //     alpha=alpha-1;
           //     mPaint.setAlpha(alpha);
               // canvas.drawBitmap(mHongbao,0,0,mPaint);
          //  }

        }
        else {
            drawPath();
            canvas.drawBitmap(mHongbao,0,0,null);
        }




    }

    /**
     * 设置Xfermode 模式  PorterDuff.Mode.DST_OUT 取下层绘制非交集部分
     */
    private void drawPath() {

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCanvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downx = x;
                downy = y;
                mPath.moveTo(downx, downy);
                break;

            case MotionEvent.ACTION_MOVE:
                distanceX = x - downx;
                distanceY = x - downy;
                //当触摸超过距离5的时候才刮
                if (Math.abs(distanceX) > 5 || Math.abs(distanceY) > 5) {

                    mPath.lineTo(x, y);
                    invalidate();
                }
                downx = x;
                downy = y;
                break;
            case MotionEvent.ACTION_UP:
                cheakArea();
                invalidate();
                break;
        }

        return true;
    }


    private void cheakArea() {
        new Thread(new Runnable() {
            int w = mHongbao.getWidth();
            int h = mHongbao.getHeight();
            //红包的像素总合
            int HBATotolrea = w * h;
            //存储图片像素点的值
            int[] mHongbaoArea = new int[HBATotolrea];
            //当前面积
            int mCurArea;
            private String TAG;

            @Override
            public void run() {
                //得到每个像素的值
                mHongbao.getPixels(mHongbaoArea, 0, w, 0, 0, w, h);
                for(int i: mHongbaoArea){
                    if (i == 0) {
                        mCurArea += 1;
                        percent = mCurArea * 100/ HBATotolrea;
                        if (percent > 1) {
                            mComplete = true;
                        }
                    }
                }

            }

        }).start();

    }
}