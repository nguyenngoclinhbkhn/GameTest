package com.cpr.zoomimageview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements Touch.ImageListenerVer1, View.OnTouchListener {
    private Touch img;
    private FrameLayout frameLayout;
    private Button btnTest;
    private Touch image2;
    private float x;
    private float y;
    private Button btnNext;
    private ImageView imageLeft;
    private ImageView imageRight;
    private Button btnSize;
    private StickerImageView stickerImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.frame);
        btnTest = findViewById(R.id.btnTest);
        btnNext = findViewById(R.id.btnNext);

        img = new Touch(this);
        imageLeft = new Touch(this);
        imageRight = new Touch(this);
        btnSize = findViewById(R.id.btnSize);
        img.setZoom(false);
        stickerImageView = new StickerImageView(this);
        frameLayout.addView(stickerImageView);
        stickerImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test));
        img.setChoose(true);
        img.setRotate(true);
        img.setImageResource(R.drawable.test);
        imageLeft.setImageResource(R.drawable.test);
        imageRight.setImageResource(R.drawable.test);

        stickerImageView.setX(0);
        stickerImageView.setY(0);
        imageLeft.setBackgroundColor(Color.RED);
        imageRight.setBackgroundColor(Color.RED);
        img.setBackgroundColor(Color.RED);

        frameLayout.post(new Runnable() {
            @Override
            public void run() {
                setupScreen();
            }
        });

        imageLeft.setOnTouchListener(this);
        imageRight.setOnTouchListener(this);

        imageLeft.setScaleType(ImageView.ScaleType.FIT_XY);
        imageRight.setScaleType(ImageView.ScaleType.FIT_XY);

        imageLeft.setTag("Left");
        imageRight.setTag("Right");


        img.setImageListenerVer1(this);


        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Log.e("TAG", "rotation " + img.getRotation());

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
    }

    private void setupScreen() {
        int width = frameLayout.getWidth();
        int height = frameLayout.getHeight();
        int distanceHeight = height / 2;
        int distance = width / 7;
        frameLayout.addView(imageLeft, distance, distanceHeight);
        frameLayout.addView(img, distance, distanceHeight);
        frameLayout.addView(imageRight, distance, distanceHeight);

        img.setX(distance);
        img.setY(height / 3);

        imageLeft.setX(distance * 3);
        imageLeft.setY(height / 3);

        imageRight.setX(distance * 5);
        imageRight.setY(height / 3);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Log.e("TAG", " x " + event.getX() + " : y " + event.getY());
                int test1[] = new int[2];
                img.getLocationInWindow(test1);
                Log.e("TAG", "xImg = " + test1[0] + " : yImg " + test1[1]);
            }
        }
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void onScale(float scale, float midX, float midY) {

    }

    @Override
    public void onRotate(double rotation) {

//        double angle = rotation * 180 / Math.PI;
//        img.setRotation((float)rotation - 45);
//        img.setRotationX(rotation);
//        img.setRotationY(rotation);
//        img.setRotation(rotation);

//        img.setRotation(rotation);
    }

    @Override
    public void onActionUpAfter() {
        if (Math.abs(img.getRotation()) <= 95 && Math.abs(img.getRotation()) >= 85) {
            img.setRotation(-90);
        }


        int testLeft[] = new int[2];
        imageLeft.getLocationInWindow(testLeft);
        int testImg[] = new int[2];
        img.getLocationOnScreen(testImg);
        int yImage = testImg[1];
        int xImage = testImg[0];
        int xLeft = testLeft[0];
        int yLeft = testLeft[1];
        if (Math.abs(yImage - yLeft) <= 5 && (xImage <= xLeft) ){
            Toast.makeText(MainActivity.this, "Correct", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActionDown() {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getTag().toString()) {
            case "Left": {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        x = motionEvent.getRawX();
                        y = motionEvent.getRawY();
//                        superImageViewCat.setEnabled(false);

                    }
                    break;
                    case MotionEvent.ACTION_MOVE: {
                        float xNew = motionEvent.getRawX();
                        float yNew = motionEvent.getRawY();
                        float orgX = xNew - x;
                        float orgY = yNew - y;
                        imageLeft.setX(imageLeft.getX() + orgX);
                        imageLeft.setY(imageLeft.getY() + orgY);
                        x = xNew;
                        y = yNew;
                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                        animationForView(imageLeft, frameLayout.getWidth() * 3 / 7, frameLayout.getHeight() / 3);
                    }
                    break;
                }

            }
            break;
            case "Right": {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        x = motionEvent.getRawX();
                        y = motionEvent.getRawY();
//                        superImageViewCat.setEnabled(false);

                    }
                    break;
                    case MotionEvent.ACTION_MOVE: {
                        float xNew = motionEvent.getRawX();
                        float yNew = motionEvent.getRawY();
                        float orgX = xNew - x;
                        float orgY = yNew - y;
                        imageRight.setX(imageRight.getX() + orgX);
                        imageRight.setY(imageRight.getY() + orgY);
                        x = xNew;
                        y = yNew;
                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                        animationForView(imageRight, frameLayout.getWidth() * 5 / 7,
                                frameLayout.getHeight() / 3);
                    }
                    break;
                }
            }
            break;
        }
        return true;
    }

    public void animationForView(View v, float coordinateX, float coordinateY) {
        AnimatorSet animSetXY = new AnimatorSet();

        ObjectAnimator y = ObjectAnimator.ofFloat(v,
                "translationY", v.getY(), coordinateY);

        ObjectAnimator x = ObjectAnimator.ofFloat(v,
                "translationX", v.getX(), coordinateX);

        animSetXY.playTogether(x, y);
        animSetXY.setInterpolator(new LinearInterpolator());
        animSetXY.setDuration(800);
        animSetXY.setDuration(600);
        animSetXY.start();
    }
}
