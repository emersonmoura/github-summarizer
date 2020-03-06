package scalac.summarizer.handler

import scalac.summarizer.integration.handler.{ContributorHandler, RepositoryHandler}
import scalac.summarizer.json.JsonSupport
import scalac.summarizer.model.Contributor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ScalacOrganizationHandler(repositoryHandler: RepositoryHandler, contributorHandler: ContributorHandler) extends JsonSupport with OrganizationHandler{

  def contributorsRankingByOrganization(organization: String): Future[Seq[Contributor]] = {

    def getContributorsByRepository(repositoryUrl: String):Future[Set[Contributor]] = {
      contributorHandler.contributorsByRepository(repositoryUrl)
    }

    def reduceAndSort(contributorsForReduction: Seq[Set[Contributor]]):Seq[Contributor] = {
      contributorsForReduction.fold(Set.empty[Contributor])((acc, contributors) => acc ++ contributors).toSeq.sortBy(_.contributions)
    }

    repositoryHandler.repositoriesByOrganization(organization).flatMap { repositories =>
      Future.sequence(repositories.map(_.contributorsUrl).map(getContributorsByRepository)).map(reduceAndSort)
    }
  }


}
