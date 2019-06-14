package com.melardev.spring.restcrud.controllers

import com.melardev.spring.restcrud.dtos.responses.ErrorResponse
import com.melardev.spring.restcrud.entities.Todo
import com.melardev.spring.restcrud.services.TodoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.function.Function
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/todos")
class TodosController(@Autowired
                      private val todoService: TodoService) {


    val notCompletedTodos: Flux<Todo>
        @GetMapping("/pending")
        get() = this.todoService.findAllPending()

    val completedTodos: Flux<Todo>
        @GetMapping("/completed")
        get() = todoService.findAllCompleted()

    @GetMapping
    fun index(): Flux<Todo> {
        return this.todoService.findAllHqlSummary()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): Mono<ResponseEntity<Any>> {
        return this.todoService.findById(id)
                .map(Function<Optional<Todo>, ResponseEntity<Any>> { optionalTodo ->
                    if (optionalTodo.isPresent)
                        ResponseEntity.ok(optionalTodo.get())
                    else
                        ResponseEntity(ErrorResponse("Todo not found"), HttpStatus.NOT_FOUND)
                })
    }

    @PostMapping
    fun create(@Valid @RequestBody todo: Todo): Mono<ResponseEntity<Todo>> {
        return todoService.save(todo).map { ResponseEntity(it, HttpStatus.CREATED) }
    }


    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody todoInput: Todo): Mono<ResponseEntity<*>> {
        // Do you know how to make it better? Let me know on Twitter or Pull request please.
        return todoService.findById(id)
                .map(Function<Optional<Todo>, Mono<ResponseEntity<*>>> { t ->
                    if (!t.isPresent)
                        return@Function Mono.just(ResponseEntity(ErrorResponse("Not found"), HttpStatus.NOT_FOUND) as ResponseEntity<*>)

                    val todo = t.get()
                    val title = todoInput.title
                    todo.title = title

                    val description = todoInput.description
                    if (description != null)
                        todo.description = description


                    todo.isCompleted = todoInput.isCompleted

                    todoService.save(todo)
                            .flatMap { todo1 -> Mono.just(ResponseEntity.ok<Any>(todo1)) }
                }).flatMap { responseEntityMono -> responseEntityMono.map<ResponseEntity<*>> { responseEntity -> responseEntity } }
    }


    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): Mono<ResponseEntity<*>> {
        return todoService.findById(id)
                .flatMap { ot -> todoService.delete(ot) }
                .map { ResponseEntity("", HttpStatus.NO_CONTENT) as ResponseEntity<*> }
                .defaultIfEmpty(ResponseEntity(ErrorResponse("Todo not found"), HttpStatus.NOT_FOUND))
    }


    @DeleteMapping
    fun deleteAll(): Mono<ResponseEntity<Void>> {
        return todoService.deleteAll().then(Mono.just(ResponseEntity(HttpStatus.NO_CONTENT)))
    }

}
