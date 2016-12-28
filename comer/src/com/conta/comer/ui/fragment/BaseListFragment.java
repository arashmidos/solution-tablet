package com.conta.comer.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.conta.comer.R;
import com.conta.comer.data.model.BaseListModel;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.adapter.BaseListAdapter;
import com.conta.comer.ui.component.ContaTabContainer;
import com.conta.comer.util.Empty;

import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public abstract class BaseListFragment<T extends BaseListModel, AD extends BaseListAdapter> extends BaseContaFragment
{

    protected ListView dataModelLv;
    protected List<T> dataModel;
    protected EditText searchTxt;
    protected BaseListAdapter adapter;
    protected ContaTabContainer tabContainer;
    protected LinearLayout headerViewLayout;
    private MainActivity context;
    protected LinearLayout buttonPanel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        try
        {
            context = (MainActivity) getActivity();

            View view = inflater.inflate(R.layout.fragment_base, null);
            final HorizontalScrollView view1 = (HorizontalScrollView) view.findViewById(R.id.scroll);
            view1.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    view1.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            }, 100);

            buttonPanel = (LinearLayout) view.findViewById(R.id.button_panel);
            searchTxt = (EditText) view.findViewById(R.id.searchTxt);
            tabContainer = (ContaTabContainer) view.findViewById(R.id.tabContainer);
            dataModelLv = (ListView) view.findViewById(R.id.dataModelLv);
            dataModelLv.setEmptyView(view.findViewById(R.id.emptyElement));
            headerViewLayout = (LinearLayout) view.findViewById(R.id.headerViewLayout);

            View headerView = getHeaderView();
            if (Empty.isNotEmpty(headerView))
            {
                headerViewLayout.addView(headerView);
            }

            dataModel = getDataModel();
            if (Empty.isNotEmpty(dataModel))
            {
                adapter = getAdapter();
                dataModelLv.setAdapter(adapter);
            }
            dataModelLv.setOnItemClickListener(getOnItemClickListener());

            searchTxt.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    if (Empty.isNotEmpty(adapter))
                    {
                        adapter.getFilter().filter(s);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                }

                @Override
                public void afterTextChanged(Editable s)
                {
                }
            });

            return view;
        } catch (Exception e)
        {
            Log.e(getClassTag(), e.getMessage(), e);
            return getErrorPageView(inflater);
        }
    }

    protected void updateList()
    {
        dataModelLv.setAdapter(getAdapter());
    }

    protected abstract List<T> getDataModel();

    protected abstract View getHeaderView();

    protected abstract AD getAdapter();

    protected abstract ListView.OnItemClickListener getOnItemClickListener();

    protected abstract String getClassTag();

    protected abstract String getTitle();

}
