package com.melardev.spring.restcrud.services

import com.melardev.spring.restcrud.config.DbConfig.Companion.DB_SCHEDULER
import com.melardev.spring.restcrud.entities.Todo
import com.melardev.spring.restcrud.repositories.TodosRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.Callable
import java.util.function.Supplier

@Service
class TodoService {
    @Autowired
    private lateinit var todosRepository: TodosRepository

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate


    fun findAllHqlSummary(): Flux<Todo> {
        val defer = Flux.defer { Flux.fromIterable(this.todosRepository.findAllHqlSummary()) }
        return defer.subscribeOn(DB_SCHEDULER)
    }

    fun findAllPending(): Flux<Todo> {
        val defer = Flux.defer { Flux.fromIterable(this.todosRepository.findByHqlPending()) }
        return defer.subscribeOn(DB_SCHEDULER)
    }

    fun findAllCompleted(): Flux<Todo> {
        val defer = Flux.defer { Flux.fromIterable(this.todosRepository.findByHqlCompleted()) }
        return defer.subscribeOn(DB_SCHEDULER)
    }

    fun findById(id: Long): Mono<Optional<Todo>> {

        return Mono
                .defer { Mono.just(todosRepository.findById(id)) }
                .subscribeOn(DB_SCHEDULER)
    }

    fun save(todo: Todo): Mono<Todo> {
        return Mono.fromCallable<Todo> {
            transactionTemplate.execute { status ->
                val persistedTodo = todosRepository.save(todo)
                persistedTodo
            }
        }.subscribeOn(DB_SCHEDULER)
    }

    fun deleteAll(): Mono<Boolean> {
        return Mono.fromCallable {
            todosRepository.deleteAll()
            true
        }.subscribeOn(DB_SCHEDULER)
    }

    fun delete(todo: Optional<Todo>): Mono<Boolean> {
        return if (!todo.isPresent) Mono.empty() else Mono.defer {
            todosRepository.delete(todo.get())
            Mono.just(true)
        }.subscribeOn(DB_SCHEDULER)
    }

    fun count(): Mono<Long> {
        return Mono.defer { Mono.just(todosRepository.count()) }.subscribeOn(DB_SCHEDULER)
    }

    fun saveAll(todos: Set<Todo>): Flux<Todo> {
        return Flux.defer { Flux.fromIterable(this.todosRepository.saveAll(todos)) }.subscribeOn(DB_SCHEDULER)
    }
}
