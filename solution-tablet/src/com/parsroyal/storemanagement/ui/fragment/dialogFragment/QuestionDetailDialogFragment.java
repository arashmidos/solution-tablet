package com.parsroyal.storemanagement.ui.fragment.dialogFragment;

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
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.QuestionType;
import com.parsroyal.storemanagement.data.model.QuestionDto;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.QuestionDetailAdapter;
import com.parsroyal.storemanagement.ui.adapter.QuestionsAdapter;
import com.parsroyal.storemanagement.util.NumberUtil;
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
  @BindView(R.id.prerequisite_btn)
  protected Button prerequisiteBtn;

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
    positionTv.setText(
        NumberUtil.digitsToPersian(String.format(Locale.getDefault(), "%d/%d", currentPosition,
            questionsAdapter.getItemCount())));

    if (questionDto.getType() == QuestionType.CHOICE_SINGLE || questionDto.getType() == QuestionType.DATE) {
      radioDetailTv.setVisibility(View.VISIBLE);
      checkBoxDetailTv.setVisibility(View.GONE);
    } else if (questionDto.getType() == QuestionType.CHOICE_MULTIPLE) {
      checkBoxDetailTv.setVisibility(View.VISIBLE);
      radioDetailTv.setVisibility(View.GONE);
    } else {
      radioDetailTv.setVisibility(View.GONE);
      checkBoxDetailTv.setVisibility(View.GONE);
    }
    if (TextUtils.isEmpty(questionDto.getAnswer()) && questionsAdapter.hasPrerequisite(questionDto)
        && questionsAdapter.preRequisiteNotAnswered(questionDto)) {
      errorMsg.setVisibility(View.VISIBLE);
      prerequisiteBtn.setVisibility(View.VISIBLE);
      prerequisiteBtn.setText(
          NumberUtil.digitsToPersian(String.format(Locale.getDefault(), getString(R.string.show_question_x),
              questionsAdapter.findPositionByBackendId(questionDto.getPrerequisite()))));
      errorMsg.setText(NumberUtil.digitsToPersian(String
          .format(Locale.getDefault(),              getString(R.string.question_has_prerequisite_x),
              questionDto.getPrerequisite())));
      recyclerView.setVisibility(View.GONE);
      saveQuestionImg.setVisibility(View.GONE);
      radioDetailTv.setVisibility(View.GONE);
      checkBoxDetailTv.setVisibility(View.GONE);
      questionTv.setTextColor(ContextCompat.getColor(mainActivity, R.color.gray_9e));
    } else {
      questionTv.setTextColor(ContextCompat.getColor(mainActivity, R.color.black_85));
      recyclerView.setVisibility(View.VISIBLE);
      errorMsg.setVisibility(View.GONE);
      prerequisiteBtn.setVisibility(View.GONE);
      saveQuestionImg.setVisibility(View.VISIBLE);
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
      R.id.save_question_img, R.id.prerequisite_btn})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.close_btn:
        getDialog().dismiss();
        break;
      case R.id.prerequisite_btn:
        questionDto = questionsAdapter.getQuestionDtoByBackendId(questionDto.getPrerequisite());
        currentPosition = questionsAdapter.getCurrentItemPosition() + 1;
        setData();
        setUpRecyclerView();
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
          if( questionDto.getType() == QuestionType.CHOICE_SINGLE) {
            questionDetailAdapter.removeAllSelections();
          }else{
            //Date type
            questionDetailAdapter.removeDate();
          }
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
    setUpRecyclerView();
  }

}
