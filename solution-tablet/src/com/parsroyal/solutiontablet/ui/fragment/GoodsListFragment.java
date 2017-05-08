package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.listmodel.GoodsListModel;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.SaleOrderItemCountExceedExistingException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.service.order.SaleOrderService;
import com.parsroyal.solutiontablet.service.order.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.adapter.GoodsListAdapter;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;

import java.util.List;

/**
 * Created by mahyar on 2/12/16.
 * Edited by Arash on 7/8/2016
 */
public class GoodsListFragment extends BaseFragment
{

    public static final String TAG = GoodsListFragment.class.getSimpleName();

    private EditText searchTxt;
    private TableLayout dataTb;

    private List<GoodsListModel> goodsListModelList;
    private GoodsService goodsService;
    private GoodsSo goodsSo;
    private Long orderId;
    private SaleOrderDto order;

    private SaleOrderService saleOrderService;

    private GoodsListAdapter adapter;
    private Long orderStatus;
    private GoodsDtoList rejectedGoodsList;
    private String constraint;
    private boolean isViewAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        try
        {

            View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_goods_list, null);

            goodsService = new GoodsServiceImpl(getActivity());
            saleOrderService = new SaleOrderServiceImpl(getActivity());

            isViewAll = getArguments().getBoolean("view_all", false);
            if (!isViewAll)
            {
                try
                {
                    orderId = getArguments().getLong("orderId");
                } catch (Exception ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                }
                if (Empty.isNotEmpty(orderId))
                {
                    order = saleOrderService.findOrderDtoById(orderId);
                } else
                {
                    TextView operationsHeader = (TextView) view.findViewById(R.id.operationsHeader);
                    operationsHeader.setVisibility(View.INVISIBLE);
                }

                orderStatus = order.getStatus();
            } else
            {
                TextView operationsHeader = (TextView) view.findViewById(R.id.operationsHeader);
                operationsHeader.setVisibility(View.INVISIBLE);
            }

            goodsSo = new GoodsSo();
            goodsListModelList = goodsService.searchForGoods(goodsSo);

            if (SaleOrderStatus.REJECTED_DRAFT.getId().equals(orderStatus))
            {
                TextView tvPaymentTime = (TextView) view.findViewById(R.id.tvPaymentTime);
                tvPaymentTime.setVisibility(View.GONE);
                rejectedGoodsList = (GoodsDtoList) getArguments().getSerializable("rejectedList");
            }

            dataTb = (TableLayout) view.findViewById(R.id.goodsDataTable);

            if (SaleOrderStatus.REJECTED_DRAFT.getId().equals(orderStatus))
            {
                for (Goods goods : rejectedGoodsList.getGoodsDtoList())
                {
                    dataTb.addView(createGoodsRowView(goods));
                }
            } else
            {
                for (GoodsListModel goodsListModel : goodsListModelList)
                {
                    dataTb.addView(createGoodsRowView(goodsListModel));
                }
            }

            searchTxt = (EditText) view.findViewById(R.id.searchTxt);
            searchTxt.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    constraint = s.toString();

                    if (Empty.isNotEmpty(constraint))
                    {
                        goodsSo.setConstraint(CharacterFixUtil.fixString("%" + constraint + "%"));
                        updateGoodsDataTb();
                    } else
                    {
                        goodsSo.setConstraint(null);
                        updateGoodsDataTb();
                    }
                }

                @Override
                public void afterTextChanged(Editable s)
                {

                }
            });

            return view;

        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
            return inflater.inflate(R.layout.view_error_page, null);
        }
    }

    private void updateGoodsDataTb()
    {
        dataTb.removeAllViews();
        goodsListModelList = goodsService.searchForGoods(goodsSo);
        if (SaleOrderStatus.REJECTED_DRAFT.getId().equals(orderStatus))
        {
            if (Empty.isNotEmpty(rejectedGoodsList))
            {
                for (Goods goods : rejectedGoodsList.getGoodsDtoList())
                {
                    if (Empty.isNotEmpty(constraint) && !goods.getTitle().contains(constraint))
                        continue;
                    dataTb.addView(createGoodsRowView(goods));
                }
            } else
            {
                dataTb.addView(createEmptyView());
            }
        } else
        {
            if (Empty.isNotEmpty(goodsListModelList))
            {
                for (GoodsListModel goodsListModel : goodsListModelList)
                {
                    dataTb.addView(createGoodsRowView(goodsListModel));
                }
            } else
            {
                dataTb.addView(createEmptyView());
            }
        }
    }

    private View createEmptyView()
    {
        return getActivity().getLayoutInflater().inflate(R.layout.row_layout_empty, null);
    }

    private View createGoodsRowView(final GoodsListModel goods)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.row_layout_goods, null);
        LinearLayout goodsRowLayout = (LinearLayout) view.findViewById(R.id.goodsRowLayout);
        TextView goodsNameTv = (TextView) view.findViewById(R.id.goodNameTv);
        TextView goodsCodeTv = (TextView) view.findViewById(R.id.goodCodeTv);
        TextView goodsAmountTv = (TextView) view.findViewById(R.id.goodAmountTv);
        TextView unit1ExistingTv = (TextView) view.findViewById(R.id.unit1ExistingTv);
        TextView recoveryDateTv = (TextView) view.findViewById(R.id.recoveryDateTv);
        //
        ImageButton addBtn = (ImageButton) view.findViewById(R.id.addBtn);
        LinearLayout operationsContainer = (LinearLayout) view.findViewById(R.id.operationsContainer);

        if (Empty.isEmpty(orderId))
        {
            operationsContainer.setVisibility(View.INVISIBLE);
        }

        goodsNameTv.setText(goods.getTitle());
        goodsCodeTv.setText(goods.getCode());
        {
            Double goodsAmount = Double.valueOf(goods.getGoodsAmount()) / 1000D;
            goodsAmountTv.setText(NumberUtil.getCommaSeparated(goodsAmount) + " " + getActivity().getString(R.string.common_irr_currency));
        }
        StringBuilder existingSb = new StringBuilder();
        {
            Double unit1Existing = Double.valueOf(goods.getExisting()) / 1000D;
            existingSb.append(NumberUtil.formatDoubleWith2DecimalPlaces(unit1Existing)).append(" ").append(Empty.isNotEmpty(goods.getUnit1Title()) ? goods.getUnit1Title() : "--");

        }
        {
            Long unit1Count = goods.getUnit1Count();
            if (Empty.isNotEmpty(unit1Count) && !unit1Count.equals(0L))
            {
                Double unit2Existing = Double.valueOf(goods.getExisting()) / Double.valueOf(unit1Count);
                unit2Existing = unit2Existing / 1000D;
                existingSb.append("  ").append(NumberUtil.formatDoubleWith2DecimalPlaces(unit2Existing)).append(" ").append(Empty.isNotEmpty(goods.getUnit2Title()) ? goods.getUnit2Title() : "--");
            }
        }

        unit1ExistingTv.setText(existingSb.toString());

        if (Empty.isNotEmpty(goods.getRecoveryDate()))
        {
            recoveryDateTv.setText(String.valueOf(goods.getRecoveryDate()));
        }

        if (Empty.isNotEmpty(orderId))
        {
            addBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    handleOnGoodsItemClickListener(goods);
                }
            });
        } else
        {
            addBtn.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private View createGoodsRowView(final Goods goods)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.row_layout_goods, null);
        LinearLayout goodsRowLayout = (LinearLayout) view.findViewById(R.id.goodsRowLayout);
        TextView goodsNameTv = (TextView) view.findViewById(R.id.goodNameTv);
        TextView goodsCodeTv = (TextView) view.findViewById(R.id.goodCodeTv);
        TextView goodsAmountTv = (TextView) view.findViewById(R.id.goodAmountTv);
        TextView unit1ExistingTv = (TextView) view.findViewById(R.id.unit1ExistingTv);
        TextView recoveryDateTv = (TextView) view.findViewById(R.id.recoveryDateTv);

        ImageButton addBtn = (ImageButton) view.findViewById(R.id.addBtn);
        LinearLayout operationsContainer = (LinearLayout) view.findViewById(R.id.operationsContainer);

        if (Empty.isEmpty(orderId))
        {
            operationsContainer.setVisibility(View.INVISIBLE);
        }

        goodsNameTv.setText(goods.getTitle());
        goodsCodeTv.setText(goods.getCode());

        Double goodsAmount = Double.valueOf(goods.getPrice()) / 1000D;
        goodsAmountTv.setText(NumberUtil.getCommaSeparated(goodsAmount) + " " + getActivity().getString(R.string.common_irr_currency));

        StringBuilder existingSb = new StringBuilder();
        {
            Double unit1Existing = Double.valueOf(goods.getExisting()) / 1000D;
            existingSb.append(NumberUtil.formatDoubleWith2DecimalPlaces(unit1Existing)).append(" ")
                    .append(Empty.isNotEmpty(goods.getUnit1Title()) ? goods.getUnit1Title() : " ");

        }

        recoveryDateTv.setVisibility(View.GONE);
        unit1ExistingTv.setText(existingSb.toString());

        if (Empty.isNotEmpty(orderId))
        {
            addBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    handleOnGoodsItemClickListener(goods);
                }
            });
        } else
        {
            addBtn.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private void handleOnGoodsItemClickListener(final GoodsListModel goods)
    {
        try
        {
            GoodsDetailDialogFragment goodsDetailDialog = new GoodsDetailDialogFragment();

            final SaleOrderItem item = saleOrderService.findOrderItemByOrderIdAndGoodsBackendId(order.getId(), goods.getGoodsBackendId(), 0);

            Bundle bundle = new Bundle();
            bundle.putLong("goodsBackendId", goods.getGoodsBackendId());
            bundle.putLong("orderStatus", orderStatus);
            if (Empty.isNotEmpty(item))
            {
                Double count = Double.valueOf(item.getGoodsCount()) / 1000D;
                bundle.putDouble("count", count);
                if (Empty.isNotEmpty(item.getSelectedUnit()))
                {
                    bundle.putLong("selectedUnit", item.getSelectedUnit());
                }
            }

            goodsDetailDialog.setArguments(bundle);
            goodsDetailDialog.setOnClickListener(new GoodsDetailDialogFragment.GoodsDialogOnClickListener()
            {
                @Override
                public void onConfirmBtnClicked(Double count, Long selectedUnit)
                {

                    handleGoodsDialogConfirmBtn(count, selectedUnit, item, goods);

                    updateGoodsDataTb();
                }
            });

            goodsDetailDialog.show(getActivity().getSupportFragmentManager(), "GoodsDetailDialog");
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
        }

    }

    private void handleOnGoodsItemClickListener(final Goods goods)
    {
        try
        {
            GoodsDetailDialogFragment goodsDetailDialog = new GoodsDetailDialogFragment();

            final SaleOrderItem item = saleOrderService.findOrderItemByOrderIdAndGoodsBackendId(
                    order.getId(), goods.getBackendId(), goods.getInvoiceBackendId());

            Bundle bundle = new Bundle();
            bundle.putLong("goodsBackendId", goods.getBackendId());
            bundle.putLong("goodsInvoiceId", goods.getInvoiceBackendId());
            bundle.putLong("orderStatus", orderStatus);
            bundle.putSerializable("rejectedList", rejectedGoodsList);
            if (Empty.isNotEmpty(item))
            {
                Double count = item.getGoodsCount() / 1000D;

                bundle.putDouble("count", count);
                if (Empty.isNotEmpty(item.getSelectedUnit()))
                {
                    bundle.putLong("selectedUnit", item.getSelectedUnit());
                }
            }

            goodsDetailDialog.setArguments(bundle);
            goodsDetailDialog.setOnClickListener(new GoodsDetailDialogFragment.GoodsDialogOnClickListener()
            {
                @Override
                public void onConfirmBtnClicked(Double count, Long selectedUnit)
                {

                    handleGoodsDialogConfirmBtn(count, selectedUnit, item, goods);

                    updateGoodsDataTb();
                }
            });

            goodsDetailDialog.show(getActivity().getSupportFragmentManager(), "GoodsDetailDialog");
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
        }

    }

    private void handleGoodsDialogConfirmBtn(Double count, Long selectedUnit, SaleOrderItem item,
                                             GoodsListModel goods)
    {
        try
        {
            if (Empty.isEmpty(item))
            {
                if (count * 1000L > Double.valueOf(String.valueOf(goods.getExisting())))
                {
                    ToastUtil.toastError(getActivity(), new SaleOrderItemCountExceedExistingException());
                    return;
                }
                item = createOrderItem(goods);
            }

            saleOrderService.updateOrderItemCount(item.getId(), count, selectedUnit, orderStatus, null);
            Long orderAmount = saleOrderService.updateOrderAmount(order.getId());
            order.setOrderItems(saleOrderService.getOrderItemDtoList(order.getId()));
            order.setAmount(orderAmount);
        } catch (BusinessException ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(getActivity(), ex);
        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
        }
    }

    private void handleGoodsDialogConfirmBtn(Double count, Long selectedUnit, SaleOrderItem item, Goods goods)
    {
        try
        {
            if (Empty.isEmpty(item))
            {
                if (count * 1000L > Double.valueOf(String.valueOf(goods.getExisting())))
                {
                    ToastUtil.toastError(getActivity(), new SaleOrderItemCountExceedExistingException());
                    return;
                }
                item = createOrderItem(goods);
            }
            //

            saleOrderService.updateOrderItemCount(item.getId(), count, selectedUnit, orderStatus, goods);
            Long orderAmount = saleOrderService.updateOrderAmount(order.getId());
            order.setOrderItems(saleOrderService.getOrderItemDtoList(order.getId()));
            order.setAmount(orderAmount);
//            goods.setExisting((long) (goods.getExisting() + item.getGoodsCount() - count*1000L));
        } catch (BusinessException ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(getActivity(), ex);
        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
        }
    }

    private SaleOrderItem createOrderItem(GoodsListModel goods)
    {
        SaleOrderItem saleOrderItem = new SaleOrderItem();
        saleOrderItem.setGoodsBackendId(goods.getGoodsBackendId());
        saleOrderItem.setGoodsCount(0L);
        saleOrderItem.setSaleOrderId(order.getId());
        saleOrderItem.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
        saleOrderItem.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
        Long orderItemId = saleOrderService.saveOrderItem(saleOrderItem);
        saleOrderItem.setId(orderItemId);
        return saleOrderItem;
    }

    private SaleOrderItem createOrderItem(Goods goods)
    {
        SaleOrderItem saleOrderItem = new SaleOrderItem();
        saleOrderItem.setGoodsBackendId(goods.getBackendId());
        saleOrderItem.setGoodsCount(0L);
        saleOrderItem.setSaleOrderId(order.getId());
        saleOrderItem.setInvoiceBackendId(goods.getInvoiceBackendId());
        saleOrderItem.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
        saleOrderItem.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
        Long orderItemId = saleOrderService.saveOrderItem(saleOrderItem);
        saleOrderItem.setId(orderItemId);
        return saleOrderItem;
    }

    @Override
    public int getFragmentId()
    {
        return 0;
    }

}
