package com.dosse.dozeoff;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Utils.isRooted()){
            findViewById(R.id.main_text).setVisibility(View.INVISIBLE);
            findViewById(R.id.settingsArea).setVisibility(View.VISIBLE);
            SharedPreferences prefs=Utils.getPreferences(getApplicationContext());
            ((CheckBox)findViewById(R.id.doze_off)).setChecked(prefs.getBoolean("doze_off",true));
            ((CheckBox)findViewById(R.id.wakelock_cpu)).setChecked(prefs.getBoolean("wakelock_cpu",false));
            ((CheckBox)findViewById(R.id.wakelock_wifi)).setChecked(prefs.getBoolean("wakelock_wifi",false));
            findViewById(R.id.doze_off).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("doze_off",((CheckBox)findViewById(R.id.doze_off)).isChecked());
                    applySettings();
                }
            });
            findViewById(R.id.wakelock_cpu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("wakelock_cpu",((CheckBox)findViewById(R.id.wakelock_cpu)).isChecked());
                    applySettings();
                }
            });
            findViewById(R.id.wakelock_wifi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("wakelock_wifi",((CheckBox)findViewById(R.id.wakelock_wifi)).isChecked());
                    applySettings();
                }
            });
            findViewById(R.id.main_hideBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.hide_dialog);
                    builder.setMessage(R.string.hide_dialog_desc);
                    DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==DialogInterface.BUTTON_POSITIVE){	//hide confirmed
                                PackageManager p = getApplicationContext().getPackageManager();
                                p.setComponentEnabledSetting(new ComponentName(getApplicationContext(), MainActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                                Toast.makeText(getApplicationContext(), getString(R.string.hide_clicked), Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    };
                    builder.setPositiveButton(getString(R.string.yes), l);
                    builder.setNegativeButton(getString(R.string.no), l);
                    builder.show();
                }
            });
            DozeKiller.apply(getApplicationContext());
        }else{
            ((TextView)findViewById(R.id.main_text)).setText(R.string.main_notRooted);
            findViewById(R.id.main_text).setVisibility(View.VISIBLE);
            findViewById(R.id.settingsArea).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.main_dev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.website)));
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        onCreate(null);
    }

    private void applySettings(){
        findViewById(R.id.doze_off).setEnabled(false);
        findViewById(R.id.wakelock_cpu).setEnabled(false);
        findViewById(R.id.wakelock_wifi).setEnabled(false);
        long ts=System.currentTimeMillis();
        DozeKiller.apply(getApplicationContext());
        long msToWait=300-(System.currentTimeMillis()-ts);
        try {
            if(msToWait>0) Thread.sleep(msToWait); //prevent the user from changing the settings too quickly
        }catch (Throwable t){
        }
        findViewById(R.id.doze_off).setEnabled(true);
        findViewById(R.id.wakelock_cpu).setEnabled(true);
        findViewById(R.id.wakelock_wifi).setEnabled(true);
    }

}
