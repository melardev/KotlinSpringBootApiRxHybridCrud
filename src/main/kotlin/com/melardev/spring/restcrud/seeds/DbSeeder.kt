package com.melardev.spring.restcrud.seeds

import com.github.javafaker.Faker
import com.melardev.spring.restcrud.entities.Todo
import com.melardev.spring.restcrud.services.TodoService
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.LongStream
import javax.sql.DataSource


@Service
class DbSeeder : CommandLineRunner {
    @Autowired
    lateinit var todosRepository: TodoService

    @Autowired
    internal var dataSource: DataSource? = null

    override fun run(vararg args: String) {
        System.out.printf("[+] We are using the following database connection string : %s\n" +
                "Go ahead into http://localhost:8080/api/h2-console and paste that connection string,\nusername=user,password=password, to access" +
                "the h2 database console ;)", (dataSource as HikariDataSource).jdbcUrl)
        todosRepository.deleteAll()
        val currentTodosInDb = this.todosRepository.count().block()
        val faker = Faker(Random(System.currentTimeMillis()))
        var todosToSeed: Long = 23

        if (currentTodosInDb != null)
            todosToSeed -= currentTodosInDb


        val startDate: Date = Date.from(LocalDateTime.of(2016, 1, 1, 0, 0).toInstant(ZoneOffset.UTC))
        val endDate: Date = Date.from(LocalDateTime.of(2019, 1, 1, 0, 0).toInstant(ZoneOffset.UTC))

        LongStream.range(0, todosToSeed).forEach { _ ->
            val todo = Todo(
                    StringUtils.collectionToDelimitedString(faker.lorem().words(faker.random().nextInt(2, 5)), "\n"),
                    org.apache.commons.lang3.StringUtils.join(faker.lorem().sentences(faker.random().nextInt(1, 3)), "\n"),
                    faker.random().nextBoolean())

            val dateForCreatedAt = faker.date().between(startDate, endDate)
            val createdAt = dateForCreatedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val dateForUpdatedAt = faker.date().future(2 * 365, TimeUnit.DAYS, dateForCreatedAt)
            val updatedAt = dateForUpdatedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

            todo.createdAt = createdAt
            todo.updatedAt = updatedAt
            todosRepository.save(todo).subscribe()
        }
    }
}
