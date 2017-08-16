package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
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
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MediaUtil;
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
  @BindView(R.id.goods_unit_title_2)
  TextView goodsUnitTitle2;
  @BindView(R.id.sale_rate_count)
  TextView saleRateCount;
  @BindView(R.id.total_amount)
  TextView totalAmount;
  @BindView(R.id.goods_image)
  ImageView goodsImage;

  private OldMainActivity context;
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
  private boolean saleRateEnabled;
  private String saleType;
  private Long unit1Count;
  private String unit1Title;
  private String unit2Title;
  private Long saleRate;

  public void setOnClickListener(GoodsDialogOnClickListener onClickListener) {
    this.onClickListener = onClickListener;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_goods_detail_dialog, null);
    ButterKnife.bind(this, view);

    context = (OldMainActivity) getActivity();
    Bundle arguments = getArguments();
    orderStatus = arguments.getLong(Constants.ORDER_STATUS);
    goodsBackendId = arguments.getLong(Constants.GOODS_BACKEND_ID);
    count = arguments.getDouble(Constants.COUNT);
    selectedUnit = arguments.getLong(Constants.SELECTED_UNIT);

    goodsService = new GoodsServiceImpl(context);
    settingService = new SettingServiceImpl(context);
    saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

    saleRateEnabled = "1"
        .equals(settingService.getSettingValue(ApplicationKeys.SETTING_SALE_RATE_ENABLE));

    if (orderStatus == SaleOrderStatus.REJECTED_DRAFT.getId()) {
      rejectedGoodsList = (GoodsDtoList) arguments.getSerializable(Constants.REJECTED_LIST);
      goodsInvoiceId = arguments.getLong(Constants.GOODS_INVOICE_ID);
      selectedGoods = getGoodFromLocal();
    } else {
      selectedGoods = goodsService.getGoodsByBackendId(goodsBackendId);
    }

    unit1Count = selectedGoods.getUnit1Count();
    unit1Title = selectedGoods.getUnit1Title();
    unit2Title = selectedGoods.getUnit2Title();
    saleRate = selectedGoods.getSaleRate();

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
        fillLeftPanel();
        errorMsg.setVisibility(View.GONE);
      }
    });

    List<LabelValue> unitsList = new ArrayList<>();
    unitsList.add(new LabelValue(1L, unit1Title));
    if (Empty.isNotEmpty(unit2Title)) {
      unitsList.add(new LabelValue(2L, unit2Title));
    }
    goodUnitsSp.setAdapter(new LabelValueArrayAdapter(context, unitsList));

    if (Empty.isNotEmpty(selectedUnit)) {
      if (selectedUnit.equals(1L)) {
        goodUnitsSp.setSelection(0);
      } else if (selectedUnit.equals(2L)) {
        goodUnitsSp.setSelection(1);
      }
    }

    goodUnitsSp.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        fillLeftPanel();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    this.getDialog().setTitle(context.getString(R.string.message_please_enter_count));

    fillLeftPanel();
    Glide.with(this)
        .load(MediaUtil.getGoodImage(selectedGoods.getCode()))
        .error(R.drawable.no_image)
        .into(goodsImage);
    return view;
  }

  private void fillLeftPanel() {
    String input = countTxt.getText().toString();
    if (Empty.isNotEmpty(input)) {
      try {
        Double count1 = Double.valueOf(NumberUtil.digitsToEnglish(input));
        if (Empty.isNotEmpty(count1) && !count1.equals(0D)) {
          int selectedUnit1 = goodUnitsSp.getSelectedItemPosition();
          if (selectedUnit1 == 1) {
            goodsUnitTitle2
                .setText(String.format(Locale.US, "%d %s", count1.intValue(), unit2Title));
            count1 *= Double.valueOf(unit1Count);
          } else {
            goodsUnitTitle2.setText(
                String.format(Locale.US, "%d %s", (count1.longValue() / unit1Count), unit2Title));
          }
          long total = (long) (count1 * selectedGoods.getPrice() / 1000);
          totalAmount.setText(String.format(Locale.US, "%,d %s", total, getString(
              R.string.common_irr_currency)));
          if (hasSaleRate(selectedUnit1)) {
            saleRateCount.setText(String.format(Locale.US, "%d %s", saleRate, unit1Title));
          } else {
            saleRateCount.setText("--");
          }
        } else {
          clearLeftPanel();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        Crashlytics.log(Log.ERROR, "GoodDetails Left Panel", ex.getMessage());
        clearLeftPanel();
      }
    } else {
      clearLeftPanel();
    }
  }

  private void clearLeftPanel() {
    saleRateCount.setText("--");
    totalAmount.setText("0");
    goodsUnitTitle2.setText("");
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

    int currentUnit = goodUnitsSp.getSelectedItemPosition();

    //If saleRate setting is enabled & default unit is unit1 & mod unit1 is not zero
    if (shouldApplySaleRate(countValue, currentUnit)) {
      errorMsg.setText(String.format(context.getString(R.string.error_sale_rate_not_correct),
          String.valueOf(saleRate), unit1Title));
      errorMsg.setVisibility(View.VISIBLE);
      return false;
    }
    return true;
  }

  private boolean shouldApplySaleRate(String countValue, int currentUnit) {
    return hasSaleRate(currentUnit)
        && Double.valueOf(countValue) % saleRate != 0.0;
  }

  private boolean hasSaleRate(int currentUnit) {
    if (!saleRateEnabled || currentUnit != 0) {
      return false;
    }
    if (saleType.equals(ApplicationKeys.SALE_COLD)) {

      if (orderStatus == SaleOrderStatus.DRAFT.getId()
          || orderStatus == SaleOrderStatus.READY_TO_SEND.getId()) {
        return true;
      }
    }
    return false;
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
              count1 *= Double.valueOf(unit1Count);
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
