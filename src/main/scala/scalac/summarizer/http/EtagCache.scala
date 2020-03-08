package scalac.summarizer.http

import akka.http.scaladsl.model.HttpResponse

import scala.concurrent.Future

object EtagCache {

  import com.github.blemale.scaffeine.{Cache, Scaffeine}

  import scala.concurrent.duration._

  case class Wrapper[T](hash: String, value: Future[Seq[T]])

  val cache: Cache[String, Wrapper[Any]] =
    Scaffeine()
      .expireAfterWrite(10.minutes)
      .maximumSize(500)
      .build[String, Wrapper[Any]]()

  val ETAG = "etag"

  def cacheIfRequired[T](url: String, response: HttpResponse, future: Future[Seq[T]]):Unit = {
    def extractEtag = {
      response.headers.find(_.is(ETAG)).map(_.value()).orNull
    }
    val etag = extractEtag
    if (etag != null && modified(response)) cache.put(url, Wrapper(etag, future))
  }

  private def modified[T](response: HttpResponse) = {
    response.status.intValue() == 200
  }

  def getHash(url: String) = {
    cache.getIfPresent(url).map(_.hash).orNull
  }

  def getCachedValueWhenRequired[T](url: String, response: HttpResponse): Option[Future[Seq[T]]] = {
    val cached = cache.getIfPresent(url).map(_.value.asInstanceOf[Future[Seq[T]]])
    if(modified(response)) Option.empty else cached
  }



}
