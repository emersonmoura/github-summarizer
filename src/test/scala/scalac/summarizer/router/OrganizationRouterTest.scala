package scalac.summarizer.router

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._

class OrganizationRouterTest extends FlatSpec with Matchers with ScalatestRouteTest {
  val route = new OrganizationRouter(null).route

  it should "process the right path" in {
    Get("/org/scalac/contributors") ~> route ~> check {
      responseAs[String] shouldEqual "<h1>Say hello to akka-http scalac</h1>"
    }
  }
}
