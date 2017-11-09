package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.model.QuestionDto;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.QuestionDetailAdapter;
import com.parsroyal.solutiontablet.ui.adapter.QuestionsAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by shkbhbb on 10/16/17.
 */

public class QuestionDetailDialogFragment extends DialogFragment {

  public static final String TAG = QuestionDetailDialogFragment.class.getSimpleName();

  @BindView(R.id.question_tv)
  protected TextView questionTv;
  @BindView(R.id.recycler_view)
  protected RecyclerView recyclerView;
  @BindView(R.id.position_tv)
  protected TextView positionTv;
  protected Unbinder unbinder;
  @BindView(R.id.check_box_detail_tv)
  protected TextView checkBoxDetailTv;
  @BindView(R.id.radio_detail_tv)
  protected TextView radioDetailTv;
  @BindView(R.id.main_lay)
  protected View mainLay;
  @BindView(R.id.next_btn)
  protected Button nextBtn;
  @BindView(R.id.previous_tv)
  protected TextView previousTv;
  @BindView(R.id.error_msg)
  protected TextView errorMsg;
  @BindView(R.id.save_question_img)
  protected ImageView saveQuestionImg;

  protected QuestionDto questionDto;
  protected Context context;
  protected MainActivity mainActivity;
  protected QuestionsAdapter questionsAdapter;
  protected int currentPosition;
  protected QuestionDetailAdapter questionDetailAdapter;

  public QuestionDetailDialogFragment() {
  }

  public static QuestionDetailDialogFragment newInstance(Bundle bundle,
      QuestionsAdapter questionsAdapter) {
    QuestionDetailDialogFragment questionDetailDialogFragment = new QuestionDetailDialogFragment();
    questionDetailDialogFragment.setArguments(bundle);
    questionDetailDialogFragment.questionsAdapter = questionsAdapter;
    return questionDetailDialogFragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!getTAG().contains("Sheet")) {
      setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
    }
    setRetainInstance(true);
  }

  protected String getTAG() {
    return TAG;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_dialog_question_detail, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    Bundle bundle = getArguments();
    questionDto = (QuestionDto) bundle.getSerializable(Constants.QUESTION_DTO);
    currentPosition = bundle.getInt(Constants.QUESTION_POSITION);

    setUpRecyclerView();

    setData();

    return view;
  }

  protected void setData() {
    navigateButtonStatus(currentPosition != 1, false);
    navigateButtonStatus(currentPosition != questionsAdapter.getItemCount(), true);
    questionTv.setText(questionDto.getQuestion());
    positionTv.setText(String.format(Locale.getDefault(), "%d/%d", currentPosition,
        questionsAdapter.getItemCount()));

    if (questionDto.getType().getValue() == 102) {
      radioDetailTv.setVisibility(View.VISIBLE);
      checkBoxDetailTv.setVisibility(View.GONE);
    } else if (questionDto.getType().getValue() == 103) {
      checkBoxDetailTv.setVisibility(View.VISIBLE);
      radioDetailTv.setVisibility(View.GONE);
    } else {
      radioDetailTv.setVisibility(View.GONE);
      checkBoxDetailTv.setVisibility(View.GONE);
    }
  }

  protected void setUpRecyclerView() {
    List<String> answers = Arrays.asList(questionDto.getqAnswers().split("[*]"));
    questionDetailAdapter = new QuestionDetailAdapter(mainActivity, answers,
        questionDto.getType(), questionDto.getAnswer(), this);
    LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(questionDetailAdapter);
  }

  @Override
  public void onDestroyView() {
    Dialog dialog = getDialog();
    // handles https://code.google.com/p/android/issues/detail?id=17423
    if (dialog != null && getRetainInstance()) {
      dialog.setDismissMessage(null);
    }
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick({R.id.previous_tv, R.id.close_btn, R.id.next_btn, R.id.radio_detail_tv,
      R.id.save_question_img})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.close_btn:
        getDialog().dismiss();
        break;
      case R.id.previous_tv:
        if (currentPosition != 1) {
          changeQuestion(false);
        } else {
          questionsAdapter.saveUserAnswer(questionDetailAdapter.getUserAnswers());
          getDialog().dismiss();
        }
        break;
      case R.id.radio_detail_tv:
        if (questionDetailAdapter != null) {
          questionDetailAdapter.removeAllSelections();
        }
        break;
      case R.id.next_btn:
        if (currentPosition != questionsAdapter.getItemCount()) {
          changeQuestion(true);
        } else {
          questionsAdapter.saveUserAnswer(questionDetailAdapter.getUserAnswers());
          getDialog().dismiss();
        }
        break;
      case R.id.save_question_img:
        questionsAdapter.saveUserAnswer(questionDetailAdapter.getUserAnswers());
        getDialog().dismiss();
        break;
    }
  }

  public void navigateButtonStatus(boolean isEnable, boolean isNext) {
    if (isEnable) {
      if (isNext) {
        nextBtn.setBackgroundResource(R.drawable.get_data_background);
        nextBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        nextBtn
            .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chevron_left_24_white, 0, 0, 0);
      } else {
        previousTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_dark));
        previousTv
            .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_right_blue, 0);

      }
    } else {
      if (isNext) {
        nextBtn.setBackgroundResource(R.drawable.rec_gray);
        nextBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_e0));
        nextBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chevron_left_e0, 0, 0, 0);
      } else {
        previousTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_9e));
        previousTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_right_9e, 0);
      }
    }
  }

  protected void changeQuestion(boolean isNext) {
    if (isNext) {
      questionDto = questionsAdapter.getNext(questionDetailAdapter.getUserAnswers());
    } else {
      questionDto = questionsAdapter.getPrevious(questionDetailAdapter.getUserAnswers());
    }
    currentPosition = questionsAdapter.getCurrentItemPosition() + 1;
    setData();
    if (TextUtils.isEmpty(questionDto.getAnswer()) && questionsAdapter.hasPrerequisite(questionDto)
        && questionsAdapter.preRequisiteNotAnswered(questionDto)) {
      errorMsg.setVisibility(View.VISIBLE);
      errorMsg.setText(String
          .format(Locale.getDefault(),
              "جهت پاسخ به این سوال، ابتدا باید به سوال شماره %d پاسخ داده شود!",
              questionsAdapter.findPositionByBackendId(questionDto.getPrerequisite())));
      recyclerView.setVisibility(View.GONE);
      saveQuestionImg.setVisibility(View.GONE);
    } else {
      recyclerView.setVisibility(View.VISIBLE);
      errorMsg.setVisibility(View.GONE);
      saveQuestionImg.setVisibility(View.VISIBLE);
      setUpRecyclerView();
    }
  }

}
