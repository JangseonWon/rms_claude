package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.noCondition
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

interface QueryDao {
    enum class JoinType {
        INNER, LEFT, RIGHT
    }

    data class JoinInfo(val table: TableLike<*>, val condition: Condition, val joinType: JoinType = JoinType.INNER)

    fun <T> DSLContext.selectPage(
        mainTable: Table<*>,
        query: Query,
        joinTables: List<JoinInfo> = emptyList(),
        selectFields: List<Field<*>> = listOf(),
        where: Condition = noCondition(),
        groupByFields: List<Field<*>> = emptyList(),
        mapper: (Record) -> T
    ): Mono<Page<T>> {
        val totalElementsMono = getTotalElementsMono(mainTable, query, joinTables, selectFields, where, groupByFields)
        val contentMono = getContentMono(mainTable, query, joinTables, selectFields, where, groupByFields, mapper)

        return Mono.zip(totalElementsMono, contentMono)
            .map { tuple ->
                val totalElements = tuple.t1
                val content = tuple.t2
                val totalPages = query.size?.let { size -> (totalElements + size - 1) / size }
                Page(totalCount = totalElements, totalPage = totalPages, pageSize = query.size, currentPage = query.page, data = content)
            }
    }
    fun <T> DSLContext.selectQuery(
        mainTable: Table<*>,
        query: Query,
        joinTables: List<JoinInfo> = emptyList(),
        selectFields: List<Field<*>> = listOf(),
        where: Condition = noCondition(),
        groupByFields: List<Field<*>> = emptyList(),
        mapper: (Record) -> T
    ): Mono<T> {
        val fields = selectFields.takeIf { it.isNotEmpty() } ?: mainTable.fields().toList()

        val queryBuilder = select(*fields.toTypedArray())
            .from(from(mainTable, joinTables))
            .where(condition(query).and(where))
            .apply {
                if (groupByFields.isNotEmpty()) {
                    groupBy(*groupByFields.toTypedArray())
                }
            }
            .orderBy(orderBy(query))

        return Mono.from(queryBuilder).map(mapper)
    }

    private fun DSLContext.getTotalElementsMono(
        mainTable: Table<*>,
        query: Query,
        joinTables: List<JoinInfo> = emptyList(),
        selectFields: List<Field<*>>,
        where: Condition,
        groupByFields: List<Field<*>>
    ): Mono<Int> {
        val fields = selectFields.takeIf { it.isNotEmpty() } ?: mainTable.fields().toList()
        val queryBuilder = select(*fields.toTypedArray())
            .from(from(mainTable, joinTables))
            .where(condition(query).and(where))
            .apply {
                if (groupByFields.isNotEmpty()) {
                    groupBy(*groupByFields.toTypedArray())
                }
            }
        return Mono.from(
            selectCount()
                .from(queryBuilder)
        ).map { it.component1() }
    }
    private fun <T> DSLContext.getContentMono(
        mainTable: Table<*>,
        query: Query,
        joinTables: List<JoinInfo>,
        selectFields: List<Field<*>>,
        where: Condition,
        groupByFields: List<Field<*>>,
        mapper: (Record) -> T
    ): Mono<List<T>> {
        val fields = selectFields.takeIf { it.isNotEmpty() } ?: mainTable.fields().toList()

        val queryBuilder = select(*fields.toTypedArray())
            .from(from(mainTable, joinTables))
            .where(condition(query).and(where))
            .apply {
                if (groupByFields.isNotEmpty()) {
                    groupBy(*groupByFields.toTypedArray())
                }
            }
            .orderBy(orderBy(query))
            .apply {
                query.page?.let { page ->
                    query.size?.let { size ->
                        offset((page - 1) * size)
                        limit(size)
                    }
                }
            }

        return Flux.from(queryBuilder).map{mapper(it)}.collectList()
    }
    private fun getSelectFields(mainTable: Table<*>, selectFields: List<Field<*>>): List<Field<*>> {
        return selectFields.takeIf { it.isNotEmpty() } ?: mainTable.fields().toList()
    }

    private fun from(mainTable: Table<*>, joins: List<JoinInfo>): Table<*> {
        var joinedTable: Table<*> = mainTable
        joins.forEach { joinInfo ->
            joinedTable = when (joinInfo.joinType) {
                JoinType.INNER -> joinedTable.join(joinInfo.table).on(joinInfo.condition)
                JoinType.LEFT -> joinedTable.leftJoin(joinInfo.table).on(joinInfo.condition)
                JoinType.RIGHT -> joinedTable.rightJoin(joinInfo.table).on(joinInfo.condition)
            }
        }
        return joinedTable
    }

    private fun condition(query: Query): Condition {
        var finalCondition = noCondition()
        query.filterGroups?.forEach { filterGroup ->
            var groupCondition = noCondition()
            filterGroup.filters.forEach { filter ->
                val table = filter.table
                val column = filter.column
                val field = field(DSL.name(table, column))

                val filterCondition = when (filter.operator) {
                    "=" -> field.eq(filter.value)
                    "!=" -> field.ne(filter.value)
                    ">" -> field.gt(filter.value)
                    "<" -> field.lt(filter.value)
                    ">=" -> if (isTimestampColumn(column)) {
                        field.ge(parseTimestamp(filter.value))
                    } else field.ge(filter.value)
                    "<=" -> if (isTimestampColumn(column)) {
                        field.le(parseEndOfDayTimestamp(filter.value))
                    } else field.le(filter.value)
                    "LIKE" -> field.likeIgnoreCase("%${filter.value}%")
                    else -> null
                }
                filterCondition?.let {
                    groupCondition = when (filterGroup.conditionType.uppercase()) {
                        "OR" -> groupCondition.or(filterCondition)
                        "AND" -> groupCondition.and(filterCondition)
                        else -> groupCondition.and(filterCondition)
                    }
                }
            }
            finalCondition = finalCondition.and(groupCondition)
        }
        return finalCondition
    }
    private fun orderBy(query: Query): List<SortField<*>> {
        val sortFields = mutableListOf<SortField<*>>()
        query.sortBy?.let { sortBy ->
            val field = field(sortBy)
            val sortField = if (query.asc == true) {
                field.asc()
            } else {
                field.desc()
            }
            sortFields.add(sortField)
        }
        return sortFields
    }
    private fun isTimestampColumn(column: String): Boolean {
        return column.contains("_at", ignoreCase = true)
    }
    private fun parseTimestamp(value: String): LocalDateTime {
        return LocalDate.parse(value).atStartOfDay()
    }
    private fun parseEndOfDayTimestamp(date: String): LocalDateTime {
        return LocalDate.parse(date).atTime(LocalTime.MAX)
    }
}