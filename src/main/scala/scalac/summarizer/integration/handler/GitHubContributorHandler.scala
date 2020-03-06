package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import scalac.summarizer.http.HttpClient
import scalac.summarizer.integration.model.GitHubContributor
import scalac.summarizer.json.JsonSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GitHubContributorHandler(httpClient: HttpClient) extends JsonSupport with ContributorHandler{

  def contributorsByRepository(organization: String): Future[Set[GitHubContributor]] = {
    httpClient.sendRequest(HttpRequest(uri = "url")).flatMap(response => Unmarshal(response).to[Set[GitHubContributor]])
  }


}