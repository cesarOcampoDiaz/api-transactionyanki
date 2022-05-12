package com.nttdata.message;

import com.nttdata.document.Transaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class KafkaSenderImpl  implements  KafkaSender{

    private  final KafkaTemplate<String, Transaction> kafkaTemplate;

    public KafkaSenderImpl(@Qualifier("kafkaJsonTemplate") KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sedMessage(String topic, Transaction transaction) {

        ListenableFuture<SendResult<String,Transaction>> future = this.kafkaTemplate.send(topic,transaction);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Transaction>>() {
            @Override
            public void onFailure(Throwable ex) {
                ex.printStackTrace();
            }

            @Override
            public void onSuccess(SendResult<String, Transaction> result) {
                System.out.println("send "+result.getProducerRecord().value().getPhone()+" topic "+result.getRecordMetadata().topic());
            }
        });



        /*kafkaTemplate.send(topic,transaction).addCallback(new ListenableFutureCallback<SendResult<String,Transaction>>() {


            @Override
            public void onSuccess(SendResult<String, Transaction> result) {
                System.out.println("send "+result.getProducerRecord().value().getId()+" topic "+result.getRecordMetadata().topic());
            }

            @Override
            public void onFailure(Throwable ex) {
                ex.printStackTrace();
            }


        });*/

    }
}
