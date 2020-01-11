package com.cpr.zoomimageview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


public abstract class StickerView extends FrameLayout {
    private BorderView imageViewBorder;
    private ImageView imageViewScale;
    private ImageView imageViewDelete;
    private ImageView imageViewFlip;
    private ImageView imageViewDone;
    private String TAG = "TAG";
    private float mDist = 0f;
    private boolean isEdit = false;
    private BroadcastReceiver broadcastReceiver;


    // For scalling
    private float this_orgX = -1, this_orgY = -1;
    private float scale_orgX = -1, scale_orgY = -1;
    private double scale_orgWidth = -1, scale_orgHeight = -1;
    // For rotating
    private float rotate_orgX = -1, rotate_orgY = -1, rotate_newX = -1, rotate_newY = -1;
    // For moving
    private float move_orgX = -1, move_orgY = -1;

    private double centerX, centerY;

    private final static int BUTTON_SIZE_DP = 30;
    private final static int SELF_SIZE_DP = 100;



    public StickerView(Context context) {
        super(context);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
//        this.isEdit = isEdit;
        this.imageViewBorder = new BorderView(context);
        this.imageViewScale = new ImageView(context);
        this.imageViewDelete = new ImageView(context);
        this.imageViewFlip = new ImageView(context);
        this.imageViewDone = new ImageView(context);

//        this.imageViewDone.setImageResource(R.drawable.edit);
        this.imageViewScale.setBackgroundColor(Color.RED);
//        this.imageViewDelete.setImageResource(R.drawable.ic_close_text_unclicked);
//        this.imageViewFlip.setImageResource(R.drawable.ic_reflect_text);


//        StickerView.this.setBackgroundColor(Color.GRAY);
        this.setTag("DraggableViewGroup");
        this.imageViewBorder.setTag("iv_border");
        this.imageViewScale.setTag("iv_scale");
        this.imageViewDelete.setTag("iv_delete");
        this.imageViewFlip.setTag("iv_flip");

//        StickerView.this.setBackgroundColor(Color.BLACK);

        int margin = convertDpToPixel(BUTTON_SIZE_DP, getContext()) / 2;
        int size = convertDpToPixel(SELF_SIZE_DP, getContext());

        FrameLayout.LayoutParams this_params =
                new FrameLayout.LayoutParams(
                        size,
                        size
                );
        this_params.gravity = Gravity.CENTER;

        FrameLayout.LayoutParams iv_main_params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        iv_main_params.setMargins(margin, margin, margin, margin);

        FrameLayout.LayoutParams iv_border_params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        iv_border_params.setMargins(margin, margin, margin, margin);

        FrameLayout.LayoutParams iv_scale_params =
                new FrameLayout.LayoutParams(
                        convertDpToPixel(BUTTON_SIZE_DP, getContext()),
                        convertDpToPixel(BUTTON_SIZE_DP, getContext())
                );
        iv_scale_params.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        FrameLayout.LayoutParams iv_delete_params =
                new FrameLayout.LayoutParams(
                        convertDpToPixel(BUTTON_SIZE_DP, getContext()),
                        convertDpToPixel(BUTTON_SIZE_DP, getContext())
                );
        iv_delete_params.gravity = Gravity.TOP | Gravity.RIGHT;

        FrameLayout.LayoutParams iv_flip_params =
                new FrameLayout.LayoutParams(
                        convertDpToPixel(BUTTON_SIZE_DP, getContext()),
                        convertDpToPixel(BUTTON_SIZE_DP, getContext())
                );
        iv_flip_params.gravity = Gravity.BOTTOM | Gravity.LEFT;
        FrameLayout.LayoutParams imageViewDone_params = new FrameLayout.LayoutParams(convertDpToPixel(BUTTON_SIZE_DP, getContext()),
                convertDpToPixel(BUTTON_SIZE_DP, getContext()));
        imageViewDone_params.gravity = Gravity.TOP | Gravity.LEFT;

        this.setLayoutParams(this_params);
        this.addView(getMainView(), iv_main_params);
        this.addView(imageViewBorder, iv_border_params);
        this.addView(imageViewScale, iv_scale_params);
        this.addView(imageViewDelete, iv_delete_params);
        this.addView(imageViewFlip, iv_flip_params);
        this.addView(imageViewDone, imageViewDone_params);
        this.setOnTouchListener(mTouchListener);
        this.imageViewScale.setOnTouchListener(mTouchListener);
        this.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StickerView.this.getParent() != null) {
                    ViewGroup myCanvas = ((ViewGroup) StickerView.this.getParent());
                    myCanvas.removeView(StickerView.this);
                }
            }
        });
        this.imageViewFlip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.v(TAG, "flip the view");

                View mainView = getMainView();
                mainView.setRotationY(mainView.getRotationY() == -180f ? 0f : -180f);
                mainView.invalidate();
                requestLayout();
            }
        });
        this.imageViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setEdit(true);
                Toast.makeText(getContext(), "set true ok", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public interface Sendata {
//        public void sendData(StickerView stickerView);
//    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

//    public void setSendData(Sendata sendData){
//        this.sendata = sendData;
//    }

    public boolean isFlip() {
        return getMainView().getRotationY() == -180f;
    }

    protected abstract View getMainView();



    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {


            if (view.getTag().equals("DraggableViewGroup")) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.v(TAG, "sticker view action down");
                        setControlItemsHidden(true);
//                        setEdit(true);
                        move_orgX = event.getRawX();
                        move_orgY = event.getRawY();
//                        Intent i = new Intent();
//                        i.putExtra("msg", "DATA ARRIVE");
//                        i.setAction(EditPhotoActivity.EDIT);
//                        getContext().sendBroadcast(i);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float offsetX = event.getRawX() - move_orgX;
                        float offsetY = event.getRawY() - move_orgY;
                        float coordinateX1 = StickerView.this.getX() + offsetX;
                        float coordinateY1 = StickerView.this.getY() + offsetY;
                        float coordinateX = (int) (StickerView.this.getX() + offsetX);
                        float coordinateY = (int) (StickerView.this.getY() + offsetY);

                        int coordinateXLeft = (int) (StickerView.this.getLeft() + offsetX);
                        int coordinateYTop = (int) (StickerView.this.getTop() + offsetY);

//                        StickerView.this.setLeft(coordinateXLeft);
//                        StickerView.this.setTop(coordinateYTop);
                        StickerView.this.setX(coordinateX);
                        StickerView.this.setY(coordinateY);
//                        StickerView.this.setLeft((int) (StickerView.this.getX() + offsetX));
//                        StickerView.this.setTop((int) (StickerView.this.getY() + offsetY));
                        move_orgX = event.getRawX();
                        move_orgY = event.getRawY();
                        Log.e("TAG", "x da lam tron " + coordinateX);
                        Log.e("TAG", "y da lam tron " + coordinateY);
                        Log.e("TAG", "x chua lam tron " + coordinateX1);
                        Log.e("TAG", "y chua lam tron " + coordinateY1);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:

//                        Intent intent = new Intent();
//                        intent.putExtra("msg", "DATA ARRIVE");
//                        intent.setAction(EditPhotoActivity.CHOOSE);
//                        getContext().sendBroadcast(intent);
                        Log.v(TAG, "sticker view action up");
                        break;
                }
            } else if (view.getTag().equals("iv_scale")) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.v(TAG, "iv_scale action down");

                        this_orgX = StickerView.this.getX();
                        this_orgY = StickerView.this.getY();

                        scale_orgX = event.getRawX();
                        scale_orgY = event.getRawY();
                        scale_orgWidth = StickerView.this.getLayoutParams().width;
                        scale_orgHeight = StickerView.this.getLayoutParams().height;

                        rotate_orgX = event.getRawX();
                        rotate_orgY = event.getRawY();

                        centerX = StickerView.this.getX() +
                                ((View) StickerView.this.getParent()).getX() +
                                (float) StickerView.this.getWidth() / 2;


                        //double statusBarHeight = Math.ceil(25 * getContext().getResources().getDisplayMetrics().density);
                        int result = 0;
                        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                        if (resourceId > 0) {
                            result = getResources().getDimensionPixelSize(resourceId);
                        }
                        double statusBarHeight = result;
                        centerY = StickerView.this.getY() +
                                ((View) StickerView.this.getParent()).getY() +
                                statusBarHeight +
                                (float) StickerView.this.getHeight() / 2;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.v(TAG, "iv_scale action move");

                        rotate_newX = event.getRawX();
                        rotate_newY = event.getRawY();

                        double angle_diff = Math.abs(
                                Math.atan2(event.getRawY() - scale_orgY, event.getRawX() - scale_orgX)
                                        - Math.atan2(scale_orgY - centerY, scale_orgX - centerX)) * 180 / Math.PI;

                        Log.v(TAG, "angle_diff: " + angle_diff);

                        double length1 = getLength(centerX, centerY, scale_orgX, scale_orgY);
                        double length2 = getLength(centerX, centerY, event.getRawX(), event.getRawY());

                        int size = convertDpToPixel(SELF_SIZE_DP, getContext());
                        if (length2 > length1
                                && (angle_diff < 25 || Math.abs(angle_diff - 180) < 25)
                        ) {
                            //scale up
                            double offsetX = Math.abs(event.getRawX() - scale_orgX);
                            double offsetY = Math.abs(event.getRawY() - scale_orgY);
                            double offset = Math.max(offsetX, offsetY);
                            offset = Math.round(offset);
                            StickerView.this.getLayoutParams().width += offset;
                            StickerView.this.getLayoutParams().height += offset;
                            onScaling(true);
                            //DraggableViewGroup.this.setX((float) (getX() - offset / 2));
                            //DraggableViewGroup.this.setY((float) (getY() - offset / 2));
                        } else if (length2 < length1
                                && (angle_diff < 25 || Math.abs(angle_diff - 180) < 25)
                                && StickerView.this.getLayoutParams().width > size / 2
                                && StickerView.this.getLayoutParams().height > size / 2) {
                            //scale down
                            double offsetX = Math.abs(event.getRawX() - scale_orgX);
                            double offsetY = Math.abs(event.getRawY() - scale_orgY);
                            double offset = Math.max(offsetX, offsetY);
                            offset = Math.round(offset);
                            StickerView.this.getLayoutParams().width -= offset;
                            StickerView.this.getLayoutParams().height -= offset;
                            onScaling(false);
                        }


                        double angle = Math.atan2(event.getRawY() - centerY, event.getRawX() - centerX) * 180 / Math.PI;
                        Log.e("TAG", "log angle: " + angle);

                        //setRotation((float) angle - 45);
                        setRotation((float) angle - 45);
                        Log.e("Test", " degrees2: " + getRotation() * Math.PI / 180 + "\n" +
                                "degrees 1: " + getRotation());
                        onRotating();

                        rotate_orgX = rotate_newX;
                        rotate_orgY = rotate_newY;

                        scale_orgX = event.getRawX();
                        scale_orgY = event.getRawY();

                        postInvalidate();
                        requestLayout();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.v(TAG, "iv_scale action up");
                        break;
                }
            }
            return true;
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private double getLength(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
    }


    private float[] getRelativePos(float absX, float absY) {
        Log.v("ken", "getRelativePos getX:" + ((View) this.getParent()).getX());
        Log.v("ken", "getRelativePos getY:" + ((View) this.getParent()).getY());
        float[] pos = new float[]{
                absX - ((View) this.getParent()).getX(),
                absY - ((View) this.getParent()).getY()
        };
        Log.v(TAG, "getRelativePos absY:" + absY);
        Log.v(TAG, "getRelativePos relativeY:" + pos[1]);
        return pos;
    }

    public void setControlItemsHidden(boolean isHidden) {
        if (isHidden) {
            imageViewBorder.setVisibility(View.VISIBLE);
            imageViewScale.setVisibility(View.VISIBLE);
            imageViewDelete.setVisibility(View.VISIBLE);
            imageViewFlip.setVisibility(View.VISIBLE);
            imageViewDone.setVisibility(View.VISIBLE);
        } else {
            imageViewBorder.setVisibility(View.GONE);
            imageViewScale.setVisibility(View.GONE);
            imageViewDelete.setVisibility(View.GONE);
            imageViewFlip.setVisibility(View.GONE);
            imageViewDone.setVisibility(View.GONE);
        }
    }

    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) (Math.sqrt(x * x + y * y));
    }

    protected View getImageViewFlip() {
        return imageViewFlip;
    }

    protected void onScaling(boolean scaleUp) {
    }

    protected void onRotating() {
    }

    private class BorderView extends View {

        public BorderView(Context context) {
            super(context);
        }

        public BorderView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public BorderView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // Draw sticker border

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();

            Log.v(TAG, "params.leftMargin: " + params.leftMargin);

            Rect border = new Rect();
            border.left = (int) this.getLeft() - params.leftMargin;
            border.top = (int) this.getTop() - params.topMargin;
            border.right = (int) this.getRight() - params.rightMargin;
            border.bottom = (int) this.getBottom() - params.bottomMargin;
            Paint borderPaint = new Paint();
            borderPaint.setStrokeWidth(3);
            borderPaint.setColor(Color.WHITE);
            borderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(border, borderPaint);

        }
    }

    private static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
}
