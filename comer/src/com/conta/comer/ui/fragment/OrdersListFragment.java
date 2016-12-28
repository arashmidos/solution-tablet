package com.conta.comer.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.conta.comer.R;
import com.conta.comer.constants.SaleOrderStatus;
import com.conta.comer.data.model.GoodsDtoList;
import com.conta.comer.data.model.SaleOrderListModel;
import com.conta.comer.data.searchobject.SaleOrderSO;
import com.conta.comer.exception.ContaBusinessException;
import com.conta.comer.exception.UnknownSystemException;
import com.conta.comer.service.DataTransferService;
import com.conta.comer.service.impl.DataTransferServiceImpl;
import com.conta.comer.service.impl.SettingServiceImpl;
import com.conta.comer.service.order.SaleOrderService;
import com.conta.comer.service.order.impl.SaleOrderServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.adapter.SaleOrderListAdapter;
import com.conta.comer.ui.component.ContaTab;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.Empty;
import com.conta.comer.util.ToastUtil;
import com.conta.comer.util.constants.ApplicationKeys;

import java.util.List;

/**
 * Created by Mahyar on 8/25/2015.
 */
public class OrdersListFragment extends BaseListFragment<SaleOrderListModel, SaleOrderListAdapter> implements ResultObserver
{

    private static final String TAG = OrdersListFragment.class.getSimpleName();

    private MainActivity context;
    private SaleOrderSO saleOrderSO;
    private SaleOrderService saleOrderService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        context = (MainActivity) getActivity();
        this.saleOrderService = new SaleOrderServiceImpl(context);
        String saleType = new SettingServiceImpl(context).getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

        //Set default list to show
        {
            saleOrderSO = new SaleOrderSO();
            if (Empty.isNotEmpty(getArguments()))
            {
                Object statusId = getArguments().get("statusId");
                saleOrderSO.setStatusId(Empty.isNotEmpty(statusId) ? (Long) statusId : null);
                Object customerBackendId = getArguments().get("customerBackendId");
                saleOrderSO.setCustomerBackendId(Empty.isNotEmpty(customerBackendId) ? (Long) customerBackendId : null);
            }
            if (Empty.isEmpty(saleOrderSO.getStatusId()))
            {
                if (saleType.equals(ApplicationKeys.COLD_SALE))
                {
                    saleOrderSO.setStatusId(SaleOrderStatus.READY_TO_SEND.getId());
                } else
                {
                    saleOrderSO.setStatusId(SaleOrderStatus.INVOICED.getId());
                }
            }

            dataModel = saleOrderService.findOrders(saleOrderSO);
        }

        View view = super.onCreateView(inflater, container, savedInstanceState);

        setupGeneralTabs();
        if (saleType.equals(ApplicationKeys.COLD_SALE))
        {
            setupColdSaleTabs();
        }

