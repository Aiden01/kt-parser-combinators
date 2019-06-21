package com.aiden.ktparsec

import arrow.core.Either
import arrow.core.flatMap


typealias ParseError = String
typealias ParseResult<T> = Either<ParseError, Pair<String, T>>

class Parser<T>(val f: (String) -> ParseResult<T>) {
    fun parse(stream: String) = f(stream)
    fun <B> fmap(g: (T) -> B) = Parser {
        parse(it).map { r ->
            Pair(r.first, g(r.second))
        }
    } 
    fun <B> flatMap(g: (T) -> Parser<B>) = Parser {
        parse(it).flatMap {(stream, r) ->
            g(r).parse(stream)
        }
    }
    fun <B> andThen(p: Parser<B>) = Parser {
        parse(it).flatMap {(stream, _) -> p.parse(stream)}
    }

    infix fun or(p: Parser<T>) = Parser {
        val r = parse(it)
        when (r) {
            is Either.Left -> p.parse(it)
            is Either.Right -> r 
        }
    }

}





