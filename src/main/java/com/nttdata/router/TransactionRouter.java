package com.nttdata.router;


import com.nttdata.handler.TransactionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class TransactionRouter {
    @Bean
    public RouterFunction<ServerResponse> transactionyankiRouterFunc(TransactionHandler transactionHandler) {
        return RouterFunctions.route(POST("/transactionyanki").and(accept(MediaType.APPLICATION_JSON)), transactionHandler::add)
                .andRoute(GET("/transactionyanki").and(accept(MediaType.TEXT_EVENT_STREAM)), transactionHandler::findAll)
                .andRoute(GET("/transactionyanki/{id}").and(accept(MediaType.APPLICATION_JSON)), transactionHandler::findById)
                .andRoute(PUT("/transactionyanki/{id}").and(accept(MediaType.APPLICATION_JSON)), transactionHandler::update);


    }

}
