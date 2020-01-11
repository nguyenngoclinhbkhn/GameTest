package com.cpr.zoomimageview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class Touch extends ImageView implements
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private Matrix matrix;
    private Matrix savedMatrix;
    private static final int INVALID_POINTER_ID = -1;
    private float fX, fY, sX, sY;
    private int ptrID1, ptrID2;
    private float mAngle;
    private boolean isRotate;
    private boolean isZoom;
    float d = 0f;
    float newRot = 0f;
    public static String fileNAME;
    public static int framePos = 0;

    private float scale = 0;
    private float newDist = 0;
    private double centerX;
    private double centerY;
    // Fields
    private String TAG = this.getClass().getSimpleName();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    float oldDist = 1f;

    // We can be in one of these 3 states

    // Remember some things for zooming
    PointF last = new PointF();

    float minScale = -10f;
    float maxScale = 3f;
    float[] m;
    private boolean isChoose;
    int viewWidth, viewHeight;
    static final int CLICK = 3;
    float saveScale = 1f;
    protected float origWidth, origHeight;
    int oldMeasuredWidth, oldMeasuredHeight;

//    ScaleGestureDetector mScaleDetector;

    float[] lastEvent = null;
    Context context;
    private float x;
    private float y;

    public Touch(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public Touch(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }


    private ImageListenerVer1 imageListenerVer1;

    GestureDetector mGestureDetector;

    public interface ImageListenerVer1 {

        void onScale(float scale, float midX, float midY);

        void onRotate(double rotation);
        void onActionUpAfter();
        void onActionDown();
    }

    public void setImageListenerVer1(ImageListenerVer1 imageListenerVer1) {
        this.imageListenerVer1 = imageListenerVer1;

    }

    public boolean isRotate() {
        return isRotate;
    }

    public void setRotate(boolean rotate) {
        isRotate = rotate;
    }

    public boolean isZoom() {
        return isZoom;
    }

    public void setZoom(boolean zoom) {
        isZoom = zoom;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        isRotate = false;
        isZoom = false;
        isChoose = false;
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setOnDoubleTapListener(this);
        matrix = new Matrix();
        savedMatrix = new Matrix();
        m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.FIT_CENTER);
        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(onTouchListener);
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            mScaleDetector.onTouchEvent(event);
//            mGestureDetector.onTouchEvent(event);
//            PointF curr = new PointF(event.getX(), event.getY());

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getRawX();
                    y = event.getRawY();
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    lastEvent = null;
                    imageListenerVer1.onActionDown();
//                    Toast.makeText(context, "touch", Toast.LENGTH_SHORT).show();
                    break;

                case MotionEvent.ACTION_UP:
                    mode = NONE;
                    imageListenerVer1.onActionUpAfter();
//                    int xDiff = (int) Math.abs(curr.x - start.x);
//                    int yDiff = (int) Math.abs(curr.y - start.y);
//                    if (xDiff < CLICK && yDiff < CLICK)
//                        performClick();
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    lastEvent = null;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN: {
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    lastEvent = new float[4];
                    lastEvent[0] = event.getX(0);
                    lastEvent[1] = event.getX(1);
                    lastEvent[2] = event.getY(0);
                    lastEvent[3] = event.getY(1);
                    d = rotation(event);
//                    imageListenerVer1.onRotate(d);
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    if (mode == DRAG) {
                        float xNew = event.getRawX();
                        float yNew = event.getRawY();
                        float orgX = xNew - x;
                        float orgY = yNew - y;
                        Touch.this.setX(Touch.this.getX() + orgX);
                        Touch.this.setY(Touch.this.getY() + orgY);
                        x = xNew;
                        y = yNew;
                        // ...
//                        matrix.set(savedMatrix);
//                        matrix.postTranslate(event.getX() - start.x, event.getY()
//                                - start.y);
                    } else if (mode == ZOOM && event.getPointerCount() == 2) {
                        if (isZoom == true && isRotate == false) {
                            float newDist = spacing(event);
                            matrix.set(savedMatrix);
                            if (newDist > 10f) {
                                float scale = newDist / oldDist;
//                            matrix.postScale(scale, scale, mid.x, mid.y);
                                imageListenerVer1.onScale(scale, mid.x, mid.y);
                            }
                        } else if (isZoom == false && isRotate == true) {
                            if (lastEvent != null) {
                                int result = 0;
                                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                                if (resourceId > 0) {
                                    result = getResources().getDimensionPixelSize(resourceId);
                                }
                                double statusBarHeight = result;
                                centerX = Touch.this.getX() +
                                        ((View) Touch.this.getParent()).getX() +
                                        (float) Touch.this.getWidth() / 2;
                                centerY = Touch.this.getY() +
                                        ((View) Touch.this.getParent()).getY() +
                                        statusBarHeight +
                                        (float) Touch.this.getHeight() / 2;
                                newRot = rotation(event);
                                float r = newRot - d;
//                            double angle = r * 180 / Math.PI;
                                double angle = (Math.atan2(event.getRawY() - centerY, event.getRawX() - centerX)) * 180 / Math.PI;
                                imageListenerVer1.onRotate(angle - 45);
                                Touch.this.setRotation((float)angle - 45);
//                            matrix.postRotate(r);
//                            matrix.postRotate(r, v.getMeasuredWidth() / 2,
//                                    v.getMeasuredHeight() / 2);
                            }
                        }

                    }
                    break;

                }
            }
