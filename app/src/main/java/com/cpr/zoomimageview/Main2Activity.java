package com.cpr.zoomimageview;

import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity implements Touch.ImageListenerVer1 {
    private FrameLayout frameLayout;
    private Touch imgEgg;
    private Touch imgBasket;
    private float x;
    private float y;
    private boolean isTouch;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        frameLayout = findViewById(R.id.frame2);
        isTouch = false;
        imgEgg = new Touch(this);
        imgBasket = new Touch(this);
        imgEgg.setImageResource(R.drawable.easter);
        imgBasket.setImageResource(R.drawable.basket);
        imgBasket.setVisibility(View.GONE);
        frameLayout.post(new Runnable() {
            @Override
            public void run() {
                setupScreen();
            }
        });

        handler = new Handler();
        imgBasket.setImageListenerVer1(this);
        imgEgg.setImageListenerVer1(this);

    }

    private void setupScreen() {
        int width = frameLayout.getWidth();
        int height = frameLayout.getHeight();

        frameLayout.addView(imgEgg, width / 5, width / 5);
        frameLayout.addView(imgBasket, width / 4, width / 4);

        imgEgg.setX(width / 2);
        imgEgg.setY(0);
        imgBasket.setX(width / 2);
        imgBasket.setY(0);

        animationForView(imgEgg, width / 2, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imgBasket.setVisibility(View.VISIBLE);
            }
        }, 1000);

    }

    public void animationVer2(final View view, float y) {
        for (int i = 0; i < y; i++) {
            final int a = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isTouch == false) {
                        view.setY(a);
                    } else {
                        handler.removeCallbacks(this);
                    }
                }
            }, 200);
        }
    }

    public void animationForView(final View v, int coordinateX, int coordinateY) {
        AnimatorSet animSetXY = new AnimatorSet();
        PropertyValuesHolder propertiXOne = PropertyValuesHolder.ofInt("Test", frameLayout.getWidth() / 2, frameLayout.getWidth() / 2);
        PropertyValuesHolder propertiXTwo = PropertyValuesHolder.ofInt("Test2", coordinateY, frameLayout.getHeight());
        ValueAnimator animator = new ValueAnimator();
        animator.setValues(propertiXOne, propertiXTwo);
        animator.setDuration(2500);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (isTouch == false) {
                    v.setX((int) valueAnimator.getAnimatedValue("Test"));
                    v.setY((int) valueAnimator.getAnimatedValue("Test2"));
                } else {
                    valueAnimator.cancel();
                }
            }
        });
        animator.start();

//        ObjectAnimator y = ObjectAnimator.ofFloat(v,
//                "translationY", v.getY(), coordinateY);
//
//        ObjectAnimator x = ObjectAnimator.ofFloat(v,
//                "translationX", v.getX(), coordinateX);
//
//        animSetXY.playTogether(x, y);
//        animSetXY.setInterpolator(new LinearInterpolator());
//        animSetXY.setDuration(2000);
////        animSetXY.setDuration(600);
        animSetXY.start();

    }

    @Override
    public void onScale(float scale, float midX, float midY) {

    }

    @Override
    public void onRotate(double rotation) {

    }

    @Override
    public void onActionUpAfter() {
        isTouch = false;
        animationForView(imgEgg, (int) imgEgg.getX(), (int) imgEgg.getY());

    }

    @Override
    public void onActionDown() {
        isTouch = true;
    }
}
