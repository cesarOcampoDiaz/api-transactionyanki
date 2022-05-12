package com.nttdata.message;

import com.nttdata.document.Transaction;

public interface KafkaSender {

    public void sedMessage(String topic, Transaction transaction) ;
}
