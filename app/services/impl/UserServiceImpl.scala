package services.impl

import com.google.inject.{Inject, Singleton}
import dao.UserDAO
import model.User
import services.UserService
import services.state.ActionState

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilosampedro on 16/10/16.
  */
@Singleton
class UserServiceImpl @Inject()(userDAO: UserDAO)(implicit executionContext: ExecutionContext) extends UserService{
  override def add(user: User): Future[ActionState] = userDAO.add(user)

  override def get(username: String): Future[Option[User]] = userDAO.get(username)

  override def checkAndGet(username: String, password: String): Future[Option[User]] = userDAO.checkAndGet(username,password)

  override def listAll: Future[Seq[User]] = userDAO.listAll
}
