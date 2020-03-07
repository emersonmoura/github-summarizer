package scalac.summarizer.http

import akka.http.scaladsl.model.HttpResponse
import scalac.summarizer.integration.handler.PaginatedHandler.cache

object EtagExtractor {

  val ETAG = "etag"

  def processEtag[T](url: String, response: HttpResponse):Unit = {
    val etag = getEtag(response, url)
    etag.getOrElse(cache.put(url, _))
  }

  private def getEtag[T](response: HttpResponse, value: String) = {
    Option(cache.getOrDefault(value,extractEtag(response)))
  }

  private def extractEtag[T](response: HttpResponse) = {
    response.headers.find(_.is(ETAG)).map(_.value()).orNull
  }

}
