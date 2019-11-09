package com.github.kokorin.calculator.io

object ConsolePrinter : Printer {
    override fun writeLn() {
        println()
    }

    override fun writeLn(s: String) {
        println(s)
    }

    override fun write(s: String) {
        print(s)
    }
}
