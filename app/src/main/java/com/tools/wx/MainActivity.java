package com.tools.wx;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.adapter.BaseDividerListItem;
import com.github.adapter.CustomAdapter;
import com.github.adapter.CustomViewHolder;
import com.github.fastshape.MyTextView;
import com.github.permissions.MyPermission;
import com.github.permissions.PermissionCallback;
import com.github.progresslayout.ProgressRelativeLayout;
import com.github.task.Emitter;
import com.github.task.Subscriber;
import com.github.task.Task;
import com.github.task.TaskException;
import com.github.task.TaskPerform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Activity mContext;
    final String wxDir = "tencent/MicroMsg/Download";
    final String wxPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + wxDir;
    private String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    ProgressRelativeLayout prlLoad;
    RecyclerView rvFile;
    MyTextView tvDeleteName;
    MyTextView tvOpenDirectory;

    CustomAdapter adapter;
    private StringBuilder itemPointNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        ToastUtils.init(getApplication());
        setContentView(R.layout.activity_main);


        prlLoad = findViewById(R.id.prlLoad);
        tvDeleteName = findViewById(R.id.tvDeleteName);
        tvDeleteName.setOnClickListener(this);

        tvOpenDirectory = findViewById(R.id.tvOpenDirectory);
        tvOpenDirectory.setOnClickListener(this);

        rvFile = findViewById(R.id.rvFile);
        adapter = new CustomAdapter<FileInfo>(R.layout.file_item) {
            @Override
            public void bindData(CustomViewHolder holder, int position, final FileInfo item) {
                View tvDeleteFile = holder.getView(R.id.tvDeleteFile);
                View flItem = holder.getView(R.id.flItem);
                TextView tvNum = holder.getView(R.id.tvNum);
                TextView tvDeleteLastName = holder.getView(R.id.tvDeleteLastName);
                ImageView ivFile = holder.getView(R.id.ivFile);
                TextView tvFileName = holder.getView(R.id.tvFileName);
                TextView tvFileDes = holder.getView(R.id.tvFileDes);

                int num = position + 1;
                tvNum.setText(itemPointNum.substring(0,itemPointNum.length()-(num+"").length())+""+num);
                if (item.isImage()) {
                    Glide.with(mContext).load(item.filePath).into(ivFile);
                } else if (item.isApk() && item.icon != null) {
                    ivFile.setImageDrawable(item.icon);
                } else {
                    Glide.with(mContext).load(R.drawable.empty_file).into(ivFile);
                }
                if (item.isApkHasPoint()) {
                    tvDeleteLastName.setVisibility(View.VISIBLE);
                    tvDeleteLastName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goRenameFile(item);
                        }
                    });
                } else {
                    tvDeleteLastName.setVisibility(View.INVISIBLE);
                }
                tvFileName.setText(item.fileName);
                tvFileDes.setText(item.sizeStr + " | " + item.timeStr);


                flItem.setOnClickListener(new MyOnClickListener() {
                    @Override
                    protected void onNoDoubleClick(View v) {
//                        FileInfo info = (FileInfo) adapter.getList().get(position);
                        openFile(item.filePath);
                    }
                });
                tvDeleteFile.setOnClickListener(new MyOnClickListener() {
                    @Override
                    protected void onNoDoubleClick(View v) {
                        deleteFile(item);
                    }
                });
            }
        };
        rvFile.setLayoutManager(new LinearLayoutManager(mContext));
        rvFile.addItemDecoration(new BaseDividerListItem(mContext, 1, R.color.app_hint_color));
        rvFile.setAdapter(adapter);

        initData();

    }

    private void initData() {
        showProgress();
        getData();
    }

    private void getData() {
        preRefreshDirector();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvOpenDirectory:
                preRefreshDirector();
                break;
        }
    }

    public void showContent() {
        prlLoad.showContent();
    }

    public void showProgress() {
        prlLoad.showProgress();
    }

    private void preRefreshDirector() {
        MyPermission.get(this).request(permission, new PermissionCallback() {
            @Override
            public void granted() {
                refreshWXDownloadFileList();
            }

            @Override
            public void denied(String firstDenied) {
                showContent();
                showPromptDialog();
            }
        });
    }

    private void showPromptDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("打开文件目录需要文件储存权限,请设置权限之后再试");
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
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void refreshWXDownloadFileList() {
        Task.start(new TaskPerform<List<FileInfo>>() {
            @Override
            public void perform(Emitter<List<FileInfo>> emitter) throws Exception {
                List<FileInfo> list = new ArrayList<>();
                File wxFilePath = new File(wxPath);
                if (wxFilePath.exists() == false) {
                    emitter.onError(new TaskException("微信下载目录没找到", -1));
                }
                File[] files = wxFilePath.listFiles();
                int length = files.length;
                if (files == null || length == 0) {
                    emitter.onError(new TaskException("微信下载目录暂无数据"));
                }
                for (int i = 0; i < length; i++) {
                    File childFile = files[i];
                    FileInfo info = new FileInfo();
                    if (childFile.isFile()) {
                        info.filePath = childFile.getAbsolutePath();
                        info.fileName = childFile.getName();
                        info.isFile = childFile.isFile();
                        info.size = childFile.length();
                        info.sizeStr = FileUtil.formatFileSize(info.size);
                        info.time = childFile.lastModified();
                        info.timeStr = TimeUtil.timeFormat(info.time);
                        if (info.filePath.toLowerCase().endsWith(".apk")) {
                            info.icon = FileUtil.getIconForApk(mContext, info.filePath);
                        }
                        list.add(info);
                    }
                }
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribe(new Subscriber<List<FileInfo>>() {
            @Override
            public void onNext(List<FileInfo> list) {
                setData(list);
            }
            @Override
            public void onError(TaskException exception) {
                super.onError(exception);
                ToastUtils.showToast(exception.getMessage());
                if (exception.errorCode == -1) {
                    noExist();
                }
                showContent();
            }
            @Override
            public void onComplete() {
                super.onComplete();
                showContent();
            }
        });

    }

    private void setData(List<FileInfo> list) {
        int itemCountNum;
        itemPointNum=new StringBuilder();
        if (list == null || list.size() == 0) {
            itemCountNum = 0;
        } else {
            itemCountNum = (list.size() + "").length();
        }
        for (int i = 0; i < itemCountNum; i++) {
            itemPointNum.append("0");
        }
        adapter.setList(list, true);
    }

    private void noExist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("微信下载目录不存在,请手动查看" + wxDir + "路径是否存在");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void deleteFile(FileInfo info) {
        String filePath = info.filePath;
        File file = new File(filePath);
        if (file.exists()) {
            boolean result = file.delete();
            if (result) {
                adapter.getList().remove(info);
                adapter.notifyDataSetChanged();
                ToastUtils.showToast("删除成功");
            } else {
                ToastUtils.showToast("删除失败");
            }
        } else {
            ToastUtils.showToast("该文件不存在");
        }
    }
    private void goRenameFile(FileInfo info) {
        String filePath = info.filePath;
        File file = new File(filePath);
        if (file.exists()) {
            int index = filePath.lastIndexOf(".1");
            String substring = filePath.substring(0, index);
            boolean result = renameFile(filePath, substring);
            if (result) {
                info.refreshFile(this, substring);
                adapter.notifyDataSetChanged();
            } else {
                ToastUtils.showToast("操作失败");
            }
        } else {
            ToastUtils.showToast("该文件不存在");
        }
    }

    private boolean renameFile(String oldPath, String newPath) {
        if (TextUtils.isEmpty(oldPath)) {
            return false;
        }
        if (TextUtils.isEmpty(newPath)) {
            return false;
        }
        File file = new File(oldPath);
        boolean result = file.renameTo(new File(newPath));
        return result;
    }

    private void openFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() == false) {
            ToastUtils.showToast("文件不存在");
            return;
        }
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri contentUri = FileProvider.getUriForFile(this, getPackageName(), file);
//        intent.setDataAndType(contentUri, "*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
////        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        startActivity(intent);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_VIEW);//动作，查看
        Uri contentUri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            //小于7.0
            contentUri = Uri.fromFile(file);
        } else {
            contentUri = FileProvider.getUriForFile(mContext, getPackageName(), file);
        }
        intent.setDataAndType(contentUri, FileUtil.getMIMEType(file));//设置类型
        startActivity(intent);
    }

}
