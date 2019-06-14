package com.melardev.spring.restcrud.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors

@Configuration
class DbConfig {

    @Bean
    fun transactionTemplate(transactionManager: PlatformTransactionManager): TransactionTemplate {
        return TransactionTemplate(transactionManager)
    }

    companion object {
        @JvmStatic val DB_SCHEDULER = Schedulers.fromExecutor(Executors.newCachedThreadPool())
    }
}
