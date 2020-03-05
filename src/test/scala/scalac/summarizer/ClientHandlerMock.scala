package scalac.summarizer

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import org.scalamock.scalatest.MockFactory
import scalac.summarizer.http.HttpClient

import scala.concurrent.Future

class ClientHandlerMock extends HttpClient with MockFactory {
  val mock = mockFunction[HttpRequest, Future[HttpResponse]]
  override def sendRequest(httpRequest: HttpRequest)(implicit actorSystem: ActorSystem): Future[HttpResponse] = mock(httpRequest)
}
