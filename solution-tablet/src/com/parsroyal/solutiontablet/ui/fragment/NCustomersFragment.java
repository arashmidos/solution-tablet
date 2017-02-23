package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.entity.City;
import com.parsroyal.solutiontablet.data.entity.Province;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.searchobject.NCustomerSO;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.NCustomersListAdapter;
import com.parsroyal.solutiontablet.ui.component.ParsRoyalTab;
import com.parsroyal.solutiontablet.util.Empty;

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
            ParsRoyalTab tab = new ParsRoyalTab(context);
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
            ParsRoyalTab tab = new ParsRoyalTab(context);
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
                mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID, true);
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
        return new NCustomersListAdapter(context, getDataModel(), nCustomerSO);
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
