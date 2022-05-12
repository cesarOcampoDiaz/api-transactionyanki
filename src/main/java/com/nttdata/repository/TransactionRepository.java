package com.nttdata.repository;

import com.nttdata.document.Transaction;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<Transaction,String> {

     Flux<Transaction> findByClientIdAndPhone(Integer clientId,String phone);
}
