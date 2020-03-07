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
    val json = """[
                 |  {
                 |    "name": "styleguide",
                 |    "contributors_url": "https://api.g/styleguide/contributors"
                 |  },
                 |  {
                 |    "name": "payola",
                 |    "contributors_url": "https://api.g/payola/contributors"
                 |  },
                 |  {
                 |    "name": "ubc-ceih",
                 |    "contributors_url": "https://api.g/ubc-ceih/contributors"
                 |  }
                 |]""".stripMargin

    httpClientMock.mockResponse(json)

    val contributors: Future[Seq[GitHubRepository]] = handler.repositoriesByOrganization(organization)

    contributors map  { it => assert(!it.map(_.contributorsUrl).contains(null)) }
  }

  "given a failed response" should "return an empty list" in {
    httpClientMock.mock.expects(*).returning(Future.failed(new IllegalArgumentException()))

    val contributors: Future[Seq[GitHubRepository]] = handler.repositoriesByOrganization("*")

    contributors map  { it => it should have size 0 }
  }

  "given an response with next pages" should "follow them" in {
    val organization = "myOrg"
    val firstPage = "https://api.github.com/organizations/3430433/repos?page=1"
    val nextPage = "https://api.github.com/organizations/3430433/repos?page=2"
    val firstPagination = s"<$nextPage>; rel=next, <$nextPage>; rel=last"
    val lastPagination = s"<$firstPage>; rel=prev, <$nextPage>; rel=last"
    val json = """[
                 |  {
                 |    "name": "styleguide",
                 |    "contributors_url": "https://api.g/styleguide/contributors"
                 |  }
                 |]""".stripMargin
    val firstHeaders = immutable.Seq[HttpHeader](RawHeader("Link", firstPagination))
    val secondHeaders = immutable.Seq[HttpHeader](RawHeader("Link", lastPagination))

    httpClientMock.mockResponse(json, firstHeaders)

    httpClientMock.mockResponse(json, secondHeaders)

    val contributors: Future[Seq[GitHubRepository]] = handler.repositoriesByOrganization(organization)

    contributors map  { it => it should have size 2 }
  }

  "given an response without next pages" should "follow nothing" in {
    val organization = "myOrg"
    val firstPage = "https://api.github.com/organizations/3430433/repos?page=1"
    val nextPage = "https://api.github.com/organizations/3430433/repos?page=2"
    val firstPagination = s"<$nextPage>; rel=prev, <$nextPage>; rel=last"
    val lastPagination = s"<$firstPage>; rel=prev, <$nextPage>; rel=last"
    val json = """[
                 |  {
                 |    "name": "styleguide",
                 |    "contributors_url": "https://api.g/styleguide/contributors"
                 |  }
                 |]""".stripMargin
    val firstHeaders = immutable.Seq[HttpHeader](RawHeader("Link", firstPagination))
    val secondHeaders = immutable.Seq[HttpHeader](RawHeader("Link", lastPagination))

    httpClientMock.mockResponse(json, firstHeaders)

    httpClientMock.mockResponse(json, secondHeaders)

    val contributors: Future[Seq[GitHubRepository]] = handler.repositoriesByOrganization(organization)

    contributors map  { it => it should have size 1 }
  }


}
