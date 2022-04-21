package com.example

import arrow.core.Either
import arrow.core.flatMap
import kotlin.test.Test

class Arrow {

    @Test
    fun `some test`() {
        val left: Either<String, Int> = Either.Left("Something went wrong")
        val value = left.flatMap { Either.Right(it + 1) }

        println(value)
    }

    fun parse(s: String): Either<NumberFormatException, Int> =
        if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
        else Either.Left(NumberFormatException("$s is not a valid integer."))

    fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
        if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
        else Either.Right(1.0 / i)

    fun stringify(d: Double): String = d.toString()

    fun magic(s: String): Either<java.lang.Exception, String> =
        parse(s).flatMap { reciprocal(it) }.map { stringify(it) }

    @Test
    fun `something`() {
        println(magic("asd"))
    }
}