package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.VideoView;

import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

public class MyVideoView extends VideoView {
    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(getWidth(),widthMeasureSpec);
        int height = getDefaultSize(getHeight(),heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    //为了让高的安卓版本播放https视频
    @Override
    public void setVideoURI(Uri uri) {
        super.setVideoURI(uri);
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(SSlUtils.createSSLSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new SSlUtils.TrustAllHostnameVerifier());
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
