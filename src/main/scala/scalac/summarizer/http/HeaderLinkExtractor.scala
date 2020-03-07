package scalac.summarizer.http

import akka.http.scaladsl.model.HttpHeader

object HeaderLinkExtractor {

  val DELIMITER = ";"
  val LINK = "link"
  val NEXT = "next"
  val LINK_DELIMITER = "[\\<\\>]"

  def extract(headers: Seq[HttpHeader]): Option[String] = {
    headers.find(_.is(LINK)).map(_.value()).filter(_.contains(NEXT)).flatMap(_.split(DELIMITER).headOption.map(clean))
  }

  private def clean(value: String) = {
    value.replaceAll(LINK_DELIMITER, "")
  }
}
