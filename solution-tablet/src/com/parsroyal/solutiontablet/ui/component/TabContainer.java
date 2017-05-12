package com.parsroyal.solutiontablet.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 7/15/2015.
 */
public class TabContainer extends LinearLayout {

  private Context context;
  private List<ParsRoyalTab> tabs;
  private int defaultPosition;

  public TabContainer(Context context) {
    super(context);
    this.context = context;
    this.tabs = new ArrayList<>();

  }

  public TabContainer(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    this.tabs = new ArrayList<>();
  }

  public TabContainer(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public int getDefaultPosition() {
    return defaultPosition;
  }

  public void setDefaultPosition(int defaultPosition) {
    this.defaultPosition = defaultPosition;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    init();
  }

  private void init() {
    this.setOrientation(LinearLayout.HORIZONTAL);
    for (int i = 0; i < this.getChildCount(); i++) {
      View child = this.getChildAt(i);
      if (child instanceof ParsRoyalTab) {
        ParsRoyalTab tab = (ParsRoyalTab) child;
        if (defaultPosition == i) {
          tab.setActivated(true);
        }
        tabs.add(tab);
      }
    }
  }

  public boolean activeTab(ParsRoyalTab toActiveTab) {
    for (ParsRoyalTab tab : tabs) {
      if (!tab.equals(toActiveTab)) {
        tab.setActivated(false);
      }
    }
    return toActiveTab.callOnClick();
  }

  public void addTab(ParsRoyalTab tab) {
    if (Empty.isNotEmpty(tab)) {
      tabs.add(tab);
      addView(tab);
      invalidate();
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
  }
}
