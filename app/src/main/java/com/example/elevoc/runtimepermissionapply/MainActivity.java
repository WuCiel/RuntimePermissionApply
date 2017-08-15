package com.example.elevoc.runtimepermissionapply;

import android.Manifest;
import android.app.usage.ExternalStorageStats;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SDCARD=0x101;
    public static final String[] PERMISSION=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};

    public static boolean hasPermission=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permission(PERMISSION);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(hasPermission){
            writeSomething();
        }

    }

    /**
     * 往sdcard写文件测试是否正常获取读写权限
     */
    private void writeSomething(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File sdcardDir=Environment.getExternalStorageDirectory();
            try {
                FileOutputStream out=new FileOutputStream(new File(sdcardDir,"test.txt"));
                String write="aasdasdasdasdasdasd";
                out.write(write.getBytes());
                out.close();
                Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
            }
        }
    }

    /**
     * 判断是否所有权限都已获取，对未获取的权限进行动态申请
     * @param permission
     */
    private void Permission(String... permission){
        if(Build.VERSION.SDK_INT>=23){
            List<String> per=new ArrayList<>();
            for(int i=0;i<permission.length;i++){
                if(ContextCompat.checkSelfPermission(this,permission[i])== PackageManager.PERMISSION_DENIED){
                    per.add(permission[i]);
                }
            }
            if(per.size()>0) {
                String[] per2 = new String[per.size()];
                for (int i = 0; i < per.size(); i++) {
                    per2[i] = per.get(i);
                }
                ActivityCompat.requestPermissions(this, per2, REQUEST_CODE_SDCARD);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE_SDCARD){
            int len=grantResults.length;
            for(int i=0;i<len;i++){
                if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, permissions[i]+" 获取失败", Toast.LENGTH_SHORT).show();
//                    System.exit(0);
                    hasPermission=false;
                    return;
                }
            }
            writeSomething();
            hasPermission=true;
            Toast.makeText(this, "所有权限获取成功", Toast.LENGTH_SHORT).show();
        }
    }
}
