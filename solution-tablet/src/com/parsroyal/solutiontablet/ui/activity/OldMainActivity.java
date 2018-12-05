package com.parsroyal.solutiontablet.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.event.UpdateEvent;
import com.parsroyal.solutiontablet.service.DataTransferService;
import com.parsroyal.solutiontablet.service.LocationUpdatesService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.fragment.AboutUsFragment;
import com.parsroyal.solutiontablet.ui.fragment.BaseFragment;
import com.parsroyal.solutiontablet.ui.fragment.GeneralQuestionnairesFragment;
import com.parsroyal.solutiontablet.ui.fragment.GoodsListForQuestionnairesFragment;
import com.parsroyal.solutiontablet.ui.fragment.GoodsQuestionnairesFragment;
import com.parsroyal.solutiontablet.ui.fragment.OldGoodsListFragment;
import com.parsroyal.solutiontablet.ui.fragment.OrderDetailFragment;
import com.parsroyal.solutiontablet.ui.fragment.QuestionnairesListFragment;
import com.parsroyal.solutiontablet.ui.fragment.SaveLocationFragment;
import com.parsroyal.solutiontablet.ui.fragment.UserTrackingFragment;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Mahyar on 6/2/2015.
 */
public class OldMainActivity extends AppCompatActivity {

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
  public static final int QUESTIONAIRE_LIST_FRAGMENT_ID = 26;
  private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

  @BindView(R.id.mainLayout)
  LinearLayout mainLayout;
  private DrawerLayout mDrawerLayout;
  private ListView drawerItemsList;
  private ImageView customerListTabIv;
  private ImageView newCustomerTabIv;
  private ImageView dashBoardTabIv;
  private ImageView ordersTabIv;
  private ImageView goodsTabIv;
  private ImageView userPerformanceTabIv;
  private ImageView fundsTabIv;
  private ImageView questionaireTabIv;
  private DataTransferService dataTransferService;
  private SettingService settingService;
  private boolean isMenuEnabled = true;
  private LocationUpdatesService gpsRecieverService = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dataTransferService = new DataTransferServiceImpl(this);
    settingService = new SettingServiceImpl(this);
    setContentView(R.layout.activity_main_old);
    ButterKnife.bind(this);

