package com.example.myapplication10;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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

public class MainActivity extends AppCompatActivity {

    private boolean isBackgroundLocationPermissionGranted = false;
    private boolean isLocationPermissionGranted = false; //foreground location

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    DatabaseReference databaseReference;

    TextView messageText, allowAllTheTimeFormView, allowWhileUsingFormView, denyFormView;
    String username = generateRandomPassword(6); // the unique username generator

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageText = findViewById(R.id.messageText);

        requestMultiPermission();
    }

    public static String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestMultiPermission() {
        isBackgroundLocationPermissionGranted = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        isLocationPermissionGranted = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<String>();

        if (!isBackgroundLocationPermissionGranted) {
            permissionRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        if (!isLocationPermissionGranted) {
            permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!permissionRequest.isEmpty()){
            ActivityCompat.requestPermissions(this,permissionRequest.toArray(new String[0]),0 );
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> locationCheck = new ArrayList<String>();

        databaseReference = firebaseDatabase.getReference("Users").child(username);

        //Check granted permissions and denied permissions
        if (requestCode == 0 && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionRequest", permissions[i]);
                    if (permissions[i].equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        locationCheck.add(permissions[i]);
                    }

                    if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        locationCheck.add(permissions[i]);
                    }
                }
            }
        }
        if (locationCheck.size() == 1 && locationCheck.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            databaseReference.child("Location: Allow while using the app").setValue("1");

            allowWhileUsingFormView = findViewById(R.id.allowWhileUsingForm);

            String allWhileUsingFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLScPSmGduq2hCz3IUFLT_Jg6NUhjvE_csXnrsk8fTak04c9RWQ/viewform?entry.500974868="+username;
            allowWhileUsingFormView .setText(allWhileUsingFormUrl);
        } else if (locationCheck.size() == 2 && locationCheck.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && locationCheck.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            databaseReference.child("Location: Allow all the time").setValue("1");

            allowAllTheTimeFormView = findViewById(R.id.allowAllTheTimeForm);
            String allTheTimeUrl = "https://docs.google.com/forms/d/e/1FAIpQLSca1ch7HPVrfACFLgQsB_rareltmjlISb1iiM3bkxbHozeouw/viewform?entry.763291267=" + username;
            allowAllTheTimeFormView.setText(allTheTimeUrl);
        } else if (locationCheck.isEmpty()){
            databaseReference.child("Location: Deny").setValue("1");

            denyFormView = findViewById(R.id.denyForm);
            String denyUrl = "https://docs.google.com/forms/d/e/1FAIpQLSc4wIWsxziQdvpU98BxOK5h4Ah6GUqpzOmLgHt6MRX_NoCHaw/viewform?entry.366340186=" + username;
            denyFormView.setText(denyUrl);
        } else {
            Log.d("LocationCheck", "onRequestPermissionsResult: Error Catch");
        }
    }
}

