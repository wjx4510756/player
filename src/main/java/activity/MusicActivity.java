package activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusicplayer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adapter.LocalMusicAdapter;
import adapter.LocalMusicItem;
import db.LocalMusicDB;
import db.MusicDetailInfo;
import fragment.MusicDetailFragment;
import utils.MusicUtil;
import utils.StaticUtil;

/**
 * Created by wjx4510756 on 2017/4/19.
 */
public class MusicActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView backImg;
    private TextView title;
    private ListView mListView;

    private ImageView playBarBtn;
    private ImageView listBtn;
    private ImageView playBtn;
    private ImageView next;


    private TextView songName;
    private TextView singer;


    private String to;

    private LocalMusicAdapter adapter;

    private List<MusicDetailInfo> songList = new ArrayList<>();

    private List<LocalMusicItem> list = new ArrayList<>();

    private List<String> currentSongNameList = new ArrayList<>();

    private List<MusicDetailInfo> currentSongList = new ArrayList<>();

    private LocalMusicDB db;


    private static final String PLAY_NEW_BROADCAST = "com.example.myMusicPlayer.playNew";

    /**
     * 内部类广播，接收自动换歌之后的发出的广播
     */
    private BroadcastReceiver playNewReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(PLAY_NEW_BROADCAST)){

                playBtn.setImageResource(R.drawable.playbar_btn_pause);
                songName.setText(currentSongList.get(StaticUtil.currentPosition).getMusicName());
                singer.setText(currentSongList.get(StaticUtil.currentPosition).getSinger());

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_play);

        registerBroadcastReceiver();

        backImg = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        mListView = (ListView) findViewById(R.id.recent_list);

        playBarBtn = (ImageView) findViewById(R.id.playBar_img);
        listBtn = (ImageView) findViewById(R.id.play_list);
        playBtn = (ImageView) findViewById(R.id.control);
        next = (ImageView) findViewById(R.id.play_next);

        songName = (TextView) findViewById(R.id.song_name);
        singer = (TextView) findViewById(R.id.singer_name);

        to = getIntent().getStringExtra("to");

        playBtn.setOnClickListener(this);
        next.setOnClickListener(this);
        playBarBtn.setOnClickListener(this);
        listBtn.setOnClickListener(this);

        songName.setText(StaticUtil.sharedPreferences.getString("songName", ""));
        singer.setText(StaticUtil.sharedPreferences.getString("singer", ""));

        if (StaticUtil.isplay) {
            playBtn.setImageResource(R.drawable.playbar_btn_pause);
        } else
            playBtn.setImageResource(R.drawable.playbar_btn_play);


        backImg.setOnClickListener(this);

        db = LocalMusicDB.getInstance(this);

        currentSongNameList = db.queryCurrentList();

        for (String songName:currentSongNameList
                ) {
            currentSongList.add(db.querySongsBySongName(songName));
        }

        switch (to) {
            case "recent":
                list = db.queryRecentMusic();
                Collections.reverse(list);
                adapter = new LocalMusicAdapter(this, list);
                mListView.setAdapter(adapter);
                break;
            case "singer":
                String s = getIntent().getStringExtra("singer");
                title.setText(s);
                songList = db.querySongsBySinger(s);
                initListView();
                break;
            case "album":
                String a = getIntent().getStringExtra("album");
                title.setText(a);
                songList = db.querySongsByAlbum(a);
                initListView();
                break;
            case "file":
                String f = getIntent().getStringExtra("file");
                title.setText(f);
                songList = db.querySongsByFile(f);
                initListView();
                break;
            default:
                break;
        }


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (to) {
                    case "recent":
                        songName.setText(list.get(position).getSongName());
                        singer.setText(list.get(position).getSinger());
                        for (LocalMusicItem item : list) {
                            songList.add(db.querySongsBySongName(item.getSongName()));
                        }
                        currentSongList = songList;
                        playBtn.setImageResource(R.drawable.playbar_btn_pause);
                        MusicUtil.player(MusicActivity.this, position, songList);
                        break;
                    case "singer":
                    case "album":
                    case "file":
                        songName.setText(list.get(position).getSongName());
                        singer.setText(list.get(position).getSinger());
                        currentSongList = songList;
                        playBtn.setImageResource(R.drawable.playbar_btn_pause);
                        MusicUtil.player(MusicActivity.this, position, songList);
                        break;

                }


            }
        });

    }

    public  void initListView() {

        for (MusicDetailInfo info : songList) {
            LocalMusicItem item = new LocalMusicItem();
            item.setSongName(info.getMusicName());
            item.setSinger(info.getSinger());
            item.setMoreImg(R.drawable.play_icn_more);
            list.add(item);
        }
        adapter = new LocalMusicAdapter(this, list);
        mListView.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back:
                finish();
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
                Intent intent = new Intent(this, PlayingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.play_list:

                MusicDetailFragment fragment = new MusicDetailFragment();
                fragment.show(getSupportFragmentManager(),"list");
                break;
            default:
                break;


        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(playNewReceiver);
        super.onDestroy();
    }

    public void registerBroadcastReceiver() {

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(PLAY_NEW_BROADCAST);
        registerReceiver(playNewReceiver, myIntentFilter);
    }

}
