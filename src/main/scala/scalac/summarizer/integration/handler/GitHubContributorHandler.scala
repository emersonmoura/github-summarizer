package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.{HttpHeader, HttpRequest}
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import scalac.summarizer.http.HttpClient
import scalac.summarizer.json.JsonSupport
import scalac.summarizer.model.Contributor

import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GitHubContributorHandler(httpClient: HttpClient) extends JsonSupport with ContributorHandler{

  def contributorsByRepository(repositoryUrl: String): Future[Set[Contributor]] = {
    val header = immutable.Seq(RawHeader("If-Modified-Since", "Fri, 06 Mar 2020 03:32:00 GMT"))
    httpClient.sendRequest(HttpRequest(uri = repositoryUrl)).flatMap(response => Unmarshal(response).to[Set[Contributor]])
    .fallbackTo(Future.successful(Set.empty[Contributor]))
  }
}