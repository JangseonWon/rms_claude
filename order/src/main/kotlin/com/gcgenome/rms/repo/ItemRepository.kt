package com.gcgenome.rms.repo

import com.gcgenome.rms.entity.Item
import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface ItemRepository : ReactiveCrudRepository<Item, UUID>, ReactiveQuerydslPredicateExecutor<Item>, QuerydslR2dbcFragment<Item>