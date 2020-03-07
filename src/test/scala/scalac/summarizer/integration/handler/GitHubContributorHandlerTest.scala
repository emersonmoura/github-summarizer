package scalac.summarizer.integration.handler

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

    val contributors: Future[Seq[Contributor]] = handler.contributorsByRepository("*")

    contributors map  { it => it should have size 0 }
  }

  private def contributorJson(contributions: Int, login: String = "dakotalightning") = {
    s"""{
       | "login": "$login",
       | "contributions": $contributions
       }""".stripMargin
  }



}
