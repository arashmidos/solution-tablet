package com.parsroyal.storemanagement.ui.observer;

import com.parsroyal.storemanagement.exception.BusinessException;

/**
 * Created by Mahyar on 6/18/2015.
 */
public interface ResultObserver {

  void publishResult(BusinessException ex);

  void finished(boolean result);
}
