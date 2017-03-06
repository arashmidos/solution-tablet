package com.parsroyal.solutiontablet.ui.adapter;

import android.app.Dialog;
import android.content.DialogInterface;
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
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.model.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.NCustomerSO;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.fragment.NCustomerDetailFragment;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;

import java.util.List;

/**
 * Created by Mahyar on 7/13/2015.
 */
public class NCustomersListAdapter extends BaseListAdapter<NCustomerListModel>
{

    private final QuestionnaireService questionnaireService;
    private final NCustomerSO nCustomerSO;
    private MainActivity mainActivity;
    private CustomerService customerService;

    public NCustomersListAdapter(MainActivity mainActivity, List<NCustomerListModel> dataModel, NCustomerSO nCustomerSO)
    {
        super(mainActivity, dataModel);
        customerService = new CustomerServiceImpl(mainActivity);
        questionnaireService = new QuestionnaireServiceImpl(mainActivity);
        this.mainActivity = mainActivity;
        this.nCustomerSO = nCustomerSO;
    }

    @Override
    protected List<NCustomerListModel> getFilteredData(CharSequence constraint)
    {
        nCustomerSO.setConstraint(null);
        if (constraint.length() != 0 && Empty.isNotEmpty(constraint.toString()))
        {
            nCustomerSO.setConstraint("%" + CharacterFixUtil.fixString(constraint.toString()) + "%");
        }
        return customerService.searchForNCustomers(nCustomerSO);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try
        {
            final CustomerViewHolder holder;

            if (convertView == null)
            {
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
            } else
            {
                holder = (CustomerViewHolder) convertView.getTag();
            }

            final NCustomerListModel model = dataModel.get(position);

            holder.nameTxt.setText(model.getTitle());
            holder.phoneTxt.setText(String.valueOf(model.getPhoneNumber()));
            holder.cellPhoneTxt.setText(Empty.isNotEmpty(model.getCellPhone()) ? model.getCellPhone() : "--");

            holder.editBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle args = new Bundle();
                    args.putLong(NCustomerDetailFragment.CUSTOMER_ID_KEY, model.getPrimaryKey());
                    mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID, args, false);
                }
            });

            holder.deleteBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DialogUtil.showConfirmDialog(mainActivity, mainActivity.getString(R.string.delete_customer),
                            mainActivity.getString(R.string.message_customer_delete_confirm),
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    try
                                    {
                                        customerService.deleteCustomer(model.getPrimaryKey());
                                        NCustomersListAdapter.this.dataModel.remove(model);
                                        NCustomersListAdapter.this.notifyDataSetChanged();
                                        NCustomersListAdapter.this.notifyDataSetInvalidated();
                                        mainActivity.runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                ToastUtil.toastSuccess(mainActivity, mainActivity.getString(R.string.message_customer_deleted_successfully));
                                            }
                                        });
                                    } catch (BusinessException ex)
                                    {
                                        Log.e(TAG, ex.getMessage(), ex);
                                        ToastUtil.toastError(mainActivity, ex);
                                    } catch (Exception ex)
                                    {
                                        Log.e(TAG, ex.getMessage(), ex);
                                        ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
                                    }
                                }
                            }
                    );
                }
            });

            VisitInformation visitInformations = null;
            try
            {
                visitInformations = customerService.getVisitInformationForNewCustomer(model.getPrimaryKey());
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
            if (visitInformations != null)
            {
                holder.researchBtn.setImageResource(R.drawable.ic_action_document_active);
            }

            final VisitInformation finalVisitInformations = visitInformations;
            holder.researchBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    List<QuestionnaireListModel> generalQmodel = questionnaireService.searchForQuestionnaires(new QuestionnaireSo(true));
                    List<QuestionnaireListModel> goodsQmodel = questionnaireService.searchForQuestionnaires(new QuestionnaireSo(false));

                    //Calculate count of questions is each category ( i.e: General and Goods )
                    //There could be more than one questionary in each of them, or questionaries with no question.
                    int generalQCount = 0;
                    int goodsQCount = 0;
                    for (int i = 0; i < goodsQmodel.size(); i++)
                    {
                        QuestionnaireListModel questionnaireListModel = goodsQmodel.get(i);
                        goodsQCount += questionnaireListModel.getQuestionsCount();
                    }
                    for (int i = 0; i < generalQmodel.size(); i++)
                    {
                        QuestionnaireListModel questionnaireListModel = generalQmodel.get(i);
                        generalQCount += questionnaireListModel.getQuestionsCount();
                    }

                    //Initialize args
                    final Bundle args = new Bundle();
                    args.putLong(Constants.CUSTOMER_ID, model.getPrimaryKey());
                    long visitId;
                    if (finalVisitInformations == null)
                    {
                        visitId = customerService.startVisitingNewCustomer(model.getPrimaryKey());
                    } else
                    {
                        visitId = finalVisitInformations.getId();
                    }
                    args.putLong(Constants.VISIT_ID, visitId);
                    //

                    //if question count > 0 is each category so we should display it to user.
                    if (goodsQCount > 0 && generalQCount > 0)
                    {
                        CharSequence options[] = new CharSequence[]{"تحقیقات عمومی", "تحقیقات کالایی"};

                        Dialog dialog = new AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.select_questionary))
                                .setItems(options, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {

                                        mainActivity.changeFragment(which == 0 ? MainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID :
                                                MainActivity.GOODS_QUESTIONNAIRES_FRAGMENT_ID, args, false);
                                    }
                                })
                                .create();
                        dialog.show();
                    } else if (generalQCount > 0)
                    {
                        mainActivity.changeFragment(MainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID, args, false);
                    } else if (goodsQCount > 0)
                    {
                        mainActivity.changeFragment(MainActivity.GOODS_QUESTIONNAIRES_FRAGMENT_ID, args, false);
                    } else
                    {
                        DialogUtil.showMessageDialog(context, context.getString(R.string.select_questionary),
                                context.getString(R.string.error_no_questionary_found));
                    }
                }
            });

            if (Empty.isNotEmpty(model.getBackendId()) && !model.getBackendId().equals(0L))
            {
                holder.editBtn.setVisibility(View.INVISIBLE);
                holder.deleteBtn.setVisibility(View.INVISIBLE);
            } else
            {
                holder.editBtn.setVisibility(View.VISIBLE);
                holder.deleteBtn.setVisibility(View.VISIBLE);
            }

            return convertView;
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private static class CustomerViewHolder
    {
        public RelativeLayout rowLayout;
        public TextView nameTxt;
        public TextView phoneTxt;
        public TextView cellPhoneTxt;
        public ImageButton deleteBtn;
        public ImageButton editBtn;
        public ImageButton researchBtn;
    }
}
