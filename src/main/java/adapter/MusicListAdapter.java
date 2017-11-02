package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mymusicplayer.R;

import java.util.List;

/**
 * Created by wjx4510756 on 2017/5/4.
 */

public class MusicListAdapter extends BaseAdapter {


    private List<MusicListItem> mList;
    private LayoutInflater mInflater;

    public MusicListAdapter(Context context, List<MusicListItem> List) {

        mList = List;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MusicListAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new MusicListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.music_list_item, null);
            viewHolder.songName = (TextView) convertView.findViewById(R.id.local_list_music);
            viewHolder.singer = (TextView) convertView.findViewById(R.id.local_list_singer);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (MusicListAdapter.ViewHolder) convertView.getTag();

        MusicListItem item = mList.get(position);
        viewHolder.songName.setText(item.songName);
        viewHolder.singer.setText(item.singer);

        return convertView;
    }


    class ViewHolder {
        public TextView songName;
        public TextView singer;
    }
}
