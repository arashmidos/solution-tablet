package com.parsroyal.solutiontablet.ui.fragment.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.ui.adapter.PathDetailAdapter;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.CustomerInfoDialogFragment;

public class CustomerInfoBottomSheet extends CustomerInfoDialogFragment {

  public final String TAG = CustomerInfoBottomSheet.class.getSimpleName();

  public static CustomerInfoBottomSheet newInstance() {
    return new CustomerInfoBottomSheet();
  }

  public static CustomerInfoBottomSheet newInstance(PathDetailAdapter adapter,
      CustomerListModel model, int position) {
    CustomerInfoBottomSheet fragment = new CustomerInfoBottomSheet();
    fragment.model = model;
    fragment.adapter = adapter;
    fragment.position = position;
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new BottomSheetDialog(getContext(), getTheme());
  }

  protected String getTAG() {
    return TAG;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getDialog().setOnShowListener(dialog -> {
      BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
      FrameLayout bottomSheet = bottomSheetDialog
          .findViewById(android.support.design.R.id.design_bottom_sheet);
      CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) bottomSheet
          .getLayoutParams();
      DisplayMetrics displayMetrics = new DisplayMetrics();
      getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
      int width = displayMetrics.widthPixels;
      params.setMargins(width / 4, 0, width / 4, 0);
      bottomSheet.setLayoutParams(params);
      BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
      getDialog().setCancelable(false);
    });

    return super.onCreateView(inflater, container, savedInstanceState);
  }
}