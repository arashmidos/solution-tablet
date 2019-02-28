package com.parsroyal.storemanagement.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.PageStatus;
import com.parsroyal.storemanagement.data.entity.Customer;
import com.parsroyal.storemanagement.data.listmodel.QuestionListModel;
import com.parsroyal.storemanagement.data.listmodel.QuestionnaireListModel;
import com.parsroyal.storemanagement.data.model.QuestionDto;
import com.parsroyal.storemanagement.data.searchobject.QuestionSo;
import com.parsroyal.storemanagement.service.QuestionnaireService;
import com.parsroyal.storemanagement.service.impl.CustomerServiceImpl;
import com.parsroyal.storemanagement.service.impl.GoodsServiceImpl;
import com.parsroyal.storemanagement.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.storemanagement.service.impl.VisitServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.QuestionsAdapter;
import com.parsroyal.storemanagement.util.DialogUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.util.List;
import org.jetbrains.annotations.NotNull;


public class QuestionsListFragment extends BaseFragment {

  @BindView(R.id.title_tv)
  TextView titleTv;
  @BindView(R.id.questionnaire_size_tv)
  TextView questionnaireSizeTv;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  Unbinder unbinder;

  private MainActivity mainActivity;
  private QuestionnaireService questionnaireService;
  private CustomerServiceImpl customerService;
  private GoodsServiceImpl goodsService;
  private VisitServiceImpl visitService;
  private long questionnaireBackendId;
  private QuestionnaireListModel questionnaire;
  private long visitId;
  private long customerId;
  private long goodsGroupBackendId;
  private int parent;
  private long answersGroupNo;
  private Customer customer;
  private PageStatus pageStatus;

  public QuestionsListFragment() {
    // Required empty public constructor
  }


  public static QuestionsListFragment newInstance() {
    return new QuestionsListFragment();
  }

  @Override
  public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_questions_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    customerService = new CustomerServiceImpl(mainActivity);
    goodsService = new GoodsServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);
    mainActivity.changeTitle(getString(R.string.questionnaire_page));

    Bundle arguments = getArguments();
    questionnaireBackendId = arguments.getLong(Constants.QUESTIONNAIRE_BACKEND_ID);
    questionnaire = (QuestionnaireListModel) arguments.getSerializable(Constants.QUESTIONNAIRE_OBJ);
    visitId = arguments.getLong(Constants.VISIT_ID);
    customerId = arguments.getLong(Constants.CUSTOMER_ID);
    goodsGroupBackendId = arguments.getLong(Constants.GOODS_GROUP_BACKEND_ID);
    parent = arguments.getInt(Constants.PARENT, 0);
    answersGroupNo = arguments.getLong(Constants.ANSWERS_GROUP_NO, -1);
    pageStatus = (PageStatus) arguments.getSerializable(Constants.PAGE_STATUS);
    if (pageStatus != null && pageStatus == PageStatus.VIEW) {
      mainActivity.setToolbarIconVisibility(R.id.save_img, View.GONE);
    } else {
      mainActivity.setToolbarIconVisibility(R.id.save_img, View.VISIBLE);
    }
    customer = customerService.getCustomerById(customerId);

    setUpRecyclerView();

    questionnaireSizeTv
        .setText(NumberUtil.digitsToPersian(String.valueOf(questionnaire.getQuestionsCount())));
    titleTv.setText(questionnaire.getDescription());
    return view;
  }

  private void setUpRecyclerView() {
    QuestionsAdapter questionsAdapter = new QuestionsAdapter(this, mainActivity, getQuestions(),
        visitId,
        goodsGroupBackendId, answersGroupNo, customerId, questionnaireBackendId, pageStatus);
    LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(questionsAdapter);
  }

  public List<QuestionListModel> getQuestions() {
    QuestionSo questionSo = new QuestionSo();
    questionSo.setQuestionnaireBackendId(questionnaireBackendId);
    questionSo.setVisitId(visitId);
    questionSo.setGoodsBackendId(goodsGroupBackendId == -1 ? null : goodsGroupBackendId);
    questionSo.setAnswersGroupNo(answersGroupNo);
    List<QuestionListModel> questionListModels = questionnaireService
        .searchForQuestions(questionSo);
    questionnaire.setQuestionsCount(questionListModels.size());
    return questionListModels;
  }

  @Override
  public int getFragmentId() {
    return MainActivity.QUESTION_LIST_FRAGMENT_ID;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  public void exit() {
    if (pageStatus != null && pageStatus == PageStatus.VIEW) {
      mainActivity.onSaveImageClicked(false);
    } else {
      DialogUtil.showCustomDialog(mainActivity, getString(R.string.warning),
          getString(R.string.message_save_questionnaire), "", (dialog, which) ->
              mainActivity.onSaveImageClicked(true),
          "", (dialog, which) -> deleteAllQuestions(), Constants.ICON_MESSAGE);
    }
  }

  private void deleteAllQuestions() {
    questionnaireService.deleteAllAnswer(visitId, answersGroupNo);
    mainActivity.onSaveImageClicked(false);
  }

  @Override
  public void onResume() {
    super.onResume();

    if (getView() == null) {
      return;
    }

    getView().setFocusableInTouchMode(true);
    getView().requestFocus();
    getView().setOnKeyListener((v, keyCode, event) ->
    {
      if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
        exit();
        return true;
      }
      return false;
    });
  }

  public void closeVisit() {
    if (pageStatus == null || pageStatus == PageStatus.EDIT || Empty.isEmpty(customer)
        || parent == MainActivity.NEW_CUSTOMER_FRAGMENT_ID) {
      //It's anonymous questionaire or New customer
      //known bug, it he has not answered any quesiton, should remove the entire visit.
      visitService.finishVisiting(visitId);
    }
  }

  public boolean hasRequiredQuestionAnswer() {
    if (pageStatus != null && pageStatus == PageStatus.VIEW) {
      return true;
    }
    for (QuestionListModel question : getQuestions()) {
      QuestionDto questionDto = questionnaireService
          .getQuestionDto(question.getPrimaryKey(), visitId, goodsGroupBackendId,
              answersGroupNo);
      if (questionDto.isRequired() && TextUtils.isEmpty(questionDto.getAnswer())) {
        return false;
      }
    }
    return true;
  }
}
