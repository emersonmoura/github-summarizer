package scalac.summarizer

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.util.ByteString
import org.scalamock.scalatest.MockFactory
import scalac.summarizer.http.HttpClient

import scala.collection.immutable
import scala.concurrent.Future

class ClientHandlerMock extends HttpClient with MockFactory {
  val mock = mockFunction[HttpRequest, Future[HttpResponse]]
  override def sendRequest(httpRequest: HttpRequest)(implicit actorSystem: ActorSystem): Future[HttpResponse] = mock(httpRequest)

  def mockResponse(jsonString: String, headers: immutable.Seq[HttpHeader] = immutable.Seq.empty[HttpHeader]) = {
    val httpEntity = HttpEntity(ContentTypes.`application/json`, ByteString(jsonString))
    mock.expects(*).returning(Future.successful(HttpResponse(headers = headers, entity = httpEntity)))
  }
}
