package com.itkluo.demo.tts2.way2;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.itkluo.demo.MyApplication;
import com.itkluo.demo.tts2.VoiceBuilder;
import com.itkluo.demo.tts2.VoiceTextTemplate;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音频播放
 * @author luobingyong
 * @date 2020/6/18
 */
public class VoicePlay {
    private ExecutorService mExecutorService;
    private Context mContext;
    private List<Integer> mVoicePlay1;

    private VoicePlay(Context context) {
        this.mContext = context;
        this.mExecutorService = Executors.newCachedThreadPool();
    }

    private volatile static VoicePlay mVoicePlay = null;

    /**
     * 单例
     *
     * @return
     */
    public static VoicePlay with(Context context) {
        if (mVoicePlay == null) {
            synchronized (VoicePlay.class) {
                if (mVoicePlay == null) {
                    mVoicePlay = new VoicePlay(context);
                }
            }
        }
        return mVoicePlay;
    }

    /**
     * 接收自定义
     *
     * @param voiceBuilder
     */
    public void play(VoiceBuilder voiceBuilder) {
        executeStart(voiceBuilder);
    }

    /**
     * 开启线程
     *
     * @param builder
     */
    private void executeStart(VoiceBuilder builder) {
        mVoicePlay1 = VoiceTextTemplate.genVoiceList(builder);

        if (mVoicePlay1.isEmpty()) {
            return;
        }

        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                start(mVoicePlay1);
            }
        });
    }

    /**
     * 开始播报
     *
     * @param voicePlay
     */
    private void start(final List<Integer> voicePlay) {
        synchronized (VoicePlay.this) {

            MediaPlayer mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(3);
            mMediaPlayer.setLooping(false);


            mMediaPlayer.reset();


            final CountDownLatch mCountDownLatch = new CountDownLatch(1);
            AssetFileDescriptor fileDescriptor = null;

            try {
                final int[] counter = {0};

                fileDescriptor = MyApplication.getInstance().getResources().openRawResourceFd(voicePlay.get(counter[0]));


                mMediaPlayer.setDataSource(
                        fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.reset();
                        counter[0]++;

                        if (counter[0] < voicePlay.size()) {
                            try {
                                AssetFileDescriptor fileDescriptor2 = MyApplication.getInstance().getResources().openRawResourceFd(voicePlay.get(counter[0]));

                                mediaPlayer.setDataSource(
                                        fileDescriptor2.getFileDescriptor(),
                                        fileDescriptor2.getStartOffset(),
                                        fileDescriptor2.getLength());
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                                mCountDownLatch.countDown();
                            }
                        } else {
                            mediaPlayer.release();
                            mCountDownLatch.countDown();
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                mCountDownLatch.countDown();
            } finally {
                if (fileDescriptor != null) {
                    try {
                        fileDescriptor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                mCountDownLatch.await();
                notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
