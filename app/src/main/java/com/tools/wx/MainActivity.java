package com.tools.wx;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.permissions.MyPermission;
import com.github.permissions.PermissionCallback;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final String wxPath="tencent/MicroMsg/Download";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtils.init(getApplication());
        setContentView(R.layout.activity_main);

        TextView tvDeleteName   =findViewById(R.id.tvDeleteName);
        tvDeleteName.setOnClickListener(this);

        TextView tvOpenDirectory=findViewById(R.id.tvOpenDirectory);
        tvOpenDirectory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvDeleteName:
                deleteName();
            break;
            case R.id.tvOpenDirectory:
                MyPermission.get(this).request(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionCallback() {
                    @Override
                    public void granted() {
                        openDirectory();
                    }
                    @Override
                    public void denied(String firstDenied) {
                        ToastUtils.showToast("无权限");
                    }
                });
            break;
        }
    }

    private void deleteName() {

    }

    private void openDirectory() {
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+wxPath);


        if(file.exists()==false){
            ToastUtils.showToast("打开失败");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        Uri contentUri = FileProvider.getUriForFile(this,getPackageName(),file);

        intent.setDataAndType(contentUri, "*/*");


//        intent.addCategory(Intent.CATEGORY_OPENABLE);



       intent.addCategory(Intent.CATEGORY_DEFAULT);

        startActivity(intent);
    }
}
