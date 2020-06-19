package com.itkluo.demo.tts;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.SparseIntArray;

import com.itkluo.demo.MyApplication;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 连续播放音频
 * 通过MediaPlayer获取每个音频时长，再通过SoundPool顺序播放
 *
 * @author luobingyong
 * @date 2020/6/9
 */
public class AudioUtil2 {
    private final static String TAG = "VoiceUtil";
    private final Context mContext;
    private static volatile AudioUtil2 mInstance = null;
    private ExecutorService mSubmit = Executors.newSingleThreadExecutor();

    private SoundPool mSoundPool;

    //定义一个map用于存放音频流的ID
    private SparseIntArray musicId = new SparseIntArray();
    //定义一个map用于存放音频流的播放时长
    private SparseIntArray musicIdLength = new SparseIntArray();
    private Thread thread;

    public static AudioUtil2 getInstance() {
        if (mInstance == null) {
            synchronized (AudioUtil2.class) {
                if (mInstance == null) {
                    mInstance = new AudioUtil2();
                }
            }
        }
        return mInstance;
    }

    private AudioUtil2() {
        mContext = MyApplication.getInstance().getApplicationContext();
    }

    private volatile BlockingQueue<Integer> soundQueue = new LinkedBlockingQueue<>();
    private int lastRawId;
    private long lastAsyncPlayStartTime;


    private void init() {
        //设置描述音频流信息的属性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aab = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(10)   //设置允许同时播放的流的最大值
                    .setAudioAttributes(aab)   //完全可以设置为null
                    .build();
        } else {
            mSoundPool = new SoundPool(60, AudioManager.STREAM_MUSIC, 8);
        }
        mSoundPool = new SoundPool(60, AudioManager.STREAM_MUSIC, 8);
        //设置资源加载监听
//        mSoundPool.setOnLoadCompleteListener(new MyOnLoadCompleteListener());
    }

    /**
     * 释放掉内存
     */
    public void release() {
        try {
            if (mSoundPool != null) {
                mSoundPool.setOnLoadCompleteListener(null);
                mSoundPool.release();
                mSoundPool = null;
            }
            if (mSubmit != null) {
                mSubmit.shutdown();
            }
            if (musicId != null) {
                musicId.clear();
            }
            if (musicIdLength != null) {
                musicIdLength.clear();
            }
            if (soundQueue != null) {
                soundQueue.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放语音
     *
     * @param rawIds
     */
    public void play(int... rawIds) {
        synchronized (AudioUtil2.class) {
            release();
            if (AppConfig.noVoice) {
                return;
            }
            if (rawIds.length == 0) {
                return;
            }
            init();

            musicId.clear();
            musicIdLength.clear();
            soundQueue.clear();
            for (int rawId : rawIds) {
                parseRaw(rawId);
            }
            mSubmit.submit(new VoicePlaySchedule());
        }
    }

    private void parseRaw(int rawId) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + rawId);
            mediaPlayer.setDataSource(mContext, uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            int duration = mediaPlayer.getDuration();
            int soundId = mSoundPool.load(mContext, rawId, 1);
            musicId.put(rawId, soundId);
            musicIdLength.put(rawId, duration);
            soundQueue.add(rawId);
            Log.e(TAG, "parseRaw: " + duration);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class VoicePlaySchedule implements Runnable {

        @Override
        public void run() {
            synchronized (AudioUtil2.class) {
                while (soundQueue.size() > 0) {
                    try {
                        lastRawId = soundQueue.take();
                        lastAsyncPlayStartTime = System.currentTimeMillis();
                        mSoundPool.play(musicId.get(lastRawId), 1, 1, 0, 0, 1);
                        while (true) {
                            //代表播放完成
                            if (System.currentTimeMillis() > lastAsyncPlayStartTime + musicIdLength.get(lastRawId)) {
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
