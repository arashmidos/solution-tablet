package com.conta.comer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.conta.comer.R;
import com.conta.comer.data.entity.City;
import com.conta.comer.data.entity.Province;
import com.conta.comer.data.listmodel.NCustomerListModel;
import com.conta.comer.data.searchobject.NCustomerSO;
import com.conta.comer.service.BaseInfoService;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.impl.BaseInfoServiceImpl;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.adapter.NCustomersListAdapter;
import com.conta.comer.ui.component.ContaTab;
import com.conta.comer.util.Empty;

import java.util.List;

/**
 * Created by Mahyar on 7/13/2015.
 */
public class NCustomersFragment extends BaseListFragment<NCustomerListModel, NCustomersListAdapter>
{

    public static final String TAG = NCustomersFragment.class.getSimpleName();
    private MainActivity context;
    private CustomerService customerService;
    private BaseInfoService baseInfoService;
    private NCustomerSO nCustomerSO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.context = (MainActivity) getActivity();
        this.customerService = new CustomerServiceImpl(context);
        this.baseInfoService = new BaseInfoServiceImpl(context);
        this.nCustomerSO = new NCustomerSO();
        nCustomerSO.setSent(0);
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initTabs();
        return view;
    }

    private void initTabs()
    {

        {
            ContaTab tab = new ContaTab(context);
            tab.setText(getString(R.string.sent));
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    nCustomerSO.setSent(1);
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }

        {
            ContaTab tab = new ContaTab(context);
            tab.setText(getString(R.string.not_sent));
            tab.setActivated(true);
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    nCustomerSO.setSent(0);
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        nCustomerSO.setSent(0);
        updateList();
    }

    @Override
    public View getHeaderView()
    {
        View headerView = getLayoutInflater(getArguments()).inflate(R.layout.list_header_n_customers, null);
        ImageButton addBtn = (ImageButton) headerView.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                List<Province> provinceList = baseInfoService.getAllProvinces();
                List<City> cityList = baseInfoService.getAllCities();
                if (Empty.isEmpty(cityList))
                {
                    toastError(R.string.message_cities_information_not_found);
                    return;
                }
                if (Empty.isEmpty(provinceList))
                {
                    toastError(R.string.message_provinces_information_not_foun);
                    return;
                }
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID, false);
            }
        });
        return headerView;
    }

    @Override
    protected List<NCustomerListModel> getDataModel()
    {
        return customerService.searchForNCustomers(nCustomerSO);
    }

    @Override
    protected NCustomersListAdapter getAdapter()
    {
        return new NCustomersListAdapter(context, getDataModel());
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        return null;
    }

    @Override
    protected String getClassTag()
    {
        return TAG;
    }

    @Override
    protected String getTitle()
    {
        return "";
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.NEW_CUSTOMER_FRAGMENT_ID;
    }
}
