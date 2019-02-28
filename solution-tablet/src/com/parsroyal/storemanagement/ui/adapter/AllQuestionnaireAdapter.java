package com.parsroyal.storemanagement.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.PageStatus;
import com.parsroyal.storemanagement.constants.SendStatus;
import com.parsroyal.storemanagement.data.listmodel.QuestionnaireListModel;
import com.parsroyal.storemanagement.service.QuestionnaireService;
import com.parsroyal.storemanagement.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.AllQuestionnaireAdapter.ViewHolder;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.DialogUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.util.Date;
import java.util.List;

/**
 * Created by shkbhbb on 10/23/17.
 */

public class AllQuestionnaireAdapter extends Adapter<ViewHolder> {

  private Context context;
  private List<QuestionnaireListModel> questionnaires;
  private LayoutInflater inflater;
  private QuestionnaireService questionnaireService;
  private Bundle args;
  private MainActivity mainActivity;

  public AllQuestionnaireAdapter(Context context, List<QuestionnaireListModel> questionnaires,
      Bundle args) {
    this.context = context;
    this.questionnaires = questionnaires;
    this.mainActivity = (MainActivity) context;
    this.args = args;
    this.questionnaireService = new QuestionnaireServiceImpl(context);
    this.inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_all_questionnaire, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
    @BindView(R.id.customer_code_tv)
    TextView customerCodeTv;
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
    @Nullable
    @BindView(R.id.customer_layout)
    LinearLayout customerLayout;

    private QuestionnaireListModel questionnaire;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @Optional
    @OnClick({R.id.main_lay_lin, R.id.main_lay_rel, R.id.delete_img_layout, R.id.edit_img_layout})
    public void onClick(View v) {
      switch (v.getId()) {
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
          } else {
            args.putSerializable(Constants.PAGE_STATUS, PageStatus.EDIT);
          }
          mainActivity.changeFragment(MainActivity.QUESTION_LIST_FRAGMENT_ID, args, false);
          break;
        case R.id.delete_img_layout:
          deleteQuestionnaire();
          break;
      }
    }

    private void deleteQuestionnaire() {
      DialogUtil.showCustomDialog(mainActivity, mainActivity.getString(R.string.warning),
          mainActivity.getString(R.string.message_questionnaire_delete_confirm), "",
          (dialog, which) -> {
            questionnaireService
                .deleteAllAnswer(questionnaire.getVisitId(), questionnaire.getAnswersGroupNo());
            questionnaires.remove(position);
            notifyDataSetChanged();
          }, "", (dialog, which) -> dialog.dismiss(), Constants.ICON_MESSAGE);
    }

    public void setData(int position) {
      this.position = position;
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
        customerCodeTv.setVisibility(View.VISIBLE);
        customerCodeTv.setText(
            NumberUtil.digitsToPersian(String.format("(%s)", questionnaire.getCustomerCode())));
        if (customerLayout != null) {
          customerLayout.setVisibility(View.VISIBLE);
        }
      } else {
        customerNameTv.setVisibility(View.INVISIBLE);
        customerCodeTv.setVisibility(View.INVISIBLE);
        if (customerLayout != null) {
          customerLayout.setVisibility(View.INVISIBLE);
        }
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
