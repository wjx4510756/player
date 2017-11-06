package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wjx4510756 on 2017/4/11.
 */
public class LocalMusicDBOpenHelper extends SQLiteOpenHelper {

    public static final String MUSIC_DETAIL = "create table if not exists Local_Music("
            + "music_name text not null primary key,"
            + "file_name text not null,"
            + "duration integer not null,"
            + "singer text not null,"
            + "album text not null,"
            + "type text not null,"
            + "size text not null,"
            + "file_url text not null)";

    public static final String RECENT_PLAY = "create table if not exists Recent_Music(" +
            "song_name text not null primary key," +
            "singer text not null," +
            "file_url text not null)";

    public static final String CURRENT_LIST = "create table if not exists Current_List(" +
            "song_name text not null primary key)";



    public LocalMusicDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(MUSIC_DETAIL);
        db.execSQL(RECENT_PLAY);
        db.execSQL(CURRENT_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
