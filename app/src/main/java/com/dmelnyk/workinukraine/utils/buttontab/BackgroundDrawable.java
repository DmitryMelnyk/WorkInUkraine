package com.dmelnyk.workinukraine.utils.buttontab;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by d264 on 7/27/17.
 */

public class BackgroundDrawable extends Drawable {
    private Paint iPaint;

    public BackgroundDrawable(int color) {
        iPaint = new Paint();
        iPaint.setColor(color);
        iPaint.setAntiAlias(true); // for smoothing edges
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect parent = getBounds();
        float width = parent.width();
        float height = parent.height();
        float radius = height / 2;

        canvas.drawCircle(radius, radius, radius, iPaint);
        int rectCount = (int) ((width - height) / height);
        float rectWidth = radius * 2;
        for(int i = 0; i < rectCount; i++) {
            float positionLeftCorner = radius + rectWidth * i;
            float positionRightCorner = radius + rectWidth * i + rectWidth;
            canvas.drawRect(positionLeftCorner, 0, positionRightCorner, height, iPaint);
        }
        canvas.drawCircle(width - radius, radius, radius, iPaint);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) { /* NOP */ }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) { /* NOP */ }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void getOutline(@NonNull Outline outline) {
        super.getOutline(outline);
    }
}