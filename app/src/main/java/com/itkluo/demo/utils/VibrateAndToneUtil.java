package com.itkluo.demo.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.itkluo.demo.MyApplication;
import com.itkluo.demo.R;

import java.io.IOException;

/**
 * 手机震动和提示音工具类
 *
 * @author luobingyong
 * @date 2020/3/17
 */
public class VibrateAndToneUtil {
    private static final String TAG = "VibrateAndToneUtil";
    private boolean mShouldPlayBeep = true;
    private final Context mContext;
    private AudioManager mAudioManager;
    private Vibrator mVibrator;
    private Ringtone mRingtone;
    /**
     * 时间间隔
     */
    private static final int MIN_TIME_OUT = 1;
    private long mLastNotifyTime;
    private MediaPlayer mMediaPlayer;
    private long[] mPattern = new long[]{0, 180, 80, 120};

    private final static class HolderClass {
        private final static VibrateAndToneUtil INSTANCE = new VibrateAndToneUtil();
    }

    public static VibrateAndToneUtil getInstance() {
        return HolderClass.INSTANCE;
    }

    private VibrateAndToneUtil() {
        mAudioManager = (AudioManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mContext = MyApplication.getInstance().getApplicationContext();
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * 开启手机震动和播放系统提示铃声
     */
    public void vibrateAndPlayTone() {
        //在2秒钟内收到新消息，跳过播放
        if (System.currentTimeMillis() - mLastNotifyTime < MIN_TIME_OUT) {
            return;
        }
        try {
            mLastNotifyTime = System.currentTimeMillis();
            // 是否在静音模式
            if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                LogUtils.d(TAG, "已经调成静音");
                return;
            }
            vibrate(mPattern, false);
//            playTone();
            playDefaultTone();

        } catch (Exception e) {
            LogUtils.d(TAG, "vibrateAndPlayTone: " + e.toString());
        }
    }

    /**
     * @param pattern  自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]单位是毫秒
     * @param isRepeat true-> 反复震动，false-> 只震动一次
     */
    public void vibrate(long[] pattern, boolean isRepeat) {
        int repeat = isRepeat ? 1 : -1;
        try {
            mVibrator.cancel();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mVibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat));
            } else {
//                mVibrator.vibrate(pattern == null ? mPattern : pattern, repeat)
            }
        } catch (Exception iae) {
            Log.e(TAG, "Failed to create VibrationEffect", iae);
        }
    }

    /**
     * 播放系统默认信息音
     *
     * @return
     */
    private boolean playDefaultTone() {
        if (mRingtone == null) {
            Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mRingtone = RingtoneManager.getRingtone(mContext, notificationUri);
            if (mRingtone == null) {
                LogUtils.d(TAG, "cant find mRingtone at:" + notificationUri.getPath());
                return true;
            }
        }
        mRingtone.stop();
        if (!mRingtone.isPlaying()) {
            mRingtone.play();
        }
        return false;
    }


    /**
     * 播放一段提示音
     */
    public void playTone() {
        playTone(null);
    }

    /**
     * 播放一段提示音
     *
     * @param listener
     */
    public void playTone(final PlayerCompleteListener listener) {
        // 铃声（这里是先释放掉内存）
        releaseMediaPlayer();
        //检查当前是否是静音模式
        if (mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            //检查当前是否是静音模式
            mShouldPlayBeep = false;
            return;
        } else {
            mShouldPlayBeep = true;
        }
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
        AssetFileDescriptor file = mContext.getResources().openRawResourceFd(
                R.raw.dingding);
        try {
            mMediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mMediaPlayer.setVolume(0, 1);
            mMediaPlayer.prepare();
        } catch (IOException ioe) {
            mMediaPlayer = null;
        }

        if (mShouldPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer player) {
                player.seekTo(0);
                player.stop();
                if (listener != null) {
                    listener.onCompletion(player);
                }
            }
        });

    }

    /**
     * 取消
     */
    public void cancle() {
        mVibrator.cancel();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 释放掉内存
     */
    public void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * MediaPlayer播放监听
     */
    public interface PlayerCompleteListener {
        /**
         * MediaPlayer播放完毕监听
         *
         * @param mp
         */
        void onCompletion(MediaPlayer mp);
    }

}
