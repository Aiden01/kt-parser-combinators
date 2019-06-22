package com.aiden.ktparsec.examples


import com.aiden.ktparsec.char
import com.aiden.ktparsec.integerLiteral

data class Date(val day: Int, val month: Int, val years: Int)

fun number() = integerLiteral().fmap { it.toInt() }

fun day() = number().ensure("Invalid day") { it in (1..31) }
fun month() = number().ensure("Invalid month") { it in (1..12) }
fun year() = number()

fun separator() = char('/') or char('-')

fun parseDate() = day().andThenL(separator()).flatMap { day ->
    month().andThenL(separator()).flatMap { month ->
        year().fmap { year ->
            Date(day, month, year)
        }
    }
}