//            setImageMatrix(matrix);
            invalidate();
            return true; // indaicate event was handled
        }
    };

    public void setMaxZoom(float x) {
        maxScale = x;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);

        return (float) Math.toDegrees(radians);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // Double tap is detected
        Log.i("MAIN_TAG", "Double tap detected");
        float origScale = saveScale;
        float mScaleFactor;

        if (saveScale == maxScale) {
            saveScale = minScale;
            mScaleFactor = minScale / origScale;
        } else {
            saveScale = maxScale;
            mScaleFactor = maxScale / origScale;
        }

        matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
                viewHeight / 2);

        fixTrans();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

//    private class ScaleListener extends
//            ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScaleBegin(ScaleGestureDetector detector) {
//            mode = ZOOM;
//            return true;
//        }
//
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            float mScaleFactor = detector.getScaleFactor();
//            float origScale = saveScale;
//            saveScale *= mScaleFactor;
//            if (saveScale > maxScale) {
//                saveScale = maxScale;
//                mScaleFactor = maxScale / origScale;
//            } else if (saveScale < minScale) {
//                saveScale = minScale;
//                mScaleFactor = minScale / origScale;
//            }
//
//            if (origWidth * saveScale <= viewWidth
//                    || origHeight * saveScale <= viewHeight)
//                matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
//                        viewHeight / 2);
//            else
//                matrix.postScale(mScaleFactor, mScaleFactor,
//                        detector.getFocusX(), detector.getFocusY());
//
//            fixTrans();
//            return true;
//        }
//    }

    void fixTrans() {
        matrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];

        float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);
        float fixTransY = getFixTrans(transY, viewHeight, origHeight
                * saveScale);

        if (fixTransX != 0 || fixTransY != 0)
            matrix.postTranslate(fixTransX, fixTransY);
    }

    float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;
        if (trans > maxTrans)
            return -trans + maxTrans;
        return 0;
    }

    float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            return 0;
        }
        return delta;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        //
        // Rescales image on rotation
        //
        if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
                || viewWidth == 0 || viewHeight == 0)
            return;
        oldMeasuredHeight = viewHeight;
        oldMeasuredWidth = viewWidth;

        if (saveScale == 1) {
            // Fit to screen.
            float scale;

            Drawable drawable = getDrawable();
            if (drawable == null || drawable.getIntrinsicWidth() == 0
                    || drawable.getIntrinsicHeight() == 0)
                return;
            int bmWidth = drawable.getIntrinsicWidth();
            int bmHeight = drawable.getIntrinsicHeight();

            Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

            float scaleX = (float) viewWidth / (float) bmWidth;
            float scaleY = (float) viewHeight / (float) bmHeight;
            scale = Math.min(scaleX, scaleY);
            matrix.setScale(scale, scale);

            // Center the image
            float redundantYSpace = (float) viewHeight
                    - (scale * (float) bmHeight);
            float redundantXSpace = (float) viewWidth
                    - (scale * (float) bmWidth);
            redundantYSpace /= (float) 2;
            redundantXSpace /= (float) 2;

            matrix.postTranslate(redundantXSpace, redundantYSpace);

            origWidth = viewWidth - 2 * redundantXSpace;
            origHeight = viewHeight - 2 * redundantYSpace;
            setImageMatrix(matrix);
        }
        fixTrans();
    }


    public float getAngle() {
        return mAngle;
    }


    private float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
        float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
        float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return angle;
    }


}
