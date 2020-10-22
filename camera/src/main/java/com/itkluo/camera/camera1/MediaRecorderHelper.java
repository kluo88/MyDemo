package com.itkluo.camera.camera1;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.itkluo.camera.util.FileUtil;

/**
 * @author luobingyong
 * @date 2020/10/21
 */
public class MediaRecorderHelper {
    private Activity mContext;
    private Camera mCamera;
    private int rotation;
    private Surface surface;
    private MediaRecorder mMediaRecorder;
    public boolean isRunning;
    private String filePath = FileUtil.createVideoFile().getAbsolutePath();

    public MediaRecorderHelper(Activity context, Camera camera, int rotation, Surface surface) {
        mContext = context;
        mCamera = camera;
        this.rotation = rotation;
        this.surface = surface;
    }

    public void startRecord() {
        try {
            mMediaRecorder = new MediaRecorder();
            mCamera.unlock(); //一定要调用
            mMediaRecorder.reset();
            mMediaRecorder.setCamera(mCamera);            //给Recorder设置Camera对象
            mMediaRecorder.setOrientationHint(rotation);  //改变保存后的视频文件播放时是否横屏(不加这句，视频文件播放的时候角度是反的)
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);      //设置从麦克风采集声音
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);   //设置从摄像头采集图像
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //设置视频的输出格式为MP4
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //设置音频的编码格式
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT); // 设置视频的编码格式
            mMediaRecorder.setVideoSize(3840, 2160);// 设置视频大小
            mMediaRecorder.setVideoFrameRate(60);  // 设置帧率
            //mMediaRecorder.setMaxDuration(10000) //设置最大录像时间为10s
            mMediaRecorder.setPreviewDisplay(surface); //设置
            mMediaRecorder.setOutputFile(filePath); //设置输出文件
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isRunning = true;
            Log.d("tag", "开始录制视频");
            Log.d("tag", "视频保存路径：" + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
        }
        isRunning = false;
        Log.d("tag", "停止录制视频");
        Toast.makeText(mContext, "视频保存路径：" + filePath, Toast.LENGTH_LONG).show();
    }

    public void release() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
            isRunning = false;
        }
    }
}
