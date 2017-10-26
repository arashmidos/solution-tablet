package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.fasterxml.jackson.databind.deser.Deserializers.Base;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.ui.adapter.AnonymousQuestionAdapter;
import java.util.List;

public class AnonymousQuestionnaireFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  Unbinder unbinder;
  private MainActivity mainActivity;
  private QuestionnaireServiceImpl questionnaireService;
  private VisitServiceImpl visitService;
  private long visitId;
  private long customerId;
  private int parent;
  private List<QuestionnaireListModel> dataModel;

  public AnonymousQuestionnaireFragment() {
    // Required empty public constructor
  }

  public static AnonymousQuestionnaireFragment newInstance() {
    return new AnonymousQuestionnaireFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_anonymous_questionnaire, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);
    dataModel = getDataModel();
    setUpRecyclerView();
    return view;
  }

  private void setUpRecyclerView() {
    Bundle arguments = new Bundle();
    arguments.putLong(Constants.CUSTOMER_ID, -1);
    AnonymousQuestionAdapter anonymousQuestionAdapter = new AnonymousQuestionAdapter(mainActivity,
        dataModel, arguments);
    LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(anonymousQuestionAdapter);
  }

  protected QuestionnaireSo getSearchObject() {
    QuestionnaireSo questionnaireSo = new QuestionnaireSo();
    questionnaireSo.setAnonymous(true);
    return questionnaireSo;
  }

  protected List<QuestionnaireListModel> getDataModel() {
    QuestionnaireSo questionnaireSo = getSearchObject();
    return questionnaireService.searchForQuestionsList(questionnaireSo);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick(R.id.add_questionnaire_fab)
  public void onViewClicked() {
    Bundle arguments = new Bundle();
    arguments.putLong(Constants.CUSTOMER_ID, -1);
    arguments.putLong(Constants.VISIT_ID, visitService.startAnonymousVisit());
    arguments.putLong(Constants.PARENT, MainActivity.ANONYMOUS_QUESTIONNAIRE_FRAGMENT_ID);
    arguments.putString(Constants.QUESTIONNAIRE_CATEGORY, Constants.GENERAL_QUESTIONNAIRE);
    mainActivity.changeFragment(MainActivity.QUESTIONNAIRE_LIST_FRAGMENT_ID, arguments, true);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.ANONYMOUS_QUESTIONNAIRE_FRAGMENT_ID;
  }
}
