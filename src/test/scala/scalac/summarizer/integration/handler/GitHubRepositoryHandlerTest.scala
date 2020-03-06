package scalac.summarizer.integration.handler

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.util.ByteString
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scalac.summarizer.ClientHandlerMock
import scalac.summarizer.integration.model.GitHubRepository

import scala.concurrent.Future

class GitHubRepositoryHandlerTest extends AsyncFlatSpec with Matchers with AsyncMockFactory {

  private val httpClientMock: ClientHandlerMock = new ClientHandlerMock()
  private val handler = new GitHubRepositoryHandler(httpClientMock)



  "given an valid json" should "be processed" in {
    val organization = "myOrg"
    val organizationString = """[
                               |  {
                               |    "name": "styleguide",
                               |    "contributors_url": "https://api.github.com/repos/animikii/styleguide/contributors"
                               |  },
                               |  {
                               |    "name": "payola",
                               |    "contributors_url": "https://api.github.com/repos/animikii/payola/contributors"
                               |  },
                               |  {
                               |    "name": "ubc-ceih",
                               |    "contributors_url": "https://api.github.com/repos/animikii/ubc-ceih/contributors"
                               |  }
                               |]""".stripMargin

      httpClientMock.mock.expects(*)
     .returning(Future.successful(HttpResponse(entity = HttpEntity(ContentTypes.`application/json`,ByteString(organizationString)))))

    val contributors: Future[List[GitHubRepository]] = handler.repositoriesByOrganization(organization)

    contributors map  { it => assert(!it.map(_.contributorsUrl).contains(null)) }
  }

}
