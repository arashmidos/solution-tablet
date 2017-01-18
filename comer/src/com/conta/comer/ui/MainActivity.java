package com.conta.comer.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.conta.comer.BuildConfig;
import com.conta.comer.R;
import com.conta.comer.receiver.TrackerAlarmReceiver;
import com.conta.comer.service.DataTransferService;
import com.conta.comer.service.SettingService;
import com.conta.comer.service.impl.DataTransferServiceImpl;
import com.conta.comer.service.impl.SettingServiceImpl;
import com.conta.comer.ui.adapter.DrawerArrayAdapter;
import com.conta.comer.ui.fragment.AboutUsFragment;
import com.conta.comer.ui.fragment.BaseContaFragment;
import com.conta.comer.ui.fragment.CustomerDetailFragment;
import com.conta.comer.ui.fragment.CustomersFragment;
import com.conta.comer.ui.fragment.DataTransferFragment;
import com.conta.comer.ui.fragment.GeneralQuestionnairesFragment;
import com.conta.comer.ui.fragment.GoodsListForQuestionnairesFragment;
import com.conta.comer.ui.fragment.GoodsListFragment;
import com.conta.comer.ui.fragment.GoodsQuestionnairesFragment;
import com.conta.comer.ui.fragment.KPIFragment;
import com.conta.comer.ui.fragment.NCustomerDetailFragment;
import com.conta.comer.ui.fragment.NCustomersFragment;
import com.conta.comer.ui.fragment.OrderDetailFragment;
import com.conta.comer.ui.fragment.OrdersListFragment;
import com.conta.comer.ui.fragment.PaymentDetailFragment;
import com.conta.comer.ui.fragment.PaymentFragment;
import com.conta.comer.ui.fragment.QuestionnaireDetailFragment;
import com.conta.comer.ui.fragment.SaveLocationFragment;
import com.conta.comer.ui.fragment.SettingFragment;
import com.conta.comer.ui.fragment.UserTrackingFragment;
import com.conta.comer.ui.fragment.VisitDetailFragment;
import com.conta.comer.ui.fragment.VisitLinesFragment;
import com.conta.comer.ui.fragment.dialog.LoginDialogFragment;
import com.conta.comer.util.DialogUtil;
import com.conta.comer.util.Empty;
import com.conta.comer.util.GPSUtil;
import com.conta.comer.util.NetworkUtil;
import com.conta.comer.util.constants.ApplicationKeys;

/**
 * Created by Mahyar on 6/2/2015.
 */
