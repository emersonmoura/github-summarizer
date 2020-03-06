package scalac.summarizer.router

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import scalac.summarizer.handler.OrganizationHandler
import scalac.summarizer.WebServer._

class OrganizationRouter(organizationHandler: OrganizationHandler) {

  val route =
    path("org" / Segment / "contributors") { name =>
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Say hello to akka-http $name</h1>"))
        }
    }

}
