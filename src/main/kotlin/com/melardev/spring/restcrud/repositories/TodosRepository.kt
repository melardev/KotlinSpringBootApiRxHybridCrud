package com.melardev.spring.restcrud.repositories


import com.melardev.spring.restcrud.entities.Todo
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

import java.time.LocalDateTime

@Repository
interface TodosRepository : CrudRepository<Todo, Long> {

    @Query("select new Todo(t.id, t.title, t.isCompleted, t.createdAt, t.updatedAt) from Todo t order by t.createdAt desc")
    fun findAllHqlSummary(): List<Todo>

    fun findByIsCompleted(done: Boolean): List<Todo>

    fun findByIsCompletedTrue(): List<Todo>

    fun findByIsCompletedFalse(): List<Todo>

    fun findByIsCompletedIsTrue(): List<Todo>

    fun findByIsCompletedIsFalse(): List<Todo>

    fun findByTitleContains(title: String): List<Todo>

    fun findByDescriptionContains(description: String): List<Todo>

    @Query("select new Todo(t.id, t.title, t.isCompleted, t.createdAt, t.updatedAt) from Todo t where t.isCompleted = :completed")
    fun findByHqlCompletedIs(completed: Boolean): List<Todo>

    @Query("select t from Todo t where t.title like %:word%")
    fun findByHqlTitleLike(word: String): List<Todo>

    @Query("SELECT t FROM Todo t WHERE title = :title and description  = :description")
    fun findByHqlTitleAndDescription(title: String, description: String): List<Todo>

    @Query("select t FROM Todo t WHERE title = ?0 and description  = ?1")
    fun findByTHqlTitleAndDescription(title: String, description: String): List<Todo>

    fun findByCreatedAtAfter(date: LocalDateTime): List<Todo>

    fun findByCreatedAtBefore(date: LocalDateTime): List<Todo>

    @Query("select new Todo(t.id, t.title, t.isCompleted, t.createdAt, t.updatedAt) from Todo t where t.isCompleted = false")
    fun findByHqlPending(): List<Todo>

    @Query("select new Todo(t.id, t.title, t.isCompleted, t.createdAt, t.updatedAt) from Todo t where t.isCompleted = true")
    fun findByHqlCompleted(): List<Todo>

}