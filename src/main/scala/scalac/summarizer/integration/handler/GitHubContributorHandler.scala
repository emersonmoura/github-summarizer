package scalac.summarizer.integration.handler

import akka.http.scaladsl.unmarshalling.Unmarshal
import scalac.summarizer.http.HttpClient
import scalac.summarizer.integration.handler.PaginatedHandler._
import scalac.summarizer.json.JsonSupport
import scalac.summarizer.model.Contributor

import scala.concurrent.Future
class GitHubContributorHandler(httpClient: HttpClient) extends JsonSupport with ContributorHandler{

  def contributorsByRepository(repositoryUrl: String): Future[Seq[Contributor]] = {
    httpClient.processPaginatedRequest(repositoryUrl, unmarshal = response => Unmarshal(response).to[Seq[Contributor]])
  }

}