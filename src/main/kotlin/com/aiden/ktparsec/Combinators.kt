package com.aiden.ktparsec

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right

fun <A, B>Either<A, B>.fromRight() = when(this) {
    is Either.Left -> throw Error("Cannot unwrap left")
    is Either.Right -> this.b
}

fun String.toPair(): Pair<Char, String> {
    val fst = this.first()
    val snd = this.slice(IntRange(1, this.length - 1))
    return Pair(fst, snd)
}

fun <A, B> Pair<A, B>.swap() = Pair(this.second, this.first)

fun satisfy(f: (Char) -> Boolean) = Parser {
    val (x, xs) = it.toPair()
    if (f(x)) {
        Right(Pair(xs, x))
    } else {
        Left("Unknown parse error")
    }
}

fun char(c: Char) = Parser {
    val (x, xs) = it.toPair()
    if (x == c) {
        Right(Pair(xs, x))
    } else {
        Left("Expected char $c")
    }
}

fun string(str: String) = Parser {
    if (it.startsWith(str)) {
        val fst = it.slice(IntRange(0, str.length - 1))
        val snd = it.slice(IntRange(str.length, it.length - 1))
        Right(Pair(snd, fst))
    } else {
        Left("Expected string $str")
    }
}

fun <A> choice(parsers: List<Parser<A>>) = 
    parsers
            .fold(parsers.first()) { prev, p -> prev.or(p) }



private fun <A> _many(stream: String, p: Parser<A>, results: List<A>): Pair<String, List<A>> {
    val r = p.parse(stream)
    return when(r) {
        is Either.Left -> Pair(stream, results)
        is Either.Right -> _many(r.fromRight().first, p, results.plus(r.fromRight().second))
    }
}

fun oneOf(items: String) = Parser { 
    val (x, xs) = it.toPair()
    if (items.contains(x)) {
        Right(Pair(xs, x))
    } else {
        Left("Expected one of $items")
    }
 }

fun <A> many(p: Parser<A>) = Parser {
    Right(_many(it, p, listOf()))
}


fun <A> many1(p: Parser<A>) = p.andThenR(many(p))

fun digit() = Parser {
    if (it == "") {
        Left("Stream is empty")
    } else {
        val (x, xs) = it.toPair()
        if (x.isDigit()) {
            Right(Pair(xs, x))
        } else {
            Left("Expected digit")
        }
    }
}


fun integerLiteral() = many(digit()).fmap { it.joinToString("") }

fun <A, B> between(p1: Parser<A>, p2: Parser<B>, p3: Parser<A>) = p1.andThenR(p2).andThenL(p3)

fun <A> parens(p: Parser<A>) = between(char('('), p, char(')'))
fun <A> brackets(p: Parser<A>) = between(char('['), p, char(']'))
fun <A> braces(p: Parser<A>) = between(char('{'), p, char('}'))

fun colon() = char(':')
fun newline() = char('\n')
fun semi() = char(';')
fun space() = char(' ')
fun spaces() = many1(space())

fun anyChar() = Parser { Right(it.toPair().swap()) }

fun <A> lexeme(p: Parser<A>) = p.andThenL(spaces())

