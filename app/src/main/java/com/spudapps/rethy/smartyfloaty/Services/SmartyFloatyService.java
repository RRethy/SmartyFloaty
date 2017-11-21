package com.spudapps.rethy.smartyfloaty.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.spudapps.rethy.smartyfloaty.R;
import com.spudapps.rethy.smartyfloaty.Utility;

public class SmartyFloatyService extends Service implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {
    private static final String LOG_TAG = SmartyFloatyService.class.getSimpleName();

    private boolean isDisabled = false;

    private WindowManager windowManager;
    private FrameLayout overlay;
    private WindowManager.LayoutParams params;
    private GestureDetectorCompat detector;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        final LayoutInflater inflater = LayoutInflater.from(this);
        this.overlay = (FrameLayout) inflater.inflate(R.layout.view_overlay, null, false);
        this.overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);

                return false;
            }
        });

        setupWindowParams();
        showOverlay();
        setupBroadcastReceiver();
        this.detector = new GestureDetectorCompat(this,this);
        this.detector.setOnDoubleTapListener(this);

        return START_STICKY;
    }

    private void setupWindowParams() {
        params = new WindowManager.LayoutParams(
                200,
                200,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.x = 0;
        params.y = 50;
    }

    private void hideOverlay() {
        if (this.overlay.getWindowToken() != null) {
            windowManager.removeViewImmediate(this.overlay);
        }
    }

    private void showOverlay() {
        if (this.overlay.getWindowToken() == null) {
            this.windowManager.addView(this.overlay, this.params);
        }
    }

    private void setupBroadcastReceiver() {
        if (Utility.isAccessibilityEnabled(this, SmartyFloatyAccessibilityService.ACCESSIBILITY_ID)) {
            broadcastReceiver = new SmartyFloatyBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter(SmartyFloatyAccessibilityService.ACTION_DISABLE_FLOATING_VIDEO);
            intentFilter.addAction(SmartyFloatyAccessibilityService.ACTION_ENABLE_FLOATING_VIDEO);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlay != null) {
            if(overlay.getWindowToken() != null) {
                windowManager.removeViewImmediate(overlay);
            }
        }
        windowManager = null;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    private class SmartyFloatyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SmartyFloatyAccessibilityService.ACTION_DISABLE_FLOATING_VIDEO)) {
                hideOverlay();
            } else if (intent.getAction().equals(SmartyFloatyAccessibilityService.ACTION_ENABLE_FLOATING_VIDEO)
                    && !isDisabled) {
                showOverlay();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        hideOverlay();
        this.isDisabled = true;
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
}
