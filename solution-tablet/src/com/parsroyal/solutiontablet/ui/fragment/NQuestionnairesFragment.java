package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
import com.parsroyal.solutiontablet.ui.adapter.NQuestionnaireListAdapter;
import com.parsroyal.solutiontablet.util.ToastUtil;

import java.util.List;

/**
 * Created by Arash on 2017-05-08
 */
public class NQuestionnairesFragment extends BaseListFragment<QuestionnaireListModel, NQuestionnaireListAdapter>
{
    public static final String TAG = NQuestionnairesFragment.class.getSimpleName();

    protected MainActivity mainActivity;
    protected QuestionnaireService questionnaireService;
    protected int parent;
    private VisitService visitService;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mainActivity = (MainActivity) getActivity();
        questionnaireService = new QuestionnaireServiceImpl(mainActivity);
        visitService = new VisitServiceImpl(mainActivity);

        try
        {
            QuestionnaireSo questionnaireSo = getSearchObject();
            questionnaireSo.setAnonymous(true);
            dataModel = questionnaireService.searchForAnonymousQuestionaire(questionnaireSo);

            View view = super.onCreateView(inflater, container, savedInstanceState);
            fab = (FloatingActionButton) view.findViewById(R.id.fab);
            initFab();
            return view;

        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
            return inflater.inflate(R.layout.view_error_page, null);
        }
    }

    private void initFab()
    {
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(v ->
        {
            Long visitId = visitService.startAnonymousVisit();
            MainActivity mainActivity = (MainActivity) getActivity();
            Bundle args = new Bundle();
            args.putLong(Constants.VISIT_ID, visitId);
            args.putInt(Constants.PARENT, MainActivity.NQUESTIONAIRE_FRAGMENT_ID);
            mainActivity.changeFragment(MainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID, args, false);
        });
    }

    protected QuestionnaireSo getSearchObject()
    {
        QuestionnaireSo questionnaireSo = new QuestionnaireSo();
        questionnaireSo.setAnonymous(true);
        return questionnaireSo;
    }

    @Override
    protected List<QuestionnaireListModel> getDataModel()
    {
        QuestionnaireSo questionnaireSo = getSearchObject();
        return questionnaireService.searchForAnonymousQuestionaire(questionnaireSo);
    }

    @Override
    protected View getHeaderView()
    {
        return null;
    }

    @Override
    protected NQuestionnaireListAdapter getAdapter()
    {
        return new NQuestionnaireListAdapter(mainActivity, getDataModel());
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        return (parent1, view, position, id) ->
        {
            QuestionnaireListModel questionnaireListModel = dataModel.get(position);
            Bundle args = new Bundle();
            args.putLong(Constants.QUESTIONAIRE_ID, questionnaireListModel.getPrimaryKey());
            args.putLong(Constants.VISIT_ID, questionnaireListModel.getVisitId());
            args.putInt(Constants.PARENT, MainActivity.NQUESTIONAIRE_FRAGMENT_ID);
            mainActivity.changeFragment(MainActivity.QUESTIONNAIRE_DETAIL_FRAGMENT_ID, args, false);
        };
    }

    @Override
    protected String getClassTag()
    {
        return TAG;
    }

    @Override
    protected String getTitle()
    {
        return "";
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "On resume");
    }
}
