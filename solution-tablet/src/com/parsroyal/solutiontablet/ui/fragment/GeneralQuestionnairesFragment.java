package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.model.QuestionnaireListModel;
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
public class GeneralQuestionnairesFragment extends BaseListFragment<QuestionnaireListModel, QuestionnaireListAdapter>
{

    public static final String TAG = GeneralQuestionnairesFragment.class.getSimpleName();

    protected MainActivity mainActivity;
    protected QuestionnaireService questionnaireService;

    protected List<QuestionnaireListModel> dataModel;
    protected Long visitId;
    protected Long customerId;
    protected int parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        mainActivity = (MainActivity) getActivity();
        questionnaireService = new QuestionnaireServiceImpl(mainActivity);

        try
        {
            visitId = getArguments().getLong(Constants.VISIT_ID);
            customerId = getArguments().getLong(Constants.CUSTOMER_ID);
            parent = getArguments().getInt(Constants.PARENT);
            QuestionnaireSo questionnaireSo = getSearchObject();
            dataModel = questionnaireService.searchForQuestionnaires(questionnaireSo);

            View view = super.onCreateView(inflater, container, savedInstanceState);
            buttonPanel.setVisibility(View.VISIBLE);
            Button canclButton = (Button) buttonPanel.findViewById(R.id.cancelBtn);
            canclButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mainActivity.removeFragment(GeneralQuestionnairesFragment.this);
                }
            });
            return view;

        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
            return inflater.inflate(R.layout.view_error_page, null);
        }
    }

    protected QuestionnaireSo getSearchObject()
    {
        QuestionnaireSo questionnaireSo = new QuestionnaireSo();
        questionnaireSo.setGeneral(true);
        return questionnaireSo;
    }

    @Override
    protected List<QuestionnaireListModel> getDataModel()
    {
        return dataModel;
    }

    @Override
    protected View getHeaderView()
    {
        return null;
    }

    @Override
    protected QuestionnaireListAdapter getAdapter()
    {
        return new QuestionnaireListAdapter(mainActivity, dataModel, true);
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                QuestionnaireListModel questionnaireListModel = dataModel.get(position);
                Bundle args = new Bundle();
                args.putLong("qnId", questionnaireListModel.getBackendId());
                args.putLong(Constants.VISIT_ID, visitId);
                args.putLong(Constants.CUSTOMER_ID, customerId);
                mainActivity.changeFragment(MainActivity.QUESTIONNAIRE_DETAIL_FRAGMENT_ID, args, false);
            }
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
}
