package com.xingshijie.transmitfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;


public class MainActivity extends ActionBarActivity {
    private WifiManager wifiManager;
    private boolean flag=false;
    private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        Button button=(Button)findViewById(R.id.button_select_file);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,FileSelecterActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        ImageView imageView=(ImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,FileSelecterActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        Intent myIntent=getIntent();
        Log.e("", myIntent.toString());
        Uri uri=myIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(uri!=null){
            String path=UriToPath.getPath(this,uri);
            Log.e(uri.toString(),"AAAAAAAAAA"+path);
            ((TextView)findViewById(R.id.textView_file_path)).setText(path);
            HttpFileServer.filePath=path;
        }

        if(HttpFileServer.filePath!=null){
            ((TextView)findViewById(R.id.textView_file_path)).setText(HttpFileServer.filePath);
        }

        intentService=new Intent(this,FileService.class);
        startService(intentService);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            ((TextView)findViewById(R.id.textView_file_path)).setText(data.getStringExtra("fileName"));
            HttpFileServer.filePath=data.getStringExtra("fileName");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent=new Intent(this,AboutActivity.class);
            startActivity(intent);
            return true;
        }if(id==R.id.action_open_wifi){
            flag=!flag;
            TextView textView=(TextView)findViewById(R.id.textView_password);
            if(flag){
                textView.setVisibility(View.VISIBLE);
                Toast.makeText(this,"热点已开启",Toast.LENGTH_SHORT).show();
            }else {
                textView.setVisibility(View.INVISIBLE);
                Toast.makeText(this,"热点已关闭",Toast.LENGTH_SHORT).show();
            }

            setWifiApEnabled(flag);
            return true;
        }if(id==R.id.action_help){
            Intent intent=new Intent(this,HelpActivity.class);
            startActivity(intent);
        }if(id==R.id.action_exit){
            //stopService(intentService);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = "传文件";
            //必须加上wpa_psk等加密算法，否则密码为空值
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            //配置热点的密码
            apConfig.preSharedKey="12345678";
            //wifiManager.addNetwork(apConfig);
            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }
}
