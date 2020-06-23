package com.itkluo.demo.tts;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.itkluo.demo.MyApplication;
import com.itkluo.demo.tts2.VoiceBuilder;
import com.itkluo.demo.tts2.VoiceTextTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class AudioUtil {
    private static final String TAG = "AudioUtil";
    private final Context mContext;
    private SoundPool mSoundPool;
    private Map<Integer, Integer> mSoundIdDurationMap;
    private ExecutorService mSubmit;
    private volatile BlockingQueue<Integer> soundQueue = new LinkedBlockingQueue<>();
    private List<Integer> mVoiceList;


    private final static class HolderClass {
        private final static AudioUtil INSTANCE = new AudioUtil();
    }

    public static AudioUtil getInstance() {
        return AudioUtil.HolderClass.INSTANCE;
    }

    private AudioUtil() {
        mContext = MyApplication.getInstance().getApplicationContext();
    }

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
            if (soundQueue != null) {
                soundQueue.clear();
            }
            if (mSoundIdDurationMap != null) {
                mSoundIdDurationMap.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 播放语音
     * 自定义格式，支持拆分播放数字
     *
     * @param voiceBuilder
     */
    public void play(VoiceBuilder voiceBuilder) {
//        synchronized (AudioUtil.class) {
        release();
        if (voiceBuilder == null) {
            return;
        }
        if (AppConfig.noVoice) {
            return;
        }

        mVoiceList = VoiceTextTemplate.genVoiceList(voiceBuilder);
        if (mVoiceList.isEmpty()) {
            return;
        }

        init();
        //保存音效ID和对应的音效时长
        mSoundIdDurationMap = new HashMap<>();
        soundQueue.clear();
        Integer[] soundIds;
        for (int rawId : mVoiceList) {
            soundIds = loadRaw(mContext, rawId);
            soundQueue.add(soundIds[0]);
            mSoundIdDurationMap.put(soundIds[0], soundIds[1]);
        }
        doPlay();
//        }
    }


    /**
     * 播放语音
     *
     * @param rawIds
     */
    public void play(int... rawIds) {
//        synchronized (AudioUtil.class) {
        release();
        if (AppConfig.noVoice) {
            return;
        }
        if (rawIds.length == 0) {
            return;
        }
        init();
        //保存音效ID和对应的音效时长
        mSoundIdDurationMap = new HashMap<>();
        soundQueue.clear();
        Integer[] soundIds;
        for (int rawId : rawIds) {
            soundIds = loadRaw(mContext, rawId);
            soundQueue.add(soundIds[0]);
            mSoundIdDurationMap.put(soundIds[0], soundIds[1]);
        }
        doPlay();
//        }
    }

    /**
     * 播放一段提示音
     */
    private void doPlay() {
        mSubmit = Executors.newSingleThreadExecutor();
        mSubmit.submit(new VoicePlaySchedule());
    }

    /**
     * 加载音频文件
     */
    private Integer[] loadRaw(Context context, int rawId) {
        int soundId = mSoundPool.load(context, rawId, 1);
        int duration = getRawFileVoiceTime(rawId);
        Log.d(TAG, "loadRaw: " + "soundId-duration " + soundId + "-" + duration);
        return new Integer[]{soundId, duration};
    }

    /**
     * 获取音频文件的总时长大小
     *
     * @param rawId raw资源文件ID
     * @return 返回时长大小
     */
    private int getRawFileVoiceTime(int rawId) {
        int duration = 0;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + rawId);
            mediaPlayer.setDataSource(mContext, uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//            AssetFileDescriptor file = mContext.getResources().openRawResourceFd(rawId);
//            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
//            file.close();

            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();

            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duration;
    }

    private class VoicePlaySchedule implements Runnable {

        @Override
        public void run() {
            synchronized (AudioUtil.class) {
                if (soundQueue == null || mSoundIdDurationMap == null || mSoundPool == null) {
                    return;
                }
                while (soundQueue.size() > 0) {
                    try {
                        int soundId = soundQueue.take();
                        long lastAsyncPlayStartTime = System.currentTimeMillis();
                        mSoundPool.play(soundId, 1, 1, 1, 0, 1);
                        while (true) {
                            //播放完一个音频
                            if (System.currentTimeMillis() > lastAsyncPlayStartTime + mSoundIdDurationMap.get(soundId)) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
