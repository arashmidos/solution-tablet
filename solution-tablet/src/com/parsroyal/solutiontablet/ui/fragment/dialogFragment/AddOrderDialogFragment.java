package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.SuccessEvent;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.GoodImagePagerAdapter;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.util.DepthPageTransformer;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.SaleUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.relex.circleindicator.CircleIndicator;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

/**
 * @author Shakib
 */
public class AddOrderDialogFragment extends DialogFragment {

  public final String TAG = AddOrderDialogFragment.class.getSimpleName();

  @BindView(R.id.pager)
  protected ViewPager viewPager;
  @BindView(R.id.indicator)
  protected CircleIndicator indicator;
  @BindView(R.id.spinner)
  protected Spinner spinner;
  @BindView(R.id.good_name_tv)
  protected TextView goodNameTv;
  @BindView(R.id.count_tv)
  protected EditText countTv;
  @BindView(R.id.good_code_tv)
  protected TextView goodCodeTv;
  @BindView(R.id.good_price_tv)
  protected TextView goodPriceTv;
  @BindView(R.id.unit1_count_tv)
  protected TextView unit1CountTv;
  @BindView(R.id.unit1_title_tv)
  protected TextView unit1TitleTv;
  @BindView(R.id.unit2_count_tv)
  protected TextView unit2CountTv;
  @BindView(R.id.unit2_title_tv)
  protected TextView unit2TitleTv;
  @BindView(R.id.cost_detail_lay)
  protected LinearLayout costDetailLay;
  @BindView(R.id.coefficient_tv)
  protected TextView coefficientTv;
  @BindView(R.id.each_Carton_tv)
  protected TextView eachCartonTv;
  @BindView(R.id.total_price_tv)
  protected TextView totalPriceTv;
  @BindView(R.id.error_msg)
  protected TextView errorMsg;
  @BindView(R.id.view_pager_position_tv)
  protected TextView viewPagerPositionTv;
  @BindView(R.id.toolbar_title)
  protected TextView toolbarText;
  @BindView(R.id.add_order_btn_text)
  protected TextView addButtonText;
  @BindView(R.id.bottom_bar)
  protected RelativeLayout bottomLayout;
  @BindView(R.id.register_order_image)
  protected ImageView registerButtonImage;
  @BindView(R.id.order_count_tv)
  protected TextView orderCountTv;
  @BindView(R.id.minus_img)
  protected ImageView minusImg;
  @BindView(R.id.discount_tv)
  protected TextView discountTv;
  @BindView(R.id.discount_edt)
  protected EditText discountEdt;
  @BindView(R.id.discount_lay)
  protected LinearLayout discountLayout;

  protected GoodsDialogOnClickListener onClickListener;
  protected Long goodsBackendId;
  protected Long orderStatus;
  protected long goodsInvoiceId;
  protected GoodsDtoList rejectedGoodsList;
  protected MainActivity mainActivity;
  protected GoodsServiceImpl goodsService;
  protected SettingServiceImpl settingService;
  protected Double count;
  protected Long selectedUnit;
  protected boolean saleRateEnabled;
  protected Goods selectedGoods;
  protected Long unit1Count;
  protected String unit1Title;
  protected String unit2Title;
  protected Long saleRate;
  private long total;
  private Long discount;
  private boolean isComplementary;

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
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);

    setRetainInstance(true);
  }

  protected String getTAG() {
    return TAG;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    // Inflate the layout for this fragment
    View view = inflater.inflate(getLayout(), container, false);
    ButterKnife.bind(this, view);
    this.mainActivity = (MainActivity) getActivity();
    Bundle arguments = getArguments();
    goodsBackendId = arguments.getLong(Constants.GOODS_BACKEND_ID);
    orderStatus = arguments.getLong(Constants.ORDER_STATUS);
    count = arguments.getDouble(Constants.COUNT);
    selectedUnit = arguments.getLong(Constants.SELECTED_UNIT);
    discount = arguments.getLong(Constants.DISCOUNT);
    isComplementary = arguments.getBoolean(Constants.COMPLIMENTARY);

    goodsService = new GoodsServiceImpl(mainActivity);
    settingService = new SettingServiceImpl();

    saleRateEnabled = Boolean
        .valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_SALE_RATE_ENABLE));

    if (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId())) {
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
    onDiscountChange();
    setData();
    setListeners();
    setUpSpinner();
    fillDetailPanel();
    return view;
  }

  private void onDiscountChange() {
    discountEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String disc = NumberUtil.digitsToEnglish(s.toString());
        if (!TextUtils.isEmpty(disc)) {
          int discInt = Integer.parseInt(disc);
          if (discInt > 0 && discInt <= 100 && total > 0L) {
            discountTv.setVisibility(View.VISIBLE);
            long discountPrice = (total * discInt) / 100;
            discountTv.setText(NumberUtil.digitsToPersian(
                String.format(Locale.getDefault(), "تخفیف : %,d %s", discountPrice, getString(
                    R.string.common_irr_currency))));
          } else {
            errorMsg.setVisibility(View.VISIBLE);
            discountTv.setVisibility(View.GONE);
            if (discInt <= 0 || discInt >= 100) {
              errorMsg.setText(R.string.valid_period_discount);
            }
          }
        } else {
          discountTv.setVisibility(View.GONE);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });
  }

  protected int getLayout() {
    if (!getTAG().contains("Sheet")) {
      setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
      return R.layout.fragment_add_order_dialog;
    }
    return R.layout.fragment_add_order_bottom_sheet;
  }

  protected void fillDetailPanel() {
    String input = countTv.getText().toString();
    if (Empty.isNotEmpty(input)) {
      try {
        Double count1 = Double.valueOf(NumberUtil.digitsToEnglish(input));
        if (Empty.isNotEmpty(count1) && !count1.equals(0D)) {
          int selectedUnit1 = spinner.getSelectedItemPosition();
          if (selectedUnit1 == 1) {
            unit2CountTv.setText(NumberUtil
                .digitsToPersian(String.format(Locale.getDefault(), "%d", count1.intValue())));
            count1 *= Double.valueOf(unit1Count);
            unit1CountTv.setText(NumberUtil.digitsToPersian(count1));
          } else {
            unit2CountTv.setText(NumberUtil.digitsToPersian(
                String.format(Locale.getDefault(), "%d", (count1.longValue() / unit1Count))));
            unit1CountTv.setText(NumberUtil.digitsToPersian(input));
          }
          total = (long) (count1 * selectedGoods.getPrice() / 1000);
          totalPriceTv.setText(NumberUtil
              .digitsToPersian(String.format(Locale.getDefault(), "%,d %s", total, getString(
                  R.string.common_irr_currency))));
        } else {
          clearDetailPanel();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        Timber.e(ex);
        clearDetailPanel();
      }
    } else {
      clearDetailPanel();
    }
  }

  protected boolean shouldApplySaleRate(String countValue, int currentUnit) {
    return hasSaleRate(currentUnit) && Double.valueOf(countValue) % saleRate != 0.0;
  }

  protected boolean hasSaleRate(int currentUnit) {
    if (!saleRateEnabled || currentUnit != 0) {
      return false;
    }
    if (PreferenceHelper.isVisitor()) {
      if (orderStatus.equals(SaleOrderStatus.DRAFT.getId())
          || orderStatus.equals(SaleOrderStatus.READY_TO_SEND.getId())) {
        return true;
      }
    }
    return false;
  }

  protected void clearDetailPanel() {
    unit1CountTv.setText(NumberUtil.digitsToPersian("0"));
    unit2CountTv.setText(NumberUtil.digitsToPersian("0"));
    totalPriceTv.setText(
        NumberUtil.digitsToPersian(String.format(Locale.getDefault(), "%,d %s", 0, getString(
            R.string.common_irr_currency))));
  }

  protected void setListeners() {
    countTv.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        fillDetailPanel();
        errorMsg.setVisibility(View.GONE);
      }
    });

    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        fillDetailPanel();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  protected void setData() {
    goodNameTv.setText(NumberUtil.digitsToPersian(selectedGoods.getTitle()));
    if (Empty.isNotEmpty(count) && !count.equals(0.0D)) {
      if (count == count.longValue()) {
        countTv.setText(String.format(Locale.getDefault(), "%d", count.longValue()));
      } else {
        countTv.setText(String.format(Locale.getDefault(), "%s", count));
      }
      countTv.setSelection(countTv.getText().length());
    }

    goodCodeTv
        .setText(String.format("کد کالا: %s", NumberUtil.digitsToPersian(selectedGoods.getCode())));
    long total = selectedGoods.getPrice() / 1000;
    goodPriceTv.setText(
        NumberUtil.digitsToPersian(String.format(Locale.getDefault(), "%,d %s", total, getString(
            R.string.common_irr_currency))));
    unit1TitleTv.setText(NumberUtil.digitsToPersian(unit1Title));
    unit2TitleTv.setText(NumberUtil.digitsToPersian(unit2Title));
    clearDetailPanel();

    if (saleRateEnabled) {
      coefficientTv.setText(
          Empty.isNotEmpty(saleRate) ? String
              .format("ضریب فروش: %s %s", NumberUtil.digitsToPersian(String.valueOf(saleRate)),
                  NumberUtil.digitsToPersian(unit1Title)) : getString(R.string.no_sale_rate));
      eachCartonTv.setText(
          String
              .format(Locale.getDefault(), "هر %s = %s %s", NumberUtil.digitsToPersian(unit2Title),
                  NumberUtil.digitsToPersian(String.valueOf(unit1Count)),
                  NumberUtil.digitsToPersian(unit1Title)));
    } else {
      costDetailLay.setVisibility(View.GONE);
    }

    if (isRejected()) {
      toolbarText.setText(R.string.add_to_return_goods);
      addButtonText.setText(R.string.register_return);
      bottomLayout
          .setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.register_return));
      registerButtonImage.setImageResource(R.drawable.ic_check_white_18_dp);
      orderCountTv.setText(R.string.return_count);
    }

    if (SaleUtil.isDelivery(orderStatus)) {
      discountLayout.setVisibility(View.GONE);
    } else {
      discountEdt.setText(discount != null && discount != 0 ? String.valueOf(discount) : "");
    }
  }

  /*
  @return true if it's one of the REJECTED states
 */
  protected boolean isRejected() {
    return (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()));
  }

  protected Goods getGoodFromLocal() {
    List<Goods> goods = rejectedGoodsList.getGoodsDtoList();
    for (int i = 0; i < goods.size(); i++) {
      Goods good = goods.get(i);
      if (goodsBackendId == good.getBackendId().longValue()) {
        return good;
      }
    }
    return null;
  }

  protected void setUpSpinner() {
    List<LabelValue> unitsList = new ArrayList<>();
    unitsList.add(new LabelValue(1L, NumberUtil.digitsToPersian(unit1Title)));
    if (Empty.isNotEmpty(unit2Title)) {
      unitsList.add(new LabelValue(2L, NumberUtil.digitsToPersian(unit2Title)));
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

  protected void setUpPager() {
    List<String> pathes = new ArrayList<>();
    pathes.add(MediaUtil.getGoodImage(selectedGoods.getCode()));
    final GoodImagePagerAdapter adapter = new GoodImagePagerAdapter(
        getChildFragmentManager(), getActivity(), pathes);
    viewPager.setAdapter(adapter);
    indicator.setViewPager(viewPager);
    viewPager.setPageTransformer(true, new DepthPageTransformer());
    viewPager.addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        String pos = NumberUtil.digitsToPersian(
            String.format(Locale.getDefault(), "%d/%d", position + 1, pathes.size()));
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

  protected boolean validate() {
    String countValue = NumberUtil.digitsToEnglish(countTv.getText().toString());

    if (Empty.isEmpty(countValue) || Double.valueOf(countValue).equals(0D)) {
      errorMsg.setText(R.string.message_please_enter_count);
      errorMsg.setVisibility(View.VISIBLE);
      hideKeyboard();
      return false;
    }

    int currentUnit = spinner.getSelectedItemPosition();

    //If saleRate setting is enabled & default unit is unit1 & mod unit1 is not zero
    if (shouldApplySaleRate(countValue, currentUnit)) {
      errorMsg.setText(String.format(getString(R.string.error_sale_rate_not_correct),
          NumberUtil.digitsToPersian(saleRate), NumberUtil.digitsToPersian(unit1Title)));
      errorMsg.setVisibility(View.VISIBLE);
      hideKeyboard();
      return false;
    }

    try {
      if (!TextUtils.isEmpty(discountEdt.getText())) {
        discount = Long.parseLong(discountEdt.getText().toString());
        if (discount < 0 || discount > 100) {
          return false;
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  @OnClick({R.id.close, R.id.minus_img, R.id.add_img, R.id.register_order_btn, R.id.bottom_bar})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
        getDialog().dismiss();
        break;
      case R.id.minus_img:
        decreaseCount();
        break;
      case R.id.add_img:
        increaseCount();
        break;
      case R.id.bottom_bar:
      case R.id.register_order_btn:
        if (Empty.isNotEmpty(onClickListener) && validate()) {

          Double count1 = Double
              .valueOf(NumberUtil.digitsToEnglish(countTv.getText().toString()));
          LabelValue selectedUnitLv = (LabelValue) spinner.getSelectedItem();
          Long selectedUnit1 = selectedUnitLv.getValue();
          if (selectedUnit1.equals(2L)) {
            count1 *= Double.valueOf(unit1Count);
          }

          onClickListener.onConfirmBtnClicked(count1, selectedUnit1, discount);
        }
        break;
    }
  }

  private void increaseCount() {
    try {
      if (TextUtils.isEmpty(countTv.getText().toString().trim())) {
        countTv.setText("1");
      } else {
        minusImg.setImageResource(R.drawable.im_minus);
        double enteredNum = Double
            .parseDouble(NumberUtil.digitsToEnglish(countTv.getText().toString().trim()));
        countTv.setText(String.valueOf(enteredNum + 1));
      }
    } catch (NumberFormatException ignore) {

    }
  }

  private void decreaseCount() {
    try {
      if (TextUtils.isEmpty(countTv.getText().toString().trim())) {
        countTv.setText("0");
      } else {
        double enteredNum = Double
            .parseDouble(NumberUtil.digitsToEnglish(countTv.getText().toString().trim()));
        if (enteredNum == 1 || enteredNum == 0) {
          minusImg.setImageResource(R.drawable.im_minus_grey);
        } else {
          minusImg.setImageResource(R.drawable.im_minus);
        }
        if (enteredNum > 0) {
          countTv.setText(String.valueOf(enteredNum - 1));
        }
      }
    } catch (NumberFormatException ignore) {

    }
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(Event event) {
    if (event instanceof ErrorEvent) {
      errorMsg.setText(event.getMessage());
      errorMsg.setVisibility(View.VISIBLE);
      hideKeyboard();

    } else if (event instanceof SuccessEvent) {
      AddOrderDialogFragment.this.dismiss();
    }
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) mainActivity
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.hideSoftInputFromWindow(countTv.getWindowToken(), 0);
    }
  }

  @Override
  public void onDestroyView() {
    //workaround for this issue: https://code.google.com/p/android/issues/detail?id=17423 (unable to retain instance after configuration change)
    if (getDialog() != null && getRetainInstance()) {
      getDialog().setDismissMessage(null);
    }
    super.onDestroyView();
  }

  public interface GoodsDialogOnClickListener {

    void onConfirmBtnClicked(Double count, Long selectedUnit, Long discount);
  }
}
