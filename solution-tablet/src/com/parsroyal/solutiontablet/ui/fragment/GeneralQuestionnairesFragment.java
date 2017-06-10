package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
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
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.QuestionnaireListAdapter;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class GeneralQuestionnairesFragment extends
    BaseListFragment<QuestionnaireListModel, QuestionnaireListAdapter> {

  public static final String TAG = GeneralQuestionnairesFragment.class.getSimpleName();

  protected MainActivity mainActivity;
  protected QuestionnaireService questionnaireService;

  protected List<QuestionnaireListModel> dataModel;
  protected Long visitId;
  protected Long customerId;
  protected int parent;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mainActivity = (MainActivity) getActivity();
    questionnaireService = new QuestionnaireServiceImpl(mainActivity);

    try {
      Bundle arguments = getArguments();

      visitId = arguments.getLong(Constants.VISIT_ID);
      customerId = arguments.getLong(Constants.CUSTOMER_ID, -1);
      parent = arguments.getInt(Constants.PARENT, 0);

      QuestionnaireSo questionnaireSo = getSearchObject();
      dataModel = questionnaireService.searchForQuestionnaires(questionnaireSo);

      View view = super.onCreateView(inflater, container, savedInstanceState);
      buttonPanel.setVisibility(View.VISIBLE);
      Button canclButton = (Button) buttonPanel.findViewById(R.id.cancelBtn);
      canclButton.setOnClickListener(v ->
      {
        mainActivity.removeFragment(GeneralQuestionnairesFragment.this);
        mainActivity.changeSidebarItem(parent);
      });
      return view;

    } catch (Exception ex) {
      Crashlytics.log(Log.ERROR, "UI Exception", "Error in creating GeneralQuestionaireFragment " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
      return inflater.inflate(R.layout.view_error_page, null);
    }
  }

  protected QuestionnaireSo getSearchObject() {
    QuestionnaireSo questionnaireSo = new QuestionnaireSo();
    questionnaireSo.setGeneral(true);
    return questionnaireSo;
  }

  @Override
  protected List<QuestionnaireListModel> getDataModel() {
    return dataModel;
  }

  @Override
  protected View getHeaderView() {
    return null;
  }

  @Override
  protected QuestionnaireListAdapter getAdapter() {
    return new QuestionnaireListAdapter(mainActivity, dataModel, true);
  }

  @Override
  protected AdapterView.OnItemClickListener getOnItemClickListener() {
    return (parent1, view, position, id) ->
    {
      QuestionnaireListModel questionnaireListModel = dataModel.get(position);
      Bundle args = new Bundle();
      args.putLong(Constants.QUESTIONAIRE_ID, questionnaireListModel.getBackendId());
      args.putLong(Constants.VISIT_ID, visitId);
      args.putLong(Constants.CUSTOMER_ID, customerId);
      args.putInt(Constants.PARENT, parent);
      mainActivity.changeFragment(MainActivity.QUESTIONNAIRE_DETAIL_FRAGMENT_ID, args, false);
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
    return MainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID;
  }
}
