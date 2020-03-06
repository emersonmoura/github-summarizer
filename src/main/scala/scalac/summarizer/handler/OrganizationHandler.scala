package scalac.summarizer.handler

import scalac.summarizer.model.Contributor

import scala.concurrent.Future

trait OrganizationHandler {
  def contributorsRankingByOrganization(organization: String): Future[Seq[Contributor]]
}
