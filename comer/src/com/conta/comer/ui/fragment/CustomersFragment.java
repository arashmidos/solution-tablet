package com.conta.comer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.conta.comer.R;
import com.conta.comer.data.listmodel.CustomerListModel;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.adapter.CustomerListAdapter;

import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class CustomersFragment extends BaseListFragment<CustomerListModel, CustomerListAdapter>
{

    public static final String TAG = VisitLinesFragment.class.getSimpleName();

    private Long visitLineId;
    private MainActivity context;
    private CustomerService customerService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        context = (MainActivity) getActivity();
        customerService = new CustomerServiceImpl(context);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected List<CustomerListModel> getDataModel()
    {
        visitLineId = getArguments().getLong("visitLineBackendId");
        return customerService.getAllCustomersListModelByVisitLineBackendId(visitLineId);
    }

    @Override
    protected View getHeaderView()
    {
        return null;
    }

    @Override
    protected CustomerListAdapter getAdapter()
    {
        return new CustomerListAdapter(context, dataModel, visitLineId);
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                CustomerListModel customerListModel = dataModel.get(position);
                Bundle bundle = new Bundle();
                bundle.putLong("customerId", customerListModel.getPrimaryKey());
                context.changeFragment(MainActivity.CUSTOMER_DETAIL_FRAGMENT_ID, bundle, false);
            }
        };
    }

    @Override
    protected String getClassTag()
    {
        return TAG;
    }

    @Override
    protected String getTitle()
    {
        return context.getString(R.string.cusomters);
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.CUSTOMERS_FRAGMENT_ID;
    }
}
