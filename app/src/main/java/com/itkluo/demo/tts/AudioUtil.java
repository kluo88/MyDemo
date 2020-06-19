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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private PlayThread mPlayThread;

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
            if (mPlayThread != null) {
                mPlayThread.interrupt();
            }
            if (mSoundIdDurationMap != null) {
                mSoundIdDurationMap.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 音频播放线程
     */
    private class PlayThread extends Thread {
        @Override
        public void run() {
            try {
                if (mSoundIdDurationMap == null || mSoundPool == null) {
                    return;
                }
                Set<Integer> soundIdSet = mSoundIdDurationMap.keySet();
                for (Integer soundId : soundIdSet) {
                    mSoundPool.play(soundId, 1, 1, 1, 0, 1);
                    try {
                        //获取当前音频的时长
                        Thread.sleep(mSoundIdDurationMap.get(soundId));
//                        Thread.sleep(400);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void play(int... rawIds) {
        release();
        if (rawIds.length == 0) {
            return;
        }
        init();
        //保存音效ID和对应的音效时长
        mSoundIdDurationMap = new HashMap<>();
        Integer[] soundIds;
        for (int rawId : rawIds) {
            soundIds = loadRaw(mContext, rawId);
            mSoundIdDurationMap.put(soundIds[0], soundIds[1]);
        }
        playTone();
    }

    /**
     * 播放一段提示音
     */
    private void playTone() {
        mPlayThread = new PlayThread();
        mPlayThread.start();
    }

    /**
     * 加载音频文件
     */
    private Integer[] loadRaw(Context context, int raw) {
        int soundId = mSoundPool.load(context, raw, 1);
        int duration = getRawFileVoiceTime(raw);
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


    public static String getRingDuring(String mUri) {
        String duration = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        Log.e(TAG, "duration " + duration);
        return duration;
    }

}
