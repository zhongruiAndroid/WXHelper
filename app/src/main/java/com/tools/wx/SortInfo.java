package com.tools.wx;

/***
 *   created by zhongrui on 2019/8/20
 */
public class SortInfo {
    public int fileTime;
    public boolean fileTimeAsc;

    public int fileName;
    public boolean fileNameAsc;

    public int fileSize;
    public boolean fileSizeAsc;

    //0fileTime,1fileName,2fileSize
    public int selectIndex=0;

    public String getTimeFlag(){
        return "修改时间"+(fileTimeAsc?"↓":"↑");
    }
    public String getNameFlag(){
        return "文件名称"+(fileNameAsc?"↓":"↑");
    }
    public String getSizeFlag(){
        return "文件大小"+(fileSizeAsc?"↓":"↑");
    }
}
