package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import java.util.List;

/**
 * Created by Mahyar on 8/28/2015.
 */
public class LabelValueArrayAdapter extends BaseAdapter {

  private LayoutInflater mLayoutInflater;
  private List<LabelValue> labelValues;

  public LabelValueArrayAdapter(Context context, List labelValues) {
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

    holder.text.setText(labelValues.get(position).getLabel());
    return convertView;
  }
//TODO:
  /*public View getDropDownView(int position, View convertView, ViewGroup parent) {
    View v = super.getDropDownView(position, convertView, parent);
    ((TextView) v).setGravity(Gravity.RIGHT);
    return v;
  }*/
  @Override
  public int getCount() {
    return labelValues.size();
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
