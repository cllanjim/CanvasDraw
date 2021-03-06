package com.anvob.canvasdraw.filters.action;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.anvob.canvasdraw.common.ActionFilter;

/**
 * Created by anvob on 18.02.2017.
 */

public class PullOutFilter extends ActionFilter {

    public static int LEFT_AND_RIGHT = 0;
    public static int TOP_AND_DOWN = 1;
    public static int TOP_LEFT_AND_BOTTOM_RIGHT = 3;
    public static int TOP_RIGHT_AND_BOTTOM_LEFT = 2;

    private PorterDuffXfermode mode;

    public PullOutFilter(int framesCount, int variant) {
        super(framesCount, variant);
        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    @Override
    public void paintFrame(Canvas canvas, int curFrame) {

        if (curFrame < framesCount) {
            int back_layer = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), paint, Canvas.ALL_SAVE_FLAG);
            if (getVariant() == LEFT_AND_RIGHT) {
                float stepWidth = (float) bitmap.getWidth() / framesCount / 2 * curFrame;
                canvas.drawRect(bitmap.getWidth() / 2 - stepWidth, 0, bitmap.getWidth() / 2 + stepWidth, bitmap.getHeight(), paint);
            } else if (getVariant() == TOP_AND_DOWN) {
                float stepHeight = (float) bitmap.getHeight() / framesCount / 2 * curFrame;
                canvas.drawRect(0, bitmap.getHeight() / 2 - stepHeight, bitmap.getWidth(), bitmap.getHeight() / 2 + stepHeight, paint);
            } else if (getVariant() == TOP_RIGHT_AND_BOTTOM_LEFT) {
                float diag = (float) Math.sqrt(Math.pow(bitmap.getWidth(), 2) + Math.pow(bitmap.getHeight(), 2));
                float stepDiag = diag / framesCount / 2 * curFrame;
                int start = canvas.save();
                canvas.rotate(-45, canvas.getWidth() / 2, canvas.getHeight() / 2);
                canvas.drawRect(
                        bitmap.getWidth() / 2 - stepDiag,
                        0 - (diag - bitmap.getHeight()) / 2,
                        bitmap.getWidth() / 2 + stepDiag,
                        bitmap.getHeight() + (diag - bitmap.getHeight()) / 2, paint);
                canvas.restoreToCount(start);
            } else if (getVariant() == TOP_LEFT_AND_BOTTOM_RIGHT) {
                float diag = (float) Math.sqrt(Math.pow(bitmap.getWidth(), 2) + Math.pow(bitmap.getHeight(), 2));
                float stepDiag = diag / framesCount / 2 * curFrame;
                int start = canvas.save();
                canvas.rotate(45, canvas.getWidth() / 2, canvas.getHeight() / 2);
                canvas.drawRect(
                        bitmap.getWidth() / 2 - stepDiag,
                        0 - (diag - bitmap.getHeight()) / 2,
                        bitmap.getWidth() / 2 + stepDiag,
                        bitmap.getHeight() + (diag - bitmap.getHeight()) / 2, paint);
                canvas.restoreToCount(start);
            }
            paint.setXfermode(mode);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            canvas.restoreToCount(back_layer);
            paint.setXfermode(null);
        } else {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }
}

