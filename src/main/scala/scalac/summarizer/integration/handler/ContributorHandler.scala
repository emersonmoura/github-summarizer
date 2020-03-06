package scalac.summarizer.integration.handler

import scalac.summarizer.model.Contributor

import scala.concurrent.Future

trait ContributorHandler {
  def contributorsByRepository(repositoryUrl: String): Future[Set[Contributor]]
}
