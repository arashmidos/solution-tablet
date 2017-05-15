package com.parsroyal.solutiontablet.ui.fragment;

import static android.view.View.GONE;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.alirezaafkar.sundatepicker.DatePicker;
import com.alirezaafkar.sundatepicker.interfaces.DateSetListener;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.listmodel.QuestionListModel;
import com.parsroyal.solutiontablet.data.model.QuestionDto;
import com.parsroyal.solutiontablet.data.searchobject.QuestionSo;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.QuestionListAdapter;
import com.parsroyal.solutiontablet.ui.component.FlowLayout;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mahyar on 7/25/2015.
 */
public class QuestionnaireDetailFragment extends
    BaseListFragment<QuestionListModel, QuestionListAdapter>
    implements CompoundButton.OnCheckedChangeListener, DateSetListener {

  public static final String TAG = QuestionnaireDetailFragment.class.getSimpleName();

  private MainActivity mainActivity;
  private QuestionnaireService questionnaireService;
  private CustomerService customerService;
  private GoodsService goodsService;
  private VisitService visitService;
  private Long questionnaireBackendId;
  private Long customerId;
  private Long goodsBackendId;
  private Long visitId;
  private Customer customer;
  private EditText answerEt;
  private EditText answerEt2;
  private FlowLayout buttonAnswerLayout;
  private LinearLayout textAnswerLayout;
  private CompoundButton userChoice;
  private long amountValue;
  private int parent;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    try {
      mainActivity = (MainActivity) getActivity();
      questionnaireService = new QuestionnaireServiceImpl(mainActivity);
      customerService = new CustomerServiceImpl(mainActivity);
      goodsService = new GoodsServiceImpl(mainActivity);
      visitService = new VisitServiceImpl(mainActivity);

      Bundle arguments = getArguments();
      questionnaireBackendId = arguments.getLong(Constants.QUESTIONAIRE_ID);
      visitId = arguments.getLong(Constants.VISIT_ID);
      customerId = arguments.getLong(Constants.CUSTOMER_ID);
      goodsBackendId = arguments.getLong(Constants.GOODS_BACKEND_ID);
      parent = arguments.getInt(Constants.PARENT, 0);

      customer = customerService.getCustomerById(customerId);

      View view = super.onCreateView(inflater, container, savedInstanceState);
      buttonPanel.setVisibility(View.VISIBLE);
      Button canclButton = (Button) buttonPanel.findViewById(R.id.cancelBtn);
      canclButton.setOnClickListener(v ->
      {
        if (Empty.isEmpty(customer)) {
          //It's anonymous questionaire
          //known bug, it he has not answered any quesiton, should remove the entire visit.
          // List<VisitInformationDetail> detailList = visitService.searchVisitDetail(visitId, VisitInformationDetailType.FILL_QUESTIONNAIRE, questionnaireBackendId);
          visitService.finishVisiting(visitId);
        }
        mainActivity.removeFragment(QuestionnaireDetailFragment.this);
        mainActivity.changeSidebarItem(parent);
      });
      return view;
    } catch (Exception ex) {
      Log.e(TAG, ex.getMessage(), ex);
      return inflater.inflate(R.layout.view_error_page, null);
    }
  }

  @Override
  protected List<QuestionListModel> getDataModel() {
    QuestionSo questionSo = new QuestionSo();
    questionSo.setQuestionnaireBackendId(questionnaireBackendId);
    questionSo.setVisitId(visitId);
    questionSo.setGoodsBackendId(goodsBackendId);
    return questionnaireService.searchForQuestions(questionSo);
  }

  @Override
  protected View getHeaderView() {
    return null;
  }

  @Override
  protected QuestionListAdapter getAdapter() {
    return new QuestionListAdapter(mainActivity, dataModel, questionnaireBackendId, visitId);
  }

  @Override
  protected AdapterView.OnItemClickListener getOnItemClickListener() {
    if (customer != null && customer.getBackendId() != 0 && !customer.isApproved()) {
      return null;
    }

    return (parent, view, position, id) ->
    {
      QuestionDto questionDto = questionnaireService
          .getQuestionDto(dataModel.get(position).getPrimaryKey(), visitId, goodsBackendId);
      openQuestionDialog(questionDto);
    };
  }

  private void openQuestionDialog(final QuestionDto questionDto) {
    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mainActivity);
    View view = mainActivity.getLayoutInflater().inflate(R.layout.dialg_layout_question, null);

    TextView questionnaireTitleTv = (TextView) view.findViewById(R.id.questionnaireTitleTv);
    TextView questionTv = (TextView) view.findViewById(R.id.questionTv);
    TextView answerTv = (TextView) view.findViewById(R.id.textView39);
    buttonAnswerLayout = (FlowLayout) view.findViewById(R.id.buttonAnswerLayout);
    textAnswerLayout = (LinearLayout) view.findViewById(R.id.textAnswerLayout);
    answerEt = (EditText) view.findViewById(R.id.answerEt);
    answerEt2 = (EditText) view.findViewById(R.id.answerEt2);

    final Button prvBtn = (Button) view.findViewById(R.id.prvBtn);
    Button nextBtn = (Button) view.findViewById(R.id.nextBtn);
    Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
    Button saveBtn = (Button) view.findViewById(R.id.saveBtn);

    questionnaireTitleTv.setText(questionDto.getQuestionnaireTitle());
    questionTv.setText(questionDto.getQuestion());
    answerEt.setText(Empty.isEmpty(questionDto.getAnswer()) ? ""
        : questionDto.getAnswer().replaceAll("[*]", " - "));

    switch (questionDto.getType()) {
      case SIMPLE:
        answerEt.setVisibility(View.VISIBLE);
        answerEt.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
        buttonAnswerLayout.setVisibility(GONE);
        answerEt2.setVisibility(GONE);
        break;
      case SIMPLE_NUMERIC:
        answerEt.setVisibility(GONE);
        answerEt2.setVisibility(View.VISIBLE);
        String s = answerEt2.getText().toString();
        if (Empty.isNotEmpty(s)) {
          answerEt2.setText(String.format(Locale.US, "%,d", Integer.parseInt(s)));
        }
        answerEt2.addTextChangedListener(new TextWatcher() {
          boolean isEditing = false;

          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
          }

          @Override
          public void afterTextChanged(Editable s) {
            if (isEditing) {
              return;
            }
            isEditing = true;
            try {
              amountValue = Long
                  .parseLong(NumberUtil.digitsToEnglish(s.toString().replaceAll(",", "")));
              String number = String.format(Locale.US, "%,d", amountValue);
              s.replace(0, s.length(), number);
            } catch (NumberFormatException e) {
              e.printStackTrace();
            }
            isEditing = false;
          }
        });
        buttonAnswerLayout.setVisibility(GONE);
        answerEt2.setText(Empty.isEmpty(questionDto.getAnswer()) ? ""
            : questionDto.getAnswer().replaceAll("[*]", " - "));
        break;
      case CHOICE_SINGLE:
        textAnswerLayout.setVisibility(GONE);
        createRadioButtons(questionDto.getqAnswers(), questionDto.getAnswer());
        break;
      case CHOICE_MULTIPLE:
        textAnswerLayout.setVisibility(GONE);
        createCheckBox(questionDto.getqAnswers(), questionDto.getAnswer());
        break;
      case DATE:
        answerEt.setVisibility(View.VISIBLE);
        answerEt2.setVisibility(GONE);
        answerEt.setFocusable(false);
        answerEt.setHint(
            Empty.isEmpty(questionDto.getAnswer()) ? "انتخاب تاریخ..." : questionDto.getAnswer());
        answerEt.setText("");
        answerEt.setOnClickListener(v ->
        {
          DatePicker.Builder builder = new DatePicker.Builder().id(1);
          if (Empty.isNotEmpty(questionDto.getAnswer())) {
            String[] date = questionDto.getAnswer().split("/");
            builder.date(Integer.parseInt(date[2]),
                Integer.parseInt(date[1]),
                Integer.parseInt("13" + date[0]));
          }
          builder.build(QuestionnaireDetailFragment.this).show(getFragmentManager(), "");
        });
        buttonAnswerLayout.setVisibility(GONE);
    }

    alertBuilder.setView(view);

    final AlertDialog alert = alertBuilder.create();
    alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    prvBtn.setOnClickListener(v ->
    {
      try {
        String answer = getAnswer(questionDto);
        if (Empty.isNotEmpty(answer)) {
          questionDto.setAnswerId(saveAnswer(questionDto, answer));
          setVisitDetail();
        }
        adapter.setDataModel(dataModel);
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
        QuestionDto prvQuestionDto = questionnaireService
            .getQuestionDto(questionnaireBackendId, visitId, questionDto.getqOrder(),
                goodsBackendId, false);
        if (Empty.isNotEmpty(prvQuestionDto)) {
          alert.dismiss();
          openQuestionDialog(prvQuestionDto);
        }

      } catch (Exception ex) {
        Log.e(TAG, ex.getMessage(), ex);
        ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
      }
    });

    nextBtn.setOnClickListener(v ->
    {
      try {
        String answer = getAnswer(questionDto);
        if (Empty.isNotEmpty(answer)) {
          questionDto.setAnswerId(saveAnswer(questionDto, answer));
          setVisitDetail();
        }
        QuestionDto nextQuestionDto = questionnaireService
            .getQuestionDto(questionnaireBackendId, visitId, questionDto.getqOrder(),
                goodsBackendId, true);
        adapter.setDataModel(dataModel);
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
        if (Empty.isNotEmpty(nextQuestionDto)) {
          alert.dismiss();
          openQuestionDialog(nextQuestionDto);
        }
      } catch (Exception ex) {
        Log.e(TAG, ex.getMessage(), ex);
        ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
      }
    });

    cancelBtn.setOnClickListener(v ->
    {
      dataModel = getDataModel();
      adapter.setDataModel(dataModel);
      adapter.notifyDataSetChanged();
      adapter.notifyDataSetInvalidated();
      alert.dismiss();
    });

    saveBtn.setOnClickListener(v ->
    {
      try {
        String answer = getAnswer(questionDto);
        Long answerId = saveAnswer(questionDto, answer);
        if (Empty.isNotEmpty(answer)) {
          setVisitDetail();
        }
        questionDto.setAnswerId(answerId);
        dataModel = getDataModel();
        adapter.setDataModel(dataModel);
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
      } catch (Exception ex) {
        Log.e(TAG, ex.getMessage(), ex);
        ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
      }
      alert.dismiss();
    });

    alert.show();
  }

  private void setVisitDetail() {
    List<VisitInformationDetail> detailList = visitService
        .searchVisitDetail(visitId, VisitInformationDetailType.FILL_QUESTIONNAIRE,
            questionnaireBackendId);
    //If we have filled this questionary before
    if (detailList.size() > 0) {
      return;
    }
    visitService.saveVisitDetail(new VisitInformationDetail(visitId,
        VisitInformationDetailType.FILL_QUESTIONNAIRE, questionnaireBackendId));
  }

  private void createCheckBox(String answer, String selectedAnswer) {
    buttonAnswerLayout.removeAllViews();

    String[] answerList = answer.split("[*]");

    List<String> selectedAnswers = null;
    if (Empty.isNotEmpty(selectedAnswer)) {
      selectedAnswers = Arrays.asList(selectedAnswer.split("[*]"));
    }
    for (int i = answerList.length - 1; i >= 0; i--) {
      CheckBox checkBox = new CheckBox(getActivity());
      checkBox.setLayoutParams(new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
          FlowLayout.LayoutParams.WRAP_CONTENT));
      checkBox.setId(i);
      checkBox.setPadding(5, 0, 5, 0);
      checkBox.setGravity(Gravity.RIGHT);
      checkBox.setText(answerList[i]);
      if (selectedAnswers != null && selectedAnswers.contains(answerList[i])) {
        checkBox.setChecked(true);
      }
      buttonAnswerLayout.addView(checkBox);
    }
  }

  private String getAnswer(QuestionDto questionDto) {
    String answer = "";
    switch (questionDto.getType()) {
      case SIMPLE:
        answer = answerEt.getText().toString();
        break;
      case SIMPLE_NUMERIC:
        answer = String.valueOf(amountValue);
        break;
      case CHOICE_SINGLE:
        answer = userChoice == null ? "" : userChoice.getText().toString();
        break;
      case CHOICE_MULTIPLE:
        for (int i = 0; i < buttonAnswerLayout.getChildCount(); i++) {
          CheckBox checkBox = (CheckBox) buttonAnswerLayout.getChildAt(i);
          if (checkBox.isChecked()) {
            answer = answer.concat(checkBox.getText().toString() + "*");
          }
        }
        if (answer.endsWith("*")) {
          answer = answer.substring(0, answer.length() - 1);
        }
        break;
      case DATE:
        answer = answerEt.getHint().toString();
    }
    return answer;
  }

  private void createRadioButtons(String answer, String selectedAnswer) {
    buttonAnswerLayout.removeAllViews();
    String[] answerList = answer.split("[*]");
    for (int i = answerList.length - 1; i >= 0; i--) {
      RadioButton rdbtn = new RadioButton(getActivity());
      rdbtn.setLayoutParams(new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
          FlowLayout.LayoutParams.WRAP_CONTENT));
      rdbtn.setId(i);
      rdbtn.setPadding(5, 0, 5, 0);
      rdbtn.setText(answerList[i]);
      rdbtn.setOnCheckedChangeListener(this);
      if (answerList[i].equals(selectedAnswer)) {
        rdbtn.setChecked(true);
      }
      buttonAnswerLayout.addView(rdbtn);
    }
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
      }
      if (Empty.isNotEmpty(goodsBackendId)) {
        qAnswer.setGoodsBackendId(goodsBackendId);
      }
      qAnswer.setVisitId(visitId);
      qAnswer.setQuestionBackendId(questionDto.getQuestionBackendId());
    } else {
      qAnswer.setAnswer(answer);
    }
    return questionnaireService.saveAnswer(qAnswer);
  }

  @Override
  protected String getClassTag() {
    return null;
  }

  @Override
  protected String getTitle() {
    return null;
  }

  @Override
  public int getFragmentId() {
    return MainActivity.QUESTIONNAIRE_DETAIL_FRAGMENT_ID;
  }

  /**
   * Called when the checked state of a compound button has changed.
   *
   * @param buttonView The compound button view whose state has changed.
   * @param isChecked The new checked state of buttonView.
   */
  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      if (userChoice != null) {
        userChoice.setChecked(false);
      }
      buttonView.setChecked(true);
      userChoice = buttonView;
    }
  }

  @Override
  public void onDateSet(int id, @Nullable Calendar calendar, int day, int month, int year) {
    int tempYear = year % 100;
    answerEt.setHint(tempYear + "/" + month + "/" + day);
  }
}
