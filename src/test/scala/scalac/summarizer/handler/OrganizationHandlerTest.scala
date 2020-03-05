package scalac.summarizer.handler

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.util.ByteString
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scalac.summarizer.ClientHandlerMock
import scalac.summarizer.model.Contributor

import scala.concurrent.Future

class OrganizationHandlerTest extends AsyncFlatSpec with Matchers with AsyncMockFactory {

  private val httpClientMock: ClientHandlerMock = new ClientHandlerMock()
  private val organizationHandler = new OrganizationHandler(httpClientMock)

  "given an valid json" should "be processed" in {
    val organization = "myOrg"
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

    val contributors: Future[List[Contributor]] = organizationHandler.contributorsRankingByOrganization(organization)

    contributors map { it => assert(it != null) }
  }

}
