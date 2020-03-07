package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import scalac.summarizer.http.{HttpClient, HeaderLinkExtractor}
import scalac.summarizer.integration.model.GitHubRepository
import scalac.summarizer.json.JsonSupport

import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GitHubRepositoryHandler(httpClient: HttpClient) extends JsonSupport with RepositoryHandler{

  def repositoriesByOrganization(organization: String): Future[Seq[GitHubRepository]] = {
    val url = s"https://api.github.com/orgs/$organization/repos"
    processRequest(url)
  }

  private def processRequest(url: String): Future[Seq[GitHubRepository]]  = {

    def repositoryFallback = {
      Future.successful(Seq.empty[GitHubRepository])
    }

    def processPagination(response: HttpResponse): Future[Seq[GitHubRepository]] = {
      val link = HeaderLinkExtractor.extract(response.headers)
      val eventualRepositories = Unmarshal(response).to[Seq[GitHubRepository]]
      link.map(value => processRequest(value).zipWith(eventualRepositories)(_ ++ _)).getOrElse(eventualRepositories)
    }

    httpClient.sendRequest(HttpRequest(uri = url)).flatMap(processPagination).fallbackTo(repositoryFallback)
  }


  private def headers = {
    immutable.Seq(
      RawHeader("Accept", "application/vnd.github.v3+json"),
      RawHeader("If-Modified-Since", "Fri, 06 Mar 2020 03:32:00 GMT")
    )
  }
}