    setupSidebar();
    initialize();

  }

  private void initialize() {
    changeFragment(NEW_CUSTOMER_FRAGMENT_ID, false);
  }

  private void setupSidebar() {
    View.OnClickListener sideBarItemsOnClickListener = view ->
    {
      if (isMenuEnabled || BuildConfig.DEBUG) {
        int fragmentId = Integer.parseInt(view.getTag().toString());
        changeFragment(fragmentId, true);
      }
    };
    customerListTabIv = findViewById(R.id.customerListTabIv);
    customerListTabIv.setOnClickListener(sideBarItemsOnClickListener);
    newCustomerTabIv = findViewById(R.id.newCustomerTabIv);
    newCustomerTabIv.setOnClickListener(sideBarItemsOnClickListener);
    dashBoardTabIv = findViewById(R.id.dashboardTabIv);
    dashBoardTabIv.setOnClickListener(sideBarItemsOnClickListener);
    ordersTabIv = findViewById(R.id.ordersTabIv);
    ordersTabIv.setOnClickListener(sideBarItemsOnClickListener);
    goodsTabIv = findViewById(R.id.goodsTabIv);
    goodsTabIv.setOnClickListener(sideBarItemsOnClickListener);
    userPerformanceTabIv = findViewById(R.id.userPerformanceTabIv);
    userPerformanceTabIv.setOnClickListener(sideBarItemsOnClickListener);
    fundsTabIv = findViewById(R.id.fundsTabIv);
    fundsTabIv.setOnClickListener(sideBarItemsOnClickListener);
    questionaireTabIv = findViewById(R.id.questionaryTabIv);
    questionaireTabIv.setOnClickListener(sideBarItemsOnClickListener);
  }

  public void changeSidebarItem(int fragmentId) {
    String contentName = "";
    switch (fragmentId) {
      case 0:
//        customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list_active);
        contentName = "Customer List";
        break;
      case 1:
//        newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer_active);
        contentName = "New Customer";
        break;
      case 12:
//        dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_active);
        contentName = "Map";
        break;
      case 14:
//        ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_active);
        contentName = "Reports";
        break;
      case 16:
//        goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods_active);
        contentName = "Goods List";
        break;
      case KPI_SALESMAN_FRAGMENT_ID://22
//        userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance_active);
        contentName = "User KPI";
        break;
      case FUNDS_FRAGMENT_ID://23
//        fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report_active);
        contentName = "Payment Reports";
        break;
      case QUESTIONAIRE_LIST_FRAGMENT_ID://26
//        questionaireTabIv.setImageResource(R.drawable.ic_sidebar_questionaire_active);
        contentName = "New Questionnaire";
        break;
    }
    Analytics.logContentView(contentName);
  }

  public void changeFragment(int fragmentId, boolean addToBackStack) {
    BaseFragment fragment = findFragment(fragmentId, new Bundle());
    if (Empty.isNotEmpty(fragment) && !fragment.isVisible()) {
      commitFragment(fragment.getFragmentTag(), fragment, addToBackStack);
    }
  }

  public void changeFragment(int fragmentId, Bundle args, boolean addToBackStack) {
    BaseFragment fragment = findFragment(fragmentId, args);
    if (Empty.isNotEmpty(fragment) && !fragment.isVisible()) {
      if (Empty.isNotEmpty(args)) {
        fragment.setArguments(args);
      }
      commitFragment(fragment.getFragmentTag(), fragment, addToBackStack);
    }
  }

  private void commitFragment(String fragmentTag, BaseFragment fragment, boolean addToBackStack) {
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.contentFrame, fragment, fragmentTag);
    fragmentTransaction.addToBackStack(fragmentTag);
    fragmentTransaction.commit();
  }

  private BaseFragment findFragment(int fragmentId, Bundle args) {
    BaseFragment fragment = null;
    int parent = 0;
    switch (fragmentId) {
      case CUSTOMER_LIST_FRAGMENT_ID:
//        fragment = new VisitLinesFragment();
        break;
      case NEW_CUSTOMER_FRAGMENT_ID:
//        fragment = new NCustomersFragment();
        changeSidebarItem(OldMainActivity.NEW_CUSTOMER_FRAGMENT_ID);
        break;
      case NEW_CUSTOMER_DETAIL_FRAGMENT_ID:
//        fragment = new NCustomerDetailFragment();
        changeSidebarItem(OldMainActivity.NEW_CUSTOMER_FRAGMENT_ID);
        break;
      case CUSTOMERS_FRAGMENT_ID:
//        fragment = new CustomersFragment();
        changeSidebarItem(OldMainActivity.CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case CUSTOMER_DETAIL_FRAGMENT_ID:
//        fragment = new CustomerDetailFragment();
        changeSidebarItem(OldMainActivity.CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case VISIT_DETAIL_FRAGMENT_ID:
//        fragment = new OldVisitDetailFragment();
        changeSidebarItem(OldMainActivity.CUSTOMER_LIST_FRAGMENT_ID);
        setMenuEnabled(false);
        break;
      case GENERAL_QUESTIONNAIRES_FRAGMENT_ID://6
        fragment = new GeneralQuestionnairesFragment();
        parent = args.getInt(Constants.PARENT, 0);
        changeSidebarItem(parent);
        break;
      case QUESTIONNAIRE_DETAIL_FRAGMENT_ID:
//        fragment = new QuestionnaireDetailFragment();
        parent = args.getInt(Constants.PARENT);
        changeSidebarItem(parent);
        break;
      case GOODS_QUESTIONNAIRES_FRAGMENT_ID://8
        fragment = new GoodsQuestionnairesFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case GOODS_LIST_FOR_QUESTIONNAIRES_FRAGMENT_ID:
        fragment = new GoodsListForQuestionnairesFragment();
        break;
      case SETTING_FRAGMENT_ID:
//        fragment = new SettingFragment();
        changeSidebarItem(-1);
        break;
      case DATA_TRANSFER_FRAGMENT_ID:
//        if (/()) {
//          fragment = new DataTransferFragment();
        changeSidebarItem(-1);
//        }
        break;
      case DASHBOARD_FRAGMENT_ID:
        fragment = new UserTrackingFragment();
        changeSidebarItem(DASHBOARD_FRAGMENT_ID);
        break;
      case ABOUT_US_FRAGMENT_ID:
        fragment = new AboutUsFragment();
        changeSidebarItem(-1);
        break;
      case ORDERS_LIST_FRAGMENT:
//        fragment = new OldOrdersListFragment();
        changeSidebarItem(ORDERS_LIST_FRAGMENT);
        break;
      case ORDER_DETAIL_FRAGMENT_ID:
        fragment = new OrderDetailFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case GOODS_LIST_FRAGMENT_ID:
        fragment = new OldGoodsListFragment();
        args.putBoolean("view_all", true);
        fragment.setArguments(args);
        changeSidebarItem(GOODS_LIST_FRAGMENT_ID);
        break;
      case SAVE_LOCATION_FRAGMENT_ID:
        fragment = new SaveLocationFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case PAYMENT_FRAGMENT_ID://18
//        fragment = new PaymentFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case PAYMENT_DETAIL_FRAGMENT_ID://19
//        fragment = new PaymentDetailFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case USER_TRACKING_FRAGMENT_ID://20
        fragment = new UserTrackingFragment();
        changeSidebarItem(-1);
        break;
      case KPI_CUSTOMER_FRAGMENT_ID: //21
//        fragment = new OldKPIFragment();
//        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case KPI_SALESMAN_FRAGMENT_ID: //22
//        fragment = new OldKPIFragment();
//        changeSidebarItem(KPI_SALESMAN_FRAGMENT_ID);
        break;
      case FUNDS_FRAGMENT_ID: //23
//        fragment = new PaymentFragment();
        changeSidebarItem(FUNDS_FRAGMENT_ID);
        break;
      case QUESTIONAIRE_LIST_FRAGMENT_ID:
        fragment = new QuestionnairesListFragment();
        if (Empty.isNotEmpty(args)) {
          fragment.setArguments(args);
        }
        changeSidebarItem(args.getInt(Constants.PARENT, QUESTIONAIRE_LIST_FRAGMENT_ID));

    }
    Analytics.logContentView("Fragment " + String.valueOf(fragmentId));
    return fragment;
  }

  @Override
  public void onBackPressed() {
    try {
      FragmentManager supportFragmentManager = getSupportFragmentManager();

      if (supportFragmentManager.getBackStackEntryCount() > 1) {
        FragmentManager.BackStackEntry backStackEntryAt = supportFragmentManager
            .getBackStackEntryAt(supportFragmentManager.getBackStackEntryCount() - 2);
        String tag = backStackEntryAt.getName();
        BaseFragment lastFragment = (BaseFragment) supportFragmentManager.findFragmentByTag(tag);
        if (Empty.isNotEmpty(lastFragment)) {
          findFragment(lastFragment.getFragmentId(), new Bundle());
        }
        super.onBackPressed();
      } else {
//        showDialogForExit();
      }
    } catch (Exception e) {
      Logger.sendError("UI Exception", "Error in backPressed " + e.getMessage());
    }
  }

  public void openDrawer() {
    mDrawerLayout.openDrawer(Gravity.LEFT);
  }

  public void closeDrawer() {
    mDrawerLayout.closeDrawer(Gravity.LEFT);
  }

  public void removeFragment(Fragment fragment) {
    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction trans = manager.beginTransaction();
    trans.remove(fragment);
    trans.commit();
    manager.popBackStack();
  }

  public boolean isMenuEnabled() {
    return isMenuEnabled;
  }

  public void setMenuEnabled(boolean menuEnabled) {
    isMenuEnabled = menuEnabled;
  }

  @Subscribe
  public void getMessage(UpdateEvent event) {
    installNewVersion();
  }

  private void installNewVersion() {
  }
}

