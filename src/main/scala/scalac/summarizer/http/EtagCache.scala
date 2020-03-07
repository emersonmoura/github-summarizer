package scalac.summarizer.http

import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

import akka.http.scaladsl.model.HttpResponse

object EtagCache {

  val cache: ConcurrentMap[String, String] = new ConcurrentHashMap

  val ETAG = "etag"

  def putIfAbsent[T](url: String, response: HttpResponse):Unit = {
    val etag = getEtag(response, url)
    etag.getOrElse(cache.put(url, _))
  }

  def get(url: String) = {
    cache.get(url)
  }

  private def getEtag[T](response: HttpResponse, value: String) = {
    Option(cache.getOrDefault(value,extractEtag(response)))
  }

  private def extractEtag[T](response: HttpResponse) = {
    response.headers.find(_.is(ETAG)).map(_.value()).orNull
  }

}
