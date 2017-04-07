package com.spudapps.rethy.smartyfloaty;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spudapps.rethy.smartyfloaty.Services.SmartyFloatyAccessibilityService;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Button enabledButton;
    private Button startButton;
    private Button disableButton;
    private Button stopButton;
    private AlertDialog accessibilityDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtons();

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage("It is good practice to explain to the user why you need " +
                                "the Accessibility permission and how it is used to automatically " +
                                "disable the view to avoid the \"Screen Overlay Detected\" popup")
                                .setTitle("Auto-Disable");
                        builder.setPositiveButton("Auto-disable", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                startActivityForResult(intent,
                                        SmartyFloatyAccessibilityService.ACCESSIBILITY_REQUEST_CODE);
                            }
                        });
                        builder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        accessibilityDialog = builder.create();
                    }
                    accessibilityDialog.show();
                } else {
                    // Overlay permission
                }
            }
        });

        this.startButton = (Button) findViewById(R.id.start_button);
        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Start button clicked");
                // Start the service
            }
        });

        this.disableButton = (Button) findViewById(R.id.disable_button);
        this.disableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Disable button clicked");
                // Send the user to disable Accessibility
                // Send the user to disable overlay
            }
        });

        this.stopButton = (Button) findViewById(R.id.stop_button);
        this.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Stop button clicked");
                // Stop the service
            }
        });
    }
}
