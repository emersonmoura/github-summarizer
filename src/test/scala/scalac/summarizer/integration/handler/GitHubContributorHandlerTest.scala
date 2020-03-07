package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.RawHeader
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scalac.summarizer.ClientHandlerMock
import scalac.summarizer.model.Contributor
import scala.collection.immutable
import scala.concurrent.Future

class GitHubContributorHandlerTest extends AsyncFlatSpec with Matchers with AsyncMockFactory {

  private val httpClientMock: ClientHandlerMock = new ClientHandlerMock()
  private val handler = new GitHubContributorHandler(httpClientMock)

  "given an valid json" should "be processed" in {
    val json =  s"""[
          ${contributorJson(29)},
          ${contributorJson(2)}
        ]""".stripMargin

    httpClientMock.mockResponse(json)

    val contributors: Future[Seq[Contributor]] = handler.contributorsByRepository("repository")

    contributors map  { it => assert(!it.map(_.contributions).contains(0)) }
  }

  "given a failed response" should "return an empty list" in {
    httpClientMock.mock.expects(*).returning(Future.failed(new IllegalArgumentException()))

    val contributors: Future[Seq[Contributor]] = handler.contributorsByRepository("repository")

    contributors map  { it => it should have size 0 }
  }

  "given an response with next pages" should "follow them" in {
    val firstPage = "https://api.github.com/repos/octokit/auth-basic.js/contributors?page=1"
    val nextPage = "https://api.github.com/repos/octokit/auth-basic.js/contributors?page=2"
    val firstPagination = s"<$nextPage>; rel=next, <$nextPage>; rel=last"
    val lastPagination = s"<$firstPage>; rel=prev, <$nextPage>; rel=last"
    val firstExpectedContributions = 25
    val secondExpectedContributions = 12
    val firstHeaders = immutable.Seq[HttpHeader](RawHeader("Link", firstPagination))
    val secondHeaders = immutable.Seq[HttpHeader](RawHeader("Link", lastPagination))

    httpClientMock.mockResponse(contributorsJson(firstExpectedContributions), firstHeaders)

    httpClientMock.mockResponse(contributorsJson(secondExpectedContributions), secondHeaders)

    val contributors: Future[Seq[Contributor]] = handler.contributorsByRepository("organization")

    contributors map  { it =>
      it should have size 2
      it.last.contributions should be(firstExpectedContributions)
      it.head.contributions should be(secondExpectedContributions)
    }
  }

  "given an response without next pages" should "follow nothing" in {
    val nextPage = "https://api.github.com/organizations/3430433/repos?page=2"
    val firstPagination = s"<$nextPage>; rel=prev, <$nextPage>; rel=last"
    val firstHeaders = immutable.Seq[HttpHeader](RawHeader("Link", firstPagination))

    httpClientMock.mockResponse(contributorsJson(), firstHeaders)

    httpClientMock.mockResponse(contributorsJson(), firstHeaders)

    val contributors: Future[Seq[Contributor]] = handler.contributorsByRepository("organization")

    contributors map  { it => it should have size 1 }
  }

  private def contributorsJson(contributions: Int = 1) = {
    s"""[
        ${contributorJson(contributions)}
       ]""".stripMargin
  }

  private def contributorJson(contributions: Int, login: String = "dakotalightning") = {
    s"""{
       | "login": "$login",
       | "contributions": $contributions
       }""".stripMargin
  }



}
