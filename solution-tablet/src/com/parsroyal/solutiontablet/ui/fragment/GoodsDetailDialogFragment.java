package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mahyar on 8/29/2015.
 */
public class GoodsDetailDialogFragment extends DialogFragment {

  public static final String TAG = GoodsDetailDialogFragment.class.getSimpleName();
  @BindView(R.id.goodNameTv)
  TextView goodsNameTv;
  @BindView(R.id.countTxt)
  EditText countTxt;
  @BindView(R.id.goodUnitsSp)
  Spinner goodUnitsSp;
  @BindView(R.id.error_msg)
  TextView errorMsg;

  private MainActivity context;
  private GoodsService goodsService;
  private SettingService settingService;

  private Long goodsBackendId;
  private Goods selectedGoods;
  private Double count;
  private Long selectedUnit;

  private GoodsDialogOnClickListener onClickListener;
  private long orderStatus;
  private GoodsDtoList rejectedGoodsList;
  private long goodsInvoiceId;
  private long saleRate;
  private boolean saleRateEnabled;

  public void setOnClickListener(GoodsDialogOnClickListener onClickListener) {
    this.onClickListener = onClickListener;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_goods_detail_dialog, null);
    ButterKnife.bind(this, view);

    context = (MainActivity) getActivity();
    Bundle arguments = getArguments();
    orderStatus = arguments.getLong(Constants.ORDER_STATUS);
    goodsBackendId = arguments.getLong(Constants.GOODS_BACKEND_ID);
    count = arguments.getDouble(Constants.COUNT);
    selectedUnit = arguments.getLong(Constants.SELECTED_UNIT);
    saleRate = arguments.getLong(Constants.GOODS_SALE_RATE);

    goodsService = new GoodsServiceImpl(context);
    settingService = new SettingServiceImpl(context);

    saleRateEnabled = "1"
        .equals(settingService.getSettingValue(ApplicationKeys.SETTING_SALE_RATE_ENABLE));

    if (orderStatus == SaleOrderStatus.REJECTED_DRAFT.getId()) {
      rejectedGoodsList = (GoodsDtoList) arguments.getSerializable(Constants.REJECTED_LIST);
      goodsInvoiceId = arguments.getLong(Constants.GOODS_INVOICE_ID);
      selectedGoods = getGoodFromLocal();
    } else {
      selectedGoods = goodsService.getGoodsByBackendId(goodsBackendId);
    }

    goodsNameTv.setText(selectedGoods.getTitle());

    if (Empty.isNotEmpty(count) && !count.equals(0.0D)) {
      if (count == count.longValue()) {
        countTxt.setText(String.format(Locale.US, "%d", count.longValue()));
      } else {
        countTxt.setText(String.format(Locale.US, "%s", count));
      }
      countTxt.setSelection(countTxt.getText().length());
    }

    countTxt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        errorMsg.setVisibility(View.GONE);
      }
    });

    List<LabelValue> unitsList = new ArrayList<>();
    unitsList.add(new LabelValue(1L, selectedGoods.getUnit1Title()));
    if (Empty.isNotEmpty(selectedGoods.getUnit2Title())) {
      unitsList.add(new LabelValue(2L, selectedGoods.getUnit2Title()));
    }
    goodUnitsSp.setAdapter(new LabelValueArrayAdapter(context, unitsList));

    if (Empty.isNotEmpty(selectedUnit)) {
      if (selectedUnit.equals(1L)) {
        goodUnitsSp.setSelection(0);
      } else if (selectedUnit.equals(2L)) {
        goodUnitsSp.setSelection(1);
      }
    }

    this.getDialog().setTitle(context.getString(R.string.message_please_enter_count));

    return view;
  }

  private Goods getGoodFromLocal() {
    List<Goods> goods = rejectedGoodsList.getGoodsDtoList();
    for (int i = 0; i < goods.size(); i++) {
      Goods good = goods.get(i);
      if (goodsBackendId == good.getBackendId().longValue()) {
        return good;
      }
    }
    return null;
  }

  private boolean validate() {
    String countValue = NumberUtil.digitsToEnglish(countTxt.getText().toString());

    if (Empty.isEmpty(countValue) || Double.valueOf(countValue).equals(0D)) {
      errorMsg.setText(R.string.message_please_enter_count);
      errorMsg.setVisibility(View.VISIBLE);
      return false;
    }

    if (saleRateEnabled && Double.valueOf(countValue) % selectedGoods.getSaleRate() != 0.0) {
      errorMsg.setText(String.format(context.getString(R.string.error_sale_rate_not_correct),
          String.valueOf(selectedGoods.getSaleRate()), selectedGoods.getUnit1Title()));
      errorMsg.setVisibility(View.VISIBLE);
      return false;
    }
    return true;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @OnClick({R.id.cancelBtn, R.id.confirmBtn})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.cancelBtn:
        GoodsDetailDialogFragment.this.dismiss();
        break;
      case R.id.confirmBtn:
        if (Empty.isNotEmpty(onClickListener)) {
          if (validate()) {
            Double count1 = Double
                .valueOf(NumberUtil.digitsToEnglish(countTxt.getText().toString()));
            LabelValue selectedUnitLv = (LabelValue) goodUnitsSp.getSelectedItem();
            Long selectedUnit1 = selectedUnitLv.getValue();
            if (selectedUnit1.equals(2L)) {
              count1 *= Double.valueOf(selectedGoods.getUnit1Count());
            }
            onClickListener.onConfirmBtnClicked(count1, selectedUnit1);
            GoodsDetailDialogFragment.this.dismiss();
          }
        }
        break;
    }
  }

  public interface GoodsDialogOnClickListener {

    void onConfirmBtnClicked(Double count, Long selectedUnit);
  }
}
