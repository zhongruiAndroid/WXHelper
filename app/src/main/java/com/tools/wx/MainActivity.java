package com.tools.wx;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.fastshape.MyTextView;
import com.github.permissions.MyPermission;
import com.github.permissions.PermissionCallback;

import java.io.File;
import java.text.SimpleDateFormat;

import static android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final String wxDir="tencent/MicroMsg/Download";
    final String wxPath = Environment.getDownloadCacheDirectory().getAbsolutePath()+File.separator+wxDir;
    RecyclerView rvFile;
    MyTextView tvDeleteName;
    MyTextView tvOpenDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtils.init(getApplication());
        setContentView(R.layout.activity_main);


        tvDeleteName = findViewById(R.id.tvDeleteName);
        tvDeleteName.setOnClickListener(this);

        tvOpenDirectory = findViewById(R.id.tvOpenDirectory);
        tvOpenDirectory.setOnClickListener(this);

        rvFile = findViewById(R.id.rvFile);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvOpenDirectory:
                preRefreshDirector();
                break;
        }
    }

    private void preRefreshDirector() {
        MyPermission.get(this).request(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionCallback() {
            @Override
            public void granted() {
                refreshDirector();
            }
            @Override
            public void denied(String firstDenied) {
                showPromptDialog();
            }
        });
    }

    private void showPromptDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("打开文件目录需要文件读取权限,请设置权限之后再试");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openPermission();
            }
        });
        builder.show();
    }

    private void openPermission() {
        Intent intent=new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:"+getPackageName()));
        startActivity(intent);
    }

    private void refreshDirector() {
        File wxFilePath=new File(wxPath);
        if (wxFilePath.exists() == false) {
            noExist();
            return;
        }

        File[] files = wxFilePath.listFiles();
        int length = files.length;
        if(files==null||length==0){
            ToastUtils.showToast("微信下载目录暂无数据");
            return;
        }

        for (int i = 0; i < length; i++) {
            FileInfo info=new FileInfo();
            if(info.isFile){
                File childFile = files[i];
                info.filePath=childFile.getAbsolutePath();
                info.fileName=childFile.getName();
                info.isFile=childFile.isFile();
                info.size=childFile.length();
                info.sizeStr=FileUtil.formatFileSize(info.size);
                info.time=childFile.lastModified();
                info.timeStr=TimeUtil.timeFormat(info.time);
                if(info.filePath.endsWith(".apk")){
                    info.icon=FileUtil.getIconForApk(this,info.filePath);
                }
            }
        }

    }

    private void noExist() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("微信下载目录不存在,请手动查看"+wxDir+"路径是否存在");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void deleteName() {

    }

    private void openDirectory() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + wxPath);


        if (file.exists() == false) {
            ToastUtils.showToast("打开失败");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        Uri contentUri = FileProvider.getUriForFile(this, getPackageName(), file);

        intent.setDataAndType(contentUri, "*/*");


//        intent.addCategory(Intent.CATEGORY_OPENABLE);


        intent.addCategory(Intent.CATEGORY_DEFAULT);

        startActivity(intent);
    }
}
