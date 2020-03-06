package scalac.summarizer.integration.handler

import scalac.summarizer.integration.model.GitHubContributor

import scala.concurrent.Future

trait ContributorHandler {
  def contributorsByRepository(organization: String): Future[Set[GitHubContributor]]
}
