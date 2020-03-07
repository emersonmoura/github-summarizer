package scalac.summarizer.handler

import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scalac.summarizer.integration.handler.{ContributorHandler, RepositoryHandler}
import scalac.summarizer.integration.model.GitHubRepository
import scalac.summarizer.model.Contributor

import scala.concurrent.Future

class DefaultOrganizationHandlerTest extends AsyncFlatSpec with Matchers with AsyncMockFactory {

  val repositoryUrl = "http://api.github.com/v3/repository"
  private val repositoryMock = mock[RepositoryHandler]
  private val contributorMock = mock[ContributorHandler]
  private val organizationHandler = new DefaultOrganizationHandler(repositoryMock, contributorMock)

  "given a failed repositories response" should "return an empty list" in {
    returningContributors(
      Future.successful(Seq(Contributor(name = "name", contributions = 10))),
      Future.failed(new IllegalArgumentException())
    )

    val contributors: Future[Seq[Contributor]] = organizationHandler.contributorsRankingByOrganization("organization")

    contributors map  { it => it should have size 0 }
  }

  "given a failed contributors response" should "return an empty list" in {
    returningContributors(Future.failed(new IllegalArgumentException()))

    val contributors: Future[Seq[Contributor]] = organizationHandler.contributorsRankingByOrganization("organization")

    contributors map  { it => it should have size 0 }
  }

  "given a repository with contributors" should "have its ones processed" in {
    returningContributors(Future.successful(Seq(Contributor(name = "name", contributions = 10))))

    val contributors: Future[Seq[Contributor]] = organizationHandler.contributorsRankingByOrganization("organization")

    contributors map  { it => assert(!it.map(_.contributions).contains(0)) }
  }

  "given a repository with more than one contributor" should "return it sorted by their contributions" in {
    returningContributors(Future.successful(
      Seq(Contributor(name = "first", contributions = 10),
      Contributor(name = "second", contributions = 1))
    ))

    val contributors: Future[Seq[Contributor]] = organizationHandler.contributorsRankingByOrganization("organization")

    contributors map  { it =>
      it.last.name should be("first")
      it.head.name should be("second")
    }

  }

  private def returningContributors(contributors: Future[Seq[Contributor]], future: => Future[Seq[GitHubRepository]] = successFulRepoFut()) = {
    (contributorMock.contributorsByRepository _).stubs(repositoryUrl).returning(contributors)
    returnedUrl(future)
  }

  private def returnedUrl(future: => Future[Seq[GitHubRepository]] = successFulRepoFut()) = {
    (repositoryMock.repositoriesByOrganization _).stubs(*).returning(future)
  }

  private def successFulRepoFut(repositoryUrl: String =  repositoryUrl) = {
    Future.successful(Seq(GitHubRepository(name = "name", contributorsUrl = repositoryUrl)))
  }
}
