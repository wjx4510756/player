package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.mymusicplayer.R;

import java.util.ArrayList;
import java.util.List;

import adapter.LocalMusicItem;

/**
 * Created by wjx4510756 on 2017/4/11.
 */
public class LocalMusicDB {

    /*
    *数据库基础信息
     */

    public static final String DB_NAME = "local_music.db";
    public static final int VERSION = 1;
    public static final String TABLE_NAME = "Local_Music";


    private static LocalMusicDB localMusicDB;
    private SQLiteDatabase db;

    /*
    私有化构造方法
     */

    private LocalMusicDB(Context context) {

        LocalMusicDBOpenHelper dbHelper = new LocalMusicDBOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /*
    获取CoolWeatherDB实例，保证唯一性
     */
    public synchronized static LocalMusicDB getInstance(Context context) {
        if (localMusicDB == null) {
            localMusicDB = new LocalMusicDB(context);
        }
        return localMusicDB;
    }

    public long queryCount() {
        String sql = "select count(*) from " + TABLE_NAME;
        SQLiteStatement statement = db.compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }

    public long queryRecentCount() {

        String sql = "select count(*) from Recent_Music";
        SQLiteStatement statement = db.compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }

    public void saveMusicDetail(List<MusicDetailInfo> list) {

        for (MusicDetailInfo info : list) {
            ContentValues values = new ContentValues();
            values.put("file_name", info.getFileName());
            values.put("music_name", info.getMusicName());
            values.put("duration", info.getDuration());
            values.put("singer", info.getSinger());
            values.put("album", info.getAlbum());
            values.put("type", info.getType());
            values.put("size", info.getSize());
            values.put("file_url", info.getFileUrl());

            db.insert(TABLE_NAME, null, values);
        }
    }


    public void deleteAllSongs() {

        db.delete(TABLE_NAME, null, null);
    }

    public void deleteRecentSong(String songName) {

        db.delete("Recent_Music", "song_name=?", new String[]{songName});
    }

    public void saveRecentMusic(String songName, String singer, String fileUrl) {

        ContentValues values = new ContentValues();
        values.put("song_name", songName);
        values.put("singer", singer);
        values.put("file_url", fileUrl);

        db.insert("Recent_Music", null, values);
    }

    public List<LocalMusicItem> queryRecentMusic() {

        List<LocalMusicItem> list = new ArrayList<>();

        Cursor cursor = db.query("Recent_Music", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {

            do {
                LocalMusicItem item = new LocalMusicItem();
                String songName = cursor.getString(cursor.getColumnIndex("song_name"));
                String singer = cursor.getString(cursor.getColumnIndex("singer"));
                item.setSongName(songName);
                item.setSinger(singer);
                item.setMoreImg(R.drawable.play_icn_more);
                list.add(item);

            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }


    public List<MusicDetailInfo> querySongs(String string, String s) {

        List<MusicDetailInfo> list = new ArrayList<>();
        Cursor cursor = null;
        switch (s) {

            case "singer":
                cursor = db.query(TABLE_NAME, null, "singer=?", new String[]{string}, null, null, null);
                break;
            case "all":
                cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
                break;
            case "songName":
                cursor = db.query(TABLE_NAME, null, "music_name=?", new String[]{string}, null, null, null);
                break;
            case "album":
                cursor = db.query(TABLE_NAME, null, "album=?", new String[]{string}, null, null, null);
                break;
            case "file":
                cursor = db.query(TABLE_NAME, null, "file_url like ?", new String[]{"%" + string + "%"}, null, null, null);
                break;
            case "search":
                cursor = db.query(TABLE_NAME, null, "music_name like ? or singer like ?", new String[]{"%" + string + "%","%" + string + "%"}, null, null, null);
                break;
            default:
                break;
        }

        if (cursor.moveToFirst()) {
            do {
                MusicDetailInfo info = new MusicDetailInfo();
                info.setFileName(cursor.getString(cursor.getColumnIndex("file_name")));
                info.setMusicName(cursor.getString(cursor.getColumnIndex("music_name")));
                info.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                info.setSinger(cursor.getString(cursor.getColumnIndex("singer")));
                info.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
                info.setType(cursor.getString(cursor.getColumnIndex("type")));
                info.setSize(cursor.getString(cursor.getColumnIndex("size")));
                info.setFileUrl(cursor.getString(cursor.getColumnIndex("file_url")));

                list.add(info);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return list;
    }

    public MusicDetailInfo querySongsBySongName(String songName) {

        return querySongs(songName, "songName").get(0);

    }

    public List<MusicDetailInfo> querySongsBySinger(String singer) {

        return querySongs(singer, "singer");

    }

    public List<MusicDetailInfo> querySongsByAlbum(String album) {

        return querySongs(album, "album");
    }

    public List<MusicDetailInfo> querySongsByFile(String file) {

        return querySongs(file, "file");
    }

    public List<MusicDetailInfo> queryAllMusic() {

        return querySongs("", "all");
    }





    public List<String> queryCurrentList() {

        List<String> list = new ArrayList<>();

        Cursor cursor = db.query("Current_List", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String songName = cursor.getString(cursor.getColumnIndex("song_name"));
                list.add(songName);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }


    public void saveCurrentList(List<MusicDetailInfo> list) {

        for (MusicDetailInfo info : list) {
            ContentValues values = new ContentValues();
            values.put("song_name", info.getMusicName());
            db.insert("Current_List", null, values);
        }
    }

    public void deleteCurrentList() {

        db.delete("Current_List", null, null);
    }


    public List<MusicDetailInfo> queryFromSearch(String text){

        return querySongs(text, "search");
    }

}
