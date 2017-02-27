package com.parsroyal.solutiontablet.ui.fragment;

import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.KeyValueBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.model.KPIDetail;
import com.parsroyal.solutiontablet.data.model.KPIDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.KPIService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.KPIServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.KPIListAdapter;
import com.parsroyal.solutiontablet.ui.component.GaugeView;
import com.parsroyal.solutiontablet.ui.component.XYMarkerView;
import com.parsroyal.solutiontablet.ui.formatter.XAxisValueFormatter;
import com.parsroyal.solutiontablet.ui.formatter.YAxisValueFormatter;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arash on 2016-09-21.
 */
public class KPIFragment extends BaseFragment implements ResultObserver, OnChartValueSelectedListener
{
    public static final String TAG = KPIFragment.class.getSimpleName();
    protected RectF mOnValueSelectedRectF = new RectF();
    @BindView(R.id.gauge_value1)
    TextView gaugeValueTv1;
    @BindView(R.id.gauge_value2)
    TextView gaugeValueTv2;
    @BindView(R.id.gauge_view1)
    GaugeView gaugeView1;
    @BindView(R.id.gauge_view2)
    GaugeView gaugeView2;
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.chart)
    BarChart barChart;
    private MainActivity mainActivity;
    private CustomerService customerService;
    private KPIService kpiService;
    private long customerBackendId = -1;
    private boolean isCustomerKPI;
    private KPIDto kpiDto = null;
    private ListAdapter adapter;
    private Typeface mTfLight;
    private List<KPIDetail> kpiDetails;
    private KeyValueBizImpl keyValueBiz;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();
        customerService = new CustomerServiceImpl(mainActivity);
        kpiService = new KPIServiceImpl(mainActivity);

        Bundle arguments = getArguments();
        if (Empty.isNotEmpty(arguments))
        {
            customerBackendId = arguments.getLong(Constants.CUSTOMER_BACKEND_ID, -1);
        }
        isCustomerKPI = customerBackendId != -1;

        keyValueBiz = new KeyValueBizImpl(getActivity());
        KeyValue salesmanId = keyValueBiz.findByKey(ApplicationKeys.SALESMAN_ID);
        if (Empty.isEmpty(salesmanId))
        {
            View view = inflater.inflate((R.layout.view_error_page), null);
            TextView errorView = (TextView) view.findViewById(R.id.error_msg);
            errorView.setText(errorView.getText() + "\n\n" + "به قسمت تنظیمات مراجعه کنید");
            return view;
        }

        View view = inflater.inflate(R.layout.fragment_kpi, null);
        ButterKnife.bind(this, view);

        new AsyncKPILoader().execute(customerBackendId);

        return view;
    }

    private void customizeChart()
    {
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/IRANSansMobile_Light.ttf");
        barChart.setOnChartValueSelectedListener(this);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);
        barChart.setPinchZoom(true);
        barChart.setDrawGridBackground(false);

        IAxisValueFormatter xAxisFormatter = new XAxisValueFormatter(kpiDetails);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new YAxisValueFormatter();

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(getActivity(), xAxisFormatter);
        mv.setChartView(barChart); // For bounds control
        barChart.setMarker(mv); // Set the marker to the chart

        setData();
    }

    private void setData()
    {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < kpiDetails.size(); i++)
        {
            yVals1.add(new BarEntry(i, kpiDetails.get(i).getValue().floatValue()));
        }

        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0)
        {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else
        {
            set1 = new BarDataSet(yVals1, "وضعیت عملکرد");
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            barChart.setData(data);
        }

        barChart.invalidate();
    }

    @Override
    public int getFragmentId()
    {
        return isCustomerKPI ? MainActivity.KPI_CUSTOMER_FRAGMENT_ID : MainActivity.KPI_SALESMAN_FRAGMENT_ID;
    }

    @Override
    public void publishResult(final BusinessException ex)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                toastError(ex);
                mainActivity.removeFragment(KPIFragment.this);
                mainActivity.changeSidebarItem(0);
            }
        });
    }

    @Override
    public void publishResult(final String message)
    {
    }

    @Override
    public void finished(boolean result)
    {

    }

    /**
     * Called when a value has been selected inside the chart.
     *
     * @param e The selected Entry
     * @param h The corresponding highlight object that contains information
     */
    @Override
    public void onValueSelected(Entry e, Highlight h)
    {

    }

    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    @Override
    public void onNothingSelected()
    {
    }

    private class AsyncKPILoader extends AsyncTask<Long, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            showProgressDialog(getString(R.string.message_downloading_data_please_wait));
        }

        @Override
        protected Void doInBackground(Long... params)
        {
            try
            {
                kpiDto = isCustomerKPI ? kpiService.getCustomerKPI(params[0], KPIFragment.this) :
                        kpiService.getSalesmanKPI(KPIFragment.this);
            } catch (BusinessException ex)
            {
                Log.e(TAG, ex.getMessage(), ex);

            } catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            dismissProgressDialog();

            if (Empty.isNotEmpty(kpiDto))
            {
                gaugeView1.setTargetValue(kpiDto.getKpiGauge().floatValue());
                gaugeView2.setTargetValue(0);
                gaugeValueTv1.setText(kpiDto.getKpiGauge() + "");
                gaugeValueTv2.setText(String.valueOf(0));
                kpiDetails = kpiDto.getDetails();

                adapter = new KPIListAdapter((MainActivity) getActivity(), kpiDetails);
                listView.setAdapter(adapter);
                customizeChart();
            } else
            {
                ToastUtil.toastError(getActivity(),
                        getString(R.string.com_parsroyal_solutiontablet_exception_UnknownSystemException));
                mainActivity.removeFragment(KPIFragment.this);
            }
        }
    }
}
