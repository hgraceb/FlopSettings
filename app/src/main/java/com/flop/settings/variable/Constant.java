package com.flop.settings.variable;

/**
 * 常量
 * <p>
 * Created by Flop on 2020/2/10.
 */
public class Constant {
    // 应用所需的全部权限请求
    public static String[] PERMISSIONS_STORAGE = {"android.permission.WRITE_SETTINGS"};
    // 应用权限请求码
    public static final int REQUEST_EXTERNAL_STORAGE_CODE = 14512;
    // 用户拒绝请求并选择不再询问，引导用户手动授权应用所需权限
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 10513;
}
