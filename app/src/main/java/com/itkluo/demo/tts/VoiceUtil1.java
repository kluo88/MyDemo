package com.itkluo.demo.tts;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.itkluo.demo.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 连续播放音频
 * MediaPlayer播放，#onCompletion中接着播放下一个
 *
 * @author luobingyong
 * @date 2020/6/9
 */
public class VoiceUtil1 {
    private static final String TAG = "AudioUtil";
    private boolean mShouldPlayBeep = true;
    private final Context mContext;
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private List<Integer> mResources;
    private MyOnCompletionListener myOnCompletionListener;
    private int lastMedia;

    private final static class HolderClass {
        private final static VoiceUtil1 INSTANCE = new VoiceUtil1();
    }

    public static VoiceUtil1 getInstance() {
        return VoiceUtil1.HolderClass.INSTANCE;
    }

    private VoiceUtil1() {
        mAudioManager = (AudioManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mContext = MyApplication.getInstance().getApplicationContext();
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();
        // 使用系统的媒体音量控制
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build();
            mMediaPlayer.setAudioAttributes(attributes);
        }
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mResources = new ArrayList<>();
        myOnCompletionListener = new MyOnCompletionListener();
        mMediaPlayer.setOnCompletionListener(myOnCompletionListener);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    /**
     * 释放掉内存
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnCompletionListener(null);
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
            myOnCompletionListener = null;
            mResources.clear();
            mResources = null;
        }
    }


    /**
     * 播放
     */
    public void play(int resource) {
        //当前正在播放这条语音或者集合中已经有了这条语音，不重复播放
//        if (mMediaPlayer != null && (mMediaPlayer.isPlaying() && lastMedia == resource) || resources.contains(resource)) {
//            return;
//        }
        // 铃声（这里是先释放掉内存）
        release();
        if (checkDisablePlay()) {
            return;
        }
        init();
        mResources.add(resource);
        playTone();
    }

//    public void play(int... rawIds) {
//        int length = rawIds.length;
//    }

    public void play(List<Integer> _resources) {
        // 铃声（这里是先释放掉内存）
        release();
        if (checkDisablePlay()) {
            return;
        }
        init();
        mResources.addAll(_resources);
        playTone();
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer player) {
//            MyApplication.sHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
            playTone();
//                }
//            }, 200);
        }
    }

    /**
     * 播放一段提示音
     */
    private void playTone() {
        try {
            if (mResources.size() == 0) {
                lastMedia = 0;
                return;
            }
            mMediaPlayer.reset();

            AssetFileDescriptor file = mContext.getResources().openRawResourceFd(mResources.get(0));
            mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mMediaPlayer.setVolume(0, 1);
            lastMedia = mResources.get(0);
            mResources.remove(0);
            mMediaPlayer.prepare();

//            if (mShouldPlayBeep) {
//                Log.d(TAG, "loadRaw: " + "soundId-duration "  + "-" + mMediaPlayer.getDuration());
//                mMediaPlayer.start();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测当前不允许播放
     */
    private boolean checkDisablePlay() {
//        if (mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//            //检查当前是否是静音模式
//            mShouldPlayBeep = false;
//        } else {
//            mShouldPlayBeep = true;
//        }
//        return mShouldPlayBeep;
        return false;
    }

}
