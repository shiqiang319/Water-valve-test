package com.example.mymqqttuse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {



    private Timer timerSubscribeTopic = null;
    private TimerTask TimerTaskSubscribeTopic = null;
    private static String Topic;

    public static void setTopic(String topic) {
        Topic = topic;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView WenDu=findViewById(R.id.Msg_text_view);
        final TextView ShiDu=findViewById(R.id.Msg2_text_view);
        final TextView InputTem=findViewById(R.id.input_temperature);
        final Button   WenduSet=findViewById(R.id.TemperatureSet);

//        final Message lastmessage= LitePal.findLast(Message.class);
//        MyMqttClient.sharedCenter().setClientId(lastmessage.getClientid());
//        MyMqttClient.sharedCenter().setMqttUserString(lastmessage.getMqqtuser());
//        MyMqttClient.sharedCenter().setMqttPwdString(lastmessage.getMqqtpwd());
//        MyMqttClient.sharedCenter().setMqttIPString(lastmessage.getMqqttip());
//
//        MyMqttClient.sharedCenter().setConnect();


        MyMqttClient.sharedCenter().setOnServerReadStringCallback(new MyMqttClient.OnServerReadStringCallback() {
            @Override
            public void callback(String Topic, MqttMessage Msg, byte[] MsgByte) {
                Log.e("MqttMsg", "Topic" + Topic +"数据：" + Msg.toString());
                String responseData=Msg.toString();
//                showResponse(responseData);
                parseJSONWithJSONObject(responseData);
                //使用LitePal查询数据
                Data lastdata=LitePal.findLast(Data.class);
                WenDu.setText(String.valueOf(lastdata.getCmd()));
            }
           // json数据解析
            private void parseJSONWithJSONObject(String jsonData){
                try{
                    ArrayList<Integer> shuju = new ArrayList<>();
                      //第一层解析
                     JSONObject jsonObject=new JSONObject(jsonData);
                     int Cmd =jsonObject.optInt("Cmd");
                    Log.e("Json解析","Cmd:"+Cmd);
                     int Id=jsonObject.optInt("Id");
                    Log.e("Json解析","Id:"+Id);
                     JSONArray Para =jsonObject.optJSONArray("Para");
                    Log.e("Json解析","Para原始："+Para);
                     //第二层解析
                    for (int i=0;i<Para.length();i++){
                           shuju.add(Para.optInt(i));
                        Log.e("Json解析","Para:"+shuju.get(i));
                    }
                    //使用LitePal添加数据
                    Data data=new Data();
                    data.setNumber(Id);
                    data.setCmd(Cmd);
                    data.setP1(shuju.get(0).toString());
                    data.setP2(shuju.get(1).toString());
                    data.setP3(shuju.get(2).toString());
                    data.save();
                    }catch (Exception e){
                    e.printStackTrace();
                }

            }

//            //json数据解析
//            private void parseJSONWithJSONObject(String jsonData){
//                try{
//                    JSONObject jsonObject=new JSONObject(jsonData);
//                     String temperature =jsonObject.optString("temperature");
//                    String humidity =jsonObject.optString("humidity");
//                    showResponse(temperature);
//                    showResponse2(humidity);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//            //数据显示在界面
//            private void showResponse(final String Msg ){
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        WenDu.setText(Msg);
//                    }
//                });
//            }
//            private void showResponse2(final String Msg2 ){
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ShiDu.setText(Msg2);
//                    }
//                });
//            }
        });


        //按钮设置温度
        WenduSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
 //               LitePal.deleteAll(Data.class);//删除数据库所有数据
//             String inputtem= InputTem.getText().toString();
//             Float a=Float.parseFloat(inputtem);
//
//                //创建JSON格式数据
//                JSONObject jsonObject = new JSONObject();
//                JSONObject object_1 = new JSONObject();
//                try {
//                    object_1.put("temperature", a);
//                    object_1.put("humidity", 77);
//                    jsonObject.put("method", "thing.event.property.post");
//                    jsonObject.put("id", "111");
//                    jsonObject.put("params",  object_1);
//                    jsonObject.put("version", "1.0.0");
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Log.e("JSON","发送的json 数据："+jsonObject.toString());

                MyMqttClient.sharedCenter().setSendData(
                        Topic,
                        //"/sys/a1S917F388O/wenxin/thing/event/property/post",
                       // "/a1yPGkxyv1q/SimuApp/user/update",
                       // lastmessage.getTopic(),
                       //"{\"method\":\"thing.event.property.post\",\"id\":\"1111\",\"params\":{\"temperature\":99,\"humidity\":99},\"version\":\"1.0.0\"}",
                        "{\"method\":\"thing.event.property.post\",\"id\":\"1111\",\"params\":{\"Id\":1,\"Cmd\":112,\"Para\":[1]},\"version\":\"1.0.0\"}",
                        //jsonObject.toString(),
                        0,
                        false);


            }
        });


        /**
         * 订阅主题成功回调
         */
        MyMqttClient.sharedCenter().setOnServerSubscribeCallback(new MyMqttClient.OnServerSubscribeSuccessCallback() {
            @Override
            public void callback(String Topic, int qos) {
                if (Topic.equals("/sys/a1S917F388O/wenxin/thing/service/property/set")){//订阅1111成功
              //  if (Topic.equals("/a1yPGkxyv1q/Fjg1/user/get")){//订阅1111成功
                    stopTimerSubscribeTopic();//订阅到主题,停止订阅
                }
            }
        });
        startTimerSubscribeTopic();//定时订阅主题
    }




    /**
     * 定时器每隔1S尝试订阅主题
     */
    private void startTimerSubscribeTopic(){
        if (timerSubscribeTopic == null) {
            timerSubscribeTopic = new Timer();
        }
        if (TimerTaskSubscribeTopic == null) {
            TimerTaskSubscribeTopic = new TimerTask() {
                @Override
                public void run() {
                    MyMqttClient.sharedCenter().setSubscribe("/a1yPGkxyv1q/Fjg1/user/get",0);//订阅主题1111,消息等级0
                }
            };
        }
        if(timerSubscribeTopic != null && TimerTaskSubscribeTopic != null )
            timerSubscribeTopic.schedule(TimerTaskSubscribeTopic, 0, 1000);
    }

    private void stopTimerSubscribeTopic(){
        if (timerSubscribeTopic != null) {
            timerSubscribeTopic.cancel();
            timerSubscribeTopic = null;
        }
        if (TimerTaskSubscribeTopic != null) {
            TimerTaskSubscribeTopic.cancel();
            TimerTaskSubscribeTopic = null;
        }
    }

    //当活动不再可见时调用
    @Override
    protected void onStop()
    {
        super.onStop();
        stopTimerSubscribeTopic();//停止定时器订阅
    }

    /**
     * 当处于停止状态的活动需要再次展现给用户的时候，触发该方法
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        startTimerSubscribeTopic();//定时订阅主题
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimerSubscribeTopic();

    }
}
