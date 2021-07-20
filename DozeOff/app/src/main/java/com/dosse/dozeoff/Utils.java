package com.dosse.dozeoff;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class Utils {
    /**
     * Check if the phone is rooted
     * @return true if su binary is present, false otherwise
     */
    public static boolean isRooted(){
        return rootCheck1()||rootCheck2();
    }

    private static boolean rootCheck1(){
        for (String s : System.getenv("PATH").split(System.getProperty("path.separator"))) {
            if ( new File( s + (s.endsWith("/")?"":"/")+"su" ).exists() ) {
                return true;
            }
        }
        return false;
    }

    private static boolean rootCheck2(){
        for (String s : new String[]{"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"}) {
            if (new File(s + "su").exists()) {
                return true;
            }
        }
        return false;
    }

    public static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences("DozeOff",Context.MODE_PRIVATE);
    }

}
