package com.dosse.dozeoff;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Utils.isRooted()){
            ((TextView)findViewById(R.id.main_text)).setText(R.string.main_description);
            findViewById(R.id.main_hideBtn).setEnabled(true);
            findViewById(R.id.main_hideBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.hide_dialog);
                    builder.setMessage(R.string.hide_dialog_desc);
                    DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==DialogInterface.BUTTON_POSITIVE){	//delete confirmed
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
            DozeKiller.killDoze(getApplicationContext());
        }else{
            ((TextView)findViewById(R.id.main_text)).setText(R.string.main_notRooted);
            findViewById(R.id.main_hideBtn).setEnabled(false);
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
}
