package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.GoodImagePagerAdapter;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.util.DepthPageTransformer;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.relex.circleindicator.CircleIndicator;

/**
 * @author Shakib
 */
public class AddOrderDialogFragment extends DialogFragment {

  @BindView(R.id.pager)
  ViewPager viewPager;
  @BindView(R.id.indicator)
  CircleIndicator indicator;
  @BindView(R.id.spinner)
  Spinner spinner;
  @BindView(R.id.good_name_tv)
  TextView goodNameTv;
  @BindView(R.id.count_tv)
  EditText countTv;
  @BindView(R.id.good_code_tv)
  TextView goodCodeTv;
  @BindView(R.id.good_price_tv)
  TextView goodPriceTv;
  @BindView(R.id.unit1_count_tv)
  TextView unit1CountTv;
  @BindView(R.id.unit1_title_tv)
  TextView unit1TitleTv;
  @BindView(R.id.unit2_count_tv)
  TextView unit2CountTv;
  @BindView(R.id.unit2_title_tv)
  TextView unit2TitleTv;
  @BindView(R.id.cost_detail_lay)
  LinearLayout costDetailLay;
  @BindView(R.id.coefficient_tv)
  TextView coefficientTv;
  @BindView(R.id.each_Carton_tv)
  TextView eachCartonTv;
  @BindView(R.id.total_price_tv)
  TextView totalPriceTv;
  @BindView(R.id.error_msg)
  TextView errorMsg;
  @BindView(R.id.view_pager_position_tv)
  TextView viewPagerPositionTv;

  private GoodsDialogOnClickListener onClickListener;
  private Long goodsBackendId;
  private long orderStatus;
  private long goodsInvoiceId;
  private GoodsDtoList rejectedGoodsList;
  private MainActivity mainActivity;
  private GoodsServiceImpl goodsService;
  private SettingServiceImpl settingService;
  private String saleType;
  private Double count;
  private Long selectedUnit;
  private boolean saleRateEnabled;
  private Goods selectedGoods;
  private Long unit1Count;
  private String unit1Title;
  private String unit2Title;
  private Long saleRate;

  public AddOrderDialogFragment() {
    // Required empty public constructor
  }


  public static AddOrderDialogFragment newInstance() {
    return new AddOrderDialogFragment();
  }

  public void setOnClickListener(GoodsDialogOnClickListener onClickListener) {
    this.onClickListener = onClickListener;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_add_order_dialog, container, false);
    ButterKnife.bind(this, view);
    this.mainActivity = (MainActivity) getActivity();
    Bundle arguments = getArguments();
    goodsBackendId = arguments.getLong(Constants.GOODS_BACKEND_ID);
    orderStatus = arguments.getLong(Constants.ORDER_STATUS);
    count = arguments.getDouble(Constants.COUNT);
    selectedUnit = arguments.getLong(Constants.SELECTED_UNIT);

    goodsService = new GoodsServiceImpl(mainActivity);
    settingService = new SettingServiceImpl(mainActivity);
    saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

