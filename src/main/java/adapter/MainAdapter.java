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
 * Created by wjx4510756 on 2017/4/9.
 */
public class MainAdapter extends BaseAdapter {

    private List<MainItem> mList;
    private LayoutInflater mInflater;

    public MainAdapter(Context context, List<MainItem> List) {

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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.main_item, null);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.text = (TextView) convertView.findViewById(R.id.title);
            viewHolder.number = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        MainItem item = mList.get(position);
        viewHolder.image.setImageResource(item.mIcon);
        viewHolder.text.setText(item.mText);
        viewHolder.number.setText(item.mNumber);

        return convertView;
    }


    class ViewHolder {

        public ImageView image;
        public TextView text;
        public TextView number;

    }
}
