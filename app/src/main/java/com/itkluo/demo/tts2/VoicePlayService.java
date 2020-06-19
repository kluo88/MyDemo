package com.itkluo.demo.tts2;

import java.util.List;

/**
 * @author luobingyong
 * @date 2020/6/17
 */
public class VoicePlayService {
    private static final String TAG = "VoicePlayService";
    private volatile VoiceMediaPlayer mVoiceMediaPlayer = null;
    private List<Integer> mVoiceList;
    private volatile static VoicePlayService mVoicePlayService;

    public static VoicePlayService getInstance() {
        if (mVoicePlayService == null) {
            synchronized (VoicePlayService.class) {
                if (mVoicePlayService == null) {
                    mVoicePlayService = new VoicePlayService();
                }
            }
        }
        return mVoicePlayService;
    }

    /**
     * 接收自定义
     *
     * @param voiceBuilder
     */
    public void play(VoiceBuilder voiceBuilder) {
        if (this.mVoiceMediaPlayer != null) {
            stopPlay();
        }

        mVoiceList = VoiceTextTemplate.genVoiceList(voiceBuilder);
        if (mVoiceList.isEmpty()) {
            return;
        }

        mVoiceMediaPlayer = null;
        mVoiceMediaPlayer = new VoiceMediaPlayer();

        doPlay();
    }

    /**
     * 开始播报
     */
    private void doPlay() {
        mVoiceMediaPlayer.enqueue(mVoiceList);
    }

    public void stopPlay() {
        this.mVoiceMediaPlayer.forceStop();
    }


}