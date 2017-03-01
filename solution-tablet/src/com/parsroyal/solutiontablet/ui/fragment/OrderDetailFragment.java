package com.parsroyal.solutiontablet.ui.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.SaleOrderNotFoundException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.order.SaleOrderService;
import com.parsroyal.solutiontablet.service.order.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.ui.component.ParsRoyalTab;
import com.parsroyal.solutiontablet.ui.component.TabContainer;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import java.util.List;
import java.util.Locale;

/**
 * Created by Mahyar on 8/25/2015.
 * Edited by Arash on 6/29/2016
 */
public class OrderDetailFragment extends BaseFragment
{
    public static final String TAG = OrderDetailFragment.class.getSimpleName();
    private MainActivity context;
    private SaleOrderService saleOrderService;
    private BaseInfoService baseInfoService;
    private SaleOrderDto order;

    private Long orderId;
    private String saleType;

    private TabContainer tabContainer;
    private LinearLayout actionsLayout;
    private Button deliverOrderBtn;
    private Button cancelOrderBtn;
    private Button saveOrderBtn;

    private Spinner paymentTypeSp;
    private EditText orderDescriptionTxt;
    private Long orderStatus;
    private GoodsDtoList rejectedGoodsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        try
        {
            context = (MainActivity) getActivity();
            saleOrderService = new SaleOrderServiceImpl(context);
            baseInfoService = new BaseInfoServiceImpl(context);

            orderId = getArguments().getLong("orderId");
            saleType = getArguments().getString("saleType");

            order = saleOrderService.findOrderDtoById(orderId);
            orderStatus = order.getStatus();
            View view = context.getLayoutInflater().inflate(R.layout.fragment_order_detail, null);
            tabContainer = (TabContainer) view.findViewById(R.id.tabContainer);
            actionsLayout = (LinearLayout) view.findViewById(R.id.actionsLayout);

            if (isRejected())
            {
                rejectedGoodsList = (GoodsDtoList) getArguments().getSerializable("rejectedList");
            }
            {
                ParsRoyalTab tab = new ParsRoyalTab(context);

                tab.setText(String.format(Locale.US, getString(R.string.title_items_x), getProperTitle()));

                tab.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        FragmentManager childFragMan = getChildFragmentManager();
                        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
                        OrderItemsFragment orderItemsFragment = new OrderItemsFragment();

                        Bundle args = new Bundle();
                        args.putLong("orderId", order.getId());
                        args.putBoolean("disabled", isDisable());
                        args.putLong("orderStatus", orderStatus);
                        args.putSerializable("rejectedList", rejectedGoodsList);
                        orderItemsFragment.setArguments(args);

                        childFragTrans.replace(R.id.orderDetailContentFrame, orderItemsFragment);
                        childFragTrans.commit();

                    }
                });
                tabContainer.addTab(tab);
            }

            {
                ParsRoyalTab tab = new ParsRoyalTab(context);
                tab.setText(String.format(Locale.US, getString(R.string.title_x_detail), getProperTitle()));
                tab.setActivated(true);
                tab.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        FragmentManager childFragMan = getChildFragmentManager();
                        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
                        OrderInfoFragment orderInfoFrg = new OrderInfoFragment();
                        childFragTrans.replace(R.id.orderDetailContentFrame, orderInfoFrg);
                        childFragTrans.commit();

                    }
                });
                tabContainer.addTab(tab);
                tabContainer.activeTab(tab);
            }

            {

                if (orderStatus.equals(SaleOrderStatus.DRAFT.getId())
                        || orderStatus.equals(SaleOrderStatus.READY_TO_SEND.getId())
                        || orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()))

                {
                    ParsRoyalTab tab = new ParsRoyalTab(context);
                    tab.setText(getString(R.string.title_goods_list));
                    tab.setActivated(true);
                    tab.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            FragmentManager childFragMan = getChildFragmentManager();
                            FragmentTransaction childFragTrans = childFragMan.beginTransaction();

                            GoodsListFragment goodsListFragment = new GoodsListFragment();
                            Bundle args = new Bundle();
                            args.putLong("orderId", orderId);
                            args.putSerializable("rejectedList", rejectedGoodsList);
                            goodsListFragment.setArguments(args);

                            childFragTrans.replace(R.id.orderDetailContentFrame, goodsListFragment);
                            childFragTrans.commit();

                        }
                    });
                    tabContainer.addTab(tab);
                    tabContainer.activeTab(tab);
                }
            }

            {
                actionsLayout.addView(createActionButton(context.getString(R.string.title_cancel), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        context.removeFragment(OrderDetailFragment.this);
                    }
                }));

                if (orderStatus.equals(SaleOrderStatus.DELIVERABLE.getId()))
                {

                    cancelOrderBtn = createActionButton(
                            String.format(Locale.US, getString(R.string.title_cancel_sale_x), getProperTitle()),
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    showSaveOrderConfirmDialog(
                                            String.format(Locale.US, getString(R.string.title_cancel_sale_x),
                                                    getProperTitle()), SaleOrderStatus.CANCELED.getId());
                                }
                            });

                    deliverOrderBtn = createActionButton(getString(R.string.title_deliver_sale_x)
                            + getProperTitle(), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (validateOrderForDeliver())
                            {
                                showSaveOrderConfirmDialog(getString(R.string.title_deliver_sale_x)
                                        + getProperTitle(), SaleOrderStatus.INVOICED.getId());
                            }
                        }
                    });

                    actionsLayout.addView(cancelOrderBtn);
                    actionsLayout.addView(deliverOrderBtn);
                }

                if (SaleOrderStatus.READY_TO_SEND.getId().equals(orderStatus)
                        || SaleOrderStatus.DRAFT.getId().equals(orderStatus)
                        || SaleOrderStatus.REJECTED_DRAFT.getId().equals(orderStatus))

                {
                    saveOrderBtn = createActionButton(context.getString(R.string.title_save_order), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            order = saleOrderService.findOrderDtoById(orderId);
                            if (validateOrderForSave())
                            {
                                if (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()))
                                {
                                    showSaveOrderConfirmDialog(getString(R.string.title_save_order), SaleOrderStatus.REJECTED.getId());
                                } else
                                {
                                    if (saleType.equals(ApplicationKeys.COLD_SALE))
                                    {
                                        showSaveOrderConfirmDialog(getString(R.string.title_save_order), SaleOrderStatus.READY_TO_SEND.getId());
                                    } else
                                    {

                                        showSaveOrderConfirmDialog(getString(R.string.title_save_order), SaleOrderStatus.INVOICED.getId());
                                    }
                                }
                            }
                        }
                    });
                    actionsLayout.addView(saveOrderBtn);
                }
            }
            return view;
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
            return inflater.inflate(R.layout.view_error_page, null);
        }
    }

    /*
    @return true if it's one of the REJECTED states
     */
    private boolean isRejected()
    {
        return (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
                orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
                orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()));

    }

    /*
     @return Proper title for which could be "Rejected", "Order" or "Invoice"
     */
    private String getProperTitle()
    {
        if (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
                orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
                orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()))
        {
            return getString(R.string.title_reject);
        } else if (saleType.equals(ApplicationKeys.COLD_SALE))
        {
            return getString(R.string.title_order);
        } else
            return getString(R.string.title_factor);
    }

    private boolean validateOrderForSave()
    {
        if (order.getOrderItems().size() == 0)
        {
            ToastUtil.toastError(context, getProperTitle() + getString(R.string.message_x_has_no_item_for_save));
            return false;
        }
        return true;
    }

    private boolean validateOrderForDeliver()
    {
        if (order.getOrderItems().size() == 0)
        {
            ToastUtil.toastError(context, R.string.message_order_has_no_item);
            return false;
        }
        return true;
    }

    private void showSaveOrderConfirmDialog(String title, final Long statusId)
    {
        DialogUtil.showConfirmDialog(context, title,
                context.getString(R.string.message_are_you_sure), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        saveOrder(statusId);
                        context.removeFragment(OrderDetailFragment.this);
                    }
                });
    }

    private void saveOrder(Long statusId)
    {
        try
        {
            order.setStatus(statusId);

            LabelValue selectedPaymentItem = (LabelValue) paymentTypeSp.getSelectedItem();
            if (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId())
                    || orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId())
                    || orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId())
                    )
            {
                //Add reason or reject to orders
            } else
            {
                order.setPaymentTypeBackendId(selectedPaymentItem.getValue());
            }

            String description = orderDescriptionTxt.getText().toString();
            order.setDescription(description);

            saleOrderService.saveOrder(order);
        } catch (BusinessException ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(context, ex);
        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(context, new UnknownSystemException(ex));
        }

    }

    private Button createActionButton(String buttonTitle, View.OnClickListener onClickListener)
    {
        Button button = new Button(context);
        button.setText(buttonTitle);
        button.setOnClickListener(onClickListener);
        return button;
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.ORDER_DETAIL_FRAGMENT_ID;
    }

    private boolean isDisable()
    {
        if (orderStatus.equals(SaleOrderStatus.CANCELED.getId())
                || orderStatus.equals(SaleOrderStatus.INVOICED.getId())
                || orderStatus.equals(SaleOrderStatus.SENT.getId())
                || orderStatus.equals(SaleOrderStatus.SENT_INVOICE.getId())
                || orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId())
                || orderStatus.equals(SaleOrderStatus.REJECTED.getId())
                )
        {
            return true;
        }
        return false;
    }

    @SuppressLint("ValidFragment")
    public class OrderInfoFragment extends BaseFragment
    {
        public final String TAG = OrderInfoFragment.class.getSimpleName();
        private TextView customerNameTv;
        private TextView orderNumberTv;
        private TextView orderNumberLabel;
        private TextView orderDateTv;
        private TextView orderDateLabel;
        private TextView orderAmountTv;
        private TextView orderAmountLabel;
        private TextView orderDescriptionLabel;
        private TextView paymentTypeLabel;

        public OrderInfoFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            try
            {
                //Refresh order item
                order = saleOrderService.findOrderDtoById(orderId);
                if (Empty.isNotEmpty(order))
                {

                    View view = inflater.inflate(R.layout.fragment_order_info, null);

                    customerNameTv = (TextView) view.findViewById(R.id.customerNameTv);
                    paymentTypeLabel = (TextView) view.findViewById(R.id.paymentTypeLabel);
                    orderNumberTv = (TextView) view.findViewById(R.id.orderNumberTv);
                    orderNumberLabel = (TextView) view.findViewById(R.id.orderNoLabel);
                    orderDateTv = (TextView) view.findViewById(R.id.orderDateTv);
                    orderDateLabel = (TextView) view.findViewById(R.id.orderDateLabel);
                    orderAmountTv = (TextView) view.findViewById(R.id.orderAmountTv);
                    orderAmountLabel = (TextView) view.findViewById(R.id.orderAmountLabel);
                    orderDescriptionLabel = (TextView) view.findViewById(R.id.orderDescriptionLabel);
                    orderDescriptionTxt = (EditText) view.findViewById(R.id.orderDescriptionTxt);
                    orderDescriptionTxt.setEnabled(!isDisable());
                    paymentTypeSp = (Spinner) view.findViewById(R.id.paymentTypeSp);
                    paymentTypeSp.setEnabled(!isDisable());
                    tabContainer = (TabContainer) view.findViewById(R.id.tabContainer);

                    String title = getProperTitle();

                    orderNumberLabel.setText(String.format(Locale.US, getString(R.string.number_x), title));
                    orderDateLabel.setText(String.format(Locale.US, getString(R.string.date_x), title));
                    orderAmountLabel.setText(String.format(Locale.US, getString(R.string.amount_x), title));
                    orderDescriptionLabel.setText(String.format(Locale.US, getString(R.string.description_x), title));

                    if (isRejected())
                    {
                        paymentTypeLabel.setText(getString(R.string.reject_reason_title));
                    } else
                    {
                        paymentTypeLabel.setText(getString(R.string.payment_type));
                    }

                    Customer customer = order.getCustomer();
                    if (Empty.isNotEmpty(customer) && Empty.isNotEmpty(customer.getFullName()))
                    {
                        customerNameTv.setText(customer.getFullName());
                    }

                    Long number = order.getNumber();
                    orderNumberTv.setText(Empty.isNotEmpty(number) && order.getNumber() != 0 ? String.valueOf(number) : "--");

                    String orderDate = order.getDate();
                    orderDateTv.setText(Empty.isNotEmpty(orderDate) ? orderDate : "--");

                    Double orderAmount = Double.valueOf(order.getAmount()) / 1000D;
                    orderAmountTv.setText(Empty.isNotEmpty(orderAmount) ? NumberUtil.getCommaSeparated(orderAmount) : "--");

                    final List<LabelValue> paymentTypeList = baseInfoService.getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.PAYMENT_TYPE.getId());
                    if (Empty.isNotEmpty(paymentTypeList))
                    {
                        if (isRejected())
                        {
                            List<LabelValue> rejectReason = baseInfoService.getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.REJECT_TYPE.getId());
                            paymentTypeSp.setAdapter(new LabelValueArrayAdapter(context, rejectReason));
                        } else
                        {
                            paymentTypeSp.setAdapter(new LabelValueArrayAdapter(context, paymentTypeList));
                            if (Empty.isNotEmpty(order.getPaymentTypeBackendId()))
                            {
                                int position = 0;
                                for (LabelValue labelValue : paymentTypeList)
                                {
                                    if (labelValue.getValue().equals(order.getPaymentTypeBackendId()))
                                    {
                                        paymentTypeSp.setSelection(position);
                                        break;
                                    }
                                    position++;
                                }
                            }
                        }
                    }

                    String description = order.getDescription();
                    orderDescriptionTxt.setText(Empty.isNotEmpty(description) ? description : "--");

                    return view;

                } else
                {
                    throw new SaleOrderNotFoundException("orderId : " + orderId);
                }

            } catch (Exception e)
            {
                Log.e(TAG, e.getMessage(), e);
                ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
                return inflater.inflate(R.layout.view_error_page, null);
            }
        }

        @Override
        public int getFragmentId()
        {
            return 0;
        }
    }
}
