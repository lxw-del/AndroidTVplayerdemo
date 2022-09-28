package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.video_view)
    MyVideoView videoView;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.tv_play_time)
    TextView tvPlayTime;
    @BindView(R.id.time_seekbar)
    SeekBar timeSeekbar;
    @BindView(R.id.lay_finish_bg)
    RelativeLayout layFinishBg;
    @BindView(R.id.btn_play_or_pause)
    ImageButton btnPlayOrPause;
    @BindView(R.id.btn_reset_play)
    ImageButton btnRestartPlay;
    @BindView(R.id.button_play)
    ImageButton playButton;

    public final String SYSTEM_DIALOG_REASON_KEY = "reason";
    public final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private HomeReceiver homeReceiver;

    private int key = 0;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(videoView.isPlaying())
            {
                int current = videoView.getCurrentPosition();
                timeSeekbar.setProgress(current);
                tvPlayTime.setText(time(videoView.getCurrentPosition()));
                handler.postDelayed(runnable,500);
            }
        }
    };


    public void initReceiver(){
        homeReceiver = new HomeReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeReceiver,filter);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int totalTime = videoView.getDuration();//获取总时长
                tvTotalTime.setText(stringForTime(totalTime));

                handler.postDelayed(runnable,0);
                timeSeekbar.setMax(videoView.getDuration());
                videoView.start();

            }
        });
    }

    public void initVideo(){
        /*final Uri uri = Uri.parse("http://gslb.miaopai.com/stream/ed5HCfnhovu3tyIQAiv60Q__.mp4");
        videoView.setVideoURI(uri);*/
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/raw/test"));
        videoView.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initReceiver();

        timeSeekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        initVideo();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                key = 1;//这是一个全局变量，用来控制遥控单击ok键时，是暂停还是重新播放。
                btnRestartPlay.setVisibility(View.VISIBLE);
                layFinishBg.setVisibility(View.VISIBLE);
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(MainActivity.this,"播放出错",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btnRestartPlay.setOnClickListener(v->{
            initVideo();
            btnRestartPlay.setVisibility(View.GONE);
            layFinishBg.setVisibility(View.GONE);
            key = 0;
        });

        playButton.setOnClickListener(v->{
            if(videoView.isPlaying()){//暂停
                btnPlayOrPause.setBackground(getResources().getDrawable(R.drawable.pause));
                btnPlayOrPause.setVisibility(View.VISIBLE);
                playButton.setBackground(getResources().getDrawable(R.drawable.pause));
                videoView.pause();
            }else {//播放
                btnPlayOrPause.setBackground(getResources().getDrawable(R.drawable.play));
                btnPlayOrPause.setVisibility(View.VISIBLE);
                playButton.setBackground(getResources().getDrawable(R.drawable.play));
                //开启线程
                handler.postDelayed(runnable,0);
                videoView.start();
                timeSeekbar.setMax(videoView.getDuration());
                timeGone();
            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode)
        {
            case KeyEvent.KEYCODE_ENTER:

            case KeyEvent.KEYCODE_DPAD_CENTER:
                Log.d(TAG, "enter--->");
                isVideoPlay(videoView.isPlaying(),key);
                break;

            case KeyEvent.KEYCODE_BACK:    //返回键
                Log.d(TAG,"back--->");
                return true;   //这里由于break会退出，所以我们自己要处理掉 不返回上一层

            case KeyEvent.KEYCODE_SETTINGS: //设置键
                Log.d(TAG, "setting--->");
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:   //向下键

                /*    实际开发中有时候会触发两次，所以要判断一下按下时触发 ，松开按键时不触发
                 *    exp:KeyEvent.ACTION_UP
                 */
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d(TAG, "down--->");
                }

                break;

            case KeyEvent.KEYCODE_DPAD_UP:   //向上键
                Log.d(TAG, "up--->");

                break;

            case KeyEvent.KEYCODE_0:   //数字键0
                Log.d(TAG, "0--->");

                break;
            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键
                Log.d(TAG, "left--->");
                if(videoView.getCurrentPosition()>4){
                    videoView.seekTo(videoView.getCurrentPosition()-5*1000);
                }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:  //向右键
                Log.d(TAG, "right--->");
                videoView.seekTo(videoView.getCurrentPosition()+5*1000);
                break;

            case KeyEvent.KEYCODE_INFO:    //info键
                Log.d(TAG, "info--->");

                break;

            case KeyEvent.KEYCODE_PAGE_DOWN:     //向上翻页键
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                Log.d(TAG, "page down--->");
                break;


            case KeyEvent.KEYCODE_PAGE_UP:     //向下翻页键
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                Log.d(TAG, "page up--->");

                break;

            case KeyEvent.KEYCODE_VOLUME_UP:   //调大声音键
                Log.d(TAG, "voice up--->");

                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN: //降低声音键
                Log.d(TAG, "voice down--->");

                break;
            case KeyEvent.KEYCODE_VOLUME_MUTE: //禁用声音
                Log.d(TAG, "voice mute--->");
                break;
            default:
                break;
        }


        return super.onKeyDown(keyCode, event);
    }

    class HomeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if(SYSTEM_DIALOG_REASON_KEY.equals(reason)){
                    Toast.makeText(MainActivity.this,"home键触发",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "home键触发");
                }
            }
        }
    }

    //进度条监听
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //获得当前进度条的刻度
            int progress = seekBar.getProgress();
            if(videoView.isPlaying())
            {
                videoView.seekTo(progress);
            }
        }
    };

    //时间转换方法
    public String time(long millionSeconds)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }

    /**
    *控制视频是 播放还是暂停或是重播
    * @param isPlay
     * @param keys
     */
    private void isVideoPlay(boolean isPlay,int keys){
        switch (keys){
            case 0:
                if(isPlay){//暂停
                    btnPlayOrPause.setBackground(getResources().getDrawable(R.drawable.pause));
                    btnPlayOrPause.setVisibility(View.VISIBLE);
                    videoView.pause();
                }else {//播放
                    btnPlayOrPause.setBackground(getResources().getDrawable(R.drawable.play));
                    btnPlayOrPause.setVisibility(View.VISIBLE);

                    //开启线程
                    handler.postDelayed(runnable,0);
                    videoView.start();
                    timeSeekbar.setMax(videoView.getDuration());
                    timeGone();
                }
                break;
            case 1:
                initVideo();
                btnRestartPlay.setVisibility(View.GONE);
                layFinishBg.setVisibility(View.GONE);
                key = 0;
                break;
            default:
                break;
        }
    }

    private void timeGone(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnPlayOrPause.setVisibility(View.GONE);
            }
        },1000);
    }

    //将长度转换为时间
    StringBuilder builder = new StringBuilder();
    Formatter mFormatter = new Formatter(builder, Locale.getDefault());
    private String stringForTime(int timeMs){
        int totalSeconds = timeMs/1000;

        int seconds = totalSeconds%60;
        int minutes = (totalSeconds/60)%60;
        int hours = totalSeconds / 3600;

        builder.setLength(0);
        if(hours>0)
        {
            return mFormatter.format("%d:%02d:%02d",hours,minutes,seconds).toString();
        }else {
            return mFormatter.format("%02d:%02d",minutes,seconds).toString();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(homeReceiver!=null) {
            unregisterReceiver(homeReceiver);
        }
        finish();
    }
}