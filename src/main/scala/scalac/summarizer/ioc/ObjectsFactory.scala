package scalac.summarizer.ioc

import scalac.summarizer.handler.OrganizationHandler
import scalac.summarizer.http.ClientHandler
import scalac.summarizer.integration.handler.{GitHubContributorHandler, GitHubRepositoryHandler}
import scalac.summarizer.router.OrganizationRouter

object ObjectsFactory {

  def createOrganizationRouter = {
    new OrganizationRouter(createOrganizationHandler)
  }

  def createOrganizationHandler = {
    new OrganizationHandler(createGitHubRepositoryHandler, createContributorHandler)
  }

  def createGitHubRepositoryHandler = {
    new GitHubRepositoryHandler(new ClientHandler())
  }

  def createContributorHandler = {
    new GitHubContributorHandler(new ClientHandler())
  }



}
