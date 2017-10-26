package com.parsroyal.solutiontablet.ui.adapter;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.dao.QuestionnaireDao;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionnaireDaoImpl;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.NewCustomerAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;

/**
 * Created by ShakibIsTheBest on 09/01/2017.
 */

public class NewCustomerAdapter extends Adapter<ViewHolder> {

  private static final String TAG = NewCustomerAdapter.class.getName();
  private final CustomerServiceImpl customerService;
  private LayoutInflater inflater;
  private MainActivity mainActivity;
  private boolean isSend;
  private List<NCustomerListModel> customers;
  private VisitService visitService;
  private QuestionnaireDao questionnaireService;

  public NewCustomerAdapter(MainActivity mainActivity, boolean isSend,
      List<NCustomerListModel> customers) {
    this.mainActivity = mainActivity;
    this.isSend = isSend;
    this.customers = customers;
    this.visitService = new VisitServiceImpl(mainActivity);
    this.questionnaireService = new QuestionnaireDaoImpl(mainActivity);
    inflater = LayoutInflater.from(mainActivity);
    customerService = new CustomerServiceImpl(mainActivity);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_system_customer, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    NCustomerListModel customer = customers.get(position);

    setMargin(position == customers.size() - 1, holder.customerLay);
    holder.setData(customer, position);
  }

