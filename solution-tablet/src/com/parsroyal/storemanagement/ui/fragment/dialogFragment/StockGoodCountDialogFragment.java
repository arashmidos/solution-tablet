package com.parsroyal.storemanagement.ui.fragment.dialogFragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.dao.impl.StockGoodDaoImpl;
import com.parsroyal.storemanagement.data.model.LabelValue;
import com.parsroyal.storemanagement.data.model.StockGood;
import com.parsroyal.storemanagement.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;

public class StockGoodCountDialogFragment extends DialogFragment {

  public final String TAG = StockGoodCountDialogFragment.class.getSimpleName();
  protected StockGood good;
  @BindView(R.id.close_btn)
  ImageView closeBtn;
  @BindView(R.id.title_tv)
  TextView titleTv;
  @BindView(R.id.good_count)
  EditText goodCount;
  @BindView(R.id.minus)
  ImageView minus;
  @BindView(R.id.add)
  ImageView add;
  @BindView(R.id.confirm_btn)
  TextView confirmBtn;
  @BindView(R.id.root)
  NestedScrollView root;
  @BindView(R.id.spinner)
  Spinner spinner;
  @BindView(R.id.unit1_count_tv)
  TextView unit1CountTv;
  @BindView(R.id.unit3_count_tv)
  TextView unit3CountTv;
  @BindView(R.id.unit1_title_tv)
  TextView unit1TitleTv;
  @BindView(R.id.unit3_title_tv)
  TextView unit3TitleTv;
  @BindView(R.id.unit1_lay)
  LinearLayout unit1Lay;
  @BindView(R.id.unit2_count_tv)
  TextView unit2CountTv;
  @BindView(R.id.unit2_title_tv)
  TextView unit2TitleTv;
  @BindView(R.id.unit2_lay)
  LinearLayout unit2Lay;
  @BindView(R.id.unit3_lay)
  LinearLayout unit3Lay;
  @BindView(R.id.unit21_tv)
  TextView unit21Tv;
  @BindView(R.id.unit31_tv)
  TextView unit31Tv;
  @BindView(R.id.convert_lay)
  LinearLayout convertLay;


  private OnCountStockGoods parentActivity;
  private Unbinder unbinder;
  private StockGoodDaoImpl stockDaoImpl;

  public StockGoodCountDialogFragment() {
    // Required empty public constructor
  }

  public static StockGoodCountDialogFragment newInstance(StockGood good) {
    StockGoodCountDialogFragment dialogFragment = new StockGoodCountDialogFragment();
    dialogFragment.good = good;
    return dialogFragment;
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
    unbinder = ButterKnife.bind(this, view);
    stockDaoImpl = new StockGoodDaoImpl(getActivity());
    setData();
    setupSpinner();
    setListeners();
    return view;
  }

  protected void setupSpinner() {
    List<LabelValue> unitsList = new ArrayList<>();
    unitsList
        .add(new LabelValue(good.getUsnSerialGLS(), NumberUtil.digitsToPersian(good.getuName())));
    if (good.getUsnSerial2GLS() != 0L) {
      unitsList.add(
          new LabelValue(good.getUsnSerial2GLS(), NumberUtil.digitsToPersian(good.getuName1())));
      showUnit12();
      setUnit12();
    }
    if (good.getUsnSerial2_2GLS() != 0L) {
      unitsList.add(
          new LabelValue(good.getUsnSerial2_2GLS(), NumberUtil.digitsToPersian(good.getuName2())));
      showUnit3();
      setUnit3();
    }
    spinner.setAdapter(new LabelValueArrayAdapter(getActivity(), unitsList));

    spinner.setSelection(0);
  }

  private void setUnit3() {
    unit3CountTv.setText("۰");
    unit3TitleTv.setText(good.getuName2());
    unit31Tv.setText(String
        .format("هر %s = %s %s", good.getuName2(), NumberUtil.digitsToPersian(good.getbRateGLS()),
            good.getuName()));
  }

  private void setUnit12() {
    unit1CountTv.setText("۰");
    unit2CountTv.setText("۰");
    unit1TitleTv.setText(good.getuName());
    unit2TitleTv.setText(good.getuName1());
    unit21Tv.setText(String
        .format("هر %s = %s %s", good.getuName1(), NumberUtil.digitsToPersian(good.getbRateGLS()),
            good.getuName()));
  }

