package utils;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;

/**
 * Created by wjx4510756 on 2017/4/25.
 */
public class StaticUtil {

    public static int currentPosition = -1;  //当前播放列表里哪首音乐
    public static boolean isplay = false;  //音乐是否在播放

    public static MediaPlayer mediaPlayer = null;
    public static Handler handler = null;
    public static boolean isServiceRunning = false;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;



}
