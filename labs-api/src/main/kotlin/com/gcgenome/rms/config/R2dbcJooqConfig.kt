package com.gcgenome.rms.config

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.TransactionAwareConnectionFactoryProxy
import org.springframework.transaction.reactive.TransactionalOperator

//@Configuration
class R2dbcJooqConfig {

    //@Bean
    fun dslContext(cf: ConnectionFactory): DSLContext =
        DSL.using(
            TransactionAwareConnectionFactoryProxy(cf),
            SQLDialect.POSTGRES
        )
    //@Bean
    fun transactionManager(cf: ConnectionFactory): R2dbcTransactionManager =
        R2dbcTransactionManager(cf)

    //@Bean
    fun transactionalOperator(tm: R2dbcTransactionManager) =
        TransactionalOperator.create(tm)
}