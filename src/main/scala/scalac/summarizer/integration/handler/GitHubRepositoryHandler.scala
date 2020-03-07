package scalac.summarizer.integration.handler

import akka.http.scaladsl.unmarshalling.Unmarshal
import scalac.summarizer.http.HttpClient
import scalac.summarizer.integration.handler.PaginatedHandler._
import scalac.summarizer.integration.model.GitHubRepository
import scalac.summarizer.json.JsonSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GitHubRepositoryHandler(httpClient: HttpClient) extends JsonSupport with RepositoryHandler{

  def repositoriesByOrganization(organization: String): Future[Seq[GitHubRepository]] = {
    val url = s"https://api.github.com/orgs/$organization/repos"
    httpClient.processPaginatedRequest(url, unmarshal = response => Unmarshal(response).to[Seq[GitHubRepository]])
  }
}

