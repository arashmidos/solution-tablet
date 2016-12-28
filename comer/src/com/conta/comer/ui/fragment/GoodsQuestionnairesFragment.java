package com.conta.comer.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.conta.comer.constants.Constants;
import com.conta.comer.data.model.QuestionnaireListModel;
import com.conta.comer.data.searchobject.QuestionnaireSo;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.adapter.QuestionnaireListAdapter;

/**
 * Created by Mahyar on 7/28/2015.
 */
public class GoodsQuestionnairesFragment extends GeneralQuestionnairesFragment
{

    @Override
    protected QuestionnaireSo getSearchObject()
    {
        QuestionnaireSo questionnaireSo = new QuestionnaireSo();
        questionnaireSo.setGeneral(false);
        return questionnaireSo;
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
                args.putLong("ggBi", questionnaireListModel.getGoodsGroupBackendId());
                mainActivity.changeFragment(  MainActivity.GOODS_LIST_FOR_QUESTIONNAIRES_FRAGMENT_ID, args, false);
            }
        };
    }

    @Override
    protected QuestionnaireListAdapter getAdapter()
    {
        return new QuestionnaireListAdapter(mainActivity, dataModel, false);
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.GOODS_QUESTIONNAIRES_FRAGMENT_ID;
    }
}
