package com.parsroyal.storemanagement.ui.fragment.dialogFragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.dao.impl.StockGoodDaoImpl;
import com.parsroyal.storemanagement.data.model.StockGood;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.ToastUtil;

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
    return view;
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
