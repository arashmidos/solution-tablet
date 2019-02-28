package com.parsroyal.storemanagement.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.data.listmodel.QuestionnaireListModel;
import com.parsroyal.storemanagement.data.searchobject.QuestionnaireSo;
import com.parsroyal.storemanagement.service.QuestionnaireService;
import com.parsroyal.storemanagement.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.NewQuestionnaireListAdapter;
import java.util.List;

public class QuestionnaireListFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  private Unbinder unbinder;
  private MainActivity mainActivity;
  protected QuestionnaireService questionnaireService;
  protected String questionnaireCategory;


  public QuestionnaireListFragment() {
    // Required empty public constructor
  }

  public static QuestionnaireListFragment newInstance() {
    return new QuestionnaireListFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_questionnaire_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    if (getArguments() != null) {
      questionnaireCategory = getArguments().getString(Constants.QUESTIONNAIRE_CATEGORY);
      setTitle();
    }
    questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    setUpRecyclerView();
    return view;
  }

  private void setTitle() {
    if (questionnaireCategory.equals(Constants.GOOD_QUESTIONNAIRE)) {
      mainActivity.changeTitle(getString(R.string.good_questionnaire));
    } else {
      mainActivity.changeTitle(getString(R.string.general_questionnaire));
    }

  }

  //set up recycler view
  private void setUpRecyclerView() {
    NewQuestionnaireListAdapter adapter = new NewQuestionnaireListAdapter(mainActivity,
        getQuestionnaireList(),getArguments());
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<QuestionnaireListModel> getQuestionnaireList() {
    QuestionnaireSo questionnaireSo = getSearchObject();
    return questionnaireService.searchForQuestionnaires(questionnaireSo);
  }

  protected QuestionnaireSo getSearchObject() {
    QuestionnaireSo questionnaireSo = new QuestionnaireSo();
    if (questionnaireCategory.equals(Constants.GOOD_QUESTIONNAIRE)) {
      questionnaireSo.setGeneral(false);
    } else {
      questionnaireSo.setGeneral(true);
    }
    return questionnaireSo;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public int getFragmentId() {
    return MainActivity.QUESTIONNAIRE_LIST_FRAGMENT_ID;
  }
}
