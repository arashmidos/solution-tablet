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
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.GoodsFilterDialogFragment;

public class GoodsFilterBottomSheet extends GoodsFilterDialogFragment {

  public final String TAG = GoodsFilterBottomSheet.class.getSimpleName();

  public GoodsFilterBottomSheet() {
    // Required empty public constructor
  }

  public static GoodsFilterBottomSheet newInstance() {
    return new GoodsFilterBottomSheet();
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
