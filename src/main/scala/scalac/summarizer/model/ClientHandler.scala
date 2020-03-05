package scalac.summarizer.model

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.Http

import scala.concurrent.Future

class ClientHandler extends HttpClient {
  override def sendRequest(httpRequest: HttpRequest)(implicit actorSystem: ActorSystem): Future[HttpResponse] = {
    Http().singleRequest(httpRequest)
  }

  def shutDown()(implicit actorSystem: ActorSystem): Unit = {
    Http().shutdownAllConnectionPools()
  }
}
