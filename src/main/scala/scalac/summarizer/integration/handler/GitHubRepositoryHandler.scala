package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import scalac.summarizer.http.HttpClient
import scalac.summarizer.integration.model.GitHubRepository
import scalac.summarizer.json.JsonSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GitHubRepositoryHandler(httpClient: HttpClient) extends JsonSupport with RepositoryHandler{

  def repositoriesByOrganization(organization: String): Future[List[GitHubRepository]] = {
    httpClient.sendRequest(HttpRequest(uri = "url")).flatMap(response => Unmarshal(response).to[List[GitHubRepository]])
  }


}