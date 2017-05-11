package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.util.Iterator;
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
    private ImageButton filterButton;
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
        filterButton = (ImageButton) buttonPanel.findViewById(R.id.filter_btn);
        sortButton.setVisibility(View.VISIBLE);
        filterButton.setVisibility(View.VISIBLE);
        sortType = SortType.DEFAULT.getId();
        sortButton.setOnClickListener(view1 ->
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            CharSequence[] items = {"حالت پیشفرض", "نام و نام خانوادگی ( الف - ی )"
                    , "نام و نام خانوادگی ( ی - الف )", "شماره مشتری ( صعودی )", "شماره مشتری ( نزولی )"};
            builder.setSingleChoiceItems(items, sortType, (dialog, position) ->
            {
                sort(position);
                sortType = position;
                dialog.dismiss();
            })
                    .setTitle(R.string.sort_by);
            builder.create().show();
        });

        filterButton.setOnClickListener(view12 -> showFilter());
        return view;
    }

    private void showFilter()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null)
        {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        FilterDialog filterDialog = FilterDialog.newInstance();
        filterDialog.setOnClickListener(new FilterDialog.FilterClickListener()
        {
            @Override
            public void doFilter(int distance, boolean hasOrder, boolean hasNone)
            {
                adapter.setDataModel(getDataModel());

                for (Iterator<CustomerListModel> it = adapter.getDataModel().iterator(); it.hasNext(); )
                {
                    CustomerListModel listModel = it.next();
                    if (listModel.hasOrder() != hasOrder || listModel.hasRejection() != hasNone)
                    {
                        it.remove();
                    } else if (distance != 0)//If meter filter set
                    {
                        //If has not location or it's greater than user filter
                        if (listModel.getDistance() == -1 || listModel.getDistance() > distance)
                        {
                            it.remove();
                        }
                    }
                }
                sort(sortType);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void clearFilter()
            {
                adapter.setDataModel(getDataModel());
                sort(sortType);
                adapter.notifyDataSetChanged();
            }
        });
        filterDialog.show(ft, "dialog");
    }

    private void sort(int type)
    {
        List<CustomerListModel> dataModel = adapter.getDataModel();
        switch (type)
        {
            case 0:
                Collections.sort(dataModel, (item1, item2) -> item1.getPrimaryKey().compareTo(item2.getPrimaryKey()));
                break;
            case 1:
                Collections.sort(dataModel, (item1, item2) -> Collator.getInstance(new Locale("fa")).compare(item1.getTitle(), item2.getTitle()));
                break;
            case 2:
                Collections.sort(dataModel, (item1, item2) -> Collator.getInstance(new Locale("fa")).compare(item2.getTitle(), item1.getTitle()));
                break;
            case 3:
                Collections.sort(dataModel, (item1, item2) -> item1.getCodeNumber().compareTo(item2.getCodeNumber()));
                break;
            case 4:
                Collections.sort(dataModel, (item1, item2) -> item2.getCodeNumber().compareTo(item1.getCodeNumber()));
                break;
        }
        adapter.setDataModel(dataModel);
        adapter.notifyDataSetChanged();
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
        return (parent, view, position, id) ->
        {
            searchTxt.setText("");
            CustomerListModel customerListModel = (CustomerListModel) adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putLong("customerId", customerListModel.getPrimaryKey());
            context.changeFragment(MainActivity.CUSTOMER_DETAIL_FRAGMENT_ID, bundle, false);
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

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }
}
