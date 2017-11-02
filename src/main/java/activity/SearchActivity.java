package activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.mymusicplayer.R;

import java.util.ArrayList;
import java.util.List;

import adapter.LocalMusicAdapter;
import adapter.LocalMusicItem;
import adapter.MusicListAdapter;
import adapter.MusicListItem;
import db.LocalMusicDB;
import db.MusicDetailInfo;

/**
 * Created by wjx4510756 on 2017/4/9.
 */
public class SearchActivity extends AppCompatActivity {

    private ImageView searchBack;
    private SearchView mSearchView;

    private ListView mSearchList;
    private LocalMusicDB db;

    private List<MusicDetailInfo> list;
    private List<LocalMusicItem> mList;
    private LocalMusicAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        searchBack = (ImageView) findViewById(R.id.search_back);
        mSearchView = (SearchView) findViewById(R.id.search_2);
        mSearchList = (ListView) findViewById(R.id.search_list);

        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                db = LocalMusicDB.getInstance(SearchActivity.this);
                list= db.queryFromSearch(query);
                mList = new ArrayList<>();
                if (list == null) {
                    Toast.makeText(SearchActivity.this,"没有搜到",Toast.LENGTH_SHORT).show();
                } else {
                    for (MusicDetailInfo info :
                            list) {
                        LocalMusicItem item = new LocalMusicItem();
                        item.setMoreImg(R.drawable.list_icn_more);
                        item.setSongName(info.getMusicName());
                        item.setSinger(info.getSinger());
                        mList.add(item);
                    }
                }
                adapter = new LocalMusicAdapter(SearchActivity.this, mList);
                mSearchList.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

    }

}
