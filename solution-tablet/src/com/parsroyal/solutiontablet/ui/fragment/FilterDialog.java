package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Arashmidos
 */
public class FilterDialog extends DialogFragment
{
    private static FilterDialog confirmDialog;
    @BindView(R.id.distance_filter)
    EditText distanceFilter;
    @BindView(R.id.filter_has_order)
    CheckBox filterHasOrder;
    @BindView(R.id.filter_has_none)
    CheckBox filterHasNone;
    @BindView(R.id.filter_btn)
    Button filterBtn;
    @BindView(R.id.filter_clear_btn)
    Button filterClearBtn;
    private FilterClickListener onClickListener;

    public static FilterDialog newInstance()
    {
        if (confirmDialog == null)
        {
            confirmDialog = new FilterDialog();
        }
        return confirmDialog;
    }

    public void setOnClickListener(FilterClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.dialog_filter, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    void onClose()
    {
        getDialog().dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart()
    {
//        setRetainInstance(true);
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @OnClick({R.id.filter_btn, R.id.filter_clear_btn})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.filter_btn:
                doFilter();
//                getDialog().dismiss();
                break;
            case R.id.filter_clear_btn:
                clearFields();
                onClickListener.clearFilter();
                getDialog().dismiss();
                break;
        }
    }

    private void clearFields()
    {
        filterHasOrder.setChecked(false);
        filterHasNone.setChecked(false);
        distanceFilter.setText("");
    }

    private void doFilter()
    {
        String distanceText = distanceFilter.getText().toString();

        boolean hasOrder = filterHasOrder.isChecked();
        boolean hasNone = filterHasNone.isChecked();
        if (Empty.isEmpty(distanceText) && !hasOrder && !hasNone)
        {
            ToastUtil.toastMessage(getActivity(), R.string.error_no_filter_selected);
            return;
        }

        int distance;
        if (Empty.isNotEmpty(distanceText))
        {
            distance = Integer.parseInt(distanceText);
            //TODO fix this
            if (distance <= 0 || distance > 50000)
            {
                ToastUtil.toastMessage(getActivity(), R.string.error_filter_max_distance);
                return;
            }
        } else
        {
            distance = Integer.MAX_VALUE;
        }

        onClickListener.doFilter(distance, hasOrder, hasNone);
        getDialog().dismiss();
    }

    public interface FilterClickListener
    {
        void doFilter(int distance, boolean hasOrder, boolean hasNone);

        void clearFilter();
    }
}
