package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.{HttpHeader, HttpRequest, HttpResponse}
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

      def followLink(headers: Seq[HttpHeader], eventualValue: Future[Seq[T]]) = {
        val link = HeaderLinkExtractor.extract(headers)
        link.map(value => processPaginatedRequest(value, unmarshal).zipWith(eventualValue)(_ ++ _)).getOrElse(eventualValue)
      }

      def processCachedPagination(response: HttpResponse): Future[Seq[T]] = {
        val eventualValue = EtagCache.getCachedValueWhenRequired(url, response).getOrElse(unmarshal(response))
        EtagCache.cacheIfRequired(url, response, eventualValue)
        followLink(response.headers, eventualValue)
      }

      httpClient.sendRequest(HttpRequest(uri = url, headers = headers(url) )).flatMap(processCachedPagination).fallbackTo(genericFallback)
    }

    private def headers[T](url: String) = {
      HeaderFactory.gitHubHeaders(EtagCache.getHash(url))
    }
  }


}
