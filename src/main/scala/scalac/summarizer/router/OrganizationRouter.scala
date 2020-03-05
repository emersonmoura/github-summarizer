package scalac.summarizer.router

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path}
import scalac.summarizer.handler.OrganizationHandler

class OrganizationRouter(organizationHandler: OrganizationHandler) {

  val route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }

}
