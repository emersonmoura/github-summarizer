package scalac.summarizer.handler

import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scalac.summarizer.integration.handler.{ContributorHandler, RepositoryHandler}
import scalac.summarizer.integration.model.GitHubRepository
import scalac.summarizer.model.Contributor

import scala.concurrent.Future

class OrganizationHandlerTest extends AsyncFlatSpec with Matchers with AsyncMockFactory {

  private val repositoryMock = mock[RepositoryHandler]
  private val contributorMock = mock[ContributorHandler]
  private val organizationHandler = new OrganizationHandler(repositoryMock, contributorMock)

  "given an valid json" should "be processed" in {
    val repositoryUrl = "http://api.github.com/v3/repository"
    returningTheUrl(repositoryUrl)

    (contributorMock.contributorsByRepository _).expects(repositoryUrl).returning(Future.successful(Set(Contributor(name = "name", contributions = 10))))

    val contributors: Future[Set[Contributor]] = organizationHandler.contributorsRankingByOrganization("organization")

    contributors map  { it => assert(!it.map(_.contributions).contains(0)) }
  }

  private def returningTheUrl(githubUrl: String) = {
    (repositoryMock.repositoriesByOrganization _).expects(*)
      .returning(Future.successful(List(GitHubRepository(name = "name", contributorsUrl = githubUrl))))
  }
}
