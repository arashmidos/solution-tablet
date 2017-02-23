package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerListAdapter;

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
                CustomerListModel customerListModel = (CustomerListModel) adapter.getItem(position);
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
