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

fun char(c: Char) = Parser {
    val (x, xs) = it.toPair()
    if (x != c) {
        Left("Expected character $c")
    } else {
        Right(Pair(xs, x))
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


fun <A> _many(stream: String, p: Parser<A>, results: List<A>): Pair<String, List<A>> {
    val r = p.parse(stream)
    return when(r) {
        is Either.Left -> Pair(stream, results)
        is Either.Right -> _many(r.fromRight().first, p, results.plus(r.fromRight().second))
    }
}

fun <A> many(p: Parser<A>) = Parser {
    Right(_many(it, p, listOf()))
}

fun digit() = Parser {
    val (x, xs) = it.toPair()
    if (x.isDigit()) {
        Right(Pair(xs, x))
    } else {
        Left("Expected digit")
    }
}

fun <A, B> between(p1: Parser<A>, p2: Parser<B>, p3: Parser<A>) = p1.andThenR(p2).andThenL(p3)

fun <A> parens(p: Parser<A>) = between(char('('), p, char(')'))
fun <A> brackets(p: Parser<A>) = between(char('['), p, char(']'))
fun <A> braces(p: Parser<A>) = between(char('{'), p, char('}'))

fun colon() = char(':')
fun space() = char(' ')
fun spaces() = many1(space())

fun <A> lexeme(p: Parser<A>) = p.andThenL(spaces())


fun <A> many1(p: Parser<A>) = p.andThenR(many(p))
