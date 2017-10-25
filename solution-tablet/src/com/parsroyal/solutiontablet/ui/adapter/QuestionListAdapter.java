package com.parsroyal.solutiontablet.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.QuestionType;
import com.parsroyal.solutiontablet.data.listmodel.QuestionListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionSo;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mahyar on 7/25/2015.
 */
public class QuestionListAdapter extends BaseListAdapter<QuestionListModel> {

  private QuestionnaireService questionnaireService;
  private QuestionSo questionSo;
  private Long questionnaireBackendId;
  private Long visitId;

  public QuestionListAdapter(OldMainActivity context, List<QuestionListModel> dataModel,
      Long questionnaireBackendId, Long visitId) {
    super(context, dataModel);
    this.context = context;
    this.dataModel = dataModel;
    this.questionnaireService = new QuestionnaireServiceImpl(context);
    this.questionnaireBackendId = questionnaireBackendId;
    this.visitId = visitId;
  }

  @Override
  protected List<QuestionListModel> getFilteredData(CharSequence constraint) {
    questionSo = new QuestionSo();
    questionSo.setQuestionnaireBackendId(questionnaireBackendId);
    if (Empty.isNotEmpty(constraint) && Empty.isNotEmpty(constraint.toString())) {
      questionSo.setConstraint(constraint.toString());
    }
    questionSo.setVisitId(visitId);
    return questionnaireService.searchForQuestions(questionSo);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    try {

      QuestionViewHolder holder;

      if (Empty.isNotEmpty(convertView)) {
        holder = (QuestionViewHolder) convertView.getTag();
      } else {
        convertView = mLayoutInflater.inflate(R.layout.row_layout_question, null);
        holder = new QuestionViewHolder();
        holder.rowLayout = (RelativeLayout) convertView.findViewById(R.id.rowLayout);
        holder.questionTv = (TextView) convertView.findViewById(R.id.questionTv);
        holder.answerTv = (TextView) convertView.findViewById(R.id.answerTv);
        convertView.setTag(holder);
      }

      QuestionListModel model = dataModel.get(position);
      holder.questionTv.setText(model.getQuestion());
      if (model.getType().equals(QuestionType.SIMPLE_NUMERIC)) {
        holder.answerTv
            .setText(Empty.isEmpty(model.getAnswer()) ? "--" : String.format(Locale.US, "%,d",
                Long.parseLong(NumberUtil.digitsToEnglish(model.getAnswer().replaceAll(",", "")))));
      } else {
        holder.answerTv.setText(
            Empty.isEmpty(model.getAnswer()) ? "--" : model.getAnswer().replaceAll("[*]", " - "));
      }

      return convertView;

    } catch (Exception ex) {
      Logger.sendError("UI Exception", "Error in BqUESTIONListAdapter.getView " + ex.getMessage());
      ToastUtil.toastError(context, new UnknownSystemException(ex));
      return null;
    }
  }

  private class QuestionViewHolder {

    public RelativeLayout rowLayout;
    public TextView questionTv;
    public TextView answerTv;
  }
}
