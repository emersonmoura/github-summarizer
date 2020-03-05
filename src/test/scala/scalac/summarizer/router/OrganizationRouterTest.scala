package scalac.summarizer.router

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._

class OrganizationRouterTest extends FlatSpec with Matchers with ScalatestRouteTest {
  val route = new OrganizationRouter(null).route

  "The Hello request" should "say hello" in {
    Get("/hello") ~> route ~> check {
      responseAs[String] shouldEqual "<h1>Say hello to akka-http</h1>"
    }
  }
}
