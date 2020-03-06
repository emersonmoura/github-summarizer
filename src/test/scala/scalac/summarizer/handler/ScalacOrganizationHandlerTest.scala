package scalac.summarizer.handler

import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scalac.summarizer.integration.handler.{ContributorHandler, RepositoryHandler}
import scalac.summarizer.integration.model.GitHubRepository
import scalac.summarizer.model.Contributor

import scala.concurrent.Future

class ScalacOrganizationHandlerTest extends AsyncFlatSpec with Matchers with AsyncMockFactory {

  private val repositoryMock = mock[RepositoryHandler]
  private val contributorMock = mock[ContributorHandler]
  private val organizationHandler = new ScalacOrganizationHandler(repositoryMock, contributorMock)

  "given a repository with contributors" should "have its ones processed" in {
    returningContributors(Set(Contributor(name = "name", contributions = 10)))

    val contributors: Future[Seq[Contributor]] = organizationHandler.contributorsRankingByOrganization("organization")

    contributors map  { it => assert(!it.map(_.contributions).contains(0)) }
  }

  "given a repository with more than one contributor" should "return it sorted by their contributions" in {
    returningContributors(Set(Contributor(name = "first", contributions = 10), Contributor(name = "second", contributions = 1)))

    val contributors: Future[Seq[Contributor]] = organizationHandler.contributorsRankingByOrganization("organization")

    contributors map  { it =>
      it.last.name should be("first")
      it.head.name should be("second")
    }

  }

  private def returningContributors(contributors: Set[Contributor]) = {
    val repositoryUrl = "http://api.github.com/v3/repository"
    returningTheUrl(repositoryUrl)
    (contributorMock.contributorsByRepository _).expects(repositoryUrl).returning(Future.successful(contributors))
  }

  private def returningTheUrl(githubUrl: String) : Unit = {
    (repositoryMock.repositoriesByOrganization _).expects(*)
      .returning(Future.successful(Seq(GitHubRepository(name = "name", contributorsUrl = githubUrl))))
  }
}
