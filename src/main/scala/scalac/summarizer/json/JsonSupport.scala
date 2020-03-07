package scalac.summarizer.json

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.{ActorMaterializer, Materializer}
import scalac.summarizer.integration.model.GitHubRepository
import scalac.summarizer.model.Contributor
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materialize: Materializer = ActorMaterializer()
  implicit val githubRepository = jsonFormat(GitHubRepository, "name", "contributors_url")
  implicit val contributor = jsonFormat(Contributor, "login", "contributions")
}