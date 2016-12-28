package com.conta.comer.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.conta.comer.R;
import com.conta.comer.constants.SaleOrderStatus;
import com.conta.comer.data.entity.Goods;
import com.conta.comer.data.model.GoodsDtoList;
import com.conta.comer.data.model.LabelValue;
import com.conta.comer.service.GoodsService;
import com.conta.comer.service.impl.GoodsServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.adapter.LabelValueArrayAdapter;
import com.conta.comer.util.Empty;
import com.conta.comer.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 8/29/2015.
 */
public class GoodsDetailDialogFragment extends DialogFragment
{
    public static final int WIDTH = 700;
    public static final int HEIGHT = 400;

    public static final String TAG = GoodsDetailDialogFragment.class.getSimpleName();

    private MainActivity context;
    private GoodsService goodsService;

    private Long goodsBackendId;
    private Goods selectedGoods;
    private Double count;
    private Long selectedUnit;

    private TextView goodsNameTv;
    private EditText countTxt;
    private Spinner goodsUnitSp;
    private Button confirmBtn;
    private Button cancelBtn;

    private GoodsDialogOnClickListener onClickListener;
    private long orderStatus;
    private GoodsDtoList rejectedGoodsList;
    private long goodsInvoiceId;

    public void setOnClickListener(GoodsDialogOnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_goods_detail_dialog, null);

        context = (MainActivity) getActivity();
        orderStatus = getArguments().getLong("orderStatus");
        goodsBackendId = getArguments().getLong("goodsBackendId");
        count = getArguments().getDouble("count");
        selectedUnit = getArguments().getLong("selectedUnit");

        goodsService = new GoodsServiceImpl(context);

        if (orderStatus == SaleOrderStatus.REJECTED_DRAFT.getId())
        {
            rejectedGoodsList = (GoodsDtoList) getArguments().getSerializable("rejectedList");
            goodsInvoiceId = getArguments().getLong("goodsInvoiceId");

            selectedGoods = getGoodFromLocal();
        } else
        {

            selectedGoods = goodsService.getGoodsByBackendId(goodsBackendId);
        }

        goodsNameTv = (TextView) view.findViewById(R.id.goodNameTv);
        countTxt = (EditText) view.findViewById(R.id.countTxt);
        confirmBtn = (Button) view.findViewById(R.id.confirmBtn);
        cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        goodsUnitSp = (Spinner) view.findViewById(R.id.goodUnitsSp);

        goodsNameTv.setText(selectedGoods.getTitle());

        if (Empty.isNotEmpty(count) && !count.equals(0.0D))
        {
            if (count == count.longValue())
            {
                countTxt.setText(String.format("%d", count.longValue()));
            } else
            {
                countTxt.setText(String.format("%s", count));
            }
            countTxt.setSelection(countTxt.getText().length());
        }

        {
            List<LabelValue> unitsList = new ArrayList<>();
            unitsList.add(new LabelValue(1L, selectedGoods.getUnit1Title()));
            if (Empty.isNotEmpty(selectedGoods.getUnit2Title()))
            {
                unitsList.add(new LabelValue(2L, selectedGoods.getUnit2Title()));
            }
            goodsUnitSp.setAdapter(new LabelValueArrayAdapter(context, unitsList));

            if (Empty.isNotEmpty(selectedUnit))
            {
                if (selectedUnit.equals(1L))
                {
                    goodsUnitSp.setSelection(0);
                }
                if (selectedUnit.equals(2L))
                {
                    goodsUnitSp.setSelection(1);
                }
            }
        }

        {
            confirmBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (Empty.isNotEmpty(onClickListener))
                    {
                        if (validate())
                        {
                            Double count = Double.valueOf(countTxt.getText().toString());
                            LabelValue selectedUnitLv = (LabelValue) goodsUnitSp.getSelectedItem();
                            Long selectedUnit = selectedUnitLv.getValue();
                            if (selectedUnit.equals(2L))
                            {
                                count *= Double.valueOf(selectedGoods.getUnit1Count());
                            }
                            onClickListener.onConfirmBtnClicked(count, selectedUnit);
                            GoodsDetailDialogFragment.this.dismiss();
                        }
                    }
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    GoodsDetailDialogFragment.this.dismiss();
                }
            });
        }

        this.getDialog().setTitle(context.getString(R.string.message_please_enter_count));

        return view;
    }

    private Goods getGoodFromLocal()
    {
        List<Goods> goods = rejectedGoodsList.getGoodsDtoList();
        for (int i = 0; i < goods.size(); i++)
        {
            Goods good = goods.get(i);
            if (goodsBackendId == good.getBackendId().longValue())
            {
                return good;
            }
        }
        return null;
    }

    private boolean validate()
    {
        String countValue = countTxt.getText().toString();
        if (Empty.isEmpty(countValue) || Double.valueOf(countValue).equals(0D))
        {
            ToastUtil.toastError(context, R.string.message_please_enter_count);
            return false;
        }
        return true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.y = 2;
        window.setLayout(WIDTH, HEIGHT);
        window.setGravity(Gravity.CENTER);
        window.setAttributes(layoutParams);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public interface GoodsDialogOnClickListener
    {
        void onConfirmBtnClicked(Double count, Long selectedUnit);
    }
}
