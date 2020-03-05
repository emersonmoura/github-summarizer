package scalac.summarizer

import akka.http.scaladsl.server.{HttpApp, Route}
import scalac.summarizer.ioc.ObjectsFactory

object WebServer extends HttpApp {
  override def routes: Route =
    concat(ObjectsFactory.createOrganizationRouter.route)
}

object Main extends App{
  override def main(args: Array[String]): Unit = {
    WebServer.startServer("localhost", 8080)
  }
}
