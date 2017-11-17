package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.PageStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.listmodel.QuestionListModel;
import com.parsroyal.solutiontablet.data.model.QuestionDto;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.QuestionsAdapter.ViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.QuestionsListFragment;
import com.parsroyal.solutiontablet.ui.fragment.bottomsheet.QuestionDetailBottomSheet;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.QuestionDetailDialogFragment;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by shkbhbb on 10/16/17.
 */

public class QuestionsAdapter extends Adapter<ViewHolder> {

  private final QuestionsListFragment parent;
  private Context context;
  private List<QuestionListModel> questions;
  private LayoutInflater inflater;
  private MainActivity mainActivity;
  private QuestionnaireService questionnaireService;
  private long visitId;
  private long answersGroupNo;
  private int currentItemPosition = 0;
  private long goodsBackendId;
  private CustomerService customerService;
  private long customerId;
  private VisitService visitService;
  private long questionnaireBackendId;
  private PageStatus pageStatus;

  public QuestionsAdapter(QuestionsListFragment questionsListFragment, Context context,
      List<QuestionListModel> questions, long visitId,
      long goodsGroupBackendId, long answersGroupNo, long customerId, long questionnaireBackendId,
      PageStatus pageStatus) {
    this.context = context;
    this.parent = questionsListFragment;
    this.visitId = visitId;
    this.goodsBackendId = goodsGroupBackendId;
    this.customerId = customerId;
    this.answersGroupNo = answersGroupNo;
    this.questions = questions;
    this.pageStatus = pageStatus;
    this.questionnaireBackendId = questionnaireBackendId;
    this.mainActivity = (MainActivity) context;
    this.questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    this.customerService = new CustomerServiceImpl(mainActivity);
    this.visitService = new VisitServiceImpl(mainActivity);
    this.inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_question_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setData(position);
  }

  public QuestionDto getNext(String answer) {
    saveUserAnswer(answer);
    currentItemPosition += 1;
    return questionnaireService
        .getQuestionDto(questions.get(currentItemPosition).getPrimaryKey(), visitId, goodsBackendId,
            answersGroupNo);
  }

  public void saveUserAnswer(String answer) {
    QuestionDto questionDto = questionnaireService
        .getQuestionDto(questions.get(currentItemPosition).getPrimaryKey(), visitId, goodsBackendId,
            answersGroupNo);
    questionDto.setAnswerId(saveAnswer(questionDto, TextUtils.isEmpty(answer) ? "" : answer));
//    questions.get(currentItemPosition).setAnswer(answer);
    setVisitDetail();
    questions = parent.getQuestions();
    notifyDataSetChanged();
  }

  private void setVisitDetail() {
    List<VisitInformationDetail> detailList = visitService
        .searchVisitDetail(visitId, VisitInformationDetailType.FILL_QUESTIONNAIRE,
            answersGroupNo);
    //If we have filled this questionary before
    if (detailList.size() > 0) {
      return;
    }
    visitService.saveVisitDetail(new VisitInformationDetail(visitId,
        VisitInformationDetailType.FILL_QUESTIONNAIRE, answersGroupNo));
  }

