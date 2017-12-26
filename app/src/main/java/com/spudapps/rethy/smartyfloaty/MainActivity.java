package com.spudapps.rethy.smartyfloaty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.spudapps.rethy.smartyfloaty.Services.SmartyFloatyAccessibilityService;
import com.spudapps.rethy.smartyfloaty.Services.SmartyFloatyService;

public class MainActivity extends Activity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int OVERLAY_REQUEST_CODE = 67;

    private Button enabledButton;
    private Button startButton;
    private AlertDialog accessibilityDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtons();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_REQUEST_CODE && Settings.canDrawOverlays(MainActivity.this)) {
            requestOverlayPermission();
        }
    }

    private void setupButtons() {
        this.enabledButton = (Button) findViewById(R.id.enable_button);
        this.enabledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Enable button clicked");
                if (!Utility.isAccessibilityEnabled(getApplicationContext(),
                        SmartyFloatyAccessibilityService.ACCESSIBILITY_ID)) {
                    if (accessibilityDialog == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("It is good practice to explain to the user why you need " +
                                "the Accessibility permission and how it is used to automatically " +
                                "disable the view to avoid the \"Screen Overlay Detected\" popup")
                                .setTitle("Auto-Disable");
                        builder.setPositiveButton("enable", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                startActivityForResult(intent,
                                        SmartyFloatyAccessibilityService.ACCESSIBILITY_REQUEST_CODE);
                            }
                        });
                        builder.setNegativeButton("no thanks", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "SmartyFloaty is not enabled :(", Toast.LENGTH_LONG).show();
                            }
                        });
                        accessibilityDialog = builder.create();
                    }
                    accessibilityDialog.show();
                } else {
                    Toast.makeText(MainActivity.this, "Accessibility is already enabled! :)", Toast.LENGTH_LONG).show();
                }
            }
        });

        this.startButton = (Button) findViewById(R.id.start_button);
        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(MainActivity.this, SmartyFloatyService.class);
                    startService(intent);
                } else {
                    requestOverlayPermission();
                }
            }
        });
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" +getApplicationContext().getPackageName()));
        startActivityForResult(intent, OVERLAY_REQUEST_CODE);
    }
}
