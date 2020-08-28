package com.dosse.dozeoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.OutputStream;

public class DozeKiller extends BroadcastReceiver {
    public static void killDoze(Context context) {
        try {
            if (!Utils.isRooted()) throw new Exception(context.getString(R.string.notRooted));
            Process p = Runtime.getRuntime().exec("su"); //open elevated shell
            OutputStream os = p.getOutputStream();
            os.write(("dumpsys deviceidle disable\n").getBytes("ASCII")); //disable doze
            os.flush();
            os.write("exit\n".getBytes("ASCII"));
            os.flush();
            os.close(); //close elevated shell
            p.waitFor(); //wait for it to actually terminate
            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(context, context.getString(R.string.error) + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        killDoze(context);
    }
}