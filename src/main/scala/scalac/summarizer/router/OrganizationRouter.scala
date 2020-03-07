package scalac.summarizer.router

import scalac.summarizer.WebServer._
import scalac.summarizer.handler.OrganizationHandler
import scalac.summarizer.json.ServerJsonSupport

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
