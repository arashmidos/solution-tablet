package com.parsroyal.solutiontablet.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.DataTransferService;
import com.parsroyal.solutiontablet.service.LocationService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.order.SaleOrderService;
import com.parsroyal.solutiontablet.service.order.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ImageUtil;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 7/17/2015.
 * Edited by Arash on 6/29/2016
 */
public class VisitDetailFragment extends BaseFragment implements ResultObserver
{

    public static final String TAG = VisitDetailFragment.class.getSimpleName();

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int RESULT_OK = -1;
    private static final int RESULT_CANCELED = 0;
    private static final int[] icons = {
            R.drawable.selector_add_order,
            R.drawable.selector_add_returned,
            R.drawable.selector_cash,
            R.drawable.selector_customer_location,
            R.drawable.selector_customer_detail,
            R.drawable.selector_general_questionary,//5
            R.drawable.selector_goods_questionary,
            R.drawable.selector_goods_delivery,
            R.drawable.selector_taking_picture,
            R.drawable.selector_save_reject,
            R.drawable.selector_exit
    };
    private MainActivity mainActivity;
    private GridView mainLayout;
    private CustomerService customerService;
    private SaleOrderService saleOrderService;
    private LocationService locationService;
    private Long visitId;
    private Long customerId;
    private Customer customer;
    private SaleOrderDto orderDto;
    private BaseInfoService baseInfoService;
    private File file;
    private SettingServiceImpl settingService;
    private String saleType;
    private Uri fileUri;
    private GoodsDtoList rejectedGoodsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        try
        {
            mainActivity = (MainActivity) getActivity();
            customerService = new CustomerServiceImpl(mainActivity);
            saleOrderService = new SaleOrderServiceImpl(mainActivity);
            baseInfoService = new BaseInfoServiceImpl(mainActivity);
            locationService = new LocationServiceImpl(mainActivity);
            settingService = new SettingServiceImpl(getActivity());

            visitId = (Long) getArguments().get("visitId");
            customerId = (Long) getArguments().get("customerId");

            customer = customerService.getCustomerById(customerId);
            saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

            View view = inflater.inflate(R.layout.fragment_visit_detail, null);
            mainLayout = (GridView) view.findViewById(R.id.mainLayout);
            mainLayout.setAdapter(new MainLayoutAdapter());
            setItemClickListener();

            return view;
        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            return inflater.inflate(R.layout.view_error_page, null);
        }
    }

    private void setItemClickListener()
    {
        mainLayout.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    //Add Factor / Add Order
                    case 0:
                        openOrderDetailFragment(SaleOrderStatus.DRAFT.getId());
                        break;
                    //Add returned
                    case 1:
                        openOrderDetailFragment(SaleOrderStatus.REJECTED_DRAFT.getId());
                        break;
                    //Cash
                    case 2:
                        openPaymentFragment();
                        break;
                    //Location
                    case 3:
                        openSaveLocationFragment();
                        break;
                    //Detail
                    case 4:
                        openKPIFragment();
                        break;
                    //General Questionary
                    case 5:
                        openGeneralQuestionnairesFragment();
                        break;
                    //Goods Questionary
                    case 6:
                        openGoodsQuestionnairesFragment();
                        break;
                    //Delivery
                    case 7:
                        openDeliverableOrdersFragment();
                        break;
                    //Camera
                    case 8:
                        startCameraActivity();
                        break;
                    //Reject
                    case 9:
                        showWantsDialog();
                        break;
                    //Exit
                    case 10:
                        finishVisiting();
                        break;

                }
            }


        });
    }

    private void openKPIFragment()
    {
        Bundle args = new Bundle();
        args.putLong(Constants.CUSTOMER_BACKEND_ID, customer.getBackendId());
        mainActivity.changeFragment(MainActivity.KPI_CUSTOMER_FRAGMENT_ID, args, false);
    }

    private void startCameraActivity()
    {
        try
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create a media file name
            String postfix = String.valueOf((new Date().getTime()) % 1000);
            fileUri = MediaUtil.getOutputMediaFileUri(MediaUtil.MEDIA_TYPE_IMAGE, Constants.CUSTOMER_PICTURE_DIRECTORY_NAME,
                    "IMG_" + customer.getCode() + "_" + postfix); // create a file to save the image
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
            if (intent.resolveActivity(getActivity().getPackageManager()) != null)
            {
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                Bitmap bitmap = ImageUtil.decodeSampledBitmapFromUri(getActivity(), fileUri);
                bitmap = ImageUtil.getScaledBitmap(getActivity(), bitmap);

                String s = ImageUtil.saveTempImage(bitmap, MediaUtil.getOutputMediaFile(MediaUtil.MEDIA_TYPE_IMAGE, Constants.CUSTOMER_PICTURE_DIRECTORY_NAME,
                        "IMG_" + customer.getCode() + "_" + new Date().getTime()));

                CustomerPic cPic = new CustomerPic();
                cPic.setTitle(s);
                cPic.setCustomer_backend_id(customer.getBackendId());

                if (Empty.isEmpty(cPic))
                {
                    toastError(R.string.message_error_in_loading_or_creating_customer);
                    mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_FRAGMENT_ID, true);
                } else
                {
                    ToastUtil.toastMessage(mainActivity, mainActivity.getString(R.string.message_picutre_saved_successfully));
                    customerService.savePicture(cPic);
                }
            } else if (resultCode == RESULT_CANCELED)
            {
                // User cancelled the image capture

            } else
            {
                // Image capture failed, advise user
            }

        }
    }

    /**
     * @param statusID Could be DRAFT for both AddFactor/AddOrder or REJECTED_DRAFT for ReturnedOrder
     */
    private void openOrderDetailFragment(Long statusID)
    {
        orderDto = saleOrderService.findOrderDtoByCustomerBackendIdAndStatus(customer.getBackendId(), statusID);
        if (Empty.isEmpty(orderDto) || statusID.equals(SaleOrderStatus.REJECTED_DRAFT.getId()))
        {
            orderDto = createDraftOrder(customer, statusID);
        }

        if (Empty.isNotEmpty(orderDto) && Empty.isNotEmpty(orderDto.getId()))
        {
            if (statusID.equals(SaleOrderStatus.REJECTED_DRAFT.getId()))
            {
                invokeGetRejectedData();
            } else
            {
                Bundle args = new Bundle();
                args.putLong("orderId", orderDto.getId());
                args.putString("saleType", saleType);
                mainActivity.changeFragment(MainActivity.ORDER_DETAIL_FRAGMENT_ID, args, false);
            }

        } else
        {
            if (statusID.equals(SaleOrderStatus.REJECTED_DRAFT.getId()))
            {
                ToastUtil.toastError(mainActivity, mainActivity.getString(R.string.message_cannot_create_rejected_right_now));
            } else if (statusID.equals(SaleOrderStatus.DRAFT.getId()) && saleType.equals(ApplicationKeys.COLD_SALE))
            {
                ToastUtil.toastError(mainActivity, mainActivity.getString(R.string.message_cannot_create_order_right_now));
            } else
            {
                ToastUtil.toastError(mainActivity, mainActivity.getString(R.string.message_cannot_create_factor_right_now));
            }
        }
    }

    private void invokeGetRejectedData()
    {

        //TODO: Progress bar
        //  dataTransferPB.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    DataTransferService dataTransferService = new DataTransferServiceImpl(mainActivity);
                    rejectedGoodsList = dataTransferService.getRejectedData(VisitDetailFragment.this, customer.getBackendId());
                    if (rejectedGoodsList != null)
                    {

                        final Bundle args = new Bundle();
                        args.putLong("orderId", orderDto.getId());
                        args.putString("saleType", saleType);
                        args.putSerializable("rejectedList", rejectedGoodsList);
                        mainActivity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mainActivity.changeFragment(MainActivity.ORDER_DETAIL_FRAGMENT_ID, args, false);
                            }
                        });
                    } else
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                toastError(getString(R.string.err_reject_order_not_possible));
                            }
                        });
                    }
                } catch (final BusinessException ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            toastError(ex);
                        }
                    });
                } catch (final Exception ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            toastError(new UnknownSystemException(ex));
                        }
                    });
                }
            }
        });

        thread.start();
    }

    private SaleOrderDto createDraftOrder(Customer customer, Long statusID)
    {
        try
        {
            SaleOrderDto orderDto = new SaleOrderDto();
            orderDto.setStatus(statusID);
            orderDto.setCustomer(customer);
            orderDto.setDate(DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER, "FA"));
            orderDto.setAmount(0L);
            orderDto.setCustomerBackendId(customer.getBackendId());
            orderDto.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
            orderDto.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
            Long id = saleOrderService.saveOrder(orderDto);
            orderDto.setId(id);
            return orderDto;
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(mainActivity, new UnknownSystemException(e));
        }
        return null;
    }

    private void openDeliverableOrdersFragment()
    {
        Bundle args = new Bundle();
        args.putLong("customerBackendId", customer.getBackendId());
        args.putLong("visitId", visitId);
        args.putLong("statusId", SaleOrderStatus.DELIVERABLE.getId());
        mainActivity.changeFragment(MainActivity.ORDERS_LIST_FRAGMENT, args, false);
    }

    private void openPaymentFragment()
    {
        Bundle args = new Bundle();
        args.putLong("customerId", customerId);
        args.putLong("visitId", visitId);
        mainActivity.changeFragment(MainActivity.PAYMENT_FRAGMENT_ID, args, false);
    }

    private void openSaveLocationFragment()
    {
        Bundle args = new Bundle();
        args.putLong("customerId", customerId);
        args.putLong("visitId", visitId);
        mainActivity.changeFragment(MainActivity.SAVE_LOCATION_FRAGMENT_ID, args, false);
    }

    private void openGeneralQuestionnairesFragment()
    {
        Bundle args = new Bundle();
        args.putLong("customerId", customerId);
        args.putLong("visitId", visitId);
        mainActivity.changeFragment(MainActivity.GENERAL_QUESTIONNAIRES_FRAGMENT_ID, args, false);
    }

    private void openGoodsQuestionnairesFragment()
    {
        Bundle args = new Bundle();
        args.putLong(Constants.CUSTOMER_ID, customerId);
        args.putLong(Constants.VISIT_ID, visitId);
        args.putInt(Constants.PARENT, getFragmentId());
        mainActivity.changeFragment(MainActivity.GOODS_QUESTIONNAIRES_FRAGMENT_ID, args, false);
    }

    private void finishVisiting()
    {
        try
        {
            VisitInformation visitInformation = customerService.getVisitInformationById(visitId);
            if (Empty.isEmpty(visitInformation.getxLocation()) || Empty.isEmpty(visitInformation.getyLocation()))
            {
                showDialogForEmptyLocation();
            } else
            {
                doFinishVisiting();
            }
        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            toastError(new UnknownSystemException(ex));
        }
    }

    private void showDialogForEmptyLocation()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.visit_location));
        builder.setMessage(getString(R.string.visit_empty_location_message));
        builder.setPositiveButton(getString(R.string.visit_empty_location_dialog_try_again), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
                tryFindingLocation();
            }
        });

        builder.setNegativeButton(getString(R.string.visit_empty_location_dialog_finish), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
                doFinishVisiting();
            }
        });

        builder.create().show();
    }

    private void tryFindingLocation()
    {
        try
        {

            final ProgressDialog progressDialog = new ProgressDialog(mainActivity);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(Boolean.FALSE);
            progressDialog.setIcon(R.drawable.ic_action_info);
            progressDialog.setTitle(R.string.message_please_wait);
            progressDialog.setMessage(getString(R.string.message_please_wait_finding_your_location));
            progressDialog.show();

            locationService.findCurrentLocation(new FindLocationListener()
            {
                @Override
                public void foundLocation(Location location)
                {
                    progressDialog.dismiss();
                    try
                    {
                        customerService.updateVisitLocation(visitId, location);
                    } catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage(), e);
                    }

                    mainActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showFoundLocationDialog();
                        }
                    });
                }

                @Override
                public void timeOut()
                {
                    progressDialog.dismiss();
                    mainActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            toastMessage(getString(R.string.visit_found_no_location));
                            showDialogForEmptyLocation();
                        }
                    });
                }
            });

        } catch (BusinessException ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            toastError(ex);
        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            toastError(new UnknownSystemException(ex));
        }
    }

    private void showFoundLocationDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.visit_location));
        builder.setMessage(getString(R.string.visit_found_location_message));
        builder.setPositiveButton(getString(R.string.finish_visit), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                doFinishVisiting();
            }
        });

        builder.create().show();
    }

    private void doFinishVisiting()
    {
        try
        {
            customerService.finishVisiting(visitId);
            Customer customer = customerService.getCustomerById(customerId);
            saleOrderService.deleteForAllCustomerOrdersByStatus(customer.getBackendId(), SaleOrderStatus.DRAFT.getId());
//            mainActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            mainActivity.removeFragment(this);
            mainActivity.setMenuEnabled(true);
        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
        }
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.VISIT_DETAIL_FRAGMENT_ID;
    }

    private void showWantsDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);

        List<LabelValue> wants = baseInfoService.getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.WANT_TYPE.getId());
        if (Empty.isEmpty(wants))
        {
            ToastUtil.toastMessage(mainActivity, mainActivity.getString(R.string.message_found_no_wants_information));
            return;
        }

        View wantsDialogView = mainActivity.getLayoutInflater().inflate(R.layout.dialog_wants, null);
        final Spinner wantsSpinner = (Spinner) wantsDialogView.findViewById(R.id.wantsSP);

        LabelValueArrayAdapter labelValueArrayAdapter = new LabelValueArrayAdapter(mainActivity, wants);
        wantsSpinner.setAdapter(labelValueArrayAdapter);

        dialogBuilder.setView(wantsDialogView);
        dialogBuilder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                LabelValue selectedItem = (LabelValue) wantsSpinner.getSelectedItem();
                updateVisitResult(selectedItem);
            }
        });
        dialogBuilder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        dialogBuilder.setTitle(R.string.title_save_want);
        dialogBuilder.create().show();
    }

    private void updateVisitResult(LabelValue selectedItem)
    {
        try
        {
            VisitInformation visit = customerService.getVisitInformationById(visitId);
            visit.setResult(selectedItem.getValue());
            customerService.saveVisit(visit);
        } catch (Exception ex)
        {
            ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    @Override
    public void publishResult(final BusinessException ex)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtil.toastError(mainActivity, getErrorString(ex));
            }
        });
    }

    @Override
    public void publishResult(final String message)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtil.toastMessage(mainActivity, message);
            }
        });
    }

    @Override
    public void finished(boolean result)
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (getView() == null)
        {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)
                {
                    finishVisiting();
                    return true;
                }
                return false;
            }
        });
    }

    private class MainLayoutAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return icons.length;
        }

        @Override
        public Object getItem(int position)
        {
            if (position == 0 && saleType.equals(ApplicationKeys.HOT_SALE))
            {
                return R.drawable.selector_add_factor;
            }
            return icons[position];
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LinearLayout layout = new LinearLayout(mainActivity);
            layout.setLayoutParams(new GridView.LayoutParams(100, 100));
            layout.setGravity(Gravity.CENTER);
//            layout.setBackgroundResource(R.drawable.visit_row_layout_background);

            ImageView imageView = new ImageView(mainActivity);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (position == 0 && ApplicationKeys.HOT_SALE.equals(saleType))
            {
                imageView.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.selector_add_factor));
            } else
            {
                imageView.setImageDrawable(mainActivity.getResources().getDrawable(icons[position]));
            }

            layout.addView(imageView);
            return layout;
        }
    }
}
