package parser.axml;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 清单信息
 */
public class ManifestInfo {
    /**
     * 包名 - &lt;manifest&gt; 中的 package 属性。
     */
    public String packageName;
    /**
     * 版本号 - &lt;manifest&gt; 中的 android:versionCode 属性。
     */
    public String versionCode;
    /**
     * 版本名 - &lt;manifest&gt; 中的 android:versionName 属性。
     */
    public String versionName;
    /**
     * 应用名 - &lt;manifest&gt; 中的 android:label 属性。
     */
    public String label;
    public int labelId = 0;
    /**
     * 权限 - 由AndroidManifest.xml 中的 uses-permission 标签所定义。
     */
    public ArrayList<String> requestedPermissions;
//    public String[] requestedPermissions;
    /**
     * 是否有 Activity，默认没有（false）。
     */
    public boolean eActivity = false;
    /**
     * 是否有图标，默认没有（false）。
     */
    public boolean eIcon = false;
    /**
     * 是否存在接收器，默认不存在（False）。
     */
    public String[] revs;
    // key : receiver, value : action
    public HashMap<String, ArrayList<String>> receivers;
    public JSONObject jsonObjectRec = new JSONObject();
    // key : activity, value : action
    public HashMap<String, ArrayList<String>> activities;
    /**
     * 是否存在服务，默认不存在（False）。
     */
    public String[] servs;
    public ArrayList<String> services;
    public String[] acts;
    /**
     * AndroidManifest.xml
     */
    public String xml;
    /**
     * 临时值，标志是否后台运行
     */
    public boolean back;

    @Override
    public String toString() {
        return "ManifestInfo{" +
                "packageName='" + packageName + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                ", label='" + label + '\'' +
                ", labelId=" + labelId +
                ", requestedPermissions=" + requestedPermissions +
                ", eActivity=" + eActivity +
                ", eIcon=" + eIcon +
                ", revs=" + Arrays.toString(revs) +
                ", receivers=" + receivers +
                ", jsonObjectRec=" + jsonObjectRec +
                ", activities=" + activities +
                ", servs=" + Arrays.toString(servs) +
                ", services=" + services +
                ", acts=" + Arrays.toString(acts) +
//                ", xml='" + xml + '\'' +
//                ", back=" + back +
                '}';
    }
}
