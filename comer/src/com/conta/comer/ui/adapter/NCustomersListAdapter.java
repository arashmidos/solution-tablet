package com.conta.comer.ui.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conta.comer.R;
import com.conta.comer.constants.Constants;
import com.conta.comer.data.entity.VisitInformation;
import com.conta.comer.data.listmodel.NCustomerListModel;
import com.conta.comer.data.searchobject.NCustomerSO;
import com.conta.comer.exception.ContaBusinessException;
import com.conta.comer.exception.UnknownSystemException;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.fragment.NCustomerDetailFragment;
import com.conta.comer.util.CharacterFixUtil;
import com.conta.comer.util.DialogUtil;
import com.conta.comer.util.Empty;
import com.conta.comer.util.ToastUtil;

import java.util.List;

/**
 * Created by Mahyar on 7/13/2015.
 */
public class NCustomersListAdapter extends BaseListAdapter<NCustomerListModel>
{

    private MainActivity mainActivity;
    private CustomerService customerService;

    public NCustomersListAdapter(MainActivity mainActivity, List<NCustomerListModel> dataModel)
    {
        super(mainActivity, dataModel);
        this.customerService = new CustomerServiceImpl(mainActivity);
        this.mainActivity = mainActivity;
    }

    @Override
    protected List<NCustomerListModel> getFilteredData(CharSequence constraint)
    {
        NCustomerSO nCustomerSO = new NCustomerSO();
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

            {
                holder.nameTxt.setText(model.getTitle());
                holder.phoneTxt.setText(String.valueOf(model.getPhoneNumber()));
                holder.cellPhoneTxt.setText(Empty.isNotEmpty(model.getCellPhone()) ? model.getCellPhone() : "--");
            }

            {
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
                                                    ToastUtil.toastMessage(mainActivity, mainActivity.getString(R.string.message_customer_deleted_successfully));
                                                }
                                            });
                                        } catch (ContaBusinessException ex)
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
                List<VisitInformation> visitInformations = null;
                try
                {
                    visitInformations = customerService.getVisitInformationForNewCustomer(model.getPrimaryKey());
                } catch (Exception ex)
                {

                }
                if (visitInformations != null && visitInformations.size() > 0)
                {
                    holder.researchBtn.setImageResource(R.drawable.ic_action_document_active);
                }

                holder.researchBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        CharSequence options[] = new CharSequence[]{"تحقیقات عمومی", "تحقیقات کالایی"};

                        Dialog dialog = new AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.select_questionary))
                                .setItems(options, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Bundle args = new Bundle();
                                        args.putLong(Constants.CUSTOMER_ID, model.getPrimaryKey());
                                        long visitId = customerService.startVisitingNewCustomer(model.getPrimaryKey());
                                        args.putLong(Constants.VISIT_ID, visitId);
                                        mainActivity.changeFragment(which == 0 ? MainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID :
                                                MainActivity.GOODS_QUESTIONNAIRES_FRAGMENT_ID, args, false);
                                    }
                                })
                                .create();
                        dialog.show();
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
