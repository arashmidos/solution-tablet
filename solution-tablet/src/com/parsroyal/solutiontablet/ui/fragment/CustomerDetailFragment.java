package com.parsroyal.solutiontablet.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.LocationService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.service.order.SaleOrderService;
import com.parsroyal.solutiontablet.service.order.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class CustomerDetailFragment extends BaseFragment
{
    public static final String TAG = CustomerDetailFragment.class.getSimpleName();
    @BindView(R.id.phoneNumberTv)
    TextView phoneNumberTv;
    @BindView(R.id.codeTv)
    TextView codeTv;
    @BindView(R.id.fullNameTv)
    TextView fullNameTv;
    @BindView(R.id.activityTv)
    TextView activityTv;
    @BindView(R.id.cellPhoneTv)
    TextView cellPhoneTv;
    @BindView(R.id.addressTv)
    TextView addressTv;

    private MainActivity mainActivity;
    private CustomerService customerService;

    private SaleOrderService orderService;
    private LocationService locationService;
    private long customerId;
    private CustomerDto customer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();
        customerService = new CustomerServiceImpl(mainActivity);
        locationService = new LocationServiceImpl(mainActivity);
        orderService = new SaleOrderServiceImpl(mainActivity);

        Bundle arguments = getArguments();
        customerId = arguments.getLong("customerId");
        customer = customerService.getCustomerDtoById(customerId);

        View view = inflater.inflate(R.layout.fragment_customer_detail, null);
        ButterKnife.bind(this, view);

        fullNameTv.setText(customer.getFullName());
        codeTv.setText(customer.getCode());
        phoneNumberTv.setText(customer.getPhoneNumber());
        cellPhoneTv.setText(customer.getCellPhone());
        addressTv.setText(customer.getAddress());
        activityTv.setText(Empty.isNotEmpty(customer.getActivityTitle()) ? customer.getActivityTitle() : "--");

        return view;
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.CUSTOMER_DETAIL_FRAGMENT_ID;
    }

    @OnClick({R.id.saveEnteringBtn, R.id.performanceBtn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.saveEnteringBtn:
                doEnter();
                break;
            case R.id.performanceBtn:
                Bundle args = new Bundle();
                args.putLong(Constants.CUSTOMER_BACKEND_ID, customer.getBackendId());
                mainActivity.changeFragment(MainActivity.KPI_CUSTOMER_FRAGMENT_ID, args, false);
                break;
        }
    }

    private void doEnter()
    {
        try
        {
            final Long visitInformationId = customerService.startVisiting(customer.getBackendId());

            locationService.findCurrentLocation(new FindLocationListener()
            {
                @Override
                public void foundLocation(Location location)
                {
                    try
                    {
                        customerService.updateVisitLocation(visitInformationId, location);
                    } catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                @Override
                public void timeOut()
                {
                }
            });

            orderService.deleteForAllCustomerOrdersByStatus(customer.getBackendId(), SaleOrderStatus.DRAFT.getId());
            Bundle args = new Bundle();
            args.putLong(Constants.VISIT_ID, visitInformationId);
            args.putLong(Constants.CUSTOMER_ID, customerId);
            mainActivity.changeFragment(MainActivity.VISIT_DETAIL_FRAGMENT_ID, args, false);


        } catch (BusinessException e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(mainActivity, e);
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(mainActivity, new UnknownSystemException(e));
        } finally
        {
            dismissProgressDialog();
        }
    }
}
