package scalac.summarizer.http

import akka.http.scaladsl.model.headers.RawHeader

import scala.collection.immutable

object HeaderFactory {

  def gitHubHeaders(etag: String) = {
    immutable.Seq(
      RawHeader("Accept", "application/vnd.github.v3+json"),
      RawHeader("If-None-Match", if(etag == null) "" else etag )
    )
  }

}
