package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.data.searchobject.PaymentSO;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PaymentServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PaymentListAdapter;
import com.parsroyal.solutiontablet.ui.component.ParsRoyalTab;
import com.parsroyal.solutiontablet.util.Empty;

import java.util.List;

/**
 * Created by Arash on 2016-08-15
 */
public class PaymentFragment extends BaseListFragment<PaymentListModel, PaymentListAdapter>
{

    public static final String TAG = PaymentFragment.class.getSimpleName();
    private MainActivity context;
    private PaymentService paymentService;
    private long customerId;
    private long visitId;
    private CustomerService customerService;
    private Customer customer;
    private PaymentSO paymentSO;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.context = (MainActivity) getActivity();
        this.paymentService = new PaymentServiceImpl(context);
        this.customerService = new CustomerServiceImpl(context);
        Bundle arguments = getArguments();
        if (Empty.isNotEmpty(arguments))
        {
            customerId = arguments.getLong(Constants.CUSTOMER_ID);
            visitId = arguments.getLong(Constants.VISIT_ID);
            customer = customerService.getCustomerById(customerId);
            paymentSO = new PaymentSO(customer.getBackendId(), SendStatus.NEW.getId());
        } else
        {
            paymentSO = new PaymentSO(SendStatus.NEW.getId());
        }

        View view = super.onCreateView(inflater, container, savedInstanceState);
        initTabs();
        updateList();
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        initFab();
        return view;
    }

    private void initFab()
    {
        fab.setVisibility(View.VISIBLE);
        if (customer == null)
        {
            fab.setVisibility(View.GONE);
        } else
        {
            fab.setOnClickListener(v ->
            {
                MainActivity mainActivity = (MainActivity) getActivity();
                Bundle args = new Bundle();
                args.putLong(Constants.CUSTOMER_BACKEND_ID, customer.getBackendId());
                args.putLong(Constants.VISIT_ID, visitId);
                mainActivity.changeFragment(MainActivity.PAYMENT_DETAIL_FRAGMENT_ID, args, false);
            });
        }
    }

    private void initTabs()
    {
        {
            ParsRoyalTab tab = new ParsRoyalTab(context);
            tab.setText(getString(R.string.sent));
            tab.setOnClickListener(v ->
            {
                paymentSO.setSent(SendStatus.SENT.getId());
                updateList();
            });
            tabContainer.addTab(tab);
        }

        {
            ParsRoyalTab tab = new ParsRoyalTab(context);
            tab.setText(getString(R.string.not_sent));
            tab.setActivated(true);
            tab.performClick();
            tab.setOnClickListener(v ->
            {
                paymentSO.setSent(SendStatus.NEW.getId());
                updateList();
            });
            tabContainer.addTab(tab);
        }
    }

    @Override
    public View getHeaderView()
    {
        return null;
    }

    @Override
    protected List<PaymentListModel> getDataModel()
    {
        return paymentService.searchForPayments(paymentSO);
    }

    @Override
    protected PaymentListAdapter getAdapter()
    {
        return new PaymentListAdapter(context, getDataModel(),
                customer != null ? customer.getBackendId() : -1);
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        return (parent, view, position, id) ->
        {
            PaymentListModel listModel = dataModel.get(position);
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.CUSTOMER_BACKEND_ID, listModel.getCustomerBackendId());
            bundle.putLong(Constants.PAYMENT_ID, listModel.getPrimaryKey());
            bundle.putLong(Constants.VISIT_ID, visitId);
            bundle.putInt(Constants.PARENT,
                    (customer != null ? MainActivity.CUSTOMER_LIST_FRAGMENT_ID
                            : MainActivity.FUNDS_FRAGMENT_ID));
            context.changeFragment(MainActivity.PAYMENT_DETAIL_FRAGMENT_ID, bundle, true);
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
        return "";
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.PAYMENT_FRAGMENT_ID;
    }
}