    saleRateEnabled = Boolean
        .valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_SALE_RATE_ENABLE));

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
    setUpPager();
    setData();
    setListeners();
    setUpSpinner();
    fillLeftPanel();
    return view;
  }

  private void fillLeftPanel() {
    String input = countTv.getText().toString();
    if (Empty.isNotEmpty(input)) {
      try {
        Double count1 = Double.valueOf(NumberUtil.digitsToEnglish(input));
        if (Empty.isNotEmpty(count1) && !count1.equals(0D)) {
          int selectedUnit1 = spinner.getSelectedItemPosition();
          if (selectedUnit1 == 1) {
            unit2CountTv.setText(String.format(Locale.US, "%d", count1.intValue()));
            count1 *= Double.valueOf(unit1Count);
            unit1CountTv.setText(String.valueOf(count1));
          } else {
            unit2CountTv.setText(String.format(Locale.US, "%d", (count1.longValue() / unit1Count)));
            unit1CountTv.setText(input);
          }
          long total = (long) (count1 * selectedGoods.getPrice() / 1000);
          totalPriceTv.setText(String.format(Locale.US, "%,d %s", total, getString(
              R.string.common_irr_currency)));
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

  private void clearLeftPanel() {
    unit1CountTv.setText("0");
    unit2CountTv.setText("0");
    totalPriceTv.setText(String.format(Locale.US, "%,d %s", 0, getString(
        R.string.common_irr_currency)));
  }

  private void setListeners() {
    countTv.addTextChangedListener(new TextWatcher() {
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

    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        fillLeftPanel();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  private void setData() {
    goodNameTv.setText(selectedGoods.getTitle());
    if (Empty.isNotEmpty(count) && !count.equals(0.0D)) {
      if (count == count.longValue()) {
        countTv.setText(String.format(Locale.US, "%d", count.longValue()));
      } else {
        countTv.setText(String.format(Locale.US, "%s", count));
      }
      countTv.setSelection(countTv.getText().length());
    }

    goodCodeTv.setText(String.format("کد کالا: %s", selectedGoods.getCode()));
    long total = selectedGoods.getPrice() / 1000;
    goodPriceTv.setText(String.format(Locale.US, "%,d %s", total, getString(
        R.string.common_irr_currency)));
    unit1TitleTv.setText(unit1Title);
    unit2TitleTv.setText(unit2Title);
    unit1CountTv.setText("0");
    unit2CountTv.setText("0");
    totalPriceTv.setText(String.format(Locale.US, "%,d %s", 0, getString(
        R.string.common_irr_currency)));

    if (saleRateEnabled) {
      coefficientTv.setText(String.format("ضریب فروش: %s %s", saleRate, unit1Title));
      eachCartonTv.setText(String.format("هر %s = %s %s", unit2Title, unit1Count, unit1Title));
    } else {
      costDetailLay.setVisibility(View.GONE);
    }
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

  private void setUpSpinner() {
    List<LabelValue> unitsList = new ArrayList<>();
    unitsList.add(new LabelValue(1L, unit1Title));
    if (Empty.isNotEmpty(unit2Title)) {
      unitsList.add(new LabelValue(2L, unit2Title));
    }
    spinner.setAdapter(new LabelValueArrayAdapter(mainActivity, unitsList));

    if (Empty.isNotEmpty(selectedUnit)) {
      if (selectedUnit.equals(1L)) {
        spinner.setSelection(0);
      } else if (selectedUnit.equals(2L)) {
        spinner.setSelection(1);
      }
    }
  }

  private void setUpPager() {
    List<String> pathes = new ArrayList<>();
    pathes.add(MediaUtil.getGoodImage(selectedGoods.getCode()));
    final GoodImagePagerAdapter adapter = new GoodImagePagerAdapter(
        getChildFragmentManager(), getActivity(), pathes, this);
    viewPager.setAdapter(adapter);
    indicator.setViewPager(viewPager);
    viewPager.setPageTransformer(true, new DepthPageTransformer());
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        String pos = String.format("%d/%d", position + 1, pathes.size());
        viewPagerPositionTv.setText(pos);
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  private boolean validate() {
    String countValue = NumberUtil.digitsToEnglish(countTv.getText().toString());

    if (Empty.isEmpty(countValue) || Double.valueOf(countValue).equals(0D)) {
      errorMsg.setText(R.string.message_please_enter_count);
      errorMsg.setVisibility(View.VISIBLE);
      return false;
    }

    int currentUnit = spinner.getSelectedItemPosition();

    //If saleRate setting is enabled & default unit is unit1 & mod unit1 is not zero
    if (shouldApplySaleRate(countValue, currentUnit)) {
      errorMsg.setText(String.format(getString(R.string.error_sale_rate_not_correct),
          String.valueOf(saleRate), unit1Title));
      errorMsg.setVisibility(View.VISIBLE);
      return false;
    }
    return true;
  }

  @OnClick({R.id.close, R.id.register_order_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
        getDialog().dismiss();
        break;
      case R.id.register_order_btn:
        if (Empty.isNotEmpty(onClickListener)) {
          if (validate()) {
            Double count1 = Double
                .valueOf(NumberUtil.digitsToEnglish(countTv.getText().toString()));
            LabelValue selectedUnitLv = (LabelValue) spinner.getSelectedItem();
            Long selectedUnit1 = selectedUnitLv.getValue();
            if (selectedUnit1.equals(2L)) {
              count1 *= Double.valueOf(unit1Count);
            }
            onClickListener.onConfirmBtnClicked(count1, selectedUnit1);
            AddOrderDialogFragment.this.dismiss();
          }
        }
        break;
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
  }

  public interface GoodsDialogOnClickListener {

    void onConfirmBtnClicked(Double count, Long selectedUnit);
  }
}
