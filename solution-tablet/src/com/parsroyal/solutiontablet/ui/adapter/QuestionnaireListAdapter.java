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
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionnaireListAdapter extends BaseListAdapter<QuestionnaireListModel> {

  private Context context;
  private QuestionnaireSo questionnaireSo;
  private QuestionnaireService questionnaireService;
  private boolean isGeneral;

  public QuestionnaireListAdapter(OldMainActivity context, List<QuestionnaireListModel> dataModel,
      boolean isGeneral) {
    super(context, dataModel);
    this.questionnaireService = new QuestionnaireServiceImpl(context);
    this.context = context;
    this.isGeneral = isGeneral;
  }

  @Override
  protected List<QuestionnaireListModel> getFilteredData(CharSequence constraint) {
    questionnaireSo = new QuestionnaireSo();
    questionnaireSo.setGeneral(isGeneral);
    if (constraint.length() != 0 && Empty.isNotEmpty(constraint.toString())) {
      questionnaireSo.setConstraint(constraint.toString());
    }
    return questionnaireService.searchForQuestionnaires(questionnaireSo);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    try {
      QuestionnaireViewHolder holder;

      if (convertView == null) {
        convertView = mLayoutInflater.inflate(R.layout.row_layout_questionnaire, null);
        holder = new QuestionnaireViewHolder();
        holder.rowLayout = (RelativeLayout) convertView.findViewById(R.id.rowLayout);
        holder.descriptionTv = (TextView) convertView.findViewById(R.id.descriptionTv);
        holder.qCountTv = (TextView) convertView.findViewById(R.id.qCountTv);
        convertView.setTag(holder);
      } else {
        holder = (QuestionnaireViewHolder) convertView.getTag();
      }

      final QuestionnaireListModel model = dataModel.get(position);

      {
        holder.descriptionTv.setText(model.getDescription());
        holder.qCountTv.setText(String.valueOf(model.getQuestionsCount()));
      }

      return convertView;
    } catch (Exception e) {
      Logger
          .sendError("UI Exception", "Error in QuestionaireListAdapter.getView " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      return null;
    }
  }

  private class QuestionnaireViewHolder {

    private RelativeLayout rowLayout;
    private TextView descriptionTv;
    private TextView qCountTv;
  }
}
