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

fun <A> many1(p: Parser<A>) = p.andThen(many(p))
