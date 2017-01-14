package com.conta.comer.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.conta.comer.R;
import com.conta.comer.exception.UnknownSystemException;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.component.ContaTabContainer;
import com.conta.comer.util.ToastUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Arash on 2016-11-11
 */
public class TrackingBaseFragment extends BaseContaFragment
{
    public static final String TAG = TrackingBaseFragment.class.getSimpleName();
    private MainActivity context;

    private ContaTabContainer tabContainer;
    private LinearLayout actionsLayout;
    private Button deliverOrderBtn;
    private Button cancelOrderBtn;
    private Button saveOrderBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        try
        {
            context = (MainActivity) getActivity();

            View view = context.getLayoutInflater().inflate(R.layout.fragment_tracking_base, null);

            ButterKnife.bind(this, view);

            return view;
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
            return inflater.inflate(R.layout.view_error_page, null);
        }
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.BASE_TRACKING_FRAGMENT_ID;
    }

    @OnClick({R.id.user_tracking_btn, R.id.customer_tracking_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.user_tracking_btn:
                context.changeFragment(MainActivity.USER_TRACKING_FRAGMENT_ID, null, false);
                break;
            case R.id.customer_tracking_btn:
                context.changeFragment(MainActivity.CUSTOMER_TRACKING_FRAGMENT_ID, null, false);
                break;
        }
    }
}
