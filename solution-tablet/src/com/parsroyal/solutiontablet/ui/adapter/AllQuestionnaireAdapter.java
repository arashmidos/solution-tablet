package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.PageStatus;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.dao.QuestionnaireDao;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionnaireDaoImpl;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.AllQuestionnaireAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.Date;
import java.util.List;

/**
 * Created by shkbhbb on 10/23/17.
 */

public class AllQuestionnaireAdapter extends Adapter<ViewHolder> {

  private Context context;
  private List<QuestionnaireListModel> questionnaires;
  private LayoutInflater inflater;
  private QuestionnaireDao questionnaireService;
  private Bundle args;
  private MainActivity mainActivity;

  public AllQuestionnaireAdapter(Context context, List<QuestionnaireListModel> questionnaires,
      Bundle args) {
    this.context = context;
    this.questionnaires = questionnaires;
    this.mainActivity = (MainActivity) context;
    this.args = args;
    this.questionnaireService = new QuestionnaireDaoImpl(context);
    this.inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_all_questionnaire, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setData(position);
  }

  @Override
  public int getItemCount() {
    return questionnaires.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.questionnaire_type_tv)
    TextView questionnaireTypeTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.customer_name_tv)
    TextView customerNameTv;
    @Nullable
    @BindView(R.id.main_lay_rel)
    RelativeLayout mainLayRel;
    @Nullable
    @BindView(R.id.main_lay_lin)
    LinearLayout mainLayLin;
    @BindView(R.id.questionnaire_status_tv)
    TextView questionnaireStatusTv;
    @BindView(R.id.edit_img_layout)
    LinearLayout editImageLayout;
    @BindView(R.id.delete_img_layout)
    LinearLayout deleteImageLayout;

    private QuestionnaireListModel questionnaire;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @Optional
    @OnClick({R.id.main_lay_lin, R.id.main_lay_rel, R.id.delete_img, R.id.delete_img_layout,
        R.id.edit_img, R.id.edit_img_layout})
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.edit_img:
        case R.id.edit_img_layout:
        case R.id.main_lay_lin:
        case R.id.main_lay_rel:
          args.putLong(Constants.QUESTIONNAIRE_BACKEND_ID, questionnaire.getPrimaryKey());
          args.putSerializable(Constants.QUESTIONNAIRE_OBJ, questionnaire);
          args.putLong(Constants.VISIT_ID, questionnaire.getVisitId());
          args.putLong(Constants.GOODS_GROUP_BACKEND_ID,
              questionnaire.getGoodsGroupBackendId() == null ? -1
                  : questionnaire.getGoodsGroupBackendId());
          args.putLong(Constants.ANSWERS_GROUP_NO, questionnaire.getAnswersGroupNo());
          args.putLong(Constants.VISIT_ID, questionnaire.getVisitId());
          if (questionnaire.getStatus().equals(SendStatus.SENT.getId())) {
            args.putSerializable(Constants.PAGE_STATUS, PageStatus.VIEW);
          }
          mainActivity.changeFragment(MainActivity.QUESTION_LIST_FRAGMENT_ID, args, false);
          break;
        case R.id.delete_img:
        case R.id.delete_img_layout:
          Toast.makeText(mainActivity, "DELETE ACTION", Toast.LENGTH_SHORT).show();
          break;
      }
    }

    public void setData(int position) {
      questionnaire = questionnaires.get(position);
      titleTv.setText(questionnaire.getDescription());

      changeVisibility();

      questionnaireTypeTv.setText(
          Empty.isEmpty(questionnaire.getGoodsGroupBackendId()) ? mainActivity
              .getString(R.string.general_questionnaire) :
              mainActivity.getString(R.string.good_questionnaire));

      Date createdDate = DateUtil
          .convertStringToDate(questionnaire.getDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);
      dateTv.setText(NumberUtil.digitsToPersian(dateString));

    }

    private void changeVisibility() {
      if (args.getInt(Constants.PARENT) == MainActivity.REPORT_FRAGMENT) {
        customerNameTv.setVisibility(View.VISIBLE);
        customerNameTv.setText(questionnaire.getCustomerFullName());
      } else {
        customerNameTv.setVisibility(View.INVISIBLE);
      }

      if (questionnaire.getStatus().equals(SendStatus.SENT.getId())) {
        deleteImageLayout.setVisibility(View.GONE);
        editImageLayout.setVisibility(View.GONE);
        questionnaireStatusTv.setVisibility(View.VISIBLE);
        questionnaireStatusTv.setText(mainActivity.getString(R.string.has_sent));
      }
    }
  }
}
