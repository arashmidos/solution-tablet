package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.model.User;
import com.parsroyal.solutiontablet.data.model.UserInformationRequest;
import com.parsroyal.solutiontablet.exception.GotNoResponseFromBackendException;
import com.parsroyal.solutiontablet.exception.InvalidUserCodeException;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 6/18/2015.
 */
public class UserInformationDataTransferBizImpl extends AbstractDataTransferBizImpl<User> {

  private ResultObserver observer;
  private KeyValueBiz keyValueBiz;
  private KeyValue userCode;
  private KeyValue type;

  public UserInformationDataTransferBizImpl(Context context, ResultObserver observer) {
    super(context);
    this.observer = observer;
    this.keyValueBiz = new KeyValueBizImpl(context);
  }

  @Override
  public void receiveData(User data) {
    if (Empty.isNotEmpty(data)) {
      keyValueBiz.save(new KeyValue(ApplicationKeys.SALESMAN_ID, data.getSalesmanId()));
      keyValueBiz.save(new KeyValue(ApplicationKeys.USER_FULL_NAME,
          CharacterFixUtil.fixString(data.getFullName())));
      keyValueBiz.save(new KeyValue(ApplicationKeys.USER_COMPANY_NAME,
          CharacterFixUtil.fixString(data.getCompanyName())));
      keyValueBiz
          .save(new KeyValue(ApplicationKeys.USER_COMPANY_ID, String.valueOf(data.getCompanyId())));
    } else {
      getObserver().publishResult(new GotNoResponseFromBackendException());
    }
  }

  @Override
  public void beforeTransfer() {
    userCode = keyValueBiz.findByKey(ApplicationKeys.SETTING_USER_CODE);
    type = keyValueBiz.findByKey(ApplicationKeys.SETTING_SALE_TYPE);
    if (Empty.isEmpty(userCode)) {
      getObserver().publishResult(new InvalidUserCodeException());
    }
  }

  @Override
  public ResultObserver getObserver() {
    return observer;
  }

  @Override
  public String getMethod() {
    return "user/detail";
  }

  @Override
  public Class getType() {
    return User.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.POST;
  }

  @Override
  protected MediaType getContentType() {
    return MediaType.APPLICATION_JSON;
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    headers
        .add("branchCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_BRANCH_CODE).getValue());
    headers.add("stockCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_STOCK_CODE).getValue());

    UserInformationRequest request = new UserInformationRequest(userCode.getValue(),
        type.getValue());
    HttpEntity<UserInformationRequest> entity = new HttpEntity<>(request, headers);
    return entity;
  }
}
