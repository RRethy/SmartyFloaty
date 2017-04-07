package com.spudapps.rethy.smartyfloaty.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.WindowManager;

public class SmartyFloatyService extends Service{
    private static final String LOG_TAG = SmartyFloatyService.class.getSimpleName();

    private WindowManager windowManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        final LayoutInflater inflater = LayoutInflater.from(this);

        return START_STICKY;
    }

    /*@Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            super.onStartCommand(intent, flags, startId);
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            setupUI();
            setUpButtons();
            setupVideoPlayer();
            setupParams();
            setupTouchManager();
            setupBroadcastReceiver();

            return START_STICKY;
        }

        private void setupUI() {
            final LayoutInflater inflater = LayoutInflater.from(this);
            videoLayout = (FrameLayout) inflater.inflate(R.layout.floating_video, null, false);
            closeButton = (ImageView) videoLayout.findViewById(R.id.close_button);
            voteStateText = (TextView) videoLayout.findViewById(R.id.vote_state_text);
            returnToRaveButton = (ImageView) videoLayout.findViewById(R.id.return_to_rave_button);
            raveLogo = (ImageView) videoLayout.findViewById(R.id.floating_rave_logo);
            pauseIcon = (ImageView) videoLayout.findViewById(R.id.floating_paused_ic);
            spinner = (ProgressBar) videoLayout.findViewById(R.id.floating_video_spinner);
            container = (FrameLayout) videoLayout.findViewById(R.id.floating_video_container);
            subtitlesContainer = (FrameLayout) videoLayout.findViewById(R.id.video_subtitles_container);
            loadingScreen = videoLayout.findViewById(R.id.loading_screen);
            buttonsBar = (RelativeLayout) videoLayout.findViewById(R.id.floating_video_bar);
        }

        private void setUpButtons(){
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideVideoPlayer();
                }
            });
            returnToRaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideVideoPlayer();
                    Intent returnToAppIntent = new Intent(getApplicationContext(), MeshActivity.class);
                    returnToAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(returnToAppIntent);
                }
            });
        }

        private void setupVideoPlayer() {
            floatingVideoPlayer = (SurfaceView) videoLayout.findViewById(R.id.video_player);
            floatingVideoPlayer.getHolder().addCallback(this);
            floatingVideoPlayer.getHolder().setFormat(PixelFormat.TRANSPARENT);
        }

        private void setupParams() {
            params = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            params.x = 0;
            params.y = 50;
        }

        private void setupTouchManager() {
            floatingVideoTouchManager = new FloatingVideoTouchManager(
                    windowManager,
                    params,
                    this,
                    videoLayout,
                    floatingVideoPlayer,
                    buttonsBar,
                    container,
                    raveLogo,
                    subtitlesContainer,
                    loadingScreen
            );
            videoLayout.setOnTouchListener(floatingVideoTouchManager);
        }

        private void setupBroadcastReceiver() {
            if (Utility.isAccessibilityEnabled(this, FloatingAccessibilityService.ACCESSIBILITY_ID)) {
                RaveLogging.i(LOG_TAG, "Accessibility is enabled. Broadcast Receiver is being setup.");
                broadcastReceiver = new FloatingBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter(FloatingAccessibilityService.ACTION_DISABLE_FLOATING_VIDEO);
                intentFilter.addAction(FloatingAccessibilityService.ACTION_ENABLE_FLOATING_VIDEO);
                registerReceiver(broadcastReceiver, intentFilter);
            }
        }

        public void hideVideoPlayer(){
            if (isShowing()) {
                EventBus.getDefault().unregister(this);

                previousHeight = videoLayout.getLayoutParams().height;
                previousWidth = videoLayout.getLayoutParams().width;
                windowManager.removeViewImmediate(videoLayout);
                setShowing(false);
                videoPlayer.detachForegroundListeners();
            }
        }

        public void showVideoPlayer(){
            if(shouldShow && !isShowing() && !isAccessibilityDisablingFloating()){
                EventBus.getDefault().register(this);

                loadingScreen.setVisibility(View.VISIBLE);

                windowManager.addView(videoLayout, params);
                setShowing(true);
                videoPlayer = meshActivity.getVideoPlayer();
                videoPlayer.setSubtitlesContainer(subtitlesContainer);
                videoPlayer.forceSetSurface((floatingVideoPlayer).getHolder().getSurface());
                videoPlayer.attachForegroundListeners();
                if (previousHeight != 0) {
                    videoLayout.getLayoutParams().height = previousHeight;
                    videoLayout.getLayoutParams().width = previousWidth;
                }
                if (MeshStateEngine.getInstance().getCurrentMeshState() != null) {
                    if (MeshStateEngine.getInstance().getCurrentMeshState().status == MeshStateEngine.MeshState.Status.VOTE) {
                        showVoteState();
                    } else if (MeshStateEngine.getInstance().getCurrentMeshState().status == MeshStateEngine.MeshState.Status.PAUS) {
                        showPauseState();
                    } else if (MeshStateEngine.getInstance().getCurrentMeshState().status == MeshStateEngine.MeshState.Status.PLAY) {
                        showPlayState();
                    } else {
                        showLoadingState();
                    }
                } else {
                    showLoadingState();
                }
                hideLoadingScreen();

            }
        }

        @DebugTrace
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(final MeshStateEngine.MeshStateEntered event) {
            RaveLogging.i(LOG_TAG, "Mesh state event received: " + event.state.status.toString());
            switch (event.state.status) {
                case WAIT:
                    showLoadingState();
                    break;
                case PAUS:
                    showPauseState();
                    break;
                case PLAY:
                    showPlayState();
                    break;
                case VOTE:
                    showVoteState();
                    break;
                default:
                    break;

            }
        }

        private void showLoadingState() {
            voteStateText.setVisibility(View.GONE);
            pauseIcon.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            floatingVideoPlayer.setVisibility(View.INVISIBLE);
        }

        private void showPauseState() {
            voteStateText.setVisibility(View.GONE);
            pauseIcon.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
            floatingVideoPlayer.setVisibility(View.INVISIBLE);
        }

        public void showVoteState() {
            voteStateText.setVisibility(View.VISIBLE);
            pauseIcon.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            floatingVideoPlayer.setVisibility(View.INVISIBLE);
            loadingScreen.setVisibility(View.VISIBLE);
        }

        public void showPlayState() {
            voteStateText.setVisibility(View.GONE);
            pauseIcon.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            hideLoadingScreen();
        }

        public void setShouldShow(boolean shouldShow){
            this.shouldShow = shouldShow;
        }

        public boolean isAccessibilityDisablingFloating() {
            return accessibilityDisablingFloating;
        }

        public void setAccessibilityDisablingFloating(boolean accessibilityDisablingFloating) {
            this.accessibilityDisablingFloating = accessibilityDisablingFloating;
        }

        public boolean isShowing() {
            return isShowing;
        }

        public void setShowing(boolean showing) {
            isShowing = showing;
        }

        private void hideLoadingScreen(){
            loadingScreen.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingScreen.setDrawingCacheEnabled(true);
                    loadingScreen.buildDrawingCache();
                    final Bitmap bitmap = loadingScreen.getDrawingCache();
                    if(videoPlayer.getPlayerState() == ExoPlayer.STATE_READY
                            && videoPlayer.getPlayer().getPlayWhenReady()
                            && bitmap.getPixel(
                            ((int)floatingVideoPlayer.getX() + floatingVideoPlayer.getLayoutParams().width/2),
                            ((int)floatingVideoPlayer.getY() + floatingVideoPlayer.getLayoutParams().height/2)) != 0){
                        Animations.fadeView(loadingScreen,500,Animations.FADE_OUT);
                        Animations.fadeView(floatingVideoPlayer,500,Animations.FADE_IN);
                    } else {
                        loadingScreen.postDelayed(this,500);
                    }
                    loadingScreen.destroyDrawingCache();
                }
            }, 500);
        }

        public void passActivityContext(Context context){
            meshActivity = (MeshActivity) context;
        }

        public FrameLayout getVideoLayout(){
            return this.videoLayout;
        }

        @Override
        public void onTaskRemoved(Intent rootIntent) {
            super.onTaskRemoved(rootIntent);
            onDestroy();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (videoLayout != null) {
                if(videoLayout.getWindowToken() != null) {
                    // The view isn't always attached by is also not null
                    windowManager.removeViewImmediate(videoLayout);
                }
            }
            windowManager = null;
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            RaveLogging.i(LOG_TAG, "Service bound");
            return new FloatingVideoServiceBinder();
        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (getVideoPlayer() != null) {
                getVideoPlayer().forceSetSurface(holder.getSurface());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {}

        public ForegroundVideoPlayer getVideoPlayer() {
            return videoPlayer;
        }

        *//**
         * Binder implementation to let the bound context communicate with {@link MediaPlayerService}
         *//*
        public class FloatingVideoServiceBinder extends Binder {
            public FloatingVideoService getServiceInstance() {
                return FloatingVideoService.this;
            }
        }

        private class FloatingBroadcastReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(FloatingAccessibilityService.ACTION_DISABLE_FLOATING_VIDEO)) {
                    RaveLogging.i(LOG_TAG, "Broadcast received: " + FloatingAccessibilityService.ACTION_DISABLE_FLOATING_VIDEO);
                    setAccessibilityDisablingFloating(true);
                    hideVideoPlayer();
                } else if (intent.getAction().equals(FloatingAccessibilityService.ACTION_ENABLE_FLOATING_VIDEO)) {
                    RaveLogging.i(LOG_TAG, "Broadcast received: " + FloatingAccessibilityService.ACTION_ENABLE_FLOATING_VIDEO);
                    setAccessibilityDisablingFloating(false);
                    showVideoPlayer();
                }
            }
        }

    }*/


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
