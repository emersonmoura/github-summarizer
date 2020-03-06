package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.util.ByteString
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scalac.summarizer.ClientHandlerMock
import scalac.summarizer.model.Contributor

import scala.concurrent.Future

class GitHubContributorHandlerTest extends AsyncFlatSpec with Matchers with AsyncMockFactory {

  private val httpClientMock: ClientHandlerMock = new ClientHandlerMock()
  private val handler = new GitHubContributorHandler(httpClientMock)

  "given an valid json" should "be processed" in {
    val repository = "myRepo"
    val stripString =  """[
          {
            "login": "dakotalightning",
            "contributions": 29
          },
          {
            "login": "richardtape",
           "contributions": 2
          }
        ]""".stripMargin
      httpClientMock.mock.expects(*)
     .returning(Future.successful(HttpResponse(entity = HttpEntity(ContentTypes.`application/json`,ByteString(stripString)))))

    val contributors: Future[Set[Contributor]] = handler.contributorsByRepository(repository)

    contributors map  { it => assert(!it.map(_.contributions).contains(0)) }
  }

  "given a failed response" should "return an empty list" in {
    httpClientMock.mock.expects(*)
      .returning(Future.failed(new IllegalArgumentException()))

    val contributors: Future[Set[Contributor]] = handler.contributorsByRepository("*")

    contributors map  { it => it should have size 0 }
  }

}
