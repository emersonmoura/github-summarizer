package scalac.summarizer.http

import akka.http.scaladsl.model.headers.RawHeader

import scala.collection.immutable

object HeaderFactory {

  val token = scala.util.Properties.envOrNone("GH_TOKEN")

  def gitHubHeaders(etag: String) = {
    token.map(defaultHeaders(etag) ++ authHeader(_)).getOrElse(defaultHeaders(etag)).to[immutable.Seq]
  }

  private def authHeader(token: String) = {
    Seq(RawHeader("Authorization", s"token $token"))
  }

  private def defaultHeaders(etag: String) = {
    Seq(
      RawHeader("Accept", "application/vnd.github.v3+json"),
      RawHeader("If-None-Match", if (etag == null) "" else etag)
    )
  }
}
