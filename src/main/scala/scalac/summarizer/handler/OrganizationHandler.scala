package scalac.summarizer.handler

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import scalac.summarizer.json.JsonSupport
import scalac.summarizer.model.{Contributor, HttpClient}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

class OrganizationHandler(httpClient: HttpClient) extends JsonSupport {
  def getRankingByOrganization(organization: String): Future[Contributor] = {
    val result = httpClient.sendRequest(HttpRequest(uri = "url")).flatMap(response => Unmarshal(response).to[Contributor])
    result
  }


}
