package com.conta.comer.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.conta.comer.util.Empty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 7/15/2015.
 */
public class ContaTabContainer extends LinearLayout
{

    private Context context;
    private List<ContaTab> tabs;
    private int defaultPosition;

    public ContaTabContainer(Context context)
    {
        super(context);
        this.context = context;
        this.tabs = new ArrayList<>();

    }

    public ContaTabContainer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        this.tabs = new ArrayList<>();
    }

    public ContaTabContainer(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public int getDefaultPosition()
    {
        return defaultPosition;
    }

    public void setDefaultPosition(int defaultPosition)
    {
        this.defaultPosition = defaultPosition;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        this.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < this.getChildCount(); i++)
        {
            View child = this.getChildAt(i);
            if (child instanceof ContaTab)
            {
                ContaTab tab = (ContaTab) child;
                if (defaultPosition == i)
                {
                    tab.setActivated(true);
                }
                tabs.add(tab);
            }
        }
    }

    public boolean activeTab(ContaTab toActiveTab)
    {
        for (ContaTab tab : tabs)
        {
            if (!tab.equals(toActiveTab))
            {
                tab.setActivated(false);
            }
        }
        return toActiveTab.callOnClick();
    }

    public void addTab(ContaTab tab)
    {
        if (Empty.isNotEmpty(tab))
        {
            tabs.add(tab);
            addView(tab);
            invalidate();
        }
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
    }
}
