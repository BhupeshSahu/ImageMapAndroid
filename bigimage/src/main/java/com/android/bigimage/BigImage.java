/*
 * Copyright (C) 2014 Socratica LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.bigimage;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Just like image view, but with scrolling and scale abilities.
 * <p>
 * TODO write better description with usage examples.
 *
 * @author Konstantin Burov (aectann@gmail.com)
 */
public class BigImage extends android.support.v7.widget.AppCompatImageView implements OnGestureListener,
        OnTouchListener {

    private static final Map<String, SoftReference<Drawable>> DRAWABLE_CACHE = new HashMap<>();

    protected float scale;
    protected float viewWidth;
    protected float viewHeight;
    protected float dx;
    protected float dy;
    protected boolean boundsInitialized;
    protected float initScale;

    private final GestureDetector gestureDetector;
    private int imageWidth;
    private int imageHeight;
    private int bitmapResource;
    private double scaleFactor;
    private String file;
    private double prevDelta = 0;
    private String TAG = "BigImage";

    public BigImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmapResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android",
                "src", 0);
        setFocusable(true);
        setFocusableInTouchMode(true);
        gestureDetector = new GestureDetector(context, this);
        this.setOnTouchListener(this);
        if (bitmapResource != 0) {
            super.setImageDrawable(getImage());
        }
    }

    protected void updateMatrix() {
        adjustDeltas();
        Matrix m = getImageMatrix();
        m.reset();
        m.postScale(scale, scale);
        m.postTranslate(dx, dy);
        post(new Runnable() {

            @Override
            public void run() {
                invalidate();
            }
        });
    }

    public Drawable getImage() {
        String drawableKey = getDrawableKey();
        Drawable result = DRAWABLE_CACHE.containsKey(drawableKey) ? DRAWABLE_CACHE.get(drawableKey).get() : null;
        if (result == null) {
            Options options = new Options();
            options.inInputShareable = true;
            options.inPurgeable = true;
            options.inPreferredConfig = Config.RGB_565;
            options.inDither = true;
            if (bitmapResource > 0) {
                result = new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getResources().openRawResource(bitmapResource), null, options));
            } else {
                try {
                    InputStream stream = new BufferedInputStream(new FileInputStream(file), 4096);
                    result = new BitmapDrawable(getResources(), BitmapFactory.decodeStream(stream, null, options));
                    stream.close();
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
            result.setBounds(0, 0, imageWidth, imageHeight);
            DRAWABLE_CACHE.put(drawableKey, new SoftReference<>(result));
        }
        return result;
    }

    private String getDrawableKey() {
        return file == null ? String.valueOf(bitmapResource) : file;
    }

    protected synchronized void initBounds() {
        if (viewWidth > 0 && viewHeight > 0 && (bitmapResource > 0 || file != null)) {
            Options opt = loadBitmapOpts();
            if (opt != null) {
                imageWidth = opt.outWidth;
                imageHeight = opt.outHeight;
            }
            float[] f = new float[9];
            getImageMatrix().getValues(f);
            initScale = f[0];
            dx = 0;
            dy = 0;
            scale = initScale;
            scaleFactor = 1 / initScale;
            this.boundsInitialized = true;
            notify();
        } else {
            this.boundsInitialized = false;
        }
        if (!isLayoutRequested()) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initBounds();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredHeight != viewHeight || measuredWidth != viewWidth) {
            viewWidth = measuredWidth;
            viewHeight = measuredHeight;
            initBounds();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initBounds();
    }

    /**
     * To set local image file as source image
     *
     * @param file
     */
    public void setImageFile(String file) {
        setImageFile(file, null);
    }

    /**
     * To set source image dynamically. Make sure while setting remote image file that resolution is
     * same as original resolution in order to find exact coordinates while applying map definition
     *
     * @param url      fully-qualified url, in case of remote url, it will only be used as key
     * @param drawable drawable file in case of remote resource, can be null otherwise
     */
    public void setImageFile(String url, Drawable drawable) {
        this.file = url;
        this.bitmapResource = 0;
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                imageWidth = ((BitmapDrawable) drawable).getBitmap().getWidth();
                imageHeight = ((BitmapDrawable) drawable).getBitmap().getHeight();
            }
            DRAWABLE_CACHE.put(getDrawableKey(),
                    new SoftReference<>(drawable));
        }
        super.setImageDrawable(drawable);
    }


    public void setImageResource(int drawable) {
        this.file = null;
        this.bitmapResource = drawable;
        super.setImageDrawable(getImage());
    }


    /**
     *
     * @deprecated Use setImageResource(int drawable) to set image resource dynamically,
     * or set image in xml directly
     */
    @Deprecated
    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        throw new UnsupportedOperationException("This method is disabled. You can directly pass " +
                "resource id in setImageResource(int drawable)");
    }

    private Options loadBitmapOpts() {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        InputStream stream;
        if (bitmapResource > 0) {
            stream = getResources().openRawResource(bitmapResource);
        } else {
            if (file.startsWith("http")) {
                return null;
            }
            try {
                stream = new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        BitmapFactory.decodeStream(stream, null, opts);
        return opts;
    }

    private void adjustDeltas() {
        if (dx > 0) {
            dx = 0;
        }
        if (dy > 0) {
            dy = 0;
        }
        float minDx;
        if (imageWidth * scale < viewWidth) {
            minDx = (viewWidth - imageWidth * scale) / 2;
        } else {
            minDx = -imageWidth * scale + viewWidth;
        }
        if (scale == initScale && viewWidth > viewHeight) {
            dx = (viewWidth - imageWidth * scale) / 2;
        } else if (dx < minDx) {
            dx = minDx;
        }
        float minDy;
        if (imageHeight * scale < viewHeight) {
            minDy = (viewHeight - imageHeight * scale) / 2;
        } else {
            minDy = -imageHeight * scale + viewHeight;
        }
        if (scale == initScale && viewHeight > viewWidth) {
            dy = (viewHeight - imageHeight * scale) / 2;
        } else if (dy < minDy) {
            dy = minDy;
        }
    }

    /**
     * Restores the map state to the initial.
     */
    public void reset() {
        if (!boundsInitialized) {
            return;
        }
        scale = initScale;
        dx = 0;
        dy = 0;
        invalidate();
    }

    /**
     * Zooms map in, preserving currently centered point at the center of the
     * view.
     */
    public void scaleOut() {
        scale(1 / scaleFactor);
    }

    /**
     * Zooms map out to initial stage
     *
     * @return false if already in initial stage, true otherwise
     */
    public boolean resetToOverviewMode() {
        if (scale <= initScale)
            return false;
        else {
            scale(0);
            return true;
        }
    }

    /**
     * Zooms map in, preserving currently centered point at the center of the
     * view.
     */
    public boolean scaleIn() {
//        scale(scaleFactor);
        float targetScale = initScale * 3;
        if (scale < targetScale) {
            scale(scaleFactor);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Applies actual scaling
     *
     * @param scaleFactor
     * @return true if scaling is performed, false otherwise
     */
    protected void scale(double scaleFactor) {
        float prevDx = dx + (imageWidth * scale - viewWidth) / 2;
        prevDx *= scaleFactor;
        float prevDy = dy + (imageHeight * scale - viewHeight) / 2;
        prevDy *= scaleFactor;
        scale *= scaleFactor;
        if (scale < initScale) {
            scale = initScale;
        }
        dx = prevDx - (imageWidth * scale - viewWidth) / 2;
        dy = prevDy - (imageHeight * scale - viewHeight) / 2;
        updateMatrix();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        dx -= distanceX;
        dy -= distanceY;
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

//        if (scale < 0.7) {
//            dx += viewWidth / 2 - e.getX();
//            dy += viewHeight / 2 - e.getY();
//            scaleIn();
//            return true;
//        }
        return false;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getPointerCount() == 1) {
            gestureDetector.onTouchEvent(event);
        } else {
            float x = event.getX(0);
            float y = event.getY(0);
            float prevX = event.getX(1);
            float prevY = event.getY(1);

            int action = event.getAction();
            int actionCode = action & MotionEvent.ACTION_MASK;
            if (actionCode == MotionEvent.ACTION_DOWN
                    || actionCode == MotionEvent.ACTION_POINTER_DOWN) {
                prevDelta = 0;
                dx += viewWidth / 2 - (x + prevX) / 2;
                dy += viewHeight / 2 - (y + prevY) / 2;
            } else {
                double currentDelta = getDelta(x, y, prevX, prevY);
                if (prevDelta != 0) {
                    double dd = currentDelta - prevDelta;
                    float abs = (float) Math.abs(dd);
                    if (abs > 2) {
                        double scaleFactor = scale * (1 + (float) dd / 200);
                        scaleFactor = scaleFactor / scale;
                        scale(scaleFactor);
                    }
                }
                prevDelta = currentDelta;
            }
        }
        updateMatrix();
        return true;
    }

    protected double getDelta(float x, float y, float prevX, float prevY) {
        return Math.sqrt((x - prevX) * (x - prevX) + (y - prevY) * (y - prevY));
    }

    public void scaleTo(int x, int y, float scale) {
        this.scale = scale;
        dx = -x * scale + viewWidth / 2;
        dy = -y * scale + viewHeight / 2;
        updateMatrix();
    }

}