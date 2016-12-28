package com.conta.comer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.conta.comer.R;
import com.conta.comer.ui.MainActivity;

/**
 * Created by Mahyar on 8/4/2015.
 */
public class DashBoardFragment extends BaseContaFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dashboard, null);
        return view;
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.DASHBOARD_FRAGMENT_ID;
    }
}
