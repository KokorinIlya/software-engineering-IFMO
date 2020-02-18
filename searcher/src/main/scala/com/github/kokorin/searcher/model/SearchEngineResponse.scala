package com.github.kokorin.searcher.model

object SearchEngineResponse {
  type Status = Int

  val OK: Status = 200
  val ERR: Status = 500
}

case class SearchEngineResponse(status: SearchEngineResponse.Status,
                                urls: Seq[String])
