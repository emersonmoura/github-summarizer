package scalac.summarizer.router

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest._
import scalac.summarizer.handler.OrganizationHandler
import scalac.summarizer.model.Contributor
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent.Future


class OrganizationRouterTest extends AsyncFlatSpec with Matchers with ScalatestRouteTest with AsyncMockFactory {
  implicit val contributor = jsonFormat(Contributor, "name", "contributions")

  private val handlerMock = mock[OrganizationHandler]
  val route = new OrganizationRouter(handlerMock).route

  "given contributors for returning" should "return their information" in {
    val organization = "scalac"
    def contributors = Set(Contributor(name = "name", contributions = 10))
    (handlerMock.contributorsRankingByOrganization _).expects(organization).returning(Future.successful(contributors))

    Get(s"/org/$organization/contributors") ~> route ~> check {
      responseAs[String] shouldEqual s"${contributors.toJson}"
    }
  }
}
