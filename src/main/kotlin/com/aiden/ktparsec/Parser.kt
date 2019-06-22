package com.aiden.ktparsec

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.Left
import arrow.core.Right


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
    fun <B> andThenR(p: Parser<B>) = Parser {
        parse(it).flatMap {(stream, _) -> p.parse(stream)}
    }

    fun <B> andThenL(p: Parser<B>) = Parser {
        parse(it).flatMap {(stream , r) -> 
            p.parse(stream).flatMap { (s, _) ->
                Right(Pair(s, r))
            }
        }
    }

    fun ensure(error: String, f: (T) -> Boolean) = Parser {
        parse(it).flatMap { (stream, r) ->
            if (f(r)) {
                Right(Pair(stream, r))
            } else {
                Left(error)
            }
        }
    }

    infix fun or(p: Parser<T>) = Parser {
        val r = parse(it)
        when (r) {
            is Either.Left -> p.parse(it)
            is Either.Right -> r 
        }
    }

}





