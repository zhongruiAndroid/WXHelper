package com.tools.wx;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.File;

/***
 *   created by android on 2019/8/19
 */
public class FileInfo {
    public boolean isFile;
    public Drawable icon;
    public long size;
    public String sizeStr;
    public String filePath;
    public String fileName;
    public String timeStr;
    public long time;

    public boolean isApk(){
        if(TextUtils.isEmpty(fileName)){
            return false;
        }
        if(fileName.toLowerCase().endsWith(".apk")){
            return true;
        }
        return false;
    }
    public boolean isApkHasPoint(){
        if(TextUtils.isEmpty(fileName)){
            return false;
        }
        if(fileName.toLowerCase().endsWith(".apk.1")){
            return true;
        }
        return false;
    }
    public boolean isImage(){
        if(TextUtils.isEmpty(fileName)){
            return false;
        }
        if(fileName.toLowerCase().endsWith(".jpg")){
            return true;
        }
        if(fileName.toLowerCase().endsWith(".png")){
            return true;
        }
        if(fileName.toLowerCase().endsWith(".gif")){
            return true;
        }
        if(fileName.toLowerCase().endsWith(".jpeg")){
            return true;
        }
        if(fileName.toLowerCase().endsWith(".webp")){
            return true;
        }
        return false;
    }

    public void refreshFile(Activity activity,String newPath){
        if(newPath.toLowerCase().endsWith(".apk")){
            icon=FileUtil.getIconForApk(activity,newPath);
            filePath=newPath;
            fileName=new File(newPath).getName();
        }
    }

}
