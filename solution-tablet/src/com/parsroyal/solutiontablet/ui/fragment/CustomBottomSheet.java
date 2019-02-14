package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.activity.PackerActivity;
import org.jetbrains.annotations.NotNull;

/**
 * Created by arash on 12/3/17.
 */

public class CustomBottomSheet extends BottomSheetDialogFragment {

  public static CustomBottomSheet getInstance() {
    return new CustomBottomSheet();
  }

  @Nullable
  @Override
  public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.layout_custom_bottom_sheet, container, false);
    ButterKnife.bind(this, view);

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @OnClick({R.id.select_order_layout, R.id.select_request_layout})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.select_order_layout:
        selectOrder();
        break;
      case R.id.select_request_layout:
        selectRequest();
        break;

    }
    dismiss();
  }

  public void selectOrder() {
    ((PackerActivity)getActivity()).selectOrder();
  }

  public void selectRequest() {
    ((PackerActivity)getActivity()).selectRequest();

  }

}
