package com.github.kokorin.searcher.model

// TODO: make status enum {OK, ERROR}
case class SearchEngineResponse(status: Int, urls: Seq[String])