  private void showUnit12() {
    unit1Lay.setVisibility(View.VISIBLE);
    unit2Lay.setVisibility(View.VISIBLE);
    convertLay.setVisibility(View.VISIBLE);
  }

  private void showUnit3() {
    unit3Lay.setVisibility(View.VISIBLE);
    unit31Tv.setVisibility(View.VISIBLE);
  }


  private void setListeners() {
    goodCount.addTextChangedListener(new TextWatcher() {
      boolean _ignore = false;

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if (_ignore) {
          return;
        }

        _ignore = true; // prevent infinite loop
        goodCount.setText(NumberUtil.digitsToPersian(s.toString()));
        refreshData();
        _ignore = false; // release, so the TextWatcher start to listen again.
        Log.i("EDIIIIITTTTT", "SALAMA");
      }
    });

  }

  private void refreshData() {
    long count = Long.parseLong(NumberUtil.digitsToEnglish(goodCount.getText().toString()));
    int unit = spinner.getSelectedItemPosition();
    if (unit == 0) {
//unit1CountTv.setText(NumberUtil.formatPersian3DecimalPlaces());
    } else if (unit == 1) {

    } else if (unit == 2) {

    }
  }

  private void setData() {
    titleTv.setText(good.getGoodNamGLS());
    if (Empty.isNullOrZero(good.getCounted())) {
      goodCount.setText(NumberUtil.digitsToPersian(0));
    } else {
      goodCount.setText(NumberUtil.digitsToPersian(good.getCounted() / 1000));
    }

  }

  protected int getLayout() {
    if (!getTAG().contains("Sheet")) {
      setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
      return R.layout.fragment_dialog_add_stock_good;
    }
    return R.layout.fragment_dialog_add_stock_good_bottom_sheet;
  }

  @OnClick({R.id.close_btn, R.id.add, R.id.minus, R.id.confirm_btn})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.close_btn:
        dismiss();
        break;
      case R.id.add:
        increaseCount();
        break;
      case R.id.minus:
        decreaseCount();
        break;
      case R.id.confirm_btn:
        submit();
        break;
    }
  }

  private void submit() {
    if (validate()) {
      try {
        String enteredNum = goodCount.getText().toString().trim();
        long number = Long.parseLong(enteredNum);
        good.setCounted(number * 1000);
        parentActivity.update(good);
        dismiss();
      } catch (NumberFormatException ex) {
        ToastUtil.toastError(root, R.string.error_wrong_number);
      }
    } else {
      ToastUtil.toastError(root, R.string.error_wrong_number);
    }
  }

  private boolean validate() {
    String enteredString = goodCount.getText().toString().trim();
    try {
      long enteredNum = Long.parseLong(enteredString);
      if (enteredNum < 0) {
        return false;
      }
    } catch (NumberFormatException ex) {
      return false;
    }
    return true;
  }

  private void increaseCount() {
    try {
      if (TextUtils.isEmpty(goodCount.getText().toString().trim())) {
        goodCount.setText(NumberUtil.digitsToPersian("1"));
      } else {
//        minusImg.setImageResource(R.drawable.im_minus);
        long enteredNum = Long
            .parseLong(NumberUtil.digitsToEnglish(goodCount.getText().toString().trim()));

        goodCount.setText(NumberUtil.digitsToPersian(enteredNum + 1));
      }
    } catch (NumberFormatException ignore) {

    }
  }

  private void decreaseCount() {
    try {
      if (TextUtils.isEmpty(goodCount.getText().toString().trim())) {
        goodCount.setText(NumberUtil.digitsToPersian("0"));
      } else {
        long enteredNum = Long
            .parseLong(NumberUtil.digitsToEnglish(goodCount.getText().toString().trim()));
        if (enteredNum == 1 || enteredNum == 0) {
//          minusImg.setImageResource(R.drawable.im_minus_grey);
        } else {
//          minusImg.setImageResource(R.drawable.im_minus);
        }
        if (enteredNum > 0) {
          goodCount.setText(NumberUtil.digitsToPersian(enteredNum - 1));
        }
      }
    } catch (NumberFormatException ignore) {
      goodCount.setText("۰");
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      parentActivity = (OnCountStockGoods) context;
    } catch (Exception ex) {
      throw new IllegalArgumentException("You should implement OnCountStockGoods");
    }
  }

  public interface OnCountStockGoods {

    void update(StockGood good);
  }
}
