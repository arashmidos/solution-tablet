package com.parsroyal.storemanagement.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.model.LabelValue;

import java.util.List;

/**
 * Created by Shakib on 03/09/2017
 */
public class LabelValueArrayAdapterWithHint extends BaseAdapter {

  private LayoutInflater mLayoutInflater;
  private List<LabelValue> labelValues;

  public LabelValueArrayAdapterWithHint(Context context, List labelValues) {
    this.labelValues = labelValues;
    mLayoutInflater = (LayoutInflater) context.getSystemService(
        Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LabelValueViewHolder holder;

    if (convertView == null) {
      convertView = mLayoutInflater.inflate(R.layout.row_layout_label_value_spinner, null);
      holder = new LabelValueViewHolder();
      holder.text = (TextView) convertView.findViewById(R.id.labelTxt);
      convertView.setTag(holder);
    } else {
      holder = (LabelValueViewHolder) convertView.getTag();
    }
    if (position == getCount()) {
      holder.text.setText("");
      holder.text.setHint(labelValues.get(getCount()).getLabel());
    } else {
      holder.text.setText(labelValues.get(position).getLabel());
    }
    return convertView;
  }

  @Override
  public int getCount() {
    return labelValues.size() - 1;
  }

  @Override
  public Object getItem(int position) {
    return labelValues.get(position);
  }

  @Override
  public long getItemId(int position) {
    return labelValues.get(position).getValue();
  }

  private static class LabelValueViewHolder {

    TextView text;
  }
}
