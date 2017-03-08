package com.parsroyal.solutiontablet.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.alirezaafkar.sundatepicker.DatePicker;
import com.alirezaafkar.sundatepicker.interfaces.DateSetListener;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.PaymentType;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PaymentServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Arash on 2016-08-13.
 */
public class PaymentDetailFragment extends BaseFragment implements DateSetListener
{

    public static final String TAG = PaymentDetailFragment.class.getSimpleName();

    @BindView(R.id.paymentsSp)
    Spinner paymentsSp;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.cancelBtn)
    Button cancelBtn;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.electronicLayer)
    LinearLayout electronicLayer;
    @BindView(R.id.chequeLayer)
    LinearLayout chequeLayer;
    @BindView(R.id.trackingNo)
    EditText trackingNo;
    @BindView(R.id.chequeNo)
    EditText chequeNo;
    @BindView(R.id.chequeDate)
    EditText chequeDate;
    @BindView(R.id.chequeAccNo)
    EditText chequeAccNo;
    @BindView(R.id.chequeBranch)
    EditText chequeBranch;
    @BindView(R.id.bankSp)
    Spinner bankSp;
    @BindView(R.id.chequeOwner)
    EditText chequeOwner;

    private Context context;
    private MainActivity mainActivity;
    private CustomerService customerService;
    private BaseInfoService baseInfoService;
    private PaymentService paymentService;
    private VisitService visitService;
    private Customer customer;
    private boolean dateModified = false;
    private Payment payment;
    private List<LabelValue> bankList;
    private List<LabelValue> paymentTypes;
    private long amountValue;
    private int ref;
    private long visitId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_new_payment_detail, null);
        ButterKnife.bind(this, view);

        context = getActivity();
        mainActivity = (MainActivity) getActivity();
        customerService = new CustomerServiceImpl(context);
        baseInfoService = new BaseInfoServiceImpl(context);
        paymentService = new PaymentServiceImpl(context);
        visitService = new VisitServiceImpl(context);

        try
        {
            Bundle arguments = getArguments();
            if (Empty.isNotEmpty(arguments))
            {
                payment = paymentService.getPaymentById(arguments.getLong(Constants.PAYMENT_ID));
                customer = customerService.getCustomerByBackendId(arguments.getLong(Constants.CUSTOMER_BACKEND_ID));
                visitId = arguments.getLong(Constants.VISIT_ID);
                ref = arguments.getInt(Constants.PARENT);
            } else
            {
                ToastUtil.toastError(getActivity(), R.string.message_error_in_loading_or_creating_customer);
                mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_FRAGMENT_ID, true);
            }
        } catch (BusinessException ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(getActivity(), ex);
        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
        }

        if (Empty.isEmpty(customer))
        {
            ToastUtil.toastError(getActivity(), R.string.message_error_in_loading_or_creating_customer);
            mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_FRAGMENT_ID, true);
        }

        loadSpinnersData();

        if (!isDisabled())
        {
            chequeDate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DatePicker.Builder builder = new DatePicker.Builder()
                            .id(1);
                    if (Empty.isNotEmpty(payment) && Empty.isNotEmpty(payment.getChequeDate()))
                    {
                        String[] date = payment.getChequeDate().split("/");
                        builder.date(Integer.parseInt(date[2]),
                                Integer.parseInt(date[1]),
                                Integer.parseInt("13" + date[0]));
                    }
                    builder.build(PaymentDetailFragment.this).show(getFragmentManager(), "");
                }
            });
        }

        if (Empty.isNotEmpty(payment))
        {
            loadData();
            if (isDisabled())
            {
                makeFormDisable();
            }
        }

        amount.addTextChangedListener(new TextWatcher()
        {
            boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (isEditing)
                {
                    return;
                }
                isEditing = true;
                try
                {
                    amountValue = Long.parseLong(NumberUtil.digitsToEnglish(s.toString().replaceAll(",", "")));
                    String number = String.format(Locale.US, "%,d", amountValue);
                    s.replace(0, s.length(), number);
                } catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
                isEditing = false;
            }
        });

        return view;
    }

    private void makeFormDisable()
    {
        amount.setEnabled(false);
        trackingNo.setEnabled(false);
        chequeNo.setEnabled(false);
        paymentsSp.setEnabled(false);
        chequeAccNo.setEnabled(false);
        chequeOwner.setEnabled(false);
        bankSp.setEnabled(false);
        chequeBranch.setEnabled(false);
        saveBtn.setEnabled(false);
    }

    private void loadData()
    {
        try
        {
            amountValue = payment.getAmount() / 1000;
            amount.setText(String.format(Locale.US, "%,d", amountValue));
            dateModified = true;
            int position = 0;
            for (LabelValue labelValue : paymentTypes)
            {
                if (labelValue.getValue().equals(payment.getPaymentTypeId()))
                {
                    paymentsSp.setSelection(position);
                    break;
                }
                position++;
            }
            switch (payment.getPaymentTypeId().intValue())
            {
                case 2:
                    electronicLayer.setVisibility(View.VISIBLE);
                    trackingNo.setText(payment.getTrackingNo());
                    break;
                case 6:
                    chequeLayer.setVisibility(View.VISIBLE);
                    chequeDate.setHint(payment.getChequeDate());
                    chequeNo.setText(payment.getChequeNumber());
                    String chequeNo = payment.getChequeAccountNumber();
                    chequeAccNo.setText(Empty.isNotEmpty(chequeNo) ? chequeNo : "--");
                    String chequeOwnerString = payment.getChequeOwner();
                    chequeOwner.setText(Empty.isNotEmpty(chequeOwnerString) ? chequeOwnerString : "--");
                    String branch = payment.getChequeBranch();
                    chequeBranch.setText(Empty.isNotEmpty(branch) ? branch : "--");
                    position = 0;
                    for (LabelValue labelValue : bankList)
                    {
                        if (labelValue.getValue().equals(payment.getChequeBank()))
                        {
                            bankSp.setSelection(position);
                            break;
                        }
                        position++;
                    }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            mainActivity.removeFragment(this);
        }
    }

    private boolean isDisabled()
    {
        return (payment != null && payment.getStatus().equals(SendStatus.SENT.getId()));
    }

    private void loadSpinnersData()
    {
        paymentTypes = baseInfoService.getAllPaymentType();
        bankList = baseInfoService.getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.BANK_NAME_TYPE.getId());

        if (Empty.isNotEmpty(paymentTypes))
        {
            paymentsSp.setAdapter(new LabelValueArrayAdapter(context, paymentTypes));
            paymentsSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    electronicLayer.setVisibility(id == 2 ? View.VISIBLE : View.GONE);
                    chequeLayer.setVisibility(id == 6 ? View.VISIBLE : View.GONE);
                    amount.requestFocus();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });
        }
        if (Empty.isNotEmpty(bankList))
        {
            bankSp.setAdapter(new LabelValueArrayAdapter(context, bankList));
        }
    }

    private boolean validate()
    {
        if (Empty.isEmpty(customer))
        {
            return false;
        }

        if (Empty.isEmpty(amount.getText().toString()))
        {
            ToastUtil.toastError(getActivity(), R.string.message_amount_is_required);
            amount.requestFocus();
            return false;
        }

        if (paymentsSp.getSelectedItemPosition() == 1 && Empty.isEmpty(trackingNo.getText().toString()))
        {
            ToastUtil.toastError(getActivity(), R.string.message_tracking_no_is_required);
            trackingNo.requestFocus();
            return false;
        }

        if (paymentsSp.getSelectedItemPosition() == 2)
        {
            if (!dateModified)
            {
                ToastUtil.toastError(getActivity(), R.string.message_cheque_date_is_required);
                chequeDate.requestFocus();
                return false;
            }
            if (Empty.isEmpty(chequeNo.getText().toString()))
            {
                ToastUtil.toastError(getActivity(), R.string.message_cheque_number_is_required);
                chequeNo.requestFocus();
                return false;
            }
        }

        return true;
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID;
    }

    @OnClick({R.id.cancelBtn, R.id.saveBtn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.cancelBtn:
                mainActivity.removeFragment(PaymentDetailFragment.this);
                mainActivity.changeSidebarItem(ref);
                break;
            case R.id.saveBtn:
                save();
                break;
        }
    }

    private void save()
    {
        try
        {
            if (validate())
            {
                if (Empty.isEmpty(payment))
                {
                    payment = new Payment();
                }
                payment.setCustomerBackendId(customer.getBackendId());
                payment.setAmount(amountValue * 1000);
                long paymentType = paymentsSp.getSelectedItemId();
                payment.setPaymentTypeId(paymentType);

                if (PaymentType.POS.getId().equals(paymentType))
                {
                    payment.setTrackingNo(trackingNo.getText().toString());
                }

                if (PaymentType.CHEQUE.getId().equals(paymentType))
                {
                    payment.setChequeOwner(chequeOwner.getText().toString());
                    payment.setChequeAccountNumber(chequeAccNo.getText().toString());
                    payment.setChequeBank(bankSp.getSelectedItemId());
                    payment.setChequeBranch(chequeBranch.getText().toString());
                    payment.setChequeDate(chequeDate.getHint().toString());
                    payment.setChequeNumber(chequeNo.getText().toString());
                }
                payment.setStatus(SendStatus.NEW.getId());
                long paymentId = paymentService.savePayment(payment);

                VisitInformationDetail visitDetail = new VisitInformationDetail(visitId, VisitInformationDetailType.CASH, paymentId);

                visitService.saveVisitDetail(visitDetail);

                ToastUtil.toastSuccess(getActivity(), R.string.message_payment_save_successfully);
                mainActivity.removeFragment(PaymentDetailFragment.this);
            }
        } catch (BusinessException ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(getActivity(), ex);
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
        }
    }

    @Override
    public void onDateSet(int id, @Nullable Calendar calendar, int day, int month, int year)
    {
        int tempYear = year % 100;
        chequeDate.setHint(tempYear + "/" + month + "/" + day);
        dateModified = true;
    }
}
