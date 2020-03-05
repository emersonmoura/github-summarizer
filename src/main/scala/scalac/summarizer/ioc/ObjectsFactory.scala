package scalac.summarizer.ioc

import scalac.summarizer.handler.OrganizationHandler
import scalac.summarizer.model.ClientHandler
import scalac.summarizer.router.OrganizationRouter

object ObjectsFactory {

  def createOrganizationRouter = {
    new OrganizationRouter(new OrganizationHandler(new ClientHandler()))
  }

}
