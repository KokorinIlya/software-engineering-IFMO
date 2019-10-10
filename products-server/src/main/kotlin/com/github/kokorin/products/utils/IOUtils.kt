package com.github.kokorin.products.utils

import java.nio.file.Files
import java.nio.file.Path

fun readFileAsString(path: Path): String {
    return Files.newBufferedReader(path).useLines {
        it.joinToString(separator = "\n")
    }
}
