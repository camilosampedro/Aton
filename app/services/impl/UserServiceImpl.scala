package services.impl

import com.google.inject.{Inject, Singleton}
import dao.UserDAO
import model.User
import org.mindrot.jbcrypt.BCrypt
import services.UserService
import services.state.ActionState

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilosampedro on 16/10/16.
  */
@Singleton
class UserServiceImpl @Inject()(userDAO: UserDAO)(implicit executionContext: ExecutionContext) extends UserService{
  private val salt = BCrypt.gensalt()

  override def add(user: User): Future[ActionState] = userDAO.add(user.copy(password = BCrypt.hashpw(user.password, salt)))

  override def get(username: String): Future[Option[User]] = userDAO.get(username)

  override def checkAndGet(username: String, password: String): Future[Option[User]] = userDAO.get(username).map{
    case Some(user) if BCrypt.checkpw(password, user.password) => Some(user)
    case _ => None
  }

  override def listAll: Future[Seq[User]] = userDAO.listAll
}
