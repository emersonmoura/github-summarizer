package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import scalac.summarizer.http.HttpClient
import scalac.summarizer.integration.handler.PaginatedHandler._
import scalac.summarizer.json.JsonSupport
import scalac.summarizer.model.Contributor

import scala.collection.immutable
import scala.concurrent.Future
class GitHubContributorHandler(httpClient: HttpClient) extends JsonSupport with ContributorHandler{

  def contributorsByRepository(repositoryUrl: String): Future[Seq[Contributor]] = {
    httpClient.processPaginatedRequest(repositoryUrl, response => Unmarshal(response).to[Seq[Contributor]])
  }

  private def header = {
    immutable.Seq(RawHeader("If-Modified-Since", "Fri, 06 Mar 2020 03:32:00 GMT"))
  }

}