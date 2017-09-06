package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.ui.adapter.NQuestionnaireListAdapter;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;

/**
 * Created by Arash on 2017-05-08
 */
public class QuestionnairesListFragment extends
    BaseListFragment<QuestionnaireListModel, NQuestionnaireListAdapter> {

  public static final String TAG = QuestionnairesListFragment.class.getSimpleName();

  protected OldMainActivity mainActivity;
  protected QuestionnaireService questionnaireService;
  protected int parent;
  private VisitService visitService;
  private FloatingActionButton fab;
  private long visitId;
  private long customerId;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mainActivity = (OldMainActivity) getActivity();
    questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);

    Bundle arguments = getArguments();

    visitId = arguments.getLong(Constants.VISIT_ID, -1);
    customerId = arguments.getLong(Constants.CUSTOMER_ID, -1);
    parent = arguments.getInt(Constants.PARENT, 0);

    try {
      dataModel = getDataModel();

      View view = super.onCreateView(inflater, container, savedInstanceState);
      fab = (FloatingActionButton) view.findViewById(R.id.fab);
      initFab();
      if (parent != 0) {
        buttonPanel.setVisibility(View.VISIBLE);
        Button canclButton = (Button) buttonPanel.findViewById(R.id.cancelBtn);
        canclButton.setOnClickListener(v ->
        {
          mainActivity.removeFragment(QuestionnairesListFragment.this);
          mainActivity.changeSidebarItem(parent);
        });
      }

      return view;

    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "UI Exception",
              "Error in creating NquestionaireFragment " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
      return inflater.inflate(R.layout.view_error_page, null);
    }
  }

  private void initFab() {
    fab.setVisibility(View.VISIBLE);

    fab.setOnClickListener(v ->
    {
      MainActivity mainActivity = (MainActivity) getActivity();
      Bundle args = new Bundle();
      args.putLong(Constants.VISIT_ID,
          visitId == -1 ? visitService.startAnonymousVisit() : visitId);
      args.putLong(Constants.CUSTOMER_ID, customerId);
      args.putInt(Constants.PARENT, OldMainActivity.QUESTIONAIRE_LIST_FRAGMENT_ID);
      if (visitId == -1 || parent == OldMainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID) {
        mainActivity.changeFragment(OldMainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID, args, true);
      } else {
        mainActivity.changeFragment(OldMainActivity.GOODS_QUESTIONNAIRES_FRAGMENT_ID, args, true);
      }
    });
  }

  protected QuestionnaireSo getSearchObject() {
    QuestionnaireSo questionnaireSo = new QuestionnaireSo();
    if (visitId != -1) {
      //Its from a customer's visit
      questionnaireSo.setGeneral(parent == OldMainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID);
      questionnaireSo.setVisitId(visitId);
      questionnaireSo.setAnonymous(false);
    } else {
      questionnaireSo.setAnonymous(true);
    }
    return questionnaireSo;
  }

  @Override
  protected List<QuestionnaireListModel> getDataModel() {
    QuestionnaireSo questionnaireSo = getSearchObject();
    return questionnaireService.searchForQuestionsList(questionnaireSo);
  }

  @Override
  protected View getHeaderView() {
    return null;
  }

  @Override
  protected NQuestionnaireListAdapter getAdapter() {
    return new NQuestionnaireListAdapter(mainActivity, getDataModel());
  }

  @Override
  protected AdapterView.OnItemClickListener getOnItemClickListener() {
    return (parent1, view, position, id) ->
    {
      QuestionnaireListModel questionnaireListModel = dataModel.get(position);
      Bundle args = new Bundle();
      args.putLong(Constants.QUESTIONAIRE_ID, questionnaireListModel.getPrimaryKey());
      args.putLong(Constants.VISIT_ID, questionnaireListModel.getVisitId());
      args.putInt(Constants.PARENT,
          parent == 0 ? OldMainActivity.QUESTIONAIRE_LIST_FRAGMENT_ID : parent);
      args.putLong(Constants.ANSWERS_GROUP_NO,questionnaireListModel.getAnswersGroupNo());
      mainActivity.changeFragment(OldMainActivity.QUESTIONNAIRE_DETAIL_FRAGMENT_ID, args, false);
    };
  }

  @Override
  protected String getClassTag() {
    return TAG;
  }

  @Override
  protected String getTitle() {
    return "";
  }

  @Override
  public int getFragmentId() {
    return OldMainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID;
  }

  @Override
  public void onResume() {
    super.onResume();
    updateList();
  }
}
