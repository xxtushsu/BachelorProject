package com.example.myapplication11;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleObserver;

import android.Manifest;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {
    private boolean isCoarsePermissionGranted = false; //foreground location

    AlertDialog.Builder builder;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    DatabaseReference databaseReference;

    TextView messageText,onlyThisTimeFormView, allowWhileUsingFormView, denyFormView;
    String username = generateRandomPassword(6); // the unique username generator

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageText = findViewById(R.id.messageText);

        requestPermission();
    }

    public static String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestPermission() {
        isCoarsePermissionGranted = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<String>();

        if (!isCoarsePermissionGranted) {
            permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!permissionRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionRequest.toArray(new String[0]), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> locationCheck = new ArrayList<String>();

        databaseReference = firebaseDatabase.getReference("Users").child(username);

        if (requestCode == 0 && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionRequest", permissions[i]);

                    if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        locationCheck.add(permissions[i]);
                    }
                }
            }
        }
        if (locationCheck.size() == 1 && locationCheck.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            builder = new AlertDialog.Builder(this);

            builder.setMessage("For clarity purpose, did you choose 'while using the app' (Click No if you chose 'Only this time')?");

            builder.setTitle("Message");

            builder.setCancelable(false);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    databaseReference.child("Location: While using the app").setValue("1");
                    dialogInterface.cancel();

                    allowWhileUsingFormView = findViewById(R.id.allowWhileUsingForm);
                    String allowWhileUsingUrl = "https://docs.google.com/forms/d/e/1FAIpQLSeoA1q-Rpz5qn5mCbH48YUqct6-2Yw7aFRC7jkwXRIyatv-HA/viewform?entry.1677387859="+username;
                    allowWhileUsingFormView.setText(allowWhileUsingUrl);

                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    databaseReference.child("Location: Only this time").setValue("1");
                    dialogInterface.cancel();

                    onlyThisTimeFormView = findViewById(R.id.onlyThisTimeForm);
                    String onlyThisTimeUrl = "https://docs.google.com/forms/d/e/1FAIpQLScSsQt8-WRV1O-VyzwNdNso0_hd0m7HaPKw9hqK3I65zLUSIw/viewform?entry.366340186=" + username;
                    onlyThisTimeFormView.setText(onlyThisTimeUrl);
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (locationCheck.isEmpty()) {
            databaseReference.child("Location: Deny").setValue("1");

            denyFormView = findViewById(R.id.denyForm);
            String denyUrl = "https://docs.google.com/forms/d/e/1FAIpQLSenuIGLFrzXLrFP5tlTwM9l8kS-mvg5sff5okyR87cr--C-BQ/viewform?entry.54317382=" + username;
            denyFormView.setText(denyUrl);

        } else {
            Log.d("LocationCheck", "onRequestPermissionsResult: Error Catch");
        }
    }

}