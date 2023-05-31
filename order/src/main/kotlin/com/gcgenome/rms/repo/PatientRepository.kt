package com.gcgenome.rms.repo

import com.gcgenome.rms.entity.Patient
import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface PatientRepository : ReactiveCrudRepository<Patient, Patient.Companion.PatientPK>, ReactiveQuerydslPredicateExecutor<Patient>, QuerydslR2dbcFragment<Patient>