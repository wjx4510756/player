package service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import com.example.mymusicplayer.R;

import activity.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import activity.PlayingActivity;
import db.MusicDetailInfo;
import utils.StaticUtil;

/**
 * Created by wjx4510756 on 2017/4/18.
 */
public class MusicPlayerService extends Service {


    private ArrayList<MusicDetailInfo> mList;

    private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service
    private String url = null;
    private String msg = null;
    private static int curPosition;//第几首音乐


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("list");
            mList = (ArrayList<MusicDetailInfo>) bundle.getSerializable("list");
            msg = intent.getStringExtra("MSG");

            StaticUtil.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNew();
                }
            });

            if (msg.equals("0")) {
                url = intent.getStringExtra("url");
                curPosition = intent.getIntExtra("curPosition", 0);
                player();
            } else if (msg.equals("1")) {
                StaticUtil.mediaPlayer.pause();
            } else if (msg.equals("2")) {
                StaticUtil.mediaPlayer.start();
            }

            String musicName = mList.get(curPosition).getMusicName();
            String singer = mList.get(curPosition).getSinger();

            Notification notification;
            Notification.Builder builder = new Notification.Builder(this);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);
            builder.setContentIntent(contentIntent);
            builder.setSmallIcon(R.drawable.icon);

            builder.setContentTitle(musicName);
            builder.setContentText(singer);
            if (Build.VERSION.SDK_INT >= 21) {
                builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            }

            notification = builder.build();


            startForeground(NOTIFICATION_ID, notification);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void playNew() {

        switch (StaticUtil.sharedPreferences.getInt("play_mode", 1)) {
            case 0://随机
                curPosition = (new Random()).nextInt(mList.size());
                url = mList.get(curPosition).getFileUrl();
                player();
                break;
            case 1://顺序
                curPosition = (++curPosition) % mList.size();
                url = mList.get(curPosition).getFileUrl();
                player();
                break;
            case 2://单曲
                url = mList.get(curPosition).getFileUrl();
                player();
                break;
            default:
                break;
        }


    }

    private void player() {

        StaticUtil.currentPosition = curPosition;
        StaticUtil.editor.putString("songName", mList.get(curPosition).getMusicName());
        StaticUtil.editor.putString("singer", mList.get(curPosition).getSinger());
        StaticUtil.editor.commit();



        try {
            synchronized (PlayingActivity.object){
                StaticUtil.mediaPlayer.reset();
                StaticUtil.mediaPlayer.setDataSource(url);
                StaticUtil.mediaPlayer.prepare();
                StaticUtil.mediaPlayer.start();
                PlayingActivity.object.notifyAll();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setAction("com.example.myMusicPlayer.playNew");
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (StaticUtil.mediaPlayer != null) {
            StaticUtil.mediaPlayer.stop();
            StaticUtil.mediaPlayer.release();
            StaticUtil.mediaPlayer = null;
        }

        //关闭线程
        Thread.currentThread().interrupt();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
