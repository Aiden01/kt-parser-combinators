
package com.aiden.ktparsec

import com.aiden.ktparsec.examples.parseDate


fun main(args: Array<String>) {
    val input = "(hello)"
    val parser = parens(string("hello"))
    val dateParser = parseDate()
    println(parser.parse(input))
    println(dateParser.parse("1-12-2019"))
}
