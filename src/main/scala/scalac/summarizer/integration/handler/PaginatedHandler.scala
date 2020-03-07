package scalac.summarizer.integration.handler

import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import scalac.summarizer.http.{EtagCache, HeaderFactory, HeaderLinkExtractor, HttpClient}
import scalac.summarizer.json.JsonSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PaginatedHandler {

  implicit class Paginated(httpClient: HttpClient) extends JsonSupport {

    def processPaginatedRequest[T](url: String, unmarshal: HttpResponse => Future[Seq[T]]): Future[Seq[T]]  = {
      def genericFallback = {
        Future.successful(Seq.empty[T])
      }
      def processPagination(response: HttpResponse): Future[Seq[T]] = {
        val link = HeaderLinkExtractor.extract(response.headers)
        val eventualValue = unmarshal(response)
        EtagCache.putIfAbsent(url, response)
        link.map(value => processPaginatedRequest(value,unmarshal).zipWith(eventualValue)(_ ++ _)).getOrElse(eventualValue)
      }
      httpClient.sendRequest(HttpRequest(uri = url, headers = headers(url) )).flatMap(processPagination).fallbackTo(genericFallback)
    }

    private def headers[T](url: String) = {
      HeaderFactory.gitHubHeaders(EtagCache.get(url))
    }
  }


}
