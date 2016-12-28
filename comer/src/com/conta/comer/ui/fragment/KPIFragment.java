package com.conta.comer.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.conta.comer.R;
import com.conta.comer.constants.Constants;
import com.conta.comer.data.model.KPIDto;
import com.conta.comer.exception.ContaBusinessException;
import com.conta.comer.exception.UnknownSystemException;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.KPIService;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.service.impl.KPIServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.adapter.KPIListAdapter;
import com.conta.comer.ui.component.GaugeView;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.Empty;
import com.conta.comer.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arash on 2016-09-21.
 */
public class KPIFragment extends BaseContaFragment implements ResultObserver
{
    public static final String TAG = KPIFragment.class.getSimpleName();
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

    private MainActivity mainActivity;
    private CustomerService customerService;
    private KPIService kpiService;
    private long customerBackendId = -1;
    private boolean isCustomerKPI;
    private KPIDto kpiDto = null;
    private ListAdapter adapter;

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

        View view = inflater.inflate(R.layout.fragment_kpi, null);
        ButterKnife.bind(this, view);

        new AsyncKPILoader().execute(customerBackendId);

        return view;
    }

    @Override
    public int getFragmentId()
    {
        return isCustomerKPI ? MainActivity.KPI_CUSTOMER_FRAGMENT_ID : MainActivity.KPI_SALESMAN_FRAGMENT_ID;
    }

    @Override
    public void publishResult(final ContaBusinessException ex)
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
            } catch (ContaBusinessException ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                ToastUtil.toastError(getActivity(), ex);
            } catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
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
                adapter = new KPIListAdapter((MainActivity) getActivity(), kpiDto.getDetails());
                listView.setAdapter(adapter);
            } else
            {
                mainActivity.removeFragment(KPIFragment.this);
            }
        }
    }

}
