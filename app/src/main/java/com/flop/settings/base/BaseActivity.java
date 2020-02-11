package com.flop.settings.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.flop.settings.R;

import static com.flop.settings.variable.Constant.PERMISSIONS_STORAGE;
import static com.flop.settings.variable.Constant.REQUEST_EXTERNAL_STORAGE_CODE;
import static com.flop.settings.variable.Constant.STORAGE_PERMISSION_REQUEST_CODE;

/**
 * Created by Flop on 2020/2/10.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "FLOP";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 每次加载主界面时检测应用是否有相应的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE_CODE);
            }
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
                        new AlertDialog.Builder(this)
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
                                    Log.i(TAG, "用户点击取消按钮，权限请求失败");
                                    finish();
                                }).setOnCancelListener(dialog -> {
                            //引导用户手动授权，权限请求失败
                            Log.i(TAG, "用户关闭对话框，权限请求失败");
                            finish();
                        }).show();
                    } else {
                        //权限请求失败，但未选中“不再提示”选项
                        Log.i(TAG, "权限请求失败，但未选中“不再提示”选项");
                        finish();
                    }
                    break;
                }
            }
            if (hasAllGranted) {
                //权限请求成功
                Log.i(TAG, "权限请求成功");
            }
        }
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
