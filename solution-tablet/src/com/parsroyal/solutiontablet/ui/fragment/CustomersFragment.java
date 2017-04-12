package com.parsroyal.solutiontablet.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.SortType;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerListAdapter;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class CustomersFragment extends BaseListFragment<CustomerListModel, CustomerListAdapter>
{
    public static final String TAG = VisitLinesFragment.class.getSimpleName();

    private Long visitLineId;
    private MainActivity context;
    private CustomerService customerService;
    private ImageButton sortButton;
    private int sortType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        context = (MainActivity) getActivity();
        customerService = new CustomerServiceImpl(context);
        View view = super.onCreateView(inflater, container, savedInstanceState);
        buttonPanel.setVisibility(View.VISIBLE);
        buttonPanel.findViewById(R.id.cancelBtn).setVisibility(View.GONE);
        sortButton = (ImageButton) buttonPanel.findViewById(R.id.sort_btn);
        sortButton.setVisibility(View.VISIBLE);
        sortType = SortType.NAME.getId();
        sortButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                CharSequence[] items = {"نام و نام خانوادگی", "فاصله", "داشتن سفارش", "داشتن مرجوعی"};
                builder.setSingleChoiceItems(items, sortType, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int position)
                    {
                        sort(position);
                        sortType = position;
                        dialog.dismiss();
                    }
                })
                        .setTitle(R.string.sort_by);
                builder.create().show();
            }
        });
        return view;
    }

    private void sort(int type)
    {
        switch (type)
        {
            case 0:
                Collections.sort(dataModel, new Comparator<CustomerListModel>()
                {
                    @Override
                    public int compare(CustomerListModel item1, CustomerListModel item2)
                    {
                        return Collator.getInstance(new Locale("fa")).compare(item1.getTitle(), item2.getTitle());
                    }
                });
                adapter.notifyDataSetChanged();
                break;
            case 1:
                Collections.sort(dataModel, new Comparator<CustomerListModel>()
                {
                    @Override
                    public int compare(CustomerListModel item1, CustomerListModel item2)
                    {
                        return item1.getDistance().compareTo(item2.getDistance());
                    }
                });
                adapter.notifyDataSetChanged();
                break;
            case 2:
                break;
            case 3:
                break;

        }
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
