package com.flop.settings.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.flop.settings.R;
import com.flop.settings.util.LogUtil;

import static com.flop.settings.variable.Constant.PERMISSIONS_STORAGE;
import static com.flop.settings.variable.Constant.REQUEST_EXTERNAL_STORAGE_CODE;
import static com.flop.settings.variable.Constant.STORAGE_PERMISSION_REQUEST_CODE;

/**
 * Created by Flop on 2020/2/10.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements ViewTreeObserver.OnWindowFocusChangeListener {

    private AlertDialog alertDialogPermission;

    private OnWindowFocusChangedListener mOnWindowFocusChangeListener;// 窗口焦点变化的接口

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 每次加载主界面时检测应用是否有相应的权限
        requestPermission();
    }

    /**
     * 监听窗口焦点变化
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // 回调窗口焦点变化的接口
        mOnWindowFocusChangeListener.onWindowFocusChanged(hasFocus);
    }

    /**
     * 绑定窗口焦点变化的接口
     */
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        // 只有实现了指定接口的 fragment 才可以用于接口绑定
        try {
            mOnWindowFocusChangeListener = (OnWindowFocusChangedListener) fragment;
        } catch (Exception ignored) {
        }
    }

    /**
     * 窗口焦点变化的接口
     */
    public interface OnWindowFocusChangedListener {
        /**
         * @param hasFocus 当前窗口是否获取焦点
         */
        public void onWindowFocusChanged(boolean hasFocus);
    }

    /**
     * 检测应用是否有相应的权限
     */
    public boolean hasPermission() {
        // Android 6.0(API 23)及其以上动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(getApplicationContext());
        }
        return true;
    }

    /**
     * 申请应用权限
     */
    private void requestPermission() {
        // 如果当前没有权限
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE_CODE);
        }
    }

    /**
     * 检测权限请求结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE_CODE) {
            boolean hasAllGranted = true;
            for (int i = 0; i < grantResults.length; ++i) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    hasAllGranted = false;
                    //在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false
                    //则可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        //解释原因，并且引导用户至设置页手动授权
                        showPermissionDialog();
                    } else {
                        //权限请求失败，但未选中“不再提示”选项
                        LogUtil.i("权限请求失败，但未选中“不再提示”选项", new Throwable());
                        finish();
                    }
                    break;
                }
            }
            if (hasAllGranted) {
                //权限请求成功
                LogUtil.i("权限请求成功", new Throwable());
            }
        }
    }

    /**
     * 显示授权对话框
     */
    private void showPermissionDialog() {
        if (alertDialogPermission == null) {
            // 创建授权对话框
            alertDialogPermission = new AlertDialog.Builder(this)
                    .setMessage(String.format(MyApplication.getContext().getResources().getString(R.string.permission_request_message), "系统设置"))
                    .setPositiveButton("授权", (dialog, which) -> {
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, STORAGE_PERMISSION_REQUEST_CODE);
                    })
                    .setNegativeButton("退出", (dialog, which) -> {
                        //引导用户手动授权，权限请求失败
                        LogUtil.i("用户点击取消按钮，权限请求失败", new Throwable());
                        finish();
                    }).setCancelable(false).create();
        }
        // 显示授权对话框
        alertDialogPermission.show();
    }

    /**
     * 其他页面返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                // 没有相应权限，弹出对话框去申请相应的权限
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE_CODE);
            }
        }
    }
}
