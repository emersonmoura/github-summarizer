package scalac.summarizer.handler

import scalac.summarizer.integration.handler.{ContributorHandler, RepositoryHandler}
import scalac.summarizer.integration.model.GitHubRepository
import scalac.summarizer.json.JsonSupport
import scalac.summarizer.model.Contributor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ScalacOrganizationHandler(repositoryHandler: RepositoryHandler, contributorHandler: ContributorHandler) extends JsonSupport with OrganizationHandler{

  def contributorsRankingByOrganization(organization: String): Future[Seq[Contributor]] = {

    def getContributorsByRepository(repositoryUrl: String):Future[Set[Contributor]] = {
      contributorHandler.contributorsByRepository(repositoryUrl) fallbackTo Future.successful(Set.empty[Contributor])
    }

    def reduceAndSort(contributorsForReduction: Seq[Set[Contributor]]):Seq[Contributor] = {
      contributorsForReduction.fold(Set.empty[Contributor])(accumulator).toSeq.sortBy(_.contributions)
    }
    repositoryHandler.repositoriesByOrganization(organization).fallbackTo(repositoryFallback).flatMap { repositories =>
      Future.sequence(repositories.map(_.contributorsUrl).map(getContributorsByRepository)).map(reduceAndSort)
    }
  }

  private def accumulator = (acc:Set[Contributor], contributors:Set[Contributor]) => acc ++ contributors

  private def repositoryFallback = {
    Future.successful(Seq.empty[GitHubRepository])
  }


}
