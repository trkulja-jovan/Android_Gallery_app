package com.android.gallery.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.widget.TextView;

import com.android.gallery.R;
import com.android.gallery.interfaces.Permissible;
import com.android.gallery.main.ViewImagesActivity;
import com.android.gallery.utils.ImagesGuard;
import com.android.gallery.utils.Init;
import com.android.gallery.utils.Permissions;

import static com.android.gallery.utils.Permissions.STORAGE_PERMISSION_CODE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity implements Permissible {

    private static final Integer SPLASH_DURATION = 5_000;

    private List<String> listPath;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Init.getInstance().initComponents(() -> {
            txt = findViewById(R.id.textView2);
            //first word
            String t1 = Init.getColoredSpanned("Software", "#018ABE");
            //second word
            String t2 = Init.getColoredSpanned("Developer", "#02457A");
            //third word
            String t3 = Init.getColoredSpanned("Center", "#001B48");

            txt.setText(Html.fromHtml(t1 + " " + t2 + " " + t3));

            listPath = new ArrayList<>();
        });

        Init.getPermissionInstance()
            .checkPermission(getApplicationContext(),
                      this,
                            READ_EXTERNAL_STORAGE,
                            STORAGE_PERMISSION_CODE);

        new Handler().postDelayed(() -> {

            if(Permissions.getStorageAllowedStatus() == 1){
                listPath = Init.getInstance().getImagesFromUri(this, true);
            } else if(Permissions.getStorageAllowedStatus() == 0){
                System.exit(0);
            } else
                throw new RuntimeException("Cannot resolve storage status!");

            ImagesGuard.setBitmapsPath(listPath);

            Intent intent = new Intent(SplashScreen.this, ViewImagesActivity.class);
            SplashScreen.this.startActivity(intent);
            SplashScreen.this.finish();

        }, SPLASH_DURATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED )
                Init.getPermissionInstance().setStorageAllowedStatus(1);
            else
                Init.getPermissionInstance().setStorageAllowedStatus(0);
        }
    }
}
