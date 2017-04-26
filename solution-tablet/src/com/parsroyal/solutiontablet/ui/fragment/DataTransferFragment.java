package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.biz.impl.GoodsRequestDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.KeyValueBizImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.DataTransferService;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mahyar on 6/15/2015.
 */
public class DataTransferFragment extends BaseFragment implements ResultObserver
{
    public static final String TAG = DataTransferFragment.class.getSimpleName();
    @BindView(R.id.getDataBtn)
    Button getDataBtn;
    @BindView(R.id.sendDataBtn)
    Button sendDataBtn;
    @BindView(R.id.transferLogTxtV)
    TextView transferLogTxtV;
    @BindView(R.id.transferSv)
    ScrollView transferSv;
    @BindView(R.id.dataTransferPB)
    ProgressBar dataTransferPB;

    private MainActivity mainActivity;
    private DataTransferService dataTransferService;
    private KeyValueBiz keyValueBiz;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View dataTransferView = inflater.inflate(R.layout.fragment_data_transfer, null);

        ButterKnife.bind(this, dataTransferView);

        mainActivity = (MainActivity) getActivity();
        dataTransferService = new DataTransferServiceImpl(mainActivity);
        keyValueBiz = new KeyValueBizImpl(mainActivity);

        transferSv.fullScroll(View.FOCUS_DOWN);
        transferLogTxtV.setMovementMethod(new ScrollingMovementMethod());

        return dataTransferView;
    }

    private void invokeSendData()
    {
        dataTransferPB.setVisibility(View.VISIBLE);
        enableButtons(false);
        Thread thread = new Thread(() ->
        {
            try
            {
                dataTransferService.sendAllData(DataTransferFragment.this);
            } catch (final BusinessException ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                runOnUiThread(() -> ToastUtil.toastError(getActivity(), ex));
            } catch (final Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                runOnUiThread(() -> ToastUtil.toastError(getActivity(), new UnknownSystemException(ex)));
            }
        });

        thread.start();
    }

    private void enableButtons(boolean status)
    {
        getDataBtn.setClickable(status);
        getDataBtn.setEnabled(status);
        sendDataBtn.setClickable(status);
        sendDataBtn.setEnabled(status);
    }

    private void invokeGetData()
    {
        dataTransferPB.setVisibility(View.VISIBLE);
        enableButtons(false);
        Thread thread = new Thread(() ->
        {
            try
            {
                KeyValue saleType = keyValueBiz.findByKey(ApplicationKeys.SETTING_SALE_TYPE);
                if (saleType.getValue().equals(ApplicationKeys.SALE_HOT))
                {
                    new GoodsRequestDataTransferBizImpl(getActivity(), DataTransferFragment.this).exchangeData();
                }
                dataTransferService.getAllData(DataTransferFragment.this);
            } catch (final BusinessException ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                runOnUiThread(() -> ToastUtil.toastError(getActivity(), ex));
            } catch (final Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                runOnUiThread(() -> ToastUtil.toastError(getActivity(), new UnknownSystemException(ex)));
            }
        });

        thread.start();
    }

    @Override
    public void publishResult(final BusinessException ex)
    {
        runOnUiThread(() ->
        {
            transferLogTxtV.append(getErrorString(ex) + "\n\n");
            transferSv.scrollTo(0, transferSv.getBottom());
        });
    }

    @Override
    public void publishResult(final String message)
    {
        runOnUiThread(() ->
        {
            transferLogTxtV.append(message + "\n\n");
            transferSv.scrollTo(0, transferSv.getBottom());
        });
    }

    @Override
    public void finished(boolean result)
    {
        runOnUiThread(() ->
        {
            dataTransferPB.setVisibility(View.INVISIBLE);
            enableButtons(true);
            transferSv.scrollTo(0, transferSv.getBottom());
        });
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.DATA_TRANSFER_FRAGMENT_ID;
    }

    @OnClick({R.id.getDataBtn, R.id.sendDataBtn})
    public void onViewClicked(View view)
    {
        transferLogTxtV.setText("");
        switch (view.getId())
        {
            case R.id.getDataBtn:
                invokeGetData();
                break;
            case R.id.sendDataBtn:
                invokeSendData();
                break;
        }
    }
}
