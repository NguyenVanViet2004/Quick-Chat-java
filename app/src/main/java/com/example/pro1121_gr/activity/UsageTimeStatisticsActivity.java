package com.example.pro1121_gr.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.databinding.ActivityUsageTimeStatisticsBinding;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UsageTimeStatisticsActivity extends AppCompatActivity {

    private ActivityUsageTimeStatisticsBinding usageTimeStatisticsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usageTimeStatisticsBinding = ActivityUsageTimeStatisticsBinding.inflate(getLayoutInflater());
        setContentView(usageTimeStatisticsBinding.getRoot());

        setupClickEvents();
        displayUsageBarChart();
        displayUsagePieChart();
    }

    private void setupClickEvents() {
        usageTimeStatisticsBinding.backFragmentMess.setOnClickListener(view -> onBackPressed());
    }

    private void displayUsageBarChart() {
        List<BarEntry> entries = getUsageEntries();

        BarDataSet dataSet = new BarDataSet(entries, "Thời gian sử dụng 7 ngày gần nhất");
        // Tạo một danh sách màu sắc cho từng cột
        int[] colors = new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.GRAY};
        dataSet.setColors(colors);
        BarData barData = new BarData(dataSet);
        usageTimeStatisticsBinding.barChart.setData(barData);


        // Cấu hình trục X
        XAxis xAxis = usageTimeStatisticsBinding.barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Giữa các giá trị trục X là 1 đơn vị

        // Định dạng giá trị trục Y thành giờ và phút
        YAxis yAxis = usageTimeStatisticsBinding.barChart.getAxisLeft();
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long totalTimeInMillis = (long) value;
                return formatTime(totalTimeInMillis);
            }
        });

        // Refresh biểu đồ
        usageTimeStatisticsBinding.barChart.invalidate();
    }


    private List<BarEntry> getUsageEntries() {
        List<BarEntry> entries = new ArrayList<>();

        // Lấy thời gian sử dụng trong 7 ngày gần đây
        for (int i = 0; i < 7; i++) {
            long usageTime = DBhelper.getInstance(this).getUsageTimeForDay(i);
            entries.add(new BarEntry(i, usageTime));
        }

        return entries;
    }

    private void displayUsagePieChart() {
        List<PieEntry> entries = getUsageEntriesForPieChart();

        PieDataSet dataSet = new PieDataSet(entries, "Thời gian sử dụng");

        // Tạo một danh sách màu sắc cho từng phần tử
        int[] colors = new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.GRAY};
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        usageTimeStatisticsBinding.pieChart.setData(pieData);


        // Refresh biểu đồ
        usageTimeStatisticsBinding.pieChart.invalidate();
    }

    private List<PieEntry> getUsageEntriesForPieChart() {
        List<PieEntry> entries = new ArrayList<>();

        // Lấy thời gian sử dụng trong 7 ngày gần đây
        for (int i = 0; i < 7; i++) {
            long usageTime = DBhelper.getInstance(this).getUsageTimeForDay(i);
            entries.add(new PieEntry(usageTime, ""));
        }

        return entries;
    }


    private String formatTime(long totalTimeInMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(totalTimeInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalTimeInMillis) % 60;

        if (hours > 0) {
            return String.format(Locale.getDefault(), hours + "h " + minutes + "s");
        } else {
            return String.format(Locale.getDefault(), minutes + "s");
        }
    }
}