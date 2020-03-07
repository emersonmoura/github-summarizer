package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import scalac.summarizer.http.{HeaderLinkExtractor, HttpClient}
import scalac.summarizer.json.JsonSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object PaginatedHandler {

  implicit class Paginated(httpClient: HttpClient) extends JsonSupport {

    def processPaginatedRequest[T](url: String, fx: HttpResponse => Future[Seq[T]]): Future[Seq[T]]  = {
      def genericFallback = {
        Future.successful(Seq.empty[T])
      }
      def processPagination(response: HttpResponse): Future[Seq[T]] = {
        val link = HeaderLinkExtractor.extract(response.headers)
        val eventualRepositories = fx(response)
        link.map(value => processPaginatedRequest(value, fx).zipWith(eventualRepositories)(_ ++ _)).getOrElse(eventualRepositories)
      }
      httpClient.sendRequest(HttpRequest(uri = url)).flatMap(processPagination).fallbackTo(genericFallback)
    }
  }
}
