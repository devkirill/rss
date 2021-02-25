package com.project.rss

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan


@SpringBootApplication
@ComponentScan(basePackages = ["com.project.rss"])
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}