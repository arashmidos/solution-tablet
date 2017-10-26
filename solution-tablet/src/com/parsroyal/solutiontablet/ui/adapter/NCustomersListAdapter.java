package com.parsroyal.solutiontablet.ui.adapter;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.NCustomerSO;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.ui.fragment.NCustomerDetailFragment;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;

/**
 * Created by Mahyar on 7/13/2015.
 */
public class NCustomersListAdapter extends BaseListAdapter<NCustomerListModel> {

  private final QuestionnaireService questionnaireService;
  private final NCustomerSO nCustomerSO;
  private OldMainActivity oldMainActivity;
  private CustomerService customerService;
  private VisitService visitService;

  public NCustomersListAdapter(OldMainActivity oldMainActivity, List<NCustomerListModel> dataModel,
      NCustomerSO nCustomerSO) {
    super(oldMainActivity, dataModel);
    customerService = new CustomerServiceImpl(oldMainActivity);
    questionnaireService = new QuestionnaireServiceImpl(oldMainActivity);
    visitService = new VisitServiceImpl(context);
    this.oldMainActivity = oldMainActivity;
    this.nCustomerSO = nCustomerSO;
  }

  @Override
  protected List<NCustomerListModel> getFilteredData(CharSequence constraint) {
    nCustomerSO.setConstraint(null);
    if (constraint.length() != 0 && Empty.isNotEmpty(constraint.toString())) {
      nCustomerSO.setConstraint("%" + CharacterFixUtil.fixString(constraint.toString()) + "%");
    }
    return customerService.searchForNCustomers(nCustomerSO);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    try {
      final CustomerViewHolder holder;

      if (convertView == null) {
        convertView = mLayoutInflater.inflate(R.layout.row_layout_n_customer, null);
        holder = new CustomerViewHolder();
        holder.rowLayout = (RelativeLayout) convertView.findViewById(R.id.rowLayout);
        holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
        holder.phoneTxt = (TextView) convertView.findViewById(R.id.phoneTxt);
        holder.cellPhoneTxt = (TextView) convertView.findViewById(R.id.cellPhoneTxt);
        holder.deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteBtn);
        holder.editBtn = (ImageButton) convertView.findViewById(R.id.editBtn);
        holder.researchBtn = (ImageButton) convertView.findViewById(R.id.researchBtn);

        convertView.setTag(holder);
      } else {
        holder = (CustomerViewHolder) convertView.getTag();
      }

      final NCustomerListModel model = dataModel.get(position);

      holder.nameTxt.setText(model.getTitle());
      holder.phoneTxt.setText(String.valueOf(model.getPhoneNumber()));
      holder.cellPhoneTxt
          .setText(Empty.isNotEmpty(model.getCellPhone()) ? model.getCellPhone() : "--");

      holder.editBtn.setOnClickListener(v -> {
        Bundle args = new Bundle();
        args.putLong(NCustomerDetailFragment.CUSTOMER_ID_KEY, model.getPrimaryKey());
        oldMainActivity
            .changeFragment(OldMainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID, args, false);
      });

      holder.deleteBtn.setOnClickListener(v -> DialogUtil
          .showConfirmDialog(oldMainActivity, oldMainActivity.getString(R.string.delete_customer),
              oldMainActivity.getString(R.string.message_customer_delete_confirm),
              (dialog, which) -> {
                try {
                  customerService.deleteCustomer(model.getPrimaryKey());
                  NCustomersListAdapter.this.dataModel.remove(model);
                  NCustomersListAdapter.this.notifyDataSetChanged();
                  NCustomersListAdapter.this.notifyDataSetInvalidated();
                  oldMainActivity
                      .runOnUiThread(() -> ToastUtil.toastSuccess(oldMainActivity, oldMainActivity
                          .getString(R.string.message_customer_deleted_successfully)));
                } catch (BusinessException ex) {
                  Log.e(TAG, ex.getMessage(), ex);
                  ToastUtil.toastError(oldMainActivity, ex);
                } catch (Exception ex) {
                  Logger.sendError("UI Exception",
                      "Error in NCustomersListAdapter.delete " + ex.getMessage());
                  Log.e(TAG, ex.getMessage(), ex);
                  ToastUtil.toastError(oldMainActivity, new UnknownSystemException(ex));
                }
              }
          ));

      VisitInformation visitInformations = null;
      int detailSize = 0;
      try {
        visitInformations = visitService.getVisitInformationForNewCustomer(model.getPrimaryKey());
        detailSize = visitService.getAllVisitDetailById(visitInformations.getId()).size();
      } catch (Exception ex) {
        Logger.sendError("UI Exception",
            "Error in NCustomerListAdapter.getView " + ex.getMessage());
        ex.printStackTrace();
      }
      if (visitInformations != null && detailSize > 0) {
        holder.researchBtn.setImageResource(R.drawable.ic_action_document_active);
      }

      final VisitInformation finalVisitInformations = visitInformations;
      holder.researchBtn.setOnClickListener(v -> {
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
        final Bundle args = new Bundle();
        args.putLong(Constants.CUSTOMER_ID, model.getPrimaryKey());
        long visitId;
        if (finalVisitInformations == null) {
          visitId = visitService.startVisitingNewCustomer(model.getPrimaryKey());
          visitService.finishVisiting(visitId);
          Position customerPosition = new PositionServiceImpl(context).getLastPosition();

          visitService.updateVisitLocation(visitId,
              Empty.isNotEmpty(customerPosition) ? customerPosition.getLocation()
                  : new Location("Dummy"));
        } else {
          visitId = finalVisitInformations.getId();
        }
        args.putLong(Constants.VISIT_ID, visitId);
        args.putInt(Constants.PARENT, OldMainActivity.NEW_CUSTOMER_FRAGMENT_ID);
        //

        //if question count > 0 is each category so we should display it to user.
        if (goodsQCount > 0 && generalQCount > 0) {
          CharSequence options[] = new CharSequence[]{"تحقیقات عمومی", "تحقیقات کالایی"};

          Dialog dialog = new AlertDialog.Builder(context)
              .setTitle(context.getString(R.string.select_questionary))
              .setItems(options, (dialog1, which) -> oldMainActivity.changeFragment(
                  which == 0 ? OldMainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID :
                      OldMainActivity.GOODS_QUESTIONNAIRES_FRAGMENT_ID, args, false))
              .create();
          dialog.show();
        } else if (generalQCount > 0) {
          oldMainActivity
              .changeFragment(OldMainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID, args, false);
        } else if (goodsQCount > 0) {
          oldMainActivity
              .changeFragment(OldMainActivity.GOODS_QUESTIONNAIRES_FRAGMENT_ID, args, false);
        } else {
          DialogUtil.showMessageDialog(context, context.getString(R.string.select_questionary),
              context.getString(R.string.error_no_questionary_found));
        }
      });

      if (Empty.isNotEmpty(model.getBackendId()) && !model.getBackendId().equals(0L)) {
        holder.editBtn.setVisibility(View.INVISIBLE);
        holder.deleteBtn.setVisibility(View.INVISIBLE);
      } else {
        holder.editBtn.setVisibility(View.VISIBLE);
        holder.deleteBtn.setVisibility(View.VISIBLE);
      }

      return convertView;
    } catch (Exception e) {
      Logger.sendError("UI Exception", "Error in NCustomerListAdapter.getView " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      return null;
    }
  }

  private static class CustomerViewHolder {

    public RelativeLayout rowLayout;
    public TextView nameTxt;
    public TextView phoneTxt;
    public TextView cellPhoneTxt;
    public ImageButton deleteBtn;
    public ImageButton editBtn;
    public ImageButton researchBtn;
  }
}
