package com.conta.comer.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conta.comer.R;
import com.conta.comer.data.listmodel.QuestionListModel;
import com.conta.comer.data.searchobject.QuestionSo;
import com.conta.comer.exception.UnknownSystemException;
import com.conta.comer.service.QuestionnaireService;
import com.conta.comer.service.impl.QuestionnaireServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.util.Empty;

import java.util.List;

/**
 * Created by Mahyar on 7/25/2015.
 */
public class QuestionListAdapter extends BaseListAdapter<QuestionListModel>
{

    private QuestionnaireService questionnaireService;
    private QuestionSo questionSo;
    private Long questionnaireBackendId;
    private Long visitId;

    public QuestionListAdapter(MainActivity context, List<QuestionListModel> dataModel, Long questionnaireBackendId, Long visitId)
    {
        super(context, dataModel);
        this.context = context;
        this.dataModel = dataModel;
        this.questionnaireService = new QuestionnaireServiceImpl(context);
        this.questionnaireBackendId = questionnaireBackendId;
        this.visitId = visitId;
    }

    @Override
    protected List<QuestionListModel> getFilteredData(CharSequence constraint)
    {
        questionSo = new QuestionSo();
        questionSo.setQuestionnaireBackendId(questionnaireBackendId);
        if (Empty.isNotEmpty(constraint) && Empty.isNotEmpty(constraint.toString()))
        {
            questionSo.setConstraint(constraint.toString());
        }
        questionSo.setVisitId(visitId);
        return questionnaireService.searchForQuestions(questionSo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try
        {

            QuestionViewHolder holder;

            if (Empty.isNotEmpty(convertView))
            {
                holder = (QuestionViewHolder) convertView.getTag();
            } else
            {
                convertView = mLayoutInflater.inflate(R.layout.row_layout_question, null);
                holder = new QuestionViewHolder();
                holder.rowLayout = (RelativeLayout) convertView.findViewById(R.id.rowLayout);
                holder.questionTv = (TextView) convertView.findViewById(R.id.questionTv);
                holder.answerTv = (TextView) convertView.findViewById(R.id.answerTv);
                convertView.setTag(holder);
            }

            QuestionListModel model = dataModel.get(position);
            holder.questionTv.setText(model.getQuestion());
            holder.answerTv.setText(Empty.isEmpty(model.getAnswer()) ? "--" : model.getAnswer());

            return convertView;

        } catch (Exception ex)
        {
            context.toastError(new UnknownSystemException(ex));
            return null;
        }
    }

    private class QuestionViewHolder
    {
        public RelativeLayout rowLayout;
        public TextView questionTv;
        public TextView answerTv;
    }

}
