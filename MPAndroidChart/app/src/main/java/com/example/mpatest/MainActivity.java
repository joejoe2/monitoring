package com.example.mpatest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;

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
    static enum CFG_FIELD{DEV_ID_FIELD,DEV_ID_NUM_FIELD,VAL_FIELD};
    static enum DATA_FIELD{DEV_IDNUM_FIELD,DEV_STATUS_FIELD,TIME_FIELD,DEV_VAL_FIELD};
    String cfg_link="http://192.168.66.16/webserver/get_devices_cfg.php";
    MyAdapter adapter;
    int dev_size;
    JSONArray retre_list;
    public void update_chart(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(()->{
                    try {
                        //update data
                        String json = Jsoup.connect("http://192.168.66.16/webserver/testjson.php?target="+retre_list.toString()).ignoreContentType(true).execute().body();
                        //System.out.println(json);
                        JSONArray array=new JSONArray(json);
                        for (int i = 0; i < dev_size; i++) {
                            JSONArray device=(JSONArray)array.get(i);
                            String id=device.getString(DATA_FIELD.DEV_IDNUM_FIELD.ordinal());
                            String status=device.getString(DATA_FIELD.DEV_STATUS_FIELD.ordinal());
                            LocalDateTime time=LocalDateTime.parse(device.getString(DATA_FIELD.TIME_FIELD.ordinal()));
                            JSONArray sensor=new JSONArray(device.getString(DATA_FIELD.DEV_VAL_FIELD.ordinal()));
                            //System.out.println(id+" "+status+" "+time+" "+sensor);
                            int sen_size=sensor.length();
                            for (int j = 0; j < sen_size; j++) {
                                JSONObject obj=sensor.getJSONObject(j);
                                String sen_id,sen_type,sen_status;
                                Double sen_val;
                                sen_id=obj.getString("id");
                                sen_type=obj.getString("type");
                                sen_status=obj.getString("status");
                                sen_val=obj.get("value") instanceof String?0:obj.getDouble("value");
                                System.out.println(sen_id+" "+sen_type+" "+sen_val);
                            }
                        }

                        MainActivity.this.runOnUiThread(()->{
                            for(int i=0;i<adapter.getCount();i++) {
                                ((MainFragment)adapter.getItem(i)).update_all_chart();
                            }
                            update_chart();
                        });
                    }catch (Exception ex){
                        ex.printStackTrace();
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
                dev_size=array.length();
                for (int i = 0; i < dev_size; i++) {
                    //System.out.println(array.get(i));
                    JSONArray device=(JSONArray)array.get(i);
                    String dev_id=device.getString(CFG_FIELD.DEV_ID_FIELD.ordinal());
                    retre_list.put(i, dev_id);
                    System.out.println(dev_id+" "+device.get(CFG_FIELD.DEV_ID_NUM_FIELD.ordinal()));
                    JSONArray sensor=new JSONArray(device.getString(CFG_FIELD.VAL_FIELD.ordinal()));
                    int sen_size=sensor.length();
                    for (int j = 0; j < sen_size; j++) {
                        JSONObject obj=sensor.getJSONObject(j);
                        String sen_id,sen_type,sen_status;
                        Double sen_val;
                        sen_id=obj.getString("id");
                        sen_type=obj.getString("type");
                        sen_status=obj.getString("status");
                        sen_val=obj.getDouble("value");
                        System.out.println(sen_id+" "+sen_type+" "+sen_val);
                    }
                }

                tabCount=dev_size;
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
            tabLayout.addTab(tabLayout.newTab().setText("ID"+i));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        adapter = new MyAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        //
        init_cfg();
        //
    }
}
