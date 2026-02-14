package com.aymanegeek.imedia

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class ImediaApplication

fun main(args: Array<String>) {
	runApplication<ImediaApplication>(*args)
}
