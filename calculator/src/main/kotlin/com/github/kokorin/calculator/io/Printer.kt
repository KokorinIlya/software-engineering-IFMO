package com.github.kokorin.calculator.io

interface Printer {
    fun write(s: String)

    fun writeLn(s: String) {
        write(s)
        writeLn()
    }

    fun writeLn()
}
