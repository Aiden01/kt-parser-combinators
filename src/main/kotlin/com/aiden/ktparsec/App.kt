
package com.aiden.ktparsec



fun main(args: Array<String>) {
    val input = "hello"
    val parser = char('h') or char('e')
    println(parser.parse(input))
}
