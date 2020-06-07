package com.example.mymqqttuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymqqttuse.dommy.qrcode.util.Constant;
import com.example.mymqqttuse.google.zxing.activity.CaptureActivity;

import org.litepal.LitePal;

public class device extends AppCompatActivity implements View.OnClickListener{
    Button btnQrCode; // 扫码
    TextView tvResult; // 结果
    EditText Id;
    EditText User;
    EditText Pwd;
    EditText Ip;
    EditText Topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initView();


        Id=findViewById(R.id.et_id);
        User=findViewById(R.id.et_user);
        Pwd=findViewById(R.id.et_pwd);
        Ip=findViewById(R.id.et_ip);
        Topic=findViewById(R.id.et_topic);
        Button connect1=findViewById(R.id.connect1);
        Button connect2=findViewById(R.id.connect2);
        Button save=findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message=new Message();
                message.setClientid(Id.getText().toString().trim());
                message.setMqqtuser(User.getText().toString().trim());
                message.setMqqtpwd(Pwd.getText().toString().trim());
                message.setMqqttip(Ip.getText().toString().trim());
                message.setTopic(Topic.getText().toString().trim());
                message.save();
            }
        });
        connect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Message firstmessage= LitePal.findFirst(Message.class);
                MyMqttClient.sharedCenter().setClientId(firstmessage.getClientid());
                MyMqttClient.sharedCenter().setMqttUserString(firstmessage.getMqqtuser());
                MyMqttClient.sharedCenter().setMqttPwdString(firstmessage.getMqqtpwd());
                MyMqttClient.sharedCenter().setMqttIPString(firstmessage.getMqqttip());
                MainActivity.setTopic(firstmessage.getTopic());

                MyMqttClient.sharedCenter().setConnect();
                Intent intent=new Intent(device.this,MainActivity.class);
                startActivity(intent);
            }
        });
        connect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Message lasttmessage= LitePal.findLast(Message.class);
                MyMqttClient.sharedCenter().setClientId(lasttmessage.getClientid());
                MyMqttClient.sharedCenter().setMqttUserString(lasttmessage.getMqqtuser());
                MyMqttClient.sharedCenter().setMqttPwdString(lasttmessage.getMqqtpwd());
                MyMqttClient.sharedCenter().setMqttIPString(lasttmessage.getMqqttip());
                MainActivity.setTopic(lasttmessage.getTopic());

                MyMqttClient.sharedCenter().setConnect();
                Intent intent=new Intent(device.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initView() {
        btnQrCode = (Button) findViewById(R.id.btn_qrcode);
        btnQrCode.setOnClickListener(this);
       // tvResult = (TextView) findViewById(R.id.txt_result);
    }

    // 开始扫码
    private void startQrCode() {
        // 申请相机权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(device.this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(device.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQ_PERM_EXTERNAL_STORAGE);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(device.this, CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_QR_CODE);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_qrcode:
                startQrCode();
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AlertDialog.Builder builder = new AlertDialog.Builder(device.this);
        builder.setMessage("您确定要添加设备吗？");   //设置对话框的内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //扫描结果回调
                if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String[] scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN).split("\\*");
                    //将扫描出的信息显示出来
                    //tvResult.setText(scanResult);
                    Id.setText(scanResult[0]);
                    User.setText(scanResult[1]);
                    Pwd.setText(scanResult[2]);
                    Ip.setText(scanResult[3]);
                    Topic.setText(scanResult[4]);
                    Log.e("解析结果","ClientId"+scanResult[0]);
                    Log.e("解析结果","user"+scanResult[1]);
                    Log.e("解析结果","Pwd"+scanResult[2]);
                    Log.e("解析结果","Ip"+scanResult[3]);
                    Log.e("解析结果","Topic"+scanResult[4]);
                }
                Toast.makeText(device.this, "您已添加成功！", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {//设置取消按钮
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(device.this, "您已取消添加！", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog b = builder.create();
        b.show();
//        //扫描结果回调
//        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
//            Bundle bundle = data.getExtras();
//            String[] scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN).split("\\*");
//            //将扫描出的信息显示出来
//            //tvResult.setText(scanResult);
//            Id.setText(scanResult[0]);
//            User.setText(scanResult[1]);
//            Pwd.setText(scanResult[2]);
//            Ip.setText(scanResult[3]);
//            Topic.setText(scanResult[4]);
//            Log.e("解析结果","ClientId"+scanResult[0]);
//            Log.e("解析结果","user"+scanResult[1]);
//            Log.e("解析结果","Pwd"+scanResult[2]);
//            Log.e("解析结果","Ip"+scanResult[3]);
//            Log.e("解析结果","Topic"+scanResult[4]);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(device.this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
            case Constant.REQ_PERM_EXTERNAL_STORAGE:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(device.this, "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
