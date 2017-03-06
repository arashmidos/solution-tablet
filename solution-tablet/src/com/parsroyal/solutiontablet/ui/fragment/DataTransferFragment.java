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
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.DataTransferService;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.ToastUtil;

/**
 * Created by Mahyar on 6/15/2015.
 */
public class DataTransferFragment extends BaseFragment implements ResultObserver
{
    public static final String TAG = DataTransferFragment.class.getSimpleName();

    private MainActivity mainActivity;
    private ScrollView transferSv;
    private Button sendDataBtn;
    private Button getDataBtn;
    private TextView transferLogTxtV;
    private ProgressBar dataTransferPB;
    private DataTransferService dataTransferService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View dataTransferView = inflater.inflate(R.layout.fragment_data_transfer, null);

        {
            mainActivity = (MainActivity) getActivity();
            dataTransferService = new DataTransferServiceImpl(mainActivity);
        }

        {
            getDataBtn = (Button) dataTransferView.findViewById(R.id.getDataBtn);
            transferSv = (ScrollView) dataTransferView.findViewById(R.id.transferSv);
            transferSv.fullScroll(View.FOCUS_DOWN);
            transferLogTxtV = (TextView) dataTransferView.findViewById(R.id.transferLogTxtV);
            transferLogTxtV.setMovementMethod(new ScrollingMovementMethod());
            getDataBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    transferLogTxtV.setText("");
                    invokeGetData();
                }
            });
            sendDataBtn = (Button) dataTransferView.findViewById(R.id.sendDataBtn);
            sendDataBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    transferLogTxtV.setText("");
                    invokeSendData();
                }
            });
            dataTransferPB = (ProgressBar) dataTransferView.findViewById(R.id.dataTransferPB);
        }

        return dataTransferView;
    }

    private void invokeSendData()
    {
        dataTransferPB.setVisibility(View.VISIBLE);
        enableButtons(false);
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    dataTransferService.sendAllData(DataTransferFragment.this);
                } catch (final BusinessException ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtil.toastError(getActivity(), ex);
                        }
                    });
                } catch (final Exception ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
                        }
                    });
                }
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
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    dataTransferService.getAllData(DataTransferFragment.this);
                } catch (final BusinessException ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtil.toastError(getActivity(), ex);
                        }
                    });
                } catch (final Exception ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
                        }
                    });
                }
            }
        });

        thread.start();

    }

    @Override
    public void publishResult(final BusinessException ex)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                transferLogTxtV.append(getErrorString(ex) + "\n\n");
                transferSv.scrollTo(0, transferSv.getBottom());
            }
        });
    }

    @Override
    public void publishResult(final String message)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                transferLogTxtV.append(message + "\n\n");
                transferSv.scrollTo(0, transferSv.getBottom());
            }
        });
    }

    @Override
    public void finished(boolean result)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                dataTransferPB.setVisibility(View.INVISIBLE);
                enableButtons(true);
                transferSv.scrollTo(0, transferSv.getBottom());
            }
        });
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.DATA_TRANSFER_FRAGMENT_ID;
    }
}
