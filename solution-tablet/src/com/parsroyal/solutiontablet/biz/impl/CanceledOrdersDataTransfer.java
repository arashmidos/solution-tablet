package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.SaleOrderDao;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderDaoImpl;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.model.BaseSaleDocument;
import com.parsroyal.solutiontablet.data.model.SaleInvoiceDocument;
import com.parsroyal.solutiontablet.service.PostDataRestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.io.IOException;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Arash on 28/12/2017
 */
public class CanceledOrdersDataTransfer {

  private final SaleOrderDao saleOrderDao;
  protected BaseSaleDocument order;
  protected VisitService visitService;
  protected int success = 0;
  protected int total = 0;
  private Context context;

  public CanceledOrdersDataTransfer(Context context) {
    this.context = context;
    this.saleOrderDao = new SaleOrderDaoImpl(context);
  }

  public String getSuccessfulMessage() {
    return NumberUtil.digitsToPersian(String
        .format(Locale.getDefault(), context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success)));
  }

  public int getSuccess() {
    return success;
  }

  public BaseSaleDocument getOrder() {
    return order;
  }

  public void setOrder(BaseSaleDocument order) {
    this.order = order;
    this.total++;
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    PostDataRestService restService = ServiceGenerator.createService(PostDataRestService.class);

    SaleInvoiceDocument invoiceDocument = (SaleInvoiceDocument) this.order;
    Call<String> call = restService
        .cancelOrders(invoiceDocument.getSaleOrderId(), invoiceDocument.getRejectType());

    try {
      Response<String> response = call.execute();
      if (response.isSuccessful()) {
        String cancelResponse = response.body();
        if (Empty.isNotEmpty(cancelResponse) && "1".equals(cancelResponse)) {
          SaleOrder saleOrder = saleOrderDao.retrieve(this.order.getId());
          saleOrder.setStatus(SaleOrderStatus.DELIVERABLE_SENT.getId());
          saleOrderDao.update(saleOrder);
          success++;
//            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.SUCCESS));
        } else {
//            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.NO_DATA_ERROR));
        }
      } else {
        try {
          Log.d("TAG", response.errorBody().string());
        } catch (IOException e) {
          e.printStackTrace();
        }
//          EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
