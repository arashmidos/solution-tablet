package com.conta.comer.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.conta.comer.R;

/**
 * Created by Mahyar on 7/15/2015.
 */
public class ContaTab extends TextView
{

    public ContaTab(Context context)
    {
        super(context);
        this.setClickable(true);
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.conta_tab_background));
        this.setTextColor(getResources().getColor(R.color.conta_tab_text_color));
        this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setGravity(Gravity.CENTER);
        this.setPadding(10, 0, 10, 0);
    }

    public ContaTab(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ContaTab(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick()
    {
        setActivated(true);
        ViewParent parent = this.getParent();
        if (parent instanceof ContaTabContainer)
        {
            ContaTabContainer container = (ContaTabContainer) parent;
            return container.activeTab(this);
        }
        return super.performClick();
    }
}
