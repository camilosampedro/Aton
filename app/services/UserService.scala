package services

import com.google.inject.ImplementedBy
import model.User
import model.json.LoginJson
import services.impl.UserServiceImpl
import services.state.ActionState

import scala.concurrent.Future

/**
  * Created by camilosampedro on 16/10/16.
  */
@ImplementedBy(classOf[UserServiceImpl])
trait UserService {
  def checkAndGet(form: LoginJson): Future[Option[User]] = checkAndGet(form.username, form.password)
  def add(user: User): Future[ActionState]
  def listAll: Future[Seq[User]]
  def get(username: String): Future[Option[User]]
  def checkAndGet(username: String, password: String): Future[Option[User]]
}
