package com.itkluo.demo.tts2;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Looper;
import android.util.Log;

import com.itkluo.demo.MyApplication;
import com.itkluo.demo.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 音频播放
 *
 * @author luobingyong
 * @date 2020/6/17
 */
public class VoiceMediaPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private String TAG = "VoiceMediaPlayer";
    public static int STATE_INIT = 0;
    public static int STATE_PLAY = 1;
    public static int STATE_WAIT = 2;
    public static int STATE_COMPLETE = 3;
    private List<Integer> audioQueue;
    private MediaPlayer mediaPlayer;
    private AtomicBoolean isPlaying = new AtomicBoolean(false);
    private volatile boolean pauseFlag = false;
    private AtomicInteger state;
    private ExecutorService mExecutorService;

    public VoiceMediaPlayer() {
        this.state = new AtomicInteger(STATE_INIT);
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioStreamType(3);
        this.mediaPlayer.setLooping(false);
        this.mediaPlayer.setOnCompletionListener(this);
        this.mediaPlayer.setOnErrorListener(this);
        this.mediaPlayer.setOnPreparedListener(this);
        this.audioQueue = new ArrayList<>();
        this.mExecutorService = Executors.newCachedThreadPool();
    }

    public boolean enqueue(Integer audio) {
        try {
            this.pauseFlag = false;
            this.audioQueue.add(audio);
            if (this.isPlaying.compareAndSet(false, true)) {
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(TAG, "playAudio thread " + Thread.currentThread() + " main thread " + (Thread.currentThread() == Looper.getMainLooper().getThread()));
                        playAudio(dequeue());
                    }
                });
            }

            return true;
        } catch (Exception var4) {
            Log.e(this.TAG, "enqueue err: ", var4);
            return false;
        }
    }

    public boolean enqueue(List<Integer> audioList) {
        try {
            this.pauseFlag = false;

            if (audioList == null || audioList.isEmpty()) {
                return false;
            }

            audioQueue.addAll(audioList);

            if (isPlaying.compareAndSet(false, true)) {
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(TAG, "playAudio thread " + Thread.currentThread() + " main thread " + (Thread.currentThread() == Looper.getMainLooper().getThread()));
                        playAudio(dequeue());
                    }
                });
            }
            return true;

        } catch (Exception var4) {
            Log.e(this.TAG, "enqueue err: ", var4);
            return false;
        }
    }

    public Integer dequeue() {
        try {
            return audioQueue.remove(0);
        } catch (Exception var2) {
            Log.e(this.TAG, "dequeue err: ", var2);
            return -1;
        }
    }

    public void forceStop() {
        this.pauseFlag = true;
        this.stopAudio();
        this.audioQueue.clear();
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        if (this.pauseFlag) {
            Log.d(this.TAG, "mediaplayer oncompletion paused");
        } else {
            Integer buffer = this.dequeue();
            if (buffer > 0) {

                LogUtils.d(TAG, "onCompletion playAudio thread " + Thread.currentThread() + " main thread " + (Thread.currentThread() == Looper.getMainLooper().getThread()));

                this.playAudio(buffer);
            } else {
                this.isPlaying.set(false);
                this.state.set(STATE_WAIT);
            }

        }
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        this.isPlaying.set(false);
        this.state.set(STATE_COMPLETE);
        return false;
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        int status = this.state.get();
        this.isPlaying.set(true);
        mediaPlayer.start();
        this.state.set(STATE_PLAY);
    }

    private void playAudio(Integer audio) {
        if (audio <= 0) {
            return;
        }
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioStreamType(3);
            this.mediaPlayer.setLooping(false);
            this.mediaPlayer.setOnCompletionListener(this);
            this.mediaPlayer.setOnErrorListener(this);
            this.mediaPlayer.setOnPreparedListener(this);
        }

        AssetFileDescriptor fileDescriptor = null;

        try {
            this.mediaPlayer.reset();

            fileDescriptor = MyApplication.getInstance().getResources().openRawResourceFd(audio);


            this.mediaPlayer.setDataSource(
                    fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());

            this.mediaPlayer.prepare();
        } catch (Exception var4) {
            Log.e(this.TAG, "playAudio Exception", var4);
        }

    }

    private void stopAudio() {
        try {
            this.pauseFlag = true;
            if (this.mediaPlayer != null) {
                this.mediaPlayer.stop();
                this.mediaPlayer.release();
            }

            this.mediaPlayer = null;
        } catch (Exception var3) {
            Log.e(this.TAG, "", var3);
        }

    }

}
