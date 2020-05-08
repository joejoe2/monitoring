package com.example.mpatest;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class MainFragment extends Fragment {

    int chart_count;
    TextView now_time,info;
    LocalDateTime last_time=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chart_count=this.getArguments().getInt("chart_count");
        String dev_info=this.getArguments().getString("info");

        now_device_status=getView().findViewById(R.id.device_status);
        now_time=getView().findViewById(R.id.textView);
        info=getView().findViewById(R.id.info);

        info.setText(dev_info);
        for(int i=0;i<chart_count;i++){
            if(i==4){
                break;
            }
            LineChart mChart;
            mChart=getView().findViewWithTag("l"+(i+1));
            TextView ty = getView().findViewWithTag("ti"+(i+1));
            ty.setText(this.getArguments().getString("t"+(i+1), ""));
            initChart(mChart);
            chartlist.add(mChart);

            TextView t;
            t=getView().findViewWithTag("t"+(i+1));
            sensor_status_list.add(t);
        }



    }

    public static MainFragment createInstance(String name) {
        // Fragmentを作成して返すメソッド
        // createInstanceメソッドを使用することで、そのクラスを作成する際にどのような値が必要になるか制約を設けることができる
        MainFragment fragment = new MainFragment();
        // Fragmentに渡す値はBundleという型でやり取りする
        Bundle args = new Bundle();
        // Key/Pairの形で値をセットする
        // Fragmentに値をセットする
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bundleの値を受け取る際はonCreateメソッド内で行う

    }

    public void update_all_chart(LocalDateTime the_time_data_come,String device_status,ArrayList<String> idlist,float[] datalist,ArrayList<String> statuslist){
        if(last_time==null||last_time.isBefore(the_time_data_come)){
            last_time=the_time_data_come;
            now_device_status.setText(device_status);
            now_time.setText(the_time_data_come.toLocalDate()+"\n"+the_time_data_come.toLocalTime());
            for (i=0;i<chartlist.size();i++){
                addEntry(chartlist.get(id_index_list.indexOf(idlist.get(i))),datalist[i]);
                sensor_status_list.get(id_index_list.indexOf(idlist.get(i))).setText(statuslist.get(i));
            }
        }
    }

    //下面開始是linechart的部分

    ArrayList<LineChart>chartlist=new ArrayList<LineChart>();
    //ArrayList<String> xLabel = new ArrayList<>();

    ArrayList<String> id_index_list=new ArrayList<String>();

    ArrayList<TextView> sensor_status_list=new ArrayList<TextView>();

    TextView now_device_status;
    int i=0;


    private void initChart(LineChart mChart) {
        // enable description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        // l.setTypeface(mTfLight);
        l.setTextColor(Color.WHITE);
        l.setEnabled(false);

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawLabels(false);
        xl.setGranularity(1f);

        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.enableGridDashedLine(10f, 10f, 0f);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);


        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setGranularity(1f);
        leftAxis.setTextColor(Color.parseColor("#A2A2A2"));
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        //重置y轴的最小最大值   这样会让曲线随着自己的数据变化而变化
        leftAxis.resetAxisMinimum();
        leftAxis.resetAxisMaximum();

        leftAxis.setDrawGridLines(true);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void addEntry(LineChart mChart,float data_to_update) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            if (set.getEntryCount() > 10) {
                set.removeFirst();
                for (int i = 0; i < 10; i++) {
                    set.getEntryForIndex(i).setX(i);
                }
            }

            data.addEntry(new Entry(set.getEntryCount(),data_to_update), 0);
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            // 折线图最多显示的数量
            mChart.setVisibleXRangeMaximum(120);

            mChart.moveViewToX(data.getEntryCount());
        }
    }

        private LineDataSet createSet() {
            LineDataSet set = new LineDataSet(null, "Dynamic Data");
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(Color.parseColor("#FDC328"));
            set.setCircleColor(Color.WHITE);
            set.setLineWidth(2f);
            set.setCircleRadius(3f);
            set.setDrawCircles(false);
            set.setFillAlpha(65);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setValueTextColor(Color.WHITE);
            set.setValueTextSize(9f);
            set.setDrawValues(false);
            return set;
        }

}
