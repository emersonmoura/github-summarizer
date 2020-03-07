package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.RawHeader
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scalac.summarizer.ClientHandlerMock
import scalac.summarizer.integration.model.GitHubRepository

import scala.collection.immutable
import scala.concurrent.Future

class GitHubRepositoryHandlerTest extends AsyncFlatSpec with Matchers with AsyncMockFactory {

  private val httpClientMock: ClientHandlerMock = new ClientHandlerMock()
  private val handler = new GitHubRepositoryHandler(httpClientMock)

  "given an valid json" should "be processed" in {
    val organization = "myOrg"
    val json = s"""[
                 ${repositoryJson("styleguide")},
                 ${repositoryJson("payola")},
                 ${repositoryJson("ubc-ceih")}
                ]""".stripMargin

    httpClientMock.mockResponse(json)

    val eventualRepositories: Future[Seq[GitHubRepository]] = handler.repositoriesByOrganization(organization)

    eventualRepositories map  { it => assert(!it.map(_.contributorsUrl).contains(null)) }
  }

  "given a failed response" should "return an empty list" in {
    httpClientMock.mock.expects(*).returning(Future.failed(new IllegalArgumentException()))

    val eventualRepositories: Future[Seq[GitHubRepository]] = handler.repositoriesByOrganization("*")

    eventualRepositories map  { it => it should have size 0 }
  }

  "given an response with next pages" should "follow them" in {
    val firstPage = "https://api.github.com/organizations/3430433/repos?page=1"
    val nextPage = "https://api.github.com/organizations/3430433/repos?page=2"
    val firstPagination = s"<$nextPage>; rel=next, <$nextPage>; rel=last"
    val lastPagination = s"<$firstPage>; rel=prev, <$nextPage>; rel=last"
    val firstExpectedName = "styleguide"
    val secondExpectedName = "jspahrsummers"
    val firstHeaders = immutable.Seq[HttpHeader](RawHeader("Link", firstPagination))
    val secondHeaders = immutable.Seq[HttpHeader](RawHeader("Link", lastPagination))

    httpClientMock.mockResponse(repositoriesJson(firstExpectedName), firstHeaders)

    httpClientMock.mockResponse(repositoriesJson(secondExpectedName), secondHeaders)

    val eventualRepositories: Future[Seq[GitHubRepository]] = handler.repositoriesByOrganization("organization")

    eventualRepositories map  { it =>
      it should have size 2
      it.last.name should be(firstExpectedName)
      it.head.name should be(secondExpectedName)
    }
  }

  "given an response without next pages" should "follow nothing" in {
    val nextPage = "https://api.github.com/organizations/3430433/repos?page=2"
    val firstPagination = s"<$nextPage>; rel=prev, <$nextPage>; rel=last"
    val firstHeaders = immutable.Seq[HttpHeader](RawHeader("Link", firstPagination))

    httpClientMock.mockResponse(repositoriesJson(), firstHeaders)

    httpClientMock.mockResponse(repositoriesJson(), firstHeaders)

    val eventualRepositories: Future[Seq[GitHubRepository]] = handler.repositoriesByOrganization("organization")

    eventualRepositories map  { it => it should have size 1 }
  }

  private def repositoriesJson(name: String = "styleguide") = {
    s"""[
        ${repositoryJson(name)}
       ]""".stripMargin
  }

  private def repositoryJson(name: String) = {
    s"""{
        | "name": "$name",
        | "contributors_url": "https://api.g/styleguide/contributors"
       }""".stripMargin
  }


}
