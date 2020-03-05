package scalac.summarizer

import akka.http.scaladsl.server.{HttpApp, Route}
import scalac.summarizer.router.OrganizationRouter

object WebServer extends HttpApp {
  override def routes: Route =
    concat(new OrganizationRouter().route)
}

object Main extends App{
  override def main(args: Array[String]): Unit = {
    WebServer.startServer("localhost", 8080)
  }
}
