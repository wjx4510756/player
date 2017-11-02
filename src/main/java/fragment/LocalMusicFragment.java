package fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusicplayer.R;

import activity.LocalMusicActivity;
import activity.MainActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapter.LocalMusicAdapter;
import adapter.LocalMusicItem;
import db.LocalMusicDB;
import db.MusicDetailInfo;
import service.MusicPlayerService;
import utils.MusicUtil;
import utils.StaticUtil;


/**
 * Created by wjx4510756 on 2017/4/10.
 */
public class LocalMusicFragment extends Fragment {


    private List<LocalMusicItem> mList = new ArrayList<>();
    private List<MusicDetailInfo> list = new ArrayList<>();
    private ImageView playBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_local, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listView = (ListView) getView().findViewById(R.id.local_music);

        final Bundle bundle = getArguments();

        list = (List<MusicDetailInfo>) bundle.getSerializable("local_music");

        playBtn = (ImageView) getActivity().findViewById(R.id.control);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView songName = (TextView) getActivity().findViewById(R.id.song_name);
                songName.setText(list.get(position).getMusicName());
                TextView singerName = (TextView) getActivity().findViewById(R.id.singer_name);
                singerName.setText(list.get(position).getSinger());
                playBtn.setImageResource(R.drawable.playbar_btn_pause);
                MusicUtil.player(getActivity(), position, list);
            }
        });

        if (list == null) {

        }
         else {
            for (MusicDetailInfo info :
                    list) {
                LocalMusicItem item = new LocalMusicItem();
                item.setMoreImg(R.drawable.list_icn_more);
                item.setSongName(info.getMusicName());
                item.setSinger(info.getSinger());
                mList.add(item);
            }
        }
        LocalMusicAdapter adapter = new LocalMusicAdapter(getActivity(), mList);
        listView.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        mList.clear();
        super.onDestroyView();
    }

}
