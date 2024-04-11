package com.gcgenome.rms.categories

import com.gcgenome.rms.auth.ManagerAuthenticationHandler
import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.CategoryDao
import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.*
import com.gcgenome.rms.tables.pojos.Category
import org.jooq.Condition
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CategoriesHandler(
    val dslContext: DSLContext
): CategoryDao {

    fun searchCategories(): Flux<Category> {
        return dslContext.selectCategory()
            .switchIfEmpty(Mono.error(CategoryNotFoundException()))
    }
}