  private Long saveAnswer(QuestionDto questionDto, String answer) {
    Customer customer = customerService.getCustomerById(customerId);
    QAnswer qAnswer = questionnaireService.getAnswerById(questionDto.getAnswerId());
    if (Empty.isEmpty(qAnswer)) {
      qAnswer = new QAnswer();
      qAnswer.setId(questionDto.getAnswerId());
      qAnswer.setAnswer(answer);
      qAnswer.setDate(DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER, "FA"));
      if (Empty.isNotEmpty(customer)) {
        qAnswer.setCustomerBackendId(
            customer.getBackendId() == 0 ? customerId : customer.getBackendId());
      } else {
        qAnswer.setCustomerBackendId(0L);
      }
      if (Empty.isNotEmpty(goodsBackendId)) {
        qAnswer.setGoodsBackendId(goodsBackendId);
      }
      qAnswer.setVisitId(visitId);
      qAnswer.setQuestionBackendId(questionDto.getBackendId());
      qAnswer.setAnswersGroupNo(answersGroupNo);
    } else {
      qAnswer.setAnswer(answer);
    }
    return questionnaireService.saveAnswer(qAnswer);
  }

  public QuestionDto getPrevious(String answer) {
    saveUserAnswer(answer);
    currentItemPosition -= 1;
    return questionnaireService
        .getQuestionDto(questions.get(currentItemPosition).getPrimaryKey(), visitId, goodsBackendId,
            answersGroupNo);
  }

  public int getCurrentItemPosition() {
    return currentItemPosition;
  }

  @Override
  public int getItemCount() {
    return questions.size();
  }

  public boolean preRequisiteNotAnswered(QuestionDto currentQuestionDto) {
    QuestionDto nextQuestionDto = questionnaireService
        .getQuestionDtoByBackendId(currentQuestionDto.getPrerequisite(), visitId, goodsBackendId,
            answersGroupNo);
    return nextQuestionDto != null && TextUtils.isEmpty(nextQuestionDto.getAnswer());
  }

  public QuestionDto getQuestionDtoByBackendId(Long backendId) {
    currentItemPosition = (int) (long) backendId - 1;
    return questionnaireService
        .getQuestionDtoByBackendId(backendId, visitId, goodsBackendId,
            answersGroupNo);
  }

  public boolean hasPrerequisite(QuestionDto currentQuestionDto) {
    return currentQuestionDto.getPrerequisite() != null
        && currentQuestionDto.getPrerequisite() != 0L;
  }

  public int findPositionByBackendId(long backendId) {
    for (int i = 0; i < questions.size(); i++) {
      QuestionDto questionDto = questionnaireService
          .getQuestionDto(questions.get(i).getPrimaryKey(), visitId, goodsBackendId,
              answersGroupNo);
      if (questionDto != null && questionDto.getBackendId() == backendId) {
        return i + 1;
      }
    }
    return -1;
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    @BindView(R.id.question_lay)
    CardView questionLay;
    @BindView(R.id.question_tv)
    TextView questionTv;
    @BindView(R.id.question_number_btn)
    Button questionNumberBtn;
    @BindView(R.id.answer_tv)
    TextView answerTv;
    @BindView(R.id.prerequisite_tv)
    TextView preRequisiteTv;

    private QuestionListModel question;
    private int position;
    private QuestionDto questionDto;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      questionLay.setOnClickListener(this);
    }

    public void setData(int position) {
      this.position = position;
      this.question = questions.get(position);
      questionTv.setText(question.getQuestion());
      questionNumberBtn.setText(NumberUtil.digitsToPersian(String.valueOf(position + 1)));
      QuestionDto questionDto = questionnaireService
          .getQuestionDto(question.getPrimaryKey(), visitId, goodsBackendId,
              answersGroupNo);
      this.questionDto = questionDto;
      if (questionDto.isRequired()) {
        questionTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_star_red_18, 0);
        questionTv.setCompoundDrawablePadding(10);
      }
      preRequisiteTv.setVisibility(View.GONE);
      if (TextUtils.isEmpty(questionDto.getAnswer())) {
        answerTv.setVisibility(View.GONE);
        questionNumberBtn.setBackgroundResource(R.drawable.oval_ee);
        questionNumberBtn.setTextColor(ContextCompat.getColor(mainActivity, R.color.primary_dark));
        Long prerequisite = questionDto.getPrerequisite();
        if (hasPrerequisite(questionDto) && prerequisite != -1) {
          preRequisiteTv.setVisibility(View.VISIBLE);
          preRequisiteTv.setText(String.format(Locale.getDefault(), "پیش نیاز: سوال شماره %d",
              findPositionByBackendId(prerequisite)));
        }
      } else {
        questionNumberBtn.setBackgroundResource(R.drawable.oval_green_43);
        questionNumberBtn.setTextColor(ContextCompat.getColor(mainActivity, R.color.white));
        answerTv.setVisibility(View.VISIBLE);
        answerTv.setText(questionDto.getAnswer().replaceAll("[*]", " - "));
      }
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.question_lay:
          if (pageStatus == null || pageStatus == PageStatus.EDIT) {
            currentItemPosition = position;

            Bundle bundle = new Bundle();
          /*QuestionDto questionDto = questionnaireService
              .getQuestionDto(question.getPrimaryKey(), visitId, goodsBackendId,
                  answersGroupNo);*/
            bundle.putSerializable(Constants.QUESTION_DTO, questionDto);
            bundle.putInt(Constants.QUESTION_POSITION, position + 1);
            FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
            QuestionDetailDialogFragment questionDetailDialogFragment;
            if (MultiScreenUtility.isTablet(mainActivity)) {
              questionDetailDialogFragment = QuestionDetailBottomSheet
                  .newInstance(bundle, QuestionsAdapter.this);
            } else {
              questionDetailDialogFragment = QuestionDetailDialogFragment
                  .newInstance(bundle, QuestionsAdapter.this);
            }
            questionDetailDialogFragment.show(ft, "Question detail");
          }
          break;
      }
    }
  }
}
