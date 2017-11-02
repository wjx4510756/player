package utils;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.example.mymusicplayer.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import db.LocalMusicDB;
import db.MusicDetailInfo;

/**
 * Created by wjx4510756 on 2017/4/11.
 */
public class MusicUtil {

    /**
     * 获取sd卡所有的音乐文件
     */

    public static Intent intent = null;

    public static List<MusicDetailInfo> getAllSongs(Context context) {

        List<MusicDetailInfo> list = new ArrayList<>();

        StringBuilder select = new StringBuilder(" 1=1 and title != ''");
        // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
        select.append(" and " + MediaStore.Audio.Media.SIZE + " > " + 1 * 1024 * 1024);
        select.append(" and " + MediaStore.Audio.Media.DURATION + " > " + 1 * 60 * 1000);

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA}, select.toString(), null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);


        if (cursor.moveToFirst()) {

            do {
                MusicDetailInfo song = new MusicDetailInfo();
                // 文件名
                song.setFileName(cursor.getString(1));
                // 歌曲名
                song.setMusicName(cursor.getString(2));
                // 时长
                song.setDuration(cursor.getInt(3));
                // 歌手名
                song.setSinger(cursor.getString(4));
                // 专辑名
                song.setAlbum(cursor.getString(5));
                // 歌曲格式
                if ("audio/mpeg".equals(cursor.getString(6).trim())) {
                    song.setType("mp3");
                } else if ("audio/x-ms-wma".equals(cursor.getString(6).trim())) {
                    song.setType("wma");
                } else
                    song.setType("先放着啊");
                // 文件大小
                if (cursor.getString(7) != null) {
                    float size = cursor.getInt(7) / 1024f / 1024f;
                    song.setSize((size + "").substring(0, 4) + "M");
                } else {
                    song.setSize("未知");
                }
                // 文件路径
                if (cursor.getString(8) != null) {
                    song.setFileUrl(cursor.getString(8));
                }

                list.add(song);

            } while (cursor.moveToNext());

        }
        if (cursor != null)
            cursor.close();

        return list;
    }


    public static void player(Context context, List<MusicDetailInfo> songList) {
        player(context, StaticUtil.currentPosition, songList);
    }


    public static void player(Context context, int position, List<MusicDetailInfo> songList) {
        intent = new Intent();
        intent.setAction("player");
        intent.setPackage(context.getPackageName());
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) songList);
        intent.putExtra("list", bundle);
        intent.putExtra("curPosition", position);  //把位置传回去，方便再启动时调用
        intent.putExtra("url", songList.get(position).getFileUrl());
        intent.putExtra("MSG", "0");

        StaticUtil.isplay = true;
        StaticUtil.isServiceRunning = true;
        StaticUtil.currentPosition = position;

        StaticUtil.editor.putString("songName", songList.get(position).getMusicName());
        StaticUtil.editor.putString("singer", songList.get(position).getSinger());
        StaticUtil.editor.putInt("position", position);
        StaticUtil.editor.commit();

        LocalMusicDB db = LocalMusicDB.getInstance(context);
        try {
            db.deleteRecentSong(songList.get(position).getMusicName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.saveRecentMusic(songList.get(position).getMusicName(), songList.get(position).getSinger(),
                songList.get(position).getFileUrl());

        db.deleteCurrentList();
        db.saveCurrentList(songList);

        context.startService(intent);

    }


    public static void player(Context context, String info) {

        intent.putExtra("MSG", info);
        StaticUtil.isplay = true;

        context.startService(intent);

    }

    /*
   * MSG :
   *  0  未播放--->播放
   *  1    播放--->暂停
   *  2    暂停--->继续播放
   *
   * */
    public static void pause(Context context) {
        intent.putExtra("MSG", "1");
        StaticUtil.isplay = false;
        context.startService(intent);
    }


    public static int[] timeConvert(int duration) {

        int times[] = new int[2];

        duration = duration / 1000;
        times[0] = duration / 60;
        times[1] = duration % 60;

        return times;
    }


}
