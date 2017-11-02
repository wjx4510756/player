package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mymusicplayer.R;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import activity.MusicActivity;
import adapter.LocalMusicAdapter;
import adapter.LocalMusicItem;
import db.MusicDetailInfo;

/**
 * Created by wjx4510756 on 2017/4/11.
 */
public class LocalMusicFragmentFile extends Fragment {

    private List<LocalMusicItem> mList = new ArrayList<>();
    private List<MusicDetailInfo> list = new ArrayList<>();
    private List<String> fileList = new ArrayList<>();

    Map<String,Integer> map = new HashMap<>();

    Set<String> set = new HashSet<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = (ListView) getView().findViewById(R.id.local_music);

        Bundle bundle = getArguments();

        list = (List<MusicDetailInfo>) bundle.getSerializable("file");

        for (MusicDetailInfo info : list) {
            String strings[] = info.getFileUrl().split("/");
            String file = strings[strings.length-3];
            if (set.add(file))
            {
                map.put(file,1);
                fileList.add(file);
            }else
                map.put(file,map.get(file)+1);
        }
        Collections.sort(fileList);

        if (list == null) {

        } else {
            for (String info :
                    fileList) {
                LocalMusicItem item = new LocalMusicItem();
                item.setMoreImg(R.drawable.list_icn_more);
                item.setSinger(map.get(info)+"首");
                item.setSongName(info);
                mList.add(item);
            }
            LocalMusicAdapter adapter = new LocalMusicAdapter(getActivity(), mList);
            listView.setAdapter(adapter);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String file = mList.get(position).getSongName();
                Intent intent = new Intent(getActivity(), MusicActivity.class);
                intent.putExtra("to","file");
                intent.putExtra("file",file);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroyView() {
        mList.clear();
        set.clear();
        fileList.clear();
        super.onDestroyView();
    }
}
