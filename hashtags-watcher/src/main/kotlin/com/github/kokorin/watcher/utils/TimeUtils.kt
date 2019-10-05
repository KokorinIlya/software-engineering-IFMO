package com.github.kokorin.watcher.utils

import java.time.Duration
import java.util.concurrent.TimeUnit

fun Duration.toSeconds(): Long = TimeUnit.MILLISECONDS.toSeconds(toMillis())
