package com.github.kokorin.searcher.model

object SearchEngineResponse {
  type Status = String

  val OK: Status = "OK"
  val ERR: Status = "ERR"
}

case class SearchEngineResponse(status: SearchEngineResponse.Status,
                                urls: Seq[String])
