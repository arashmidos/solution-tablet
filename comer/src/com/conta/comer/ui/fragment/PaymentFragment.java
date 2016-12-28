package com.conta.comer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.conta.comer.R;
import com.conta.comer.constants.Constants;
import com.conta.comer.constants.SendStatus;
import com.conta.comer.data.entity.Customer;
import com.conta.comer.data.listmodel.PaymentListModel;
import com.conta.comer.data.searchobject.PaymentSO;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.PaymentService;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.service.impl.PaymentServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.adapter.PaymentListAdapter;
import com.conta.comer.ui.component.ContaTab;
import com.conta.comer.util.Empty;

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
    private CustomerService customerService;
    private Customer customer;
    private PaymentSO paymentSO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.context = (MainActivity) getActivity();
        this.paymentService = new PaymentServiceImpl(context);
        this.customerService = new CustomerServiceImpl(context);
        Bundle arguments = getArguments();
        if (Empty.isNotEmpty(arguments))
        {
            customerId = arguments.getLong("customerId");
            customer = customerService.getCustomerById(customerId);
            paymentSO = new PaymentSO(customer.getBackendId(), SendStatus.NEW.getId());
        } else
        {
            paymentSO = new PaymentSO(SendStatus.NEW.getId());
        }

        View view = super.onCreateView(inflater, container, savedInstanceState);
        initTabs();
        updateList();

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
                    paymentSO.setSent(SendStatus.SENT.getId());
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }

        {
            ContaTab tab = new ContaTab(context);
            tab.setText(getString(R.string.not_sent));
            tab.setActivated(true);
            tab.performClick();
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    paymentSO.setSent(SendStatus.NEW.getId());
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }
    }

    @Override
    public View getHeaderView()
    {
        View headerView = getLayoutInflater(getArguments()).inflate(R.layout.list_header_n_customers, null);
        ImageButton addBtn = (ImageButton) headerView.findViewById(R.id.addBtn);
        if (customer == null)
        {
            addBtn.setVisibility(View.GONE);
        } else
        {
            addBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    Bundle args = new Bundle();
                    args.putLong(Constants.CUSTOMER_BACKEND_ID, customer.getBackendId());
                    mainActivity.changeFragment(MainActivity.PAYMENT_DETAIL_FRAGMENT_ID, args, false);
                }
            });
        }
        return headerView;
    }

    @Override
    protected List<PaymentListModel> getDataModel()
    {
        return paymentService.searchForPayments(paymentSO);
    }

    @Override
    protected PaymentListAdapter getAdapter()
    {
        return new PaymentListAdapter(context, getDataModel(), customer != null ? customer.getBackendId() : -1);
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                PaymentListModel listModel = dataModel.get(position);
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.CUSTOMER_BACKEND_ID, listModel.getCustomerBackendId());
                bundle.putLong(Constants.PAYMENT_ID, listModel.getPrimaryKey());
                bundle.putInt(Constants.PARENT,
                        (customer != null ? MainActivity.CUSTOMER_LIST_FRAGMENT_ID : MainActivity.FUNDS_FRAGMENT_ID));
                context.changeFragment(MainActivity.PAYMENT_DETAIL_FRAGMENT_ID, bundle, true);
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
        return "";
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.PAYMENT_FRAGMENT_ID;
    }
}
