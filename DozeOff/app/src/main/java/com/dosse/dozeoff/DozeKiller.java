package com.dosse.dozeoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.OutputStream;

public class DozeKiller extends BroadcastReceiver {

    private static PowerManager.WakeLock cpuLock=null;
    private static WifiManager.WifiLock wifiLock=null;

    public static void apply(Context context) {
        try {
            if (!Utils.isRooted()) throw new Exception(context.getString(R.string.notRooted));
            SharedPreferences prefs=Utils.getPreferences(context);
            Process p = Runtime.getRuntime().exec("su"); //open elevated shell
            OutputStream os = p.getOutputStream();
            if(prefs.getBoolean("doze_off",true)){
                os.write(("dumpsys deviceidle disable\n").getBytes("ASCII")); //disable doze
                Log.d("DozeOff","Doze disabled");
            }else{
                os.write(("dumpsys deviceidle enable\n").getBytes("ASCII")); //enable doze
                Log.d("DozeOff","Doze enabled");
            }
            os.flush();
            os.write("exit\n".getBytes("ASCII"));
            os.flush();
            os.close(); //close elevated shell
            p.waitFor(); //wait for it to actually terminate
            try {
                //acquire a CPU wakelock
                if(cpuLock!=null&&cpuLock.isHeld()){ //release preexisting wakelock if present
                    cpuLock.release();
                    cpuLock=null;
                }
                if(prefs.getBoolean("wakelock_cpu",false)) { //acquire CPU wakelock if requested
                    cpuLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DozeOff::CPULock");
                    cpuLock.acquire();
                }
            }catch (Throwable t){
                Log.d("DozeOff","CPU wakelock error");
            }
            try {
                //acquire wifi wakelock
                if(wifiLock!=null&&wifiLock.isHeld()){ //release preexisting wakelock if present
                    wifiLock.release();
                    wifiLock=null;
                }
                if(prefs.getBoolean("wakelock_wifi",false)) { //acquire wifi wakelock if requested
                    wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "DozeOff::WifiLock");
                    wifiLock.acquire();
                }
            }catch(Throwable t){
                Log.d("DozeOff","Wifi wakelock error");
            }
            Log.d("DozeOff","Settings applied");
        } catch (Throwable t) {
            Toast.makeText(context, context.getString(R.string.error) + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        apply(context);
    }
}