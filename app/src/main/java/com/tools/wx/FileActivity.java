package com.tools.wx;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.github.fastshape.MyTextView;

import java.io.File;

public class FileActivity extends AppCompatActivity {
    RecyclerView rvFile;

    MyTextView tvDeleteName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        initView();
        String apkPath=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"2019/v2-lieying-release.apk";
        PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo != null) {
            String packageName = packageInfo.packageName;
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            Drawable drawable3 = null;
            try {
                drawable3 = getPackageManager().getApplicationIcon(packageName);
                Drawable icon = getPackageManager().getApplicationIcon(packageInfo.applicationInfo);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String label = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            Log.d("ApkActivity", "packageName=" + packageName + ", versionCode=" + versionCode + ", versionName=" + versionName + ", labe=" + label);
        } else {
            Log.d("ApkActivity", "apk broken");
        }
    }

    private void initView() {
        rvFile=findViewById(R.id.rvFile);
        tvDeleteName=findViewById(R.id.tvDeleteName);


    }
}
