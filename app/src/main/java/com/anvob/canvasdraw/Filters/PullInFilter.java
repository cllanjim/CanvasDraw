package com.anvob.canvasdraw.Filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;

import com.anvob.canvasdraw.ActionFilter;

/**
 * Created by anvob on 17.02.2017.
 */

public class PullInFilter extends ActionFilter {
    public static int LEFT_AND_RIGHT = 0;
    public static int TOP_AND_DOWN = 1;
    public static int TOP_LEFT_AND_BOTTOM_RIGHT = 3;
    public static int TOP_RIGHT_AND_BOTTOM_LEFT = 2;

    private Bitmap bitmap; // битмап который отрисовается фильтром.
    private int framesCount; // количество кадров, которое создает данный фильтр, от начала до конца.
    private int mVariant;
    private Paint paint;
    private Paint paintMode;
    private ActionFilter mNextFilter;

    public PullInFilter(int framesCount, int variant){
        this.framesCount=framesCount;
        this.mVariant=variant;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintMode = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintMode.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));//SRC_OUT
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void setPaint(Paint paint) {

    }

    public void setVariant(int variant){
        mVariant=variant;
    }

    @Override
    public void paintFrame(Canvas canvas, int curFrame) {
        if(curFrame<=framesCount&&curFrame>0) {
            if(curFrame<framesCount) {
                //save current layer
                canvas.saveLayer(0,0,canvas.getWidth(),canvas.getHeight(),paint,Canvas.ALL_SAVE_FLAG);
                //draw transition by variants
                if(mVariant==0) {
                    int stepWidth = bitmap.getWidth() / framesCount/2 * curFrame;
                    canvas.drawRect(stepWidth,0,bitmap.getWidth()-stepWidth,bitmap.getHeight(),paint);
                } else if(mVariant==1){
                    int stepHeight = bitmap.getHeight() / framesCount/2 * curFrame;
                    canvas.drawRect(0,stepHeight,bitmap.getWidth(),bitmap.getHeight()-stepHeight,paint);
                } else if(mVariant==2){
                    int diag = (int)Math.sqrt(Math.pow(bitmap.getWidth(),2)+Math.pow(bitmap.getHeight(),2));
                    int stepDiag = diag/framesCount/2*curFrame;
                    canvas.rotate(-45,canvas.getWidth()/2,canvas.getHeight()/2);
                    canvas.drawRect(
                            stepDiag-(diag-bitmap.getHeight())/2,
                            0-(diag-bitmap.getHeight())/2,
                            bitmap.getWidth()+ (diag-bitmap.getHeight())/2-stepDiag,
                            bitmap.getHeight()+(diag-bitmap.getHeight())/2,paint);
                    canvas.rotate(45,canvas.getWidth()/2,canvas.getHeight()/2);
                } else if(mVariant==3){
                    int diag = (int)Math.sqrt(Math.pow(bitmap.getWidth(),2)+Math.pow(bitmap.getHeight(),2));
                    int stepDiag = diag/framesCount/2*curFrame;
                    canvas.rotate(45,canvas.getWidth()/2,canvas.getHeight()/2);
                    canvas.drawRect(
                            stepDiag-(diag-bitmap.getHeight())/2,
                            0-(diag-bitmap.getHeight())/2,
                            bitmap.getWidth()+ (diag-bitmap.getHeight())/2-stepDiag,
                            bitmap.getHeight()+(diag-bitmap.getHeight())/2,paint);
                    canvas.rotate(-45,canvas.getWidth()/2,canvas.getHeight()/2);
                }
                //do next filter if exist
                if(mNextFilter!=null) {
                    mNextFilter.setBitmap(bitmap);
                    mNextFilter.setPaint(paintMode);
                    mNextFilter.paintFrame(canvas, curFrame + mNextFilter.getFramesCount()/2);
                } else {
                    canvas.drawBitmap(bitmap,0,0,paintMode);
                }
                //update current layer
                canvas.restore();
            }
            else{
                canvas.drawBitmap(bitmap,0,0,paint);
            }
        }
    }

    public static Bitmap getTriangleMaskedBitmapUsingPorterDuff(Bitmap source, Point p1, Point p2, Point p3) {
        if (source == null) {
            return null;
        }

        int minX = Math.min(p1.x, Math.min(p2.x,p3.x));
        int minY = Math.min(p1.y, Math.min(p2.y,p3.y));
        int maxX = Math.max(p1.x, Math.max(p2.x,p3.x));
        int maxY = Math.max(p1.y, Math.max(p2.y,p3.y));

        Bitmap targetBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        final int color = 0xff424242;

        final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        final Path path = new Path();
        //draw triangle
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();
        canvas.drawPath(path,paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);
        Bitmap cropedBitmap = Bitmap.createBitmap(targetBitmap, minX, minY, maxX-minX, maxY-minY);
        targetBitmap.recycle();
        return cropedBitmap;
    }

    @Override
    public void setNextFilter(ActionFilter filter) {
        this.mNextFilter = filter;
    }

    @Override
    public ActionFilter getNextFilter() {
        return mNextFilter;
    }

    @Override
    public int getFramesCount() {
        return framesCount;
    }

    @Override
    public void setFramesCount(int count) {
        this.framesCount = count;
    }
}
