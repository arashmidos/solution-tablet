package com.conta.comer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.conta.comer.R;
import com.conta.comer.data.listmodel.VisitLineListModel;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.adapter.VisitLinesAdapter;

import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class VisitLinesFragment extends BaseListFragment<VisitLineListModel, VisitLinesAdapter>
{

    public static final String TAG = VisitLinesFragment.class.getSimpleName();

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
    protected List<VisitLineListModel> getDataModel()
    {
        return customerService.getAllVisitLinesListModel();
    }

    @Override
    protected View getHeaderView()
    {
        return null;
    }

    @Override
    protected VisitLinesAdapter getAdapter()
    {
        return new VisitLinesAdapter(context, dataModel);
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                VisitLineListModel visitLine = dataModel.get(position);
                Bundle bundle = new Bundle();
                bundle.putLong("visitLineBackendId", visitLine.getPrimaryKey());
                context.changeFragment(MainActivity.CUSTOMERS_FRAGMENT_ID, bundle, true);
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
        return context.getString(R.string.visit_lines);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        context.changeSidebarItem(MainActivity.CUSTOMER_LIST_FRAGMENT_ID);
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.CUSTOMER_LIST_FRAGMENT_ID;
    }
}
