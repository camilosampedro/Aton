package controllers

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{ComputerDAO, RoomDAO, UserDAO}
import play.api.i18n.MessagesApi
import services.{ComputerService, SSHOrderService}

import scala.concurrent.ExecutionContext

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class ComputerController @Inject()(sSHOrderService: SSHOrderService, computerService: ComputerService, roomDAO: RoomDAO, computerDAO: ComputerDAO, val messagesApi: MessagesApi)(implicit userDAO: UserDAO, executionContext: ExecutionContext) extends ControllerWithNoAuthRequired
