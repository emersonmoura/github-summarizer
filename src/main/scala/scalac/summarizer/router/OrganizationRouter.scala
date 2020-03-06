package scalac.summarizer.router

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import scalac.summarizer.WebServer._
import scalac.summarizer.handler.OrganizationHandler
import scalac.summarizer.json.ServerJsonSupport
import scalac.summarizer.model.Contributor
import spray.json.DefaultJsonProtocol

class OrganizationRouter(organizationHandler: OrganizationHandler) extends ServerJsonSupport {

  val route =
    path("org" / Segment / "contributors") { name =>
        get {
          onSuccess(organizationHandler.contributorsRankingByOrganization(name)){ contributors =>
            complete(contributors)
          }
        }
    }

}
