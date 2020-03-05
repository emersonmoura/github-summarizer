package scalac.summarizer.handler

import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse}
import akka.util.ByteString
import org.scalamock.scalatest.MockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scalac.summarizer.infra.ClientHandlerMock
import scalac.summarizer.model.Contributor

import scala.concurrent.Future

class OrganizationHandlerTest() extends AsyncFlatSpec with Matchers with MockFactory {
  val httpClientMock: ClientHandlerMock = new ClientHandlerMock()
  private val organizationHandler = new OrganizationHandler(httpClientMock)

  "the class creation" should "be validated" in {
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
      httpClientMock.mock.expects(HttpRequest(uri = "http://api.github.com/v3/"))
     .returning(Future.successful(HttpResponse(entity = HttpEntity(ByteString(stripString)))))

    val futureSum: Future[Contributor] = organizationHandler.getRankingByOrganization(organization)

    futureSum map { it => assert(it == null) }
  }

}
