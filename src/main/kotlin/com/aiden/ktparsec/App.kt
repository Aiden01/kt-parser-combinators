
package com.aiden.ktparsec



fun main(args: Array<String>) {
    val input = "(hello)"
    val parser = parens(string("hello"))
    println(parser.parse(input))
}
