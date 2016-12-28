package com.conta.comer.ui.observer;

import com.conta.comer.exception.ContaBusinessException;

/**
 * Created by Mahyar on 6/18/2015.
 */
public interface ResultObserver
{

    void publishResult(ContaBusinessException ex);

    void publishResult(String message);

    void finished(boolean result);
}
