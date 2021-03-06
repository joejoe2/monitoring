package com.example.mpatest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    int tabCount=4;
    final Handler handler = new Handler();
    static enum CFG_FIELD{DEV_ID_FIELD,DEV_ID_NUM_FIELD,VAL_FIELD,INFO_FIELD};
    static enum DATA_FIELD{DEV_IDNUM_FIELD,DEV_STATUS_FIELD,TIME_FIELD,DEV_VAL_FIELD};
    String cfg_link="http://showdata.nctu.me/webserver/get_devices_cfg.php";
    String data_link="http://showdata.nctu.me/webserver/testjson.php?target=";
    MyAdapter adapter;
    int dev_size;
    JSONArray retre_list;
    ArrayList<String> id_num_list;
    //下面是通知用
    private NotificationManagerCompat mNotificationManagerCompat;
    public String channel_id="id_0";
    int notify_id =888;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name="channel_name";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public void update_chart(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(()->{
                    try {
                        //update data

                        String json = Jsoup.connect(data_link+retre_list.toString()).ignoreContentType(true).execute().body();
                        //System.out.println(json);
                        JSONArray array=new JSONArray(json);
                        for (int i = 0; i < dev_size; i++) {
                            JSONArray device=(JSONArray)array.get(i);
                            String id=device.getString(DATA_FIELD.DEV_IDNUM_FIELD.ordinal());
                            String status=device.getString(DATA_FIELD.DEV_STATUS_FIELD.ordinal());
                            String device_id_and_status="Device id : "+id+", Status : "+status;
                            LocalDateTime time=LocalDateTime.parse(device.getString(DATA_FIELD.TIME_FIELD.ordinal()));
                            JSONArray sensor=new JSONArray(device.getString(DATA_FIELD.DEV_VAL_FIELD.ordinal()));
                            //System.out.println(id+" "+status+" "+time+" "+sensor);
                            int sen_size=sensor.length();

                            float[] data_list=new float[sen_size];
                            ArrayList<String> id_list=new ArrayList<String>();
                            ArrayList<String> status_list=new ArrayList<String>();

                            for (int j = 0; j < sen_size; j++) {
                                JSONObject obj=sensor.getJSONObject(j);
                                String sen_id,sen_type,sen_status;
                                float sen_val;
                                sen_id=obj.getString("id");
                                sen_type=obj.getString("type");
                                sen_status=obj.getString("status");
                                sen_val=(float)(obj.get("value") instanceof String?0f:obj.getDouble("value"));
                                id_list.add(sen_id);
                                data_list[j]=sen_val;
                                status_list.add(sen_status);
                                System.out.println(sen_id+" "+sen_type+" "+sen_val);
                            }

                            //update chart on ui
                            final int index=i;
                            MainActivity.this.runOnUiThread(()->{
                                ((MainFragment)adapter.getItem(index)).update_all_chart(time,device_id_and_status,id_list,data_list,status_list);
                            });

                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }finally {
                        //repeat call
                        MainActivity.this.runOnUiThread(()->{
                            update_chart();
                        });
                    }
                }).start();
            }
        }, 5000);
    }

    public void init_cfg(){

        new Thread(()->{
            try {
                //init cfg
                String json = Jsoup.connect(cfg_link).ignoreContentType(true).execute().body();
                //System.out.println(json);
                JSONArray array=new JSONArray(json);
                retre_list=new JSONArray();
                id_num_list=new ArrayList<>();

                dev_size=array.length();
                tabCount=dev_size;
                adapter = new MyAdapter(this,getSupportFragmentManager(), tabCount);
                for (int i = 0; i < dev_size; i++) {
                    JSONArray device=(JSONArray)array.get(i);
                    String dev_id=device.getString(CFG_FIELD.DEV_ID_FIELD.ordinal());
                    String dev_info=device.getString(CFG_FIELD.INFO_FIELD.ordinal());

                    retre_list.put(i, dev_id);
                    id_num_list.add(i, device.getString(CFG_FIELD.DEV_ID_NUM_FIELD.ordinal()));

                    JSONArray sensor=new JSONArray(device.getString(CFG_FIELD.VAL_FIELD.ordinal()));
                    int sen_size=sensor.length();

                    MainFragment tab=new MainFragment();
                    Bundle bundle=new Bundle();
                    bundle.putInt("chart_count",sen_size);
                    bundle.putString("info",dev_info);

                    for (int j = 0; j < sen_size; j++) {
                        JSONObject obj=sensor.getJSONObject(j);
                        String sen_id,sen_type,sen_status;
                        Double sen_val;
                        sen_id=obj.getString("id");
                        tab.id_index_list.add(sen_id);
                        sen_type=obj.getString("type");
                        sen_status=obj.getString("status");
                        sen_val=obj.getDouble("value");
                        System.out.println(sen_id+" "+sen_type+" "+sen_val);

                        bundle.putString("t"+(j+1),sen_type);
                    }

                    tab.setArguments(bundle);
                    adapter.allfrag.add(tab);

                }


                MainActivity.this.runOnUiThread(() -> {
                    setup_cfg();
                });
            }catch(Exception ex){
                    ex.printStackTrace();
            }
        }).start();
    }

    public void setup_cfg(){
        for(int i=0;i<tabCount;i++){
            tabLayout.addTab(tabLayout.newTab().setText("ID"+id_num_list.get(i)));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(tabCount);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        update_chart();
    }

    public void setup_FCM(){
        FirebaseMessaging.getInstance().subscribeToTopic("sensor")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "msg subscribe success";
                        if (!task.isSuccessful()) {
                            msg = "msg subscribe failed";
                        }
                        Log.d("MainActivity", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MainActivity", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = "InstanceID Token: "+token;
                        Log.d("MainActivity", msg);
                    }
                });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        //
        setup_FCM();
        createNotificationChannel();
        init_cfg();
        //
    }
}
