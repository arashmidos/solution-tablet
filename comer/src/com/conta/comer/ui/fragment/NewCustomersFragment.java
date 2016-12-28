package com.conta.comer.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conta.comer.R;
import com.conta.comer.data.entity.City;
import com.conta.comer.data.entity.Customer;
import com.conta.comer.data.entity.Province;
import com.conta.comer.exception.ContaBusinessException;
import com.conta.comer.exception.UnknownSystemException;
import com.conta.comer.service.BaseInfoService;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.impl.BaseInfoServiceImpl;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.util.DateUtil;
import com.conta.comer.util.DialogUtil;
import com.conta.comer.util.Empty;
import com.conta.comer.util.ToastUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 6/3/2015.
 * Unused class
 */
public class NewCustomersFragment extends BaseContaFragment
{

    public static final String TAG = NewCustomersFragment.class.getSimpleName();

    protected MainActivity context;
    protected CustomerService customerService;
    protected BaseInfoService baseInfoService;
    private ImageButton addBtn;
    private ListView newCustomersLv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        try
        {
            context = (MainActivity) getActivity();
            customerService = new CustomerServiceImpl(getActivity());
            baseInfoService = new BaseInfoServiceImpl(getActivity());

            View view = getLayout(inflater);

            addBtn = (ImageButton) view.findViewById(R.id.addBtn);
            addBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    List<Province> provinceList = baseInfoService.getAllProvinces();
                    List<City> cityList = baseInfoService.getAllCities();
                    if (Empty.isEmpty(cityList))
                    {
                        toastError(R.string.message_cities_information_not_found);
                        return;
                    }
                    if (Empty.isEmpty(provinceList))
                    {
                        toastError(R.string.message_provinces_information_not_foun);
                        return;
                    }
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID, false);
                }
            });
            newCustomersLv = (ListView) view.findViewById(R.id.newCustomersLv);
            newCustomersLv.setEmptyView(view.findViewById(R.id.emptyElement));
            List<Customer> newCustomerList = getCustomerList();

            if (Empty.isNotEmpty(newCustomerList))
            {
                CustomerListAdapter adapter = new CustomerListAdapter(context, newCustomerList, isNewCustomersFragment());
                newCustomersLv.setAdapter(adapter);
            }

            return view;
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            return getErrorPageView(inflater);
        }

    }

    protected View getLayout(LayoutInflater inflater)
    {
        return inflater.inflate(R.layout.fragment_new_customers, null);
    }

    protected boolean isNewCustomersFragment()
    {
        return true;
    }

    protected List<Customer> getCustomerList()
    {
        return customerService.getAllNewCustomers();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        context.changeSidebarItem(MainActivity.NEW_CUSTOMER_FRAGMENT_ID);
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.NEW_CUSTOMER_FRAGMENT_ID;
    }

    public static class CustomerListAdapter extends BaseAdapter
    {

        private MainActivity mainActivity;
        private List<Customer> newCustomerList;
        private LayoutInflater mLayoutInflater;
        private CustomerService customerService;
        private boolean isNewCustomersFragment;

        public CustomerListAdapter(MainActivity context, List<Customer> newCustomerList, boolean isNewCustomersFragment)
        {
            this.mainActivity = context;
            this.newCustomerList = newCustomerList;
            mLayoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            this.customerService = new CustomerServiceImpl(context);
            this.isNewCustomersFragment = isNewCustomersFragment;
        }

        @Override
        public int getCount()
        {
            return newCustomerList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return newCustomerList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return newCustomerList.get(position).getPrimaryKey();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            try
            {
                NewCustomerListViewHolder holder;

                if (convertView == null)
                {
                    convertView = mLayoutInflater.inflate(R.layout.row_layout_new_customer, null);
                    holder = new NewCustomerListViewHolder();
                    holder.rowLayout = (RelativeLayout) convertView.findViewById(R.id.rowLayout);
                    holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
                    holder.addressTxt = (TextView) convertView.findViewById(R.id.addressTxt);
                    holder.createDateTxt = (TextView) convertView.findViewById(R.id.createDateTxt);
                    holder.sendStatusTxt = (TextView) convertView.findViewById(R.id.sendStatusTxt);
                    holder.sendBtn = (ImageButton) convertView.findViewById(R.id.sendBtn);
                    holder.editBtn = (ImageButton) convertView.findViewById(R.id.editBtn);
                    holder.deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteBtn);
                    convertView.setTag(holder);
                } else
                {
                    holder = (NewCustomerListViewHolder) convertView.getTag();
                }

                final Customer customer = newCustomerList.get(position);

                {

                    holder.nameTxt.setText(customer.getFullName());
                    holder.addressTxt.setText(customer.getAddress());
                    Date createDate = DateUtil.convertStringToDate(customer.getCreateDateTime(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN");
                    if (Empty.isNotEmpty(createDate))
                    {
                        holder.createDateTxt.setText(DateUtil.convertDate(createDate, DateUtil.FULL_FORMATTER_WITH_TIME, "FA"));
                    }
                    if ((Empty.isEmpty(customer.getBackendId()) || customer.getBackendId().equals(0L)) && isNewCustomersFragment)
                    {
                        holder.sendStatusTxt.setText(R.string.not_sent);
                        holder.rowLayout.setBackgroundColor(mainActivity.getResources().getColor(R.color.not_sent_row_bg));
                        holder.sendBtn.setVisibility(View.INVISIBLE);
                        holder.deleteBtn.setVisibility(View.VISIBLE);
                        holder.editBtn.setVisibility(View.VISIBLE);
                    } else
                    {
                        holder.rowLayout.setBackgroundColor(mainActivity.getResources().getColor(R.color.sent_row_bg));
                        if (isNewCustomersFragment)
                        {
                            holder.sendBtn.setVisibility(View.INVISIBLE);
                        }
                        holder.sendStatusTxt.setText(R.string.sent);
                        holder.sendBtn.setVisibility(View.INVISIBLE);
                        holder.deleteBtn.setVisibility(View.INVISIBLE);
                        holder.editBtn.setVisibility(View.INVISIBLE);
                    }
                }

                //setup buttons

                {
                    holder.editBtn.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Bundle args = new Bundle();
                            args.putLong(NCustomerDetailFragment.CUSTOMER_ID_KEY, customer.getId());
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
                                                customerService.deleteCustomer(customer.getPrimaryKey());
                                                CustomerListAdapter.this.newCustomerList.remove(customer);
                                                CustomerListAdapter.this.notifyDataSetChanged();
                                                CustomerListAdapter.this.notifyDataSetInvalidated();
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
                }
                return convertView;
            } catch (Exception e)
            {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        }

        private static class NewCustomerListViewHolder
        {
            public RelativeLayout rowLayout;
            public TextView nameTxt;
            public TextView addressTxt;
            public TextView createDateTxt;
            public TextView sendStatusTxt;
            public ImageButton sendBtn;
            public ImageButton editBtn;
            public ImageButton deleteBtn;
        }
    }

}