public class MainActivity extends BaseContaFragmentActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int CUSTOMER_LIST_FRAGMENT_ID = 0;
    public static final int NEW_CUSTOMER_FRAGMENT_ID = 1;
    public static final int NEW_CUSTOMER_DETAIL_FRAGMENT_ID = 2;
    public static final int CUSTOMERS_FRAGMENT_ID = 3;
    public static final int CUSTOMER_DETAIL_FRAGMENT_ID = 4;
    public static final int VISIT_DETAIL_FRAGMENT_ID = 5;
    public static final int GENERAL_QUESTIONNAIRES_FRAGMENT_ID = 6;
    public static final int QUESTIONNAIRE_DETAIL_FRAGMENT_ID = 7;
    public static final int GOODS_QUESTIONNAIRES_FRAGMENT_ID = 8;
    public static final int GOODS_LIST_FOR_QUESTIONNAIRES_FRAGMENT_ID = 9;
    public static final int SETTING_FRAGMENT_ID = 10;
    public static final int DATA_TRANSFER_FRAGMENT_ID = 11;
    public static final int DASHBOARD_FRAGMENT_ID = 12;
    public static final int ABOUT_US_FRAGMENT_ID = 13;
    public static final int ORDERS_LIST_FRAGMENT = 14;
    public static final int ORDER_DETAIL_FRAGMENT_ID = 15;
    public static final int GOODS_LIST_FRAGMENT_ID = 16;
    public static final int SAVE_LOCATION_FRAGMENT_ID = 17;
    public static final int PAYMENT_FRAGMENT_ID = 18;
    public static final int PAYMENT_DETAIL_FRAGMENT_ID = 19;
    public static final int USER_TRACKING_FRAGMENT_ID = 20;
    public static final int KPI_CUSTOMER_FRAGMENT_ID = 21;
    public static final int KPI_SALESMAN_FRAGMENT_ID = 22;
    public static final int FUNDS_FRAGMENT_ID = 23;
    public static final int CUSTOMER_TRACKING_FRAGMENT_ID = 24;
    public static final int BASE_TRACKING_FRAGMENT_ID = 25;

    private final Integer[] drawerItemTitles = {
            R.string.setting,
            R.string.data_transfer,
            R.string.about_us,
            R.string.version,
            R.string.exit
    };
    private final Integer[] drawerItemImages = {
            R.drawable.setting,
            R.drawable.aboutus,
            R.drawable.aboutus,
            R.drawable.version,
            R.drawable.exit,
    };
    private DrawerLayout mDrawerLayout;
    private ListView drawerItemsList;
    private ImageView customerListTabIv;
    private ImageView newCustomerTabIv;
    private ImageView dashBoardTabIv;
    private ImageView ordersTabIv;
    private ImageView goodsTabIv;
    private ImageView userPerformanceTabIv;
    private ImageView fundsTabIv;
    private DataTransferService dataTransferService;
    private SettingService settingService;
    private boolean isMenuEnabled = true;
    private BroadcastReceiver gpsReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
            {
                if (!GPSUtil.isGpsAvailable(context))
                {
                    showGpsOffDialog();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dataTransferService = new DataTransferServiceImpl(this);
        settingService = new SettingServiceImpl(this);
        setContentView(R.layout.activity_main);
        setupSidebar();
        setupActionbar();
        setupDrawer();
        initialize();
    }

    private void initialize()
    {
        changeFragment(NEW_CUSTOMER_FRAGMENT_ID, false);
    }

    private void setupSidebar()
    {
        View.OnClickListener sideBarItemsOnClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (isMenuEnabled)
                {
                    int fragmentId = Integer.parseInt(view.getTag().toString());
                    changeFragment(fragmentId, true);
                }
            }
        };
        customerListTabIv = (ImageView) findViewById(R.id.customerListTabIv);
        customerListTabIv.setOnClickListener(sideBarItemsOnClickListener);
        newCustomerTabIv = (ImageView) findViewById(R.id.newCustomerTabIv);
        newCustomerTabIv.setOnClickListener(sideBarItemsOnClickListener);
        dashBoardTabIv = (ImageView) findViewById(R.id.dashboardTabIv);
        dashBoardTabIv.setOnClickListener(sideBarItemsOnClickListener);
        ordersTabIv = (ImageView) findViewById(R.id.ordersTabIv);
        ordersTabIv.setOnClickListener(sideBarItemsOnClickListener);
        goodsTabIv = (ImageView) findViewById(R.id.goodsTabIv);
        goodsTabIv.setOnClickListener(sideBarItemsOnClickListener);
        userPerformanceTabIv = (ImageView) findViewById(R.id.userPerformanceTabIv);
        userPerformanceTabIv.setOnClickListener(sideBarItemsOnClickListener);
        fundsTabIv = (ImageView) findViewById(R.id.fundsTabIv);
        fundsTabIv.setOnClickListener(sideBarItemsOnClickListener);
    }

    public void changeSidebarItem(int fragmentId)
    {
        switch (fragmentId)
        {
            case 0:
                customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list_active);
                newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer);
                dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_inactive);
                ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_inactive);
                goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods);
                userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance);
                fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report);
                break;
            case 1:
                customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list);
                dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_inactive);
                newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer_active);
                ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_inactive);
                goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods);
                userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance);
                fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report);
                break;
            case 12:
                dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_active);
                customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list);
                newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer);
                ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_inactive);
                goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods);
                userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance);
                fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report);
                break;
            case 14:
                dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_inactive);
                customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list);
                newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer);
                ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_active);
                goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods);
                userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance);
                fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report);
                break;
            case 16:
                goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods_active);
                dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_inactive);
                customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list);
                newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer);
                ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_inactive);
                userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance);
                fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report);
                break;
            case KPI_SALESMAN_FRAGMENT_ID://22
                dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_inactive);
                customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list);
                newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer);
                ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_inactive);
                goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods);
                userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance_active);
                fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report);
                break;
            case FUNDS_FRAGMENT_ID://23
                dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_inactive);
                customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list);
                newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer);
                ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_inactive);
                goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods);
                userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance);
                fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report_active);
                break;
            default:
                customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list);
                newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer);
                dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_inactive);
                ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_inactive);
                goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods);
                userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance);
                fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report);
                break;
        }
    }

    public void changeFragment(int fragmentId, boolean addToBackStack)
    {
        BaseContaFragment fragment = findFragment(fragmentId);
        if (Empty.isNotEmpty(fragment) && !fragment.isVisible())
        {
            commitFragment(fragment.getFragmentTag(), fragment, addToBackStack);
        }
    }

    public void changeFragment(int fragmentId, Bundle args, boolean addToBackStack)
    {
        BaseContaFragment fragment = findFragment(fragmentId);
        if (Empty.isNotEmpty(fragment) && !fragment.isVisible())
        {
            if (Empty.isNotEmpty(args))
            {
                fragment.setArguments(args);
            }
            commitFragment(fragment.getFragmentTag(), fragment, addToBackStack);
        }
    }

    private void commitFragment(String fragmentTag, BaseContaFragment fragment, boolean addToBackStack)
    {
        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contentFrame, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commit();
    }

    private BaseContaFragment findFragment(int fragmentId)
    {
        BaseContaFragment fragment = null;
        switch (fragmentId)
        {
            case CUSTOMER_LIST_FRAGMENT_ID:
                fragment = new VisitLinesFragment();
                break;
            case NEW_CUSTOMER_FRAGMENT_ID:
                fragment = new NCustomersFragment();
                changeSidebarItem(MainActivity.NEW_CUSTOMER_FRAGMENT_ID);
                break;
            case NEW_CUSTOMER_DETAIL_FRAGMENT_ID:
                fragment = new NCustomerDetailFragment();
                changeSidebarItem(MainActivity.NEW_CUSTOMER_FRAGMENT_ID);
                break;
            case CUSTOMERS_FRAGMENT_ID:
                fragment = new CustomersFragment();
                changeSidebarItem(MainActivity.CUSTOMER_LIST_FRAGMENT_ID);
                break;
            case CUSTOMER_DETAIL_FRAGMENT_ID:
                fragment = new CustomerDetailFragment();
                changeSidebarItem(MainActivity.CUSTOMER_LIST_FRAGMENT_ID);
                break;
            case VISIT_DETAIL_FRAGMENT_ID:
                fragment = new VisitDetailFragment();
                changeSidebarItem(MainActivity.CUSTOMER_LIST_FRAGMENT_ID);
                setMenuEnabled(false);
                break;
            case GENERAL_QUESTIONNAIRES_FRAGMENT_ID://6
                fragment = new GeneralQuestionnairesFragment();
                changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
                break;
            case QUESTIONNAIRE_DETAIL_FRAGMENT_ID:
                fragment = new QuestionnaireDetailFragment();
                changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
                break;
            case GOODS_QUESTIONNAIRES_FRAGMENT_ID://8
                fragment = new GoodsQuestionnairesFragment();
                changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
                break;
            case GOODS_LIST_FOR_QUESTIONNAIRES_FRAGMENT_ID:
                fragment = new GoodsListForQuestionnairesFragment();
                break;
            case SETTING_FRAGMENT_ID:
                fragment = new SettingFragment();
                changeSidebarItem(-1);
                break;
            case DATA_TRANSFER_FRAGMENT_ID:
                if (isDataTransferPossible())
                {
                    fragment = new DataTransferFragment();
                    changeSidebarItem(-1);
                }
                break;
            case DASHBOARD_FRAGMENT_ID:
//                fragment = new DashBoardFragment();
//                fragment = new TrackingBaseFragment();
                fragment = new UserTrackingFragment();
                changeSidebarItem(DASHBOARD_FRAGMENT_ID);
                break;
            case ABOUT_US_FRAGMENT_ID:
                fragment = new AboutUsFragment();
                changeSidebarItem(-1);
                break;
            case ORDERS_LIST_FRAGMENT:
                fragment = new OrdersListFragment();
                changeSidebarItem(ORDERS_LIST_FRAGMENT);
                break;
            case ORDER_DETAIL_FRAGMENT_ID:
                fragment = new OrderDetailFragment();
                changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
                break;
            case GOODS_LIST_FRAGMENT_ID:
                fragment = new GoodsListFragment();
                Bundle args = new Bundle();
                args.putBoolean("view_all", true);
                fragment.setArguments(args);
                changeSidebarItem(GOODS_LIST_FRAGMENT_ID);
                break;
            case SAVE_LOCATION_FRAGMENT_ID:
                fragment = new SaveLocationFragment();
                changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
                break;
            case PAYMENT_FRAGMENT_ID://18
                fragment = new PaymentFragment();
                changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
                break;
            case PAYMENT_DETAIL_FRAGMENT_ID://19
                fragment = new PaymentDetailFragment();
                changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
                break;
            case USER_TRACKING_FRAGMENT_ID://20
                fragment = new UserTrackingFragment();
                //TODO: need update
                changeSidebarItem(-1);
                break;
            case KPI_CUSTOMER_FRAGMENT_ID: //21
                fragment = new KPIFragment();
                changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
                break;
            case KPI_SALESMAN_FRAGMENT_ID: //22
                fragment = new KPIFragment();
                changeSidebarItem(KPI_SALESMAN_FRAGMENT_ID);
                break;
            case FUNDS_FRAGMENT_ID: //23
                fragment = new PaymentFragment();
                changeSidebarItem(FUNDS_FRAGMENT_ID);
                break;

        }
        return fragment;
    }

    private boolean isDataTransferPossible()
    {
        boolean check1 = dataTransferService.isDataTransferPossible();
        boolean check2 = NetworkUtil.isNetworkAvailable(this);

        if (!check1)
        {
            toastMessage(R.string.message_required_setting_for_data_transfer_not_found);
        }

        if (!check2)
        {
            toastMessage(R.string.message_device_does_not_have_active_internet_connection);
        }

        return check1 && check2;
    }

    private void setupDrawer()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        DrawerArrayAdapter adapter = new DrawerArrayAdapter(MainActivity.this, drawerItemTitles, drawerItemImages);
        drawerItemsList = (ListView) findViewById(R.id.left_drawer);

        drawerItemsList.setAdapter(adapter);
        drawerItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        if (Empty.isNotEmpty(settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME)) &&
                                Empty.isNotEmpty(settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME)) &&
                                !BuildConfig.DEBUG)
                        {
                            settingLoginDialog();
                            closeDrawer();
                        } else
                        {
                            changeFragment(SETTING_FRAGMENT_ID, false);
                            closeDrawer();

                        }
                        break;
                    case 1:
                        changeFragment(DATA_TRANSFER_FRAGMENT_ID, false);
                        closeDrawer();
                        break;
                    case 2:
                        changeFragment(ABOUT_US_FRAGMENT_ID, false);
                        closeDrawer();
                        break;
                    case 3:
                        showVersionDialog();
                        break;
                    case 4:
                        showDialogForExit();
                        break;
                }
            }
        });

        menuIv.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (isMenuEnabled)
                {
                    if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
                    {
                        closeDrawer();
                    } else
                    {
                        openDrawer();
                    }
                }
            }
        });
    }

    private void showVersionDialog()
    {
        DialogUtil.showMessageDialog(this, getString(R.string.version),
                String.format(getString(R.string.your_version), BuildConfig.VERSION_NAME));
    }

    private void settingLoginDialog()
    {
        DialogFragment loginDialog = LoginDialogFragment.newInstance();
        loginDialog.show(getSupportFragmentManager(), "login_dialog");
    }

    @Override
    public void onBackPressed()
    {
        try
        {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            if (supportFragmentManager.getBackStackEntryCount() > 1)
            {
                FragmentManager.BackStackEntry backStackEntryAt = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.getBackStackEntryCount() - 2);
                String tag = backStackEntryAt.getName();
                BaseContaFragment lastFragment = (BaseContaFragment) supportFragmentManager.findFragmentByTag(tag);
                if (Empty.isNotEmpty(lastFragment))
                {
                    findFragment(lastFragment.getFragmentId());
                }
                super.onBackPressed();
            } else
            {
                showDialogForExit();
            }
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    protected void showDialogForExit()
    {
        DialogUtil.showConfirmDialog(this, getString(R.string.message_exit), getString(R.string.message_do_you_want_to_exit),
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        finish();
                    }
                });
    }

    public void openDrawer()
    {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void closeDrawer()
    {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void removeFragment(Fragment fragment)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(fragment);
        trans.commit();
        manager.popBackStack();
    }

    public boolean isMenuEnabled()
    {
        return isMenuEnabled;
    }

    public void setMenuEnabled(boolean menuEnabled)
    {
        isMenuEnabled = menuEnabled;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (!GPSUtil.isGpsAvailable(this))
        {
            showGpsOffDialog();
        }

        registerReceiver(gpsReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
        new TrackerAlarmReceiver().setAlarm(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(gpsReceiver);
    }

    private void showGpsOffDialog()
    {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_gps_is_disabled))

                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                })
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .create();
        dialog.show();
    }
}

