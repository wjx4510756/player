package activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusicplayer.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import db.LocalMusicDB;
import db.MusicDetailInfo;
import fragment.LocalFragmentPagerAdapter;
import fragment.LocalMusicFragmentAlbum;
import fragment.LocalMusicFragmentFile;
import fragment.LocalMusicFragmentSinger;
import fragment.LocalMusicFragment;
import fragment.MusicDetailFragment;
import service.MusicPlayerService;
import utils.MusicUtil;
import utils.StaticUtil;

/**
 * Created by wjx4510756 on 2017/4/10.
 */
public class LocalMusicActivity extends AppCompatActivity implements View.OnClickListener {


    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> tabList = new ArrayList<>();
    private ViewPager mViewPager;
    private PagerTabStrip mTab;


    private ImageView backImage;
    private ImageView searchImage;

    private ImageView playBarBtn;
    private ImageView listBtn;
    private ImageView playBtn;
    private ImageView next;

    private TextView songName;
    private TextView singer;


    private LocalMusicDB db;

    private List<MusicDetailInfo> songList = new ArrayList<>();

    private List<String> currentSongNameList = new ArrayList<>();

    private List<MusicDetailInfo> currentSongList = new ArrayList<>();

    private static final String PLAY_NEW_BROADCAST = "com.example.myMusicPlayer.playNew";

    /**
     * 内部类广播，接收自动换歌之后的发出的广播
     */
    private BroadcastReceiver playNewReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(PLAY_NEW_BROADCAST)) {

                playBtn.setImageResource(R.drawable.playbar_btn_pause);
                songName.setText(currentSongList.get(StaticUtil.currentPosition).getMusicName());
                singer.setText(currentSongList.get(StaticUtil.currentPosition).getSinger());

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//
//        if (StaticUtil.mediaPlayer != null) {
//            reInit();
//        }

        songName.setText(StaticUtil.sharedPreferences.getString("songName", ""));
        singer.setText(StaticUtil.sharedPreferences.getString("singer", ""));

        if (StaticUtil.isplay) {
            playBtn.setImageResource(R.drawable.playbar_btn_pause);
        } else
            playBtn.setImageResource(R.drawable.playbar_btn_play);

        currentSongNameList = db.queryCurrentList();

        for (String songName : currentSongNameList
                ) {
            currentSongList.add(db.querySongsBySongName(songName));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_music_detail);


        registerBroadcastReceiver();

        StaticUtil.handler = new Handler();

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTab = (PagerTabStrip) findViewById(R.id.tab);


        backImage = (ImageView) findViewById(R.id.back);
        searchImage = (ImageView) findViewById(R.id.search);

        playBarBtn = (ImageView) findViewById(R.id.playBar_img);
        listBtn = (ImageView) findViewById(R.id.play_list);
        playBtn = (ImageView) findViewById(R.id.control);
        next = (ImageView) findViewById(R.id.play_next);

        songName = (TextView) findViewById(R.id.song_name);
        singer = (TextView) findViewById(R.id.singer_name);

        backImage.setOnClickListener(this);
        searchImage.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        next.setOnClickListener(this);
        playBarBtn.setOnClickListener(this);
        listBtn.setOnClickListener(this);

        db = LocalMusicDB.getInstance(this);
        songList = db.queryAllMusic();


        LocalMusicFragment fragment = new LocalMusicFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("local_music", (Serializable) songList);
        fragment.setArguments(bundle);
        fragmentList.add(fragment);

        LocalMusicFragmentSinger fragment1 = new LocalMusicFragmentSinger();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("singer", (Serializable) songList);
        fragment1.setArguments(bundle1);
        fragmentList.add(fragment1);

        LocalMusicFragmentAlbum fragment2 = new LocalMusicFragmentAlbum();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("album", (Serializable) songList);
        fragment2.setArguments(bundle2);
        fragmentList.add(fragment2);

        LocalMusicFragmentFile fragment3 = new LocalMusicFragmentFile();
        Bundle bundle3 = new Bundle();
        bundle3.putSerializable("file", (Serializable) songList);
        fragment3.setArguments(bundle3);
        fragmentList.add(fragment3);


        tabList.add("单曲");
        tabList.add("歌手");
        tabList.add("专辑");
        tabList.add("文件夹");

        mTab.setTextColor(Color.parseColor("#000000"));
        mTab.setTabIndicatorColor(Color.parseColor("#d20000"));
        mTab.setDrawFullUnderline(false);

        LocalFragmentPagerAdapter adapter =
                new LocalFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, tabList);
        mViewPager.setAdapter(adapter);

        int from = getIntent().getIntExtra("from", 0);

        switch (from) {
            case 1:
                mViewPager.setCurrentItem(1);
                break;
        }

    }

//    private void reInit() {
//
//        StaticUtil.isServiceRunning = true;
//        if (StaticUtil.mediaPlayer.isPlaying()) {
//            StaticUtil.isplay = true;
//            playBtn.setImageResource(R.drawable.playbar_btn_pause);
//        }
//        bindService(MusicUtil.intent, StaticUtil.conn, Context.BIND_AUTO_CREATE);
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_list:

                MusicDetailFragment fragment = new MusicDetailFragment();
                fragment.show(getSupportFragmentManager(), "list");
                break;
            case R.id.back:
                finish();
                break;
            case R.id.search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.control:
                int position = StaticUtil.sharedPreferences.getInt("position", -1);

                if (StaticUtil.isServiceRunning) {//服务启动着，这里点击播放暂停按钮时只需要当前音乐暂停或者播放就好
                    if (StaticUtil.isplay) {
                        playBtn.setImageResource(R.drawable.playbar_btn_play);
                        MusicUtil.pause(this);
                    } else {
                        //暂停--->继续播放
                        playBtn.setImageResource(R.drawable.playbar_btn_pause);
                        MusicUtil.player(this, "2");
                    }
                } else {
                    if (StaticUtil.isplay) {
                        playBtn.setImageResource(R.drawable.playbar_btn_play);
                        MusicUtil.pause(this);
                    } else {
                        if (StaticUtil.currentPosition == -1 && position == -1) {
                            Toast.makeText(this, "请选择要播放的音乐", Toast.LENGTH_SHORT).show();
                        } else if (position != -1) {
                            playBtn.setImageResource(R.drawable.playbar_btn_pause);
                            MusicUtil.player(this, position, songList);
                        } else {
                            //暂停--->继续播放
                            playBtn.setImageResource(R.drawable.playbar_btn_pause);
                            MusicUtil.player(this, "2");

                        }
                    }
                }
                break;
            case R.id.play_next:

                if (StaticUtil.currentPosition < currentSongList.size() - 1) {
                    StaticUtil.currentPosition += 1;
                    songName.setText(currentSongList.get(StaticUtil.currentPosition).getMusicName());
                    singer.setText(currentSongList.get(StaticUtil.currentPosition).getSinger());
                    playBtn.setImageResource(R.drawable.playbar_btn_pause);
                    MusicUtil.player(this, currentSongList);
                } else {
                    Toast.makeText(this, "已经是最后一首音乐了", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.playBar_img:
                intent = new Intent(this, PlayingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            default:
                break;

        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        songList.clear();
        unregisterReceiver(playNewReceiver);
        super.onDestroy();
    }

    public void registerBroadcastReceiver() {

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(PLAY_NEW_BROADCAST);
        registerReceiver(playNewReceiver, myIntentFilter);
    }
}
