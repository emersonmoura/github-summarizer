package scalac.summarizer.json

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.{ActorMaterializer, Materializer}
import scalac.summarizer.model.Contributor
import spray.json.DefaultJsonProtocol

trait ServerJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materialize: Materializer = ActorMaterializer()
  implicit val contributor = jsonFormat(Contributor, "name", "contributions")
}