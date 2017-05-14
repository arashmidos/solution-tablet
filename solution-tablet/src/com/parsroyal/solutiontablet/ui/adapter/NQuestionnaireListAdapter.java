package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.Empty;

import java.util.List;

/**
 * Created by Arash on 2017-05-08
 */
public class NQuestionnaireListAdapter extends BaseListAdapter<QuestionnaireListModel>
{

    private Context context;
    private QuestionnaireSo questionnaireSo;
    private QuestionnaireService questionnaireService;

    public NQuestionnaireListAdapter(MainActivity context, List<QuestionnaireListModel> dataModel)
    {
        super(context, dataModel);
        this.questionnaireService = new QuestionnaireServiceImpl(context);
        this.context = context;
    }

    @Override
    protected List<QuestionnaireListModel> getFilteredData(CharSequence constraint)
    {
        questionnaireSo = new QuestionnaireSo();
        questionnaireSo.setAnonymous(true);
        if (constraint.length() != 0 && Empty.isNotEmpty(constraint.toString()))
        {
            questionnaireSo.setConstraint(constraint.toString());
        }
        return questionnaireService.searchForAnonymousQuestionaire(questionnaireSo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try
        {
            QuestionnaireViewHolder holder;

            if (convertView == null)
            {
                convertView = mLayoutInflater.inflate(R.layout.row_layout_nquestionnaire, null);
                holder = new QuestionnaireViewHolder();
                holder.rowLayout = (RelativeLayout) convertView.findViewById(R.id.rowLayout);
                holder.descriptionTv = (TextView) convertView.findViewById(R.id.descriptionTv);
                holder.dateTv = (TextView) convertView.findViewById(R.id.dateTv);
                convertView.setTag(holder);
            } else
            {
                holder = (QuestionnaireViewHolder) convertView.getTag();
            }

            final QuestionnaireListModel model = dataModel.get(position);

            holder.descriptionTv.setText(model.getDescription());
            holder.dateTv.setText(String.valueOf(model.getDate()));

            return convertView;
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private class QuestionnaireViewHolder
    {

        private RelativeLayout rowLayout;
        private TextView dateTv;
        private TextView descriptionTv;
    }
}
