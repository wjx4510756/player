package activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusicplayer.R;

import java.util.ArrayList;
import java.util.List;

import adapter.MainAdapter;
import adapter.MainItem;
import db.LocalMusicDB;
import db.MusicDetailInfo;
import fragment.MusicDetailFragment;
import fragment.TimingFragment;
import utils.MusicUtil;
import utils.StaticUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView playBarBtn;
    private ImageView menuImage;
    private ImageView searchImage;
    private ImageView listBtn;
    private ImageView playBtn;
    private ImageView next;

    private TextView songName;
    private TextView singer;

    private LocalMusicDB db;

    private DrawerLayout drawerLayout;

    private ListView mListView;
    private ListView leftMenuList;

    private List<MainItem> mList = new ArrayList<>();
    private List<MainItem> mMenuList = new ArrayList<>();
    private Intent intent;



    private List<String> currentSongNameList = new ArrayList<>();

    private  List<MusicDetailInfo> currentSongList = new ArrayList<>();


    private MainAdapter adapter;

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
    protected void onResume() {
        super.onResume();

        mList.get(1).mNumber = "(" + db.queryRecentCount() + ")";
        adapter.notifyDataSetChanged();

        songName.setText(StaticUtil.sharedPreferences.getString("songName", ""));
        singer.setText(StaticUtil.sharedPreferences.getString("singer", ""));
        StaticUtil.currentPosition = StaticUtil.sharedPreferences.getInt("position",-1);

        if (StaticUtil.isplay) {
            playBtn.setImageResource(R.drawable.playbar_btn_pause);
        } else
            playBtn.setImageResource(R.drawable.playbar_btn_play);

        currentSongNameList = db.queryCurrentList();

        currentSongList.clear();

        for (String songName : currentSongNameList
                ) {
            currentSongList.add(db.querySongsBySongName(songName));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        registerBroadcastReceiver();

        init();

        if (StaticUtil.mediaPlayer == null){
            StaticUtil.mediaPlayer = new MediaPlayer();
        }

        StaticUtil.sharedPreferences = getSharedPreferences("current_music", Context.MODE_PRIVATE);
        StaticUtil.editor = StaticUtil.sharedPreferences.edit();

        db = LocalMusicDB.getInstance(this);

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        db.deleteAllSongs();
        db.saveMusicDetail(MusicUtil.getAllSongs(this));

        MainItem item = new MainItem(R.drawable.music_icn_local, getString(R.string.local_music), "(" + db.queryCount() + ")");
        mList.add(item);
        item = new MainItem(R.drawable.music_icn_recent, getString(R.string.recent_music), "(" + db.queryRecentCount() + ")");
        mList.add(item);
        item = new MainItem(R.drawable.music_icn_dld, getString(R.string.dld), "(0)");
        mList.add(item);
        item = new MainItem(R.drawable.music_icn_artist, getString(R.string.artist), "(0)");
        mList.add(item);

        adapter = new MainAdapter(this, mList);
        mListView.setAdapter(adapter);

        item = new MainItem(R.drawable.leftmenu_icn_night, getString(R.string.night), "");
        mMenuList.add(item);
        item = new MainItem(R.drawable.leftmenu_icn_skin, getString(R.string.skin), "");
        mMenuList.add(item);
        item = new MainItem(R.drawable.leftmenu_icn_time, getString(R.string.time), "");
        mMenuList.add(item);
        item = new MainItem(R.drawable.leftmenu_icn_exit, getString(R.string.exit), "");
        mMenuList.add(item);

        MainAdapter adapter1 = new MainAdapter(this, mMenuList);
        leftMenuList.setAdapter(adapter1);


        LayoutInflater inflater = LayoutInflater.from(this);
        leftMenuList.addHeaderView(inflater.inflate(R.layout.navigation_view, leftMenuList, false));
        leftMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        Toast.makeText(MainActivity.this, "模式切换", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case 2:
                        DialogFragment fragment1 = new DialogFragment();
                        fragment1.show(getSupportFragmentManager(), "skining");
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case 3:
                        TimingFragment fragment2 = new TimingFragment();
                        fragment2.show(getSupportFragmentManager(), "timing");
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case 4:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });


    }

    private void init() {


        drawerLayout = (DrawerLayout) findViewById(R.id.dl);

        playBarBtn = (ImageView) findViewById(R.id.playBar_img);
        mListView = (ListView) findViewById(R.id.main_list);
        menuImage = (ImageView) findViewById(R.id.menu);
        searchImage = (ImageView) findViewById(R.id.search);
        listBtn = (ImageView) findViewById(R.id.play_list);
        playBtn = (ImageView) findViewById(R.id.control);
        next = (ImageView) findViewById(R.id.play_next);

        songName = (TextView) findViewById(R.id.song_name);
        singer = (TextView) findViewById(R.id.singer_name);

        leftMenuList = (ListView) findViewById(R.id.left_menu);


        playBarBtn.setOnClickListener(this);
        menuImage.setOnClickListener(this);
        searchImage.setOnClickListener(this);
        listBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        next.setOnClickListener(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0:
                        intent = new Intent(MainActivity.this, LocalMusicActivity.class);
                        intent.putExtra("from", 0);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, MusicActivity.class);
                        intent.putExtra("to", "recent");
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "下载管理", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, LocalMusicActivity.class);
                        intent.putExtra("from", 1);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    default:
                        break;


                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.playBar_img:
                intent = new Intent(this, PlayingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.menu:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.play_list:
                MusicDetailFragment fragment = new MusicDetailFragment();
                fragment.show(getSupportFragmentManager(), "list");
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
                            MusicUtil.player(this, position, currentSongList);
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
        super.onDestroy();
        unregisterReceiver(playNewReceiver);
    }

    /**
     * 动态注册广播
     */
    public void registerBroadcastReceiver() {

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(PLAY_NEW_BROADCAST);
        registerReceiver(playNewReceiver, myIntentFilter);
    }
}
