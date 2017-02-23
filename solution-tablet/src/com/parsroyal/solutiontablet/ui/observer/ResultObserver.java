package com.parsroyal.solutiontablet.ui.observer;

import com.parsroyal.solutiontablet.exception.BusinessException;

/**
 * Created by Mahyar on 6/18/2015.
 */
public interface ResultObserver
{

    void publishResult(BusinessException ex);

    void publishResult(String message);

    void finished(boolean result);
}
