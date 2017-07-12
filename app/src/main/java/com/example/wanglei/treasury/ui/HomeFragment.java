package com.example.wanglei.treasury.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanglei.treasury.R;
import com.example.wanglei.treasury.service.GetLineData;
import com.example.wanglei.treasury.service.TotalBillService;
import com.example.wanglei.treasury.statistics.LineChart;
import com.example.wanglei.treasury.statistics.PieChart;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by wanglei on 2017/7/6.
 * 对应主页界面
 */

public class HomeFragment extends Fragment {
    private View homeFragmentLayout;

    private TextView textViewYuE; //余额
    private LineChartView lineChartView;//折线图
    private LineChartData lineChartData;
    private PieChartView pieChartView;//饼图
    private PieChartData pieChartData;

    private GetLineData getLineData = new GetLineData();
    private String name = "刘龙航", username = "liullhitcs";
    private TextView textViewName, textViewUsername;//姓名和用户名

    private TotalBillService totalBillService = new TotalBillService();
    private HashMap<String, Double> totalMoney = new HashMap<String, Double>();

    private double[] moneyInAndOut = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] moneyTotal = {0, 0};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeFragmentLayout = inflater.inflate(R.layout.fragment_home, container, false);

        initViews();
        try {
            setLinerChartData();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            setPieChartData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(homeFragmentLayout.getContext(), String.valueOf(moneyTotal[0]), Toast.LENGTH_LONG).show();

        textViewYuE.setText(String.valueOf(moneyTotal[0]-moneyTotal[1]));
        lineChartView.setLineChartData(lineChartData);
        pieChartView.setPieChartData(pieChartData);

        return homeFragmentLayout;
    }

    /**
     * 初始化各个views
     */
    public void initViews() {
        lineChartView = (LineChartView) homeFragmentLayout.findViewById(R.id.lineChart);
        pieChartView = (PieChartView) homeFragmentLayout.findViewById(R.id.pieChart);
        textViewName = (TextView) homeFragmentLayout.findViewById(R.id.textView_name);
        textViewUsername = (TextView) homeFragmentLayout.findViewById(R.id.textView_username);
        textViewYuE = (TextView) homeFragmentLayout.findViewById(R.id.textView_yue);

        textViewName.setText(name);
        textViewUsername.setText(username);

    }

    public void setNameAndUsername(String name, String username) {
        this.name = name;
        this.username = username;
    }
    /**
     * 最近五个月的折线图数据
     *
     */
    public void setLinerChartData() throws SQLException, ClassNotFoundException, InterruptedException {
        Date curDate = new Date();
        int curMonth = curDate.getMonth();
        int beginMonth;
        if (curMonth >= 5) {
            beginMonth = curMonth - 4;
        }
        else {
            beginMonth = curMonth + 12 - 4;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    moneyInAndOut = getLineData.getLineData(beginMonth);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.currentThread().sleep(1000);
//        int[] moneyIn = new int[] {1000, 3000, 2000, 1500, 2000};
//        int[] moneyOut = new int[] {1200, 2000, 2000, 2500, 500};
        double[] moneyIn = new double[] {0, 0, 0, 0, 0};
        double[] moneyOut = new double[] {0, 0, 0, 0, 0};
        for (int i = 0; i < 10; i++)
        {
            if (i < 5)
            {
                moneyIn[i] = moneyInAndOut[i];
            }
            else
            {
                moneyOut[i-5] = moneyInAndOut[i];
            }
        }
        String[] month = new String[5];
        for (int i = 0; i < 5; i++) {
            int tmpMonth = ((beginMonth + i) % 13) + 1;
            month[i] = tmpMonth + "月";
        }
        lineChartData = new LineChart().setLineChartData(homeFragmentLayout.getContext(), moneyIn, moneyOut, month);
    }

    /**
     * 饼状图数据
     */
    public void setPieChartData() throws InterruptedException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    moneyTotal = totalBillService.query();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.currentThread().sleep(1000);

        pieChartData = new PieChart().setPieChart(homeFragmentLayout.getContext(), moneyTotal[0],
                moneyTotal[1]);
    }
}
