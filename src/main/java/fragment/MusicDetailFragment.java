package fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusicplayer.R;

import java.util.ArrayList;
import java.util.List;

import adapter.MusicListAdapter;
import adapter.MusicListItem;
import db.LocalMusicDB;
import db.MusicDetailInfo;
import utils.MusicUtil;

/**
 * Created by wjx4510756 on 2017/5/4.
 */

public class MusicDetailFragment extends DialogFragment {

    private ListView listView;

    private List<MusicDetailInfo> list = new ArrayList<>();
    private List<String> songNameList = new ArrayList<>();
    private List<MusicListItem> mList = new ArrayList<>();

    private String currentActivity = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_local, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentActivity = getActivity().getClass().toString();

        //设置从底部弹出
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;


        listView = (ListView) getView().findViewById(R.id.local_music);

        LocalMusicDB db = LocalMusicDB.getInstance(getActivity());
        songNameList = db.queryCurrentList();

        for (String s : songNameList
                ) {
            list.add(db.querySongsBySongName(s));
        }

        if (list == null) {

        } else {
            for (MusicDetailInfo info :
                    list) {
                MusicListItem item = new MusicListItem();
                item.setSongName(info.getMusicName());
                item.setSinger(info.getSinger());
                mList.add(item);
            }
        }
        MusicListAdapter adapter = new MusicListAdapter(getActivity(), mList);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView songName = (TextView) getActivity().findViewById(R.id.song_name);
                songName.setText(list.get(position).getMusicName());
                TextView singerName = (TextView) getActivity().findViewById(R.id.singer_name);
                singerName.setText(list.get(position).getSinger());
//
//                ImageView playBtn;

//                if (currentActivity.equals("class activity.PlayingActivity")) {
//                    playBtn = (ImageView) getActivity().findViewById(R.id.playing_play);
//                    playBtn.setImageResource(R.drawable.play_rdi_btn_pause);
//                } else {
//
//                    playBtn = (ImageView) getActivity().findViewById(R.id.control);
//                    playBtn.setImageResource(R.drawable.playbar_btn_pause);
//                }

                Intent intent = new Intent();
                intent.setAction("com.example.myMusicPlayer.playNew");
                getActivity().sendBroadcast(intent);

                MusicUtil.player(getActivity(), position, list);
                dismiss();
            }
        });

    }

    public void onStart() {

        super.onStart();
        //设置fragment高度 、宽度
        int dialogHeight = (int) (getActivity().getResources().getDisplayMetrics().heightPixels * 0.5);
        int dialogWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置样式
        setStyle(android.app.DialogFragment.STYLE_NO_FRAME, R.style.CustomDatePickerDialog);
    }
}