        return view;
    }

    private void setupColdSaleTabs()
    {
        {
            ContaTab tab = new ContaTab(context);
            tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.DELIVERABLE.getId()));
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saleOrderSO.setStatusId(SaleOrderStatus.DELIVERABLE.getId());
                    dataModel = saleOrderService.findOrders(saleOrderSO);
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }

        {
            ContaTab tab = new ContaTab(context);
            tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.SENT.getId()));
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saleOrderSO.setStatusId(SaleOrderStatus.SENT.getId());
                    dataModel = saleOrderService.findOrders(saleOrderSO);
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }

        {
            ContaTab tab = new ContaTab(context);
            tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.READY_TO_SEND.getId()));
            tab.setActivated(true);
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saleOrderSO.setStatusId(SaleOrderStatus.READY_TO_SEND.getId());
                    dataModel = saleOrderService.findOrders(saleOrderSO);
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }

    }

    private void setupGeneralTabs()
    {

        //Rejected and sent Order
        {
            ContaTab tab = new ContaTab(context);
            tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.REJECTED_SENT.getId()));
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saleOrderSO.setStatusId(SaleOrderStatus.REJECTED_SENT.getId());
                    dataModel = saleOrderService.findOrders(saleOrderSO);
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }

        //Rejected Order
        {
            ContaTab tab = new ContaTab(context);
            tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.REJECTED.getId()));
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saleOrderSO.setStatusId(SaleOrderStatus.REJECTED.getId());
                    dataModel = saleOrderService.findOrders(saleOrderSO);
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }

        {
            ContaTab tab = new ContaTab(context);
            tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.CANCELED.getId()));
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saleOrderSO.setStatusId(SaleOrderStatus.CANCELED.getId());
                    dataModel = saleOrderService.findOrders(saleOrderSO);
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }
        {
            ContaTab tab = new ContaTab(context);
            tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.SENT_INVOICE.getId()));
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saleOrderSO.setStatusId(SaleOrderStatus.SENT_INVOICE.getId());
                    dataModel = saleOrderService.findOrders(saleOrderSO);
                    updateList();
                }
            });
            tabContainer.addTab(tab);
        }

        {
            ContaTab tab = new ContaTab(context);
            tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.INVOICED.getId()));
            tab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saleOrderSO.setStatusId(SaleOrderStatus.INVOICED.getId());
                    dataModel = saleOrderService.findOrders(saleOrderSO);
                    updateList();
                }
            });
            tabContainer.addTab(tab);

        }
    }

    @Override
    protected List<SaleOrderListModel> getDataModel()
    {
        return dataModel;
    }

    @Override
    protected View getHeaderView()
    {
        return null;
    }

    @Override
    protected SaleOrderListAdapter getAdapter()
    {
        return new SaleOrderListAdapter(context, getDataModel(), saleOrderSO);
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SaleOrderListModel selectedOrder = dataModel.get(position);
                String saleType = new SettingServiceImpl(getActivity()).getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
                Bundle args = new Bundle();
                args.putLong("orderId", selectedOrder.getId());
                args.putString("saleType", saleType);
                Long orderStatus = selectedOrder.getStatus();
                if (orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
                        orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()))
                {
                    requestLiveData(selectedOrder.getId(),selectedOrder.getCustomerBackendId(),orderStatus,saleType);
                } else
                {
                    context.changeFragment(MainActivity.ORDER_DETAIL_FRAGMENT_ID, args, false);
                }
            }
        };
    }

    private void requestLiveData(final Long id, final Long customerBackendId, final Long orderStatus, final String saleType)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    DataTransferService dataTransferService = new DataTransferServiceImpl(getActivity());
                    GoodsDtoList rejectedGoodsList = dataTransferService.getRejectedData(OrdersListFragment.this, customerBackendId);
                    if (rejectedGoodsList != null)
                    {
                        final Bundle args = new Bundle();
                        args.putLong("orderId", id);
                        args.putLong("orderStatus", orderStatus);
                        args.putString("saleType",saleType );
                        args.putSerializable("rejectedList", rejectedGoodsList);
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ((MainActivity)getActivity()).changeFragment(MainActivity.ORDER_DETAIL_FRAGMENT_ID, args, false);
                            }
                        });
                    } else
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                toastError(getString(R.string.err_reject_order_detail_not_accessable));
                            }
                        });
                    }
                } catch (final ContaBusinessException ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            toastError(ex);
                        }
                    });
                } catch (final Exception ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            toastError(new UnknownSystemException(ex));
                        }
                    });
                }
            }
        });

        thread.start();
    }

    @Override
    protected String getClassTag()
    {
        return TAG;
    }

    @Override
    protected String getTitle()
    {
        return "Title";
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.ORDERS_LIST_FRAGMENT;
    }

    @Override
    public void publishResult(final ContaBusinessException ex)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtil.toastError(getActivity(), getErrorString(ex));
            }
        });
    }

    @Override
    public void publishResult(final String message)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtil.toastMessage(getActivity(), message);
            }
        });
    }

    @Override
    public void finished(boolean result)
    {

    }
}
