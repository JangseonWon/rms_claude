package com.gcgenome.rms.repo

import com.gcgenome.rms.entity.Order
import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface OrderRepository : ReactiveCrudRepository<Order, UUID>, ReactiveQuerydslPredicateExecutor<Order>, QuerydslR2dbcFragment<Order>