  private void setMargin(boolean isLastItem, RelativeLayout layout) {
    LayoutParams parameter = new LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    float scale = mainActivity.getResources().getDisplayMetrics().density;
    int paddingInPx = (int) (8 * scale + 0.5f);
    if (isLastItem) {
      parameter.setMargins(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
    } else {
      parameter.setMargins(paddingInPx, paddingInPx, paddingInPx, 0);
    }
    layout.setLayoutParams(parameter);
  }

  @Override
  public int getItemCount() {
    return customers.size();
  }

  public void update(List<NCustomerListModel> customers) {
    this.customers = customers;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    @BindView(R.id.customer_name_tv)
    TextView customerNameTv;
    @BindView(R.id.customer_id_tv)
    TextView customerIdTv;
    @BindView(R.id.customer_lay)
    RelativeLayout customerLay;
    @BindView(R.id.customer_shop_name_tv)
    TextView customerShopNameTv;
    @BindView(R.id.has_location_img)
    ImageView hasLocationImg;
    @BindView(R.id.delete_img)
    ImageView deleteImg;
    @BindView(R.id.edit_img)
    ImageView editImg;
    @BindView(R.id.edit_lay)
    LinearLayout editLay;
    @BindView(R.id.questionnaire_img)
    ImageView questionnaireImg;

    private NCustomerListModel customer;
    private int position;
    private VisitInformation visitInformations;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      customerLay.setOnClickListener(this);
      editImg.setOnClickListener(this);
      deleteImg.setOnClickListener(this);
      questionnaireImg.setOnClickListener(this);
    }

    public void setData(NCustomerListModel customer, int position) {
      this.customer = customer;
      this.position = position;
      customerNameTv.setText(customer.getTitle());
      customerIdTv.setText(String.format(mainActivity.getString(R.string.code_x),
          Empty.isEmpty(customer.getCode()) ? "--" : customer.getCode()));
      customerShopNameTv.setText(customer.getShopName());
      if (customer.hasLocation()) {
        hasLocationImg.setImageResource(R.drawable.ic_gps_fixed_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.primary));
      } else {
        hasLocationImg.setImageResource(R.drawable.ic_gps_off_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
      }

      if (isSend) {
        editLay.setVisibility(View.GONE);
      } else {
        editLay.setVisibility(View.VISIBLE);
        try {
          visitInformations = visitService
              .getVisitInformationForNewCustomer(customer.getPrimaryKey());
        } catch (Exception ex) {
          Logger.sendError("UI Exception",
              "Error in NCustomerListAdapter.getView " + ex.getMessage());
          ex.printStackTrace();
        }
        if (visitInformations != null) {
          questionnaireImg.setImageResource(R.drawable.ic_questionnaires_done_24_dp);
        } else {
          questionnaireImg.setImageResource(R.drawable.ic_add_questionnaires_24_dp);
        }
      }
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.customer_lay:
          break;
        case R.id.edit_img:
          Bundle args = new Bundle();
          args.putLong(Constants.CUSTOMER_ID, customer.getPrimaryKey());
          mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID, args, false);
          break;
        case R.id.delete_img:
          deleteCustomer();
          break;
        case R.id.questionnaire_img:
          final VisitInformation finalVisitInformations = visitInformations;
          List<QuestionnaireListModel> generalQmodel = questionnaireService
              .searchForQuestionnaires(new QuestionnaireSo(true));
          List<QuestionnaireListModel> goodsQmodel = questionnaireService
              .searchForQuestionnaires(new QuestionnaireSo(false));

          //Calculate count of questions is each category ( i.e: General and Goods )
          //There could be more than one questionary in each of them, or questionaries with no question.
          int generalQCount = 0;
          int goodsQCount = 0;
          for (int i = 0; i < goodsQmodel.size(); i++) {
            QuestionnaireListModel questionnaireListModel = goodsQmodel.get(i);
            goodsQCount += questionnaireListModel.getQuestionsCount();
          }
          for (int i = 0; i < generalQmodel.size(); i++) {
            QuestionnaireListModel questionnaireListModel = generalQmodel.get(i);
            generalQCount += questionnaireListModel.getQuestionsCount();
          }

          //Initialize args
          final Bundle bundle = new Bundle();
          bundle.putLong(Constants.CUSTOMER_ID, customer.getPrimaryKey());
          long visitId;
          if (finalVisitInformations == null) {
            visitId = visitService.startVisitingNewCustomer(customer.getPrimaryKey());
          } else {
            visitId = finalVisitInformations.getId();
          }
          bundle.putLong(Constants.VISIT_ID, visitId);
          bundle.putInt(Constants.PARENT, MainActivity.NEW_CUSTOMER_FRAGMENT_ID);
          //

          //if question count > 0 is each category so we should display it to user.
          if (goodsQCount > 0 && generalQCount > 0) {
            mainActivity
                .changeFragment(MainActivity.QUESTIONNAIRE_CATEGORY_FRAGMENT_ID, bundle, false);
          } else if (generalQCount > 0) {
            bundle.putString(Constants.QUESTIONNAIRE_CATEGORY, Constants.GENERAL_QUESTIONNAIRE);
            mainActivity.changeFragment(MainActivity.QUESTIONNAIRE_LIST_FRAGMENT_ID, bundle, true);
          } else if (goodsQCount > 0) {
            bundle.putString(Constants.QUESTIONNAIRE_CATEGORY, Constants.GOOD_QUESTIONNAIRE);
            mainActivity.changeFragment(MainActivity.QUESTIONNAIRE_LIST_FRAGMENT_ID, bundle, true);
          } else {
            DialogUtil.showMessageDialog(mainActivity,
                mainActivity.getString(R.string.select_questionary),
                mainActivity.getString(R.string.error_no_questionary_found));
          }
          break;
      }
    }

    private void deleteCustomer() {
      DialogUtil
          .showConfirmDialog(mainActivity, mainActivity.getString(R.string.delete_customer),
              mainActivity.getString(R.string.message_customer_delete_confirm),
              (dialog, which) -> {
                try {
                  customerService.deleteCustomer(customer.getPrimaryKey());
                  customers.remove(customer);
                  notifyDataSetChanged();

                  mainActivity.runOnUiThread(() -> ToastUtil.toastSuccess(mainActivity, mainActivity
                      .getString(R.string.message_customer_deleted_successfully)));
                } catch (BusinessException ex) {
                  Log.e(TAG, ex.getMessage(), ex);
                  ToastUtil.toastError(mainActivity, ex);
                } catch (Exception ex) {
                  Logger.sendError("UI Exception",
                      "Error in NCustomersListAdapter.delete " + ex.getMessage());
                  Log.e(TAG, ex.getMessage(), ex);
                  ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
                }
              });
    }
  }
}
