package scalac.summarizer.handler

import scalac.summarizer.integration.handler.{ContributorHandler, RepositoryHandler}
import scalac.summarizer.integration.model.GitHubRepository
import scalac.summarizer.json.JsonSupport
import scalac.summarizer.model.Contributor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultOrganizationHandler(repositoryHandler: RepositoryHandler, contributorHandler: ContributorHandler) extends JsonSupport with OrganizationHandler{

  def contributorsRankingByOrganization(organization: String): Future[Seq[Contributor]] = {

    def getContributorsByRepository(repositoryUrl: String):Future[Seq[Contributor]] = {
      contributorHandler.contributorsByRepository(repositoryUrl) fallbackTo Future.successful(Seq.empty[Contributor])
    }

    def reduceAndSort(contributorsForReduction: Seq[Seq[Contributor]]):Seq[Contributor] = {
      contributorsForReduction.fold(Seq.empty[Contributor])(accumulator).sortBy(_.contributions)
    }
    repositoryHandler.repositoriesByOrganization(organization).fallbackTo(repositoryFallback).flatMap { repositories =>
      Future.sequence(repositories.map(_.contributorsUrl).map(getContributorsByRepository)).map(reduceAndSort)
    }
  }

  private def accumulator = (acc:Seq[Contributor], contributors:Seq[Contributor]) => acc ++ contributors

  private def repositoryFallback = {
    Future.successful(Seq.empty[GitHubRepository])
  }


}
