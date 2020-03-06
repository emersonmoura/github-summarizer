package scalac.summarizer.handler

import scalac.summarizer.integration.handler.{ContributorHandler, RepositoryHandler}
import scalac.summarizer.integration.model.GitHubContributor
import scalac.summarizer.json.JsonSupport
import scalac.summarizer.model.Contributor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrganizationHandler(repositoryHandler: RepositoryHandler, contributorHandler: ContributorHandler) extends JsonSupport {

  def contributorsRankingByOrganization(organization: String): Future[Set[Contributor]] = {

    def getContributorsByRepository(repositoryUrl: String):Future[Set[GitHubContributor]] = {
      contributorHandler.contributorsByRepository(repositoryUrl)
    }

    def changeModel(repoFunc: Future[Set[GitHubContributor]]) = {
      repoFunc.map(_.map(contributor => Contributor(contributor.login, contributor.contributions)))
    }

    def reduce(contributorsForReduction: List[Set[Contributor]]) = {
      contributorsForReduction.fold(Set.empty[Contributor])((acc, contributors) => acc ++ contributors)
    }

    repositoryHandler.repositoriesByOrganization(organization).flatMap { repositories =>
      Future.sequence(repositories.map(_.contributorsUrl).map(getContributorsByRepository).map(changeModel)).map(reduce)
    }
  }


}
