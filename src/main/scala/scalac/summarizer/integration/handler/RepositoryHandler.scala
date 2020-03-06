package scalac.summarizer.integration.handler

import scalac.summarizer.integration.model.GitHubRepository

import scala.concurrent.Future

trait RepositoryHandler {
  def repositoriesByOrganization(organization: String): Future[Seq[GitHubRepository]]
}
