package com.nttdata.handler;

import com.nttdata.message.KafkaSender;
import com.nttdata.repository.TransactionRepository;
import com.nttdata.document.Transaction;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class TransactionHandler {

    private final KafkaSender kafkaSender;

    private final TransactionRepository transactionalRepository;
    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    @Autowired
    public TransactionHandler(KafkaSender kafkaSender, TransactionRepository transactionalRepository) {
        this.kafkaSender = kafkaSender;
        this.transactionalRepository = transactionalRepository;
    }

    public Mono<ServerResponse> add(ServerRequest serverRequest) {
        var transactionMono = serverRequest.bodyToMono(Transaction.class);

        String topic="trasanctionYanki";
        return transactionMono.flatMap(t -> {
            var dato = balance(t.getClientId(),t.getPhone());
            kafkaSender.sedMessage(topic,t);
                    t.setDateTransaction(LocalDateTime.now());
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(transactionalRepository.save(t), Transaction.class);
                }
        );
    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {
        var id = serverRequest.pathVariable("id");
        var transactionItem = transactionalRepository.findById(id);

        return transactionItem.flatMap(t -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(transactionItem, Transaction.class)
                .switchIfEmpty(notFound)
        );
    }

    public Mono<ServerResponse> findAll(ServerRequest serverRequest) {

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(transactionalRepository.findAll().log("Func"), Transaction.class).switchIfEmpty(notFound);


    }

    public Mono<Transaction> balance(Integer clientId, String phone) {
        var banlceFlux = transactionalRepository.findByClientIdAndPhone(clientId, phone);
        return banlceFlux.collectList()
                .flatMap(transactions -> {

                            var ingress = transactions.stream()
                                    .filter(t -> t.getAmount() > 0 &&  t.getTypeOperationId()==1)
                                    .mapToDouble(s -> s.getAmount()).sum();

                            var egress = transactions.stream()
                                    .filter(t -> t.getAmount() > 0 && t.getTypeOperationId()==2)
                                    .mapToDouble(s -> s.getAmount()).sum();

                            var trasact = transactions.stream().distinct().map(y -> {

                                y.setAmount(ingress - egress);
                                return y;
                            }).collect(Collectors.toList());

                            return Mono.just(trasact.get(0));
                        }

                );

    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        var id = serverRequest.pathVariable("id");
        var transactionItem = transactionalRepository.findById(id);
        var transaction = serverRequest.bodyToMono(Transaction.class);

        return transactionItem.flatMap(
                t -> {
                    return transaction.flatMap(x -> {
                        t.setAmount(x.getAmount());

                        return ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(transactionalRepository.save(t), Transaction.class);
                    });
                });

    }


}
