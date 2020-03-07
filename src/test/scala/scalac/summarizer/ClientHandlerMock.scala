package scalac.summarizer

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, HttpRequest, HttpResponse}
import akka.util.ByteString
import org.scalamock.scalatest.MockFactory
import scalac.summarizer.http.HttpClient

import scala.collection.immutable
import scala.concurrent.Future

class ClientHandlerMock extends HttpClient with MockFactory {
  val mock = mockFunction[HttpRequest, Future[HttpResponse]]
  override def sendRequest(httpRequest: HttpRequest)(implicit actorSystem: ActorSystem): Future[HttpResponse] = mock(httpRequest)

  def mockResponse(organizationString: String, firstHeaders: immutable.Seq[HttpHeader] = immutable.Seq.empty[HttpHeader]) = {
    val httpEntity = HttpEntity(ContentTypes.`application/json`, ByteString(organizationString))
    mock.expects(*).returning(Future.successful(HttpResponse(headers = firstHeaders, entity = httpEntity)))
  }
}
