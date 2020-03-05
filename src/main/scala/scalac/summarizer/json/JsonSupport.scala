package scalac.summarizer.json


import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import scalac.summarizer.model.Contributor
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit def format = jsonFormat2(Contributor)
  implicit def materialize: ActorMaterializer = ActorMaterializer()
  implicit def actorSystem: ActorSystem = ActorSystem()
}