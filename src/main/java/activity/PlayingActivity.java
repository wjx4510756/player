package activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusicplayer.R;

import java.util.ArrayList;
import java.util.List;

import db.LocalMusicDB;
import db.MusicDetailInfo;
import fragment.MusicDetailFragment;
import lyric.LyricView;
import utils.MusicUtil;
import utils.StaticUtil;

import static com.example.mymusicplayer.R.id.album_img;
import static com.example.mymusicplayer.R.id.back;

/**
 * Created by wjx4510756 on 2017/4/6.
 */
public class PlayingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mBack, albumImg, pre, next, playBtn, list, mode;
    private TextView songName, singer, duration, playedDuration;
    private SeekBar seekBar;
    private LyricView lyc;
    private LocalMusicDB db;
    private List<String> currentSongNameList = new ArrayList<>();
    private List<MusicDetailInfo> currentSongList = new ArrayList<>();

    private int playMode;
    private int interval = 40;//歌词每行的间隔

    private boolean seekBarRun = true;
    private boolean wait = false;

    public static final Object object = new Object();

    private static final String PLAY_NEW_BROADCAST = "com.example.myMusicPlayer.playNew";


    Handler mHandler = new Handler();


    private BroadcastReceiver playNewReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(PLAY_NEW_BROADCAST)) {

                playBtn.setImageResource(R.drawable.play_rdi_btn_pause);
                songName.setText(currentSongList.get(StaticUtil.currentPosition).getMusicName());
                singer.setText(currentSongList.get(StaticUtil.currentPosition).getSinger());

                wait = true;
                synchronized (object){

                    initTime();
                    wait = false;
                    object.notifyAll();

                }
            }

        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        songName.setText(StaticUtil.sharedPreferences.getString("songName", ""));
        singer.setText(StaticUtil.sharedPreferences.getString("singer", ""));

        if (StaticUtil.isplay) {
            playBtn.setImageResource(R.drawable.play_rdi_btn_pause);
        } else
            playBtn.setImageResource(R.drawable.play_rdi_btn_play);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        registerBroadcastReceiver();

        playMode = StaticUtil.sharedPreferences.getInt("play_mode", 1);

        db = LocalMusicDB.getInstance(this);
        currentSongNameList = db.queryCurrentList();
        currentSongList.clear();

        for (String songName : currentSongNameList
                ) {
            currentSongList.add(db.querySongsBySongName(songName));
        }

        init();

        switch (playMode) {
            case 0:
                mode.setImageResource(R.drawable.play_icn_shuffle);
                break;
            case 1:
                mode.setImageResource(R.drawable.play_icn_loop_prs);
                break;
            case 2:
                mode.setImageResource(R.drawable.play_icn_one);
                break;
            default:
                break;
        }


        initTime();

        new Thread(new SeekBarRunnable()).start();

    }

    private void initTime() {
        if (StaticUtil.currentPosition != -1) {

            searchLrc();
            seekBar.setMax(currentSongList.get(StaticUtil.currentPosition).getDuration());
            int times[] = MusicUtil.timeConvert(currentSongList.get(StaticUtil.currentPosition).getDuration());

            playedDuration.setText("00:00");
            showTime(times, duration);

        }
    }

    private void showTime(int[] times, TextView time) {
        if (times[0] < 10 && times[1] < 10) {
            time.setText("0" + times[0] + ":0" + times[1]);
        } else if (times[0] < 10) {
            time.setText("0" + times[0] + ":" + times[1]);
        } else if (times[1] < 10) {
            time.setText(times[0] + ":0" + times[1]);
        } else
            time.setText(times[0] + ":" + times[1]);
    }

    private void init() {

        songName = (TextView) findViewById(R.id.song_name);
        singer = (TextView) findViewById(R.id.singer_name);
        duration = (TextView) findViewById(R.id.music_duration);
        playedDuration = (TextView) findViewById(R.id.music_duration_played);


        seekBar = (SeekBar) findViewById(R.id.play_seek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {

                    if (!StaticUtil.isplay) {
                        MusicUtil.player(PlayingActivity.this, currentSongList);
                    }

                    playBtn.setImageResource(R.drawable.play_rdi_btn_pause);
                    StaticUtil.mediaPlayer.seekTo(progress);
                    lyc.setOffsetY(200 - lyc.selectIndex(progress)
                            * (lyc.getWordSize() + interval - 1));

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (StaticUtil.mediaPlayer != null)
                    StaticUtil.mediaPlayer.pause();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (StaticUtil.mediaPlayer != null)
                    StaticUtil.mediaPlayer.start();
            }
        });

        lyc = (LyricView) findViewById(R.id.lyc_view);
        albumImg = (ImageView) findViewById(album_img);
        pre = (ImageView) findViewById(R.id.playing_pre);
        next = (ImageView) findViewById(R.id.playing_next);
        playBtn = (ImageView) findViewById(R.id.playing_play);
        list = (ImageView) findViewById(R.id.playing_playlist);
        mode = (ImageView) findViewById(R.id.playing_mode);
        mBack = (ImageView) findViewById(R.id.back);
        mode = (ImageView) findViewById(R.id.playing_mode);

        mode.setOnClickListener(this);

        mBack.setOnClickListener(this);
        lyc.setOnClickListener(this);
        albumImg.setOnClickListener(this);
        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        list.setOnClickListener(this);
        mode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.playing_pre:
                wait = true;
                if (StaticUtil.currentPosition > 0) {

                    StaticUtil.currentPosition -= 1;
                    MusicUtil.player(this, currentSongList);
                } else
                    Toast.makeText(this, "已经是第一首音乐了", Toast.LENGTH_SHORT).show();
                wait = false;
                break;
            case R.id.playing_next:

                wait = true;

                if (StaticUtil.currentPosition < currentSongList.size() - 1) {

                    StaticUtil.currentPosition += 1;
                    MusicUtil.player(this, currentSongList);
                } else {
                    Toast.makeText(this, "已经是最后一首音乐了", Toast.LENGTH_SHORT).show();
                }

                wait = false;

                break;
            case R.id.playing_play:
                int position = StaticUtil.sharedPreferences.getInt("position", -1);

                if (StaticUtil.isServiceRunning) {//服务启动着，这里点击播放暂停按钮时只需要当前音乐暂停或者播放就好
                    if (StaticUtil.isplay) {
                        playBtn.setImageResource(R.drawable.play_rdi_btn_play);
                        MusicUtil.pause(this);
                    } else {
                        //暂停--->继续播放
                        playBtn.setImageResource(R.drawable.play_rdi_btn_pause);
                        MusicUtil.player(this, "2");
                    }
                } else {
                    if (StaticUtil.isplay) {
                        playBtn.setImageResource(R.drawable.play_rdi_btn_play);
                        MusicUtil.pause(this);
                    } else {
                        if (StaticUtil.currentPosition == -1 && position == -1) {
                            Toast.makeText(this, "请选择要播放的音乐", Toast.LENGTH_SHORT).show();
                        } else if (position != -1) {
                            playBtn.setImageResource(R.drawable.play_rdi_btn_pause);
                            MusicUtil.player(this, position, currentSongList);
                        } else {
                            //暂停--->继续播放
                            playBtn.setImageResource(R.drawable.play_rdi_btn_pause);
                            MusicUtil.player(this, "2");

                        }
                    }
                }
                break;
            case R.id.playing_playlist:
                MusicDetailFragment fragment = new MusicDetailFragment();
                fragment.show(getSupportFragmentManager(), "list");
                break;
            case R.id.playing_mode:

                switch (playMode) {
                    case 0:
                        mode.setImageResource(R.drawable.play_icn_loop_prs);
                        StaticUtil.editor.putInt("play_mode", 1);
                        playMode = 1;
                        break;
                    case 1:
                        mode.setImageResource(R.drawable.play_icn_one);
                        StaticUtil.editor.putInt("play_mode", 2);
                        playMode = 2;
                        break;
                    case 2:
                        mode.setImageResource(R.drawable.play_icn_shuffle);
                        StaticUtil.editor.putInt("play_mode", 0);
                        playMode = 0;
                        break;
                    default:
                        break;
                }
                StaticUtil.editor.commit();
                break;

            default:
                break;


        }

    }

    public void searchLrc() {
        String lrc = currentSongList.get(StaticUtil.currentPosition).getFileUrl();
        lrc = lrc.substring(0, lrc.length() - 4).trim() + ".lrc".trim();
        LyricView.read(lrc);
        lyc.setTextSize();
        lyc.setOffsetY(200);
    }

    private class SeekBarRunnable implements Runnable {
        @Override
        public void run() {

            while (seekBarRun) {

                synchronized (object) {

                    if (wait) {
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (StaticUtil.isplay) {

                        lyc.setOffsetY(lyc.getOffsetY() - lyc.speedLrc());
                        int currentPosition = StaticUtil.mediaPlayer.getCurrentPosition();

                        lyc.selectIndex(currentPosition);
                        seekBar.setProgress(currentPosition);

                        final int times[] = MusicUtil.timeConvert(currentPosition);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showTime(times, playedDuration);
                            }
                        });
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                lyc.invalidate(); // 更新视图
                            }
                        });
                    }
                }

            }
        }
    }


    @Override
    protected void onDestroy() {
        seekBarRun = false;
        wait = true;
        unregisterReceiver(playNewReceiver);
        super.onDestroy();
    }


    public void registerBroadcastReceiver() {

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(PLAY_NEW_BROADCAST);
        registerReceiver(playNewReceiver, myIntentFilter);
    }
}
