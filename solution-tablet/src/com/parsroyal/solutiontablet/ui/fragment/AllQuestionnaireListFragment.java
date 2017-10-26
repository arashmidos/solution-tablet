package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.dao.QuestionnaireDao;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionnaireDaoImpl;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.ui.adapter.AllQuestionnaireAdapter;
import java.util.List;

public class AllQuestionnaireListFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.add_questionnaire_fab)
  FloatingActionButton addQuestionnaireFab;
  Unbinder unbinder;

  private MainActivity mainActivity;
  private VisitDetailFragment parent;

  public AllQuestionnaireListFragment() {
    // Required empty public constructor
  }

  public static AllQuestionnaireListFragment newInstance(
      Bundle arguments, VisitDetailFragment newVisitDetailFragment) {
    AllQuestionnaireListFragment fragment = new AllQuestionnaireListFragment();
    fragment.parent = newVisitDetailFragment;
    fragment.setArguments(arguments);
    return fragment;
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_all_questionnaire_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    setUpRecyclerView();
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  private void setUpRecyclerView() {
    Bundle bundle = getArguments();
    bundle.putInt(Constants.PARENT, MainActivity.CUSTOMER_INFO_FRAGMENT);
    AllQuestionnaireAdapter allQuestionnaireAdapter = new AllQuestionnaireAdapter(mainActivity,
        getQuestionnaireList(), bundle);
    LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(allQuestionnaireAdapter);
  }

  private List<QuestionnaireListModel> getQuestionnaireList() {
    QuestionnaireSo questionnaireSo = new QuestionnaireSo();
    questionnaireSo.setCustomerBackendId(parent.getCustomer().getBackendId());
    QuestionnaireDao questionnaireService = new QuestionnaireDaoImpl(mainActivity);
    return questionnaireService.searchForQuestionsList(questionnaireSo);
  }

  @OnClick(R.id.add_questionnaire_fab)
  public void onViewClicked() {
    QuestionnaireServiceImpl questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    Bundle bundle = getArguments();
    bundle.putInt(Constants.PARENT, MainActivity.CUSTOMER_INFO_FRAGMENT);
    bundle.putLong(Constants.ANSWERS_GROUP_NO, questionnaireService.getNextAnswerGroupNo());
    mainActivity
        .changeFragment(MainActivity.QUESTIONNAIRE_CATEGORY_FRAGMENT_ID, bundle, true);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.ALL_QUESTIONNAIRE_FRAGMENT_ID;
  }
}
