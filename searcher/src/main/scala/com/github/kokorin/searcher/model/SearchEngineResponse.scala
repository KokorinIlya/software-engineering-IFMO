package com.github.kokorin.searcher.model

// TODO: use algebraic types
case class SearchEngineResponse(status: Int, urls: Seq[String])
