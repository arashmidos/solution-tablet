package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;


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
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.event.UpdateListEvent;
import com.parsroyal.solutiontablet.data.model.GoodDetail;
import com.parsroyal.solutiontablet.ui.activity.PackerActivity;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import org.greenrobot.eventbus.EventBus;

public class PackerAddGoodDialogFragment extends DialogFragment {

  public final String TAG = PackerAddGoodDialogFragment.class.getSimpleName();
  protected GoodDetail good;
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


  private PackerActivity packerActivity;
  private Unbinder unbinder;

  public PackerAddGoodDialogFragment() {
    // Required empty public constructor
  }

  public static PackerAddGoodDialogFragment newInstance(GoodDetail good) {
    PackerAddGoodDialogFragment dialogFragment = new PackerAddGoodDialogFragment();
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
    packerActivity = (PackerActivity) getActivity();

    setData();
    return view;
  }

  private void setData() {
    titleTv.setText(good.getGoodNameSGL());
    if (good.getPacked() == 0) {
      goodCount.setText(NumberUtil.digitsToPersian(String.valueOf(good.getQty() / 1000)));
    } else {
      goodCount.setText(NumberUtil.digitsToPersian(String.valueOf(good.getPacked() / 1000)));

    }
  }

  protected int getLayout() {
    if (!getTAG().contains("Sheet")) {
      setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
      return R.layout.fragment_dialog_add_good;
    }
    return R.layout.fragment_dialog_add_good_bottom_sheet;
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
        good.setPacked(number * 1000);
        good.setRemain(good.getQty() - number * 1000);
        dismiss();
        EventBus.getDefault().post(new UpdateListEvent());
      } catch (NumberFormatException ex) {
        ToastUtil.toastError(root, "عدد وارد شده صحیح نیست");
      }
    } else {
      ToastUtil.toastError(root, "عدد وارد شده صحیح نیست");
    }
  }

  private boolean validate() {
    String enteredString = goodCount.getText().toString().trim();
    try {
      long enteredNum = Long.parseLong(enteredString);
      if (enteredNum < 0 || enteredNum * 1000 > good.getQty()) {
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
        if (enteredNum == good.getQty() / 1000) {
          return;
        }
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
      goodCount.setText(NumberUtil.digitsToPersian("0"));
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
