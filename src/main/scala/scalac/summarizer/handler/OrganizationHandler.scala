package scalac.summarizer.handler

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import scalac.summarizer.http.HttpClient
import scalac.summarizer.json.JsonSupport
import scalac.summarizer.model.Contributor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class OrganizationHandler(httpClient: HttpClient) extends JsonSupport {

  def contributorsRankingByOrganization(organization: String): Future[List[Contributor]] = {
    httpClient.sendRequest(HttpRequest(uri = "url")).flatMap(response => Unmarshal(response).to[List[Contributor]])
  }


}
