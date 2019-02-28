package com.parsroyal.storemanagement.ui.adapter;

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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.PageStatus;
import com.parsroyal.storemanagement.constants.SendStatus;
import com.parsroyal.storemanagement.data.listmodel.QuestionnaireListModel;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.AnonymousQuestionAdapter.ViewHolder;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.util.Date;
import java.util.List;

/**
 * Created by shkbhbb on 10/25/17.
 */

public class AnonymousQuestionAdapter extends Adapter<ViewHolder> {

  private Context context;
  private List<QuestionnaireListModel> questionnaireListModels;
  private LayoutInflater inflater;
  private Bundle args;
  private MainActivity mainActivity;

  public AnonymousQuestionAdapter(Context context,
      List<QuestionnaireListModel> questionnaireListModels, Bundle args) {
    this.context = context;
    this.args = args;
    this.mainActivity = (MainActivity) context;
    this.questionnaireListModels = questionnaireListModels;
    this.inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_anonymous_questionnaitre, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    try {
      holder.setData(position);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public int getItemCount() {
    return questionnaireListModels.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @Nullable
    @BindView(R.id.main_lay_rel)
    RelativeLayout mainLayRel;
    @Nullable
    @BindView(R.id.main_lay_lin)
    LinearLayout mainLayLin;

    private QuestionnaireListModel model;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      if (mainLayLin != null) {
        mainLayLin.setOnClickListener(this);
      } else if (mainLayRel != null) {
        mainLayRel.setOnClickListener(this);
      }
    }

    public void setData(int position) {
      model = questionnaireListModels.get(position);
      titleTv.setText(NumberUtil.digitsToPersian(model.getDescription()));
      Date createdDate = DateUtil
          .convertStringToDate(model.getDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);
      dateTv.setText(NumberUtil.digitsToPersian(dateString));
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.main_lay_rel:
        case R.id.main_lay_lin:
          args.putLong(Constants.QUESTIONNAIRE_BACKEND_ID, model.getPrimaryKey());
          args.putSerializable(Constants.QUESTIONNAIRE_OBJ, model);
          args.putLong(Constants.VISIT_ID, model.getVisitId());

          args.putLong(Constants.GOODS_GROUP_BACKEND_ID,
              model.getGoodsGroupBackendId() == null ? -1
                  : model.getGoodsGroupBackendId());
          args.putLong(Constants.ANSWERS_GROUP_NO, model.getAnswersGroupNo());
          args.putLong(Constants.PARENT, MainActivity.ANONYMOUS_QUESTIONNAIRE_FRAGMENT_ID);
          if (model.getStatus().equals(SendStatus.SENT.getId())) {
            args.putSerializable(Constants.PAGE_STATUS, PageStatus.VIEW);
          }

          mainActivity.changeFragment(MainActivity.QUESTION_LIST_FRAGMENT_ID, args, false);
          break;
      }
    }
  }
}
