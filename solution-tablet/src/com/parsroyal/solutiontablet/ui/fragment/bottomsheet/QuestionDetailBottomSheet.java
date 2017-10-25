package com.parsroyal.solutiontablet.ui.fragment.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.parsroyal.solutiontablet.ui.adapter.QuestionsAdapter;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.QuestionDetailDialogFragment;

/**
 * Created by shkbhbb on 10/23/17.
 */

public class QuestionDetailBottomSheet extends QuestionDetailDialogFragment {

  public final String TAG = QuestionDetailBottomSheet.class.getSimpleName();
  private BottomSheetBehavior<FrameLayout> mBottomSheetBehavior;
  private FrameLayout bottomSheet;

  public static QuestionDetailBottomSheet newInstance(Bundle bundle,
      QuestionsAdapter questionsAdapter) {
    QuestionDetailBottomSheet questionDetailBottomSheet = new QuestionDetailBottomSheet();
    questionDetailBottomSheet.setArguments(bundle);
    questionDetailBottomSheet.questionsAdapter = questionsAdapter;
    return questionDetailBottomSheet;
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
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getDialog().setOnShowListener(dialog -> {
      BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) getDialog();

      bottomSheet = (FrameLayout) bottomSheetDialog
          .findViewById(android.support.design.R.id.design_bottom_sheet);
      CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) bottomSheet
          .getLayoutParams();
      DisplayMetrics displayMetrics = new DisplayMetrics();
      getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
      int width = displayMetrics.widthPixels;
      params.setMargins(width / 4, 48, width / 4, 0);
      bottomSheet.setLayoutParams(params);
      mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
      mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    });

    return super.onCreateView(inflater, container, savedInstanceState);
  }
}
