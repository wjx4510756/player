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
 * Created by wjx4510756 on 2017/4/10.
 */
public class LocalMusicAdapter extends BaseAdapter {


    private List<LocalMusicItem> mList;
    private LayoutInflater mInflater;

    public LocalMusicAdapter(Context context, List<LocalMusicItem> List) {

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
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.local_music_normal_item, null);
            viewHolder.moreImg = (ImageView) convertView.findViewById(R.id.local_list_more);
            viewHolder.songName = (TextView) convertView.findViewById(R.id.local_list_music);
            viewHolder.singer = (TextView) convertView.findViewById(R.id.local_list_singer);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        LocalMusicItem item = mList.get(position);
        viewHolder.moreImg.setImageResource(item.moreImg);
        viewHolder.songName.setText(item.songName);
        viewHolder.singer.setText(item.singer);

        return convertView;
    }


    class ViewHolder {


        public TextView songName;
        public TextView singer;
        public ImageView moreImg;
    }
}
