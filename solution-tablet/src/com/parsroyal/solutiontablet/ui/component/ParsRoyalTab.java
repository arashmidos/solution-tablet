package com.parsroyal.solutiontablet.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;

/**
 * Created by Mahyar on 7/15/2015.
 */
public class ParsRoyalTab extends TextView
{

    public ParsRoyalTab(Context context)
    {
        super(context);
        this.setClickable(true);
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_background));
        this.setTextColor(getResources().getColor(R.color.tab_text_color));
        this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.setGravity(Gravity.CENTER);
        this.setPadding(10, 0, 10, 0);
    }

    public ParsRoyalTab(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ParsRoyalTab(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick()
    {
        setActivated(true);
        ViewParent parent = this.getParent();
        if (parent instanceof TabContainer)
        {
            TabContainer container = (TabContainer) parent;
            return container.activeTab(this);
        }
        return super.performClick();
    }
}
