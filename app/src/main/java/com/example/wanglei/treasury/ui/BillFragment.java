package com.example.wanglei.treasury.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanglei.treasury.R;
import com.example.wanglei.treasury.entity.BillEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanglei on 2017/7/6.
 * 对应账单界面
 */

public class BillFragment extends Fragment {
    private View billFragmentLayout;

    private ListView listView;//用于显示账单信息
    private SimpleAdapter simpleAdapter;//适配器
    private List<Map<String, Object>> listViewData = new ArrayList<Map<String, Object>>();;//list数据
    private String[] key = new String[]{"time", "type" ,"money", "explain"};
    private int[] listViewId = new int[]{R.id.listView_bill_name, R.id.listView_bill_type, R.id.listView_bill_money, R.id.listView_bill_explain};

    private TextView textView_begin; //起始时间
    private TextView textView_end; //结束时间
    private DatePicker datePicker_bill; //日期
    private RadioGroup radioGroup;//类型
    private RadioButton radioButton;//类型,收入、支出
    private Button buttonAckDate, buttonAckType;


    //该用户所有账单信息列表
    List<BillEntity> listBillEntity = new ArrayList<BillEntity>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        billFragmentLayout = inflater.inflate(R.layout.fragment_bill, container, false);

        initViews();
        getListBillEntity();
        getData();

        //listView设置适配器
        simpleAdapter = new SimpleAdapter(billFragmentLayout.getContext(), listViewData, R.layout.listview_bill_item, key, listViewId);
        listView.setAdapter(simpleAdapter);

        //点击日期控件出现
        textView_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker_bill.setVisibility(View.VISIBLE);
                datePicker_bill.init(2017, 7, 7, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        textView_begin.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                        datePicker_bill.setVisibility(View.GONE);
                    }
                });
            }
        });
        textView_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker_bill.setVisibility(View.VISIBLE);
                datePicker_bill.init(2017, 12, 31, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        textView_end.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                        datePicker_bill.setVisibility(View.GONE);
                    }
                });
            }
        });

        //根据时间筛选账单信息
        buttonAckDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListBillEntityByDate("2017-01-01", "2017-09-30");
                getData();
                //listView设置适配器
                simpleAdapter = new SimpleAdapter(billFragmentLayout.getContext(), listViewData, R.layout.listview_bill_item, key, listViewId);
                listView.setAdapter(simpleAdapter);
            }
        });

        //根据类型筛选账单信息
        buttonAckType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取类型
                radioButton = (RadioButton) billFragmentLayout.findViewById(radioGroup.getCheckedRadioButtonId());
                if("收入".equals(radioButton.getText().toString())) {
                    getListBillEntityByType("收入");
                } else {
                    getListBillEntityByType("支出");
                }
                getData();
                //listView设置适配器
                simpleAdapter = new SimpleAdapter(billFragmentLayout.getContext(), listViewData, R.layout.listview_bill_item, key, listViewId);
                listView.setAdapter(simpleAdapter);

                Toast.makeText(billFragmentLayout.getContext(),"你麻痹", Toast.LENGTH_LONG).show();
            }
        });

        return billFragmentLayout;
    }

    /**
     * 初始化各个控件
     */
    public void initViews() {
        listView = (ListView) billFragmentLayout.findViewById(R.id.listView_bill);
        textView_begin = (TextView) billFragmentLayout.findViewById(R.id.textView_begin);
        textView_end = (TextView) billFragmentLayout.findViewById(R.id.textView_end);
        datePicker_bill = (DatePicker) billFragmentLayout.findViewById(R.id.datePicker_bill);
        radioGroup = (RadioGroup) billFragmentLayout.findViewById(R.id.radioGroup_type2);
        buttonAckDate = (Button) billFragmentLayout.findViewById(R.id.button_ackdate);
        buttonAckType = (Button) billFragmentLayout.findViewById(R.id.button_acktype);

    }
    /**
     * 获得账单listview数据
     */
    public void getData() {
        listViewData.clear();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("time", "时间");
        map.put("type", "类型");
        map.put("money", "金额");
        map.put("explain", "说明");
        listViewData.add(map);

        Calendar calendar= Calendar.getInstance();

        for(int i = 0; i < listBillEntity.size(); i++) {
            map = new HashMap<String, Object>();
            calendar.setTime(listBillEntity.get(i).getDate());
            map.put("time", calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH));
            if(listBillEntity.get(i).getType() == 1) {
                map.put("type", "收入");
            } else {
                map.put("type", "支出");
            }
            map.put("money", listBillEntity.get(i).getMoney()+"");
            map.put("explain", listBillEntity.get(i).getExpalin());
            listViewData.add(map);
        }
    }

    /**
     * 该用户所有账单信息
     */
    public void getListBillEntity() {
        //账单样例数据
        try {
            BillEntity billEntity = new BillEntity("1111111", new SimpleDateFormat("yyyy-MM-dd").parse("2017-05-17"), 0, 2333, "买手机", "zheng123");
            listBillEntity.add(billEntity);

            billEntity = new BillEntity("1111111", new SimpleDateFormat("yyyy-MM-dd").parse("2017-7-7"), 1, 10000, "工资到账", "zheng123");
            listBillEntity.add(billEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据起始时间、终止时间的到账单信息
     */
    public void getListBillEntityByDate(String begin, String end) {
        listBillEntity.clear();
        //账单样例数据
        try {
            BillEntity billEntity = new BillEntity("1111111", new SimpleDateFormat("yyyy-MM-dd").parse("2017-05-17"), 0, 2333, "买手机", "zheng123");
            listBillEntity.add(billEntity);

            billEntity = new BillEntity("1111111", new SimpleDateFormat("yyyy-MM-dd").parse("2017-7-7"), 1, 10000, "工资到账", "zheng123");
            listBillEntity.add(billEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据类型得到账单信息
     */
    public void getListBillEntityByType(String type) {
        listBillEntity.clear();
        //账单样例数据
        try {
            BillEntity billEntity = new BillEntity("1111111", new SimpleDateFormat("yyyy-MM-dd").parse("2017-7-7"), 1, 10000, "工资到账", "zheng123");
            listBillEntity.add(billEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
