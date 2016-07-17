package controllers

import com.google.inject.Inject
import controllers.{routes => normalroutes}
import dao.{ComputerDAO, RoomDAO, UserDAO}
import play.api.i18n.MessagesApi
import services.{ComputerService, SSHOrderService}

import scala.concurrent.ExecutionContext

/**
  * Created by camilo on 7/05/16.
  */
class ComputerController @Inject()(sSHOrderService: SSHOrderService, computerService: ComputerService, roomDAO: RoomDAO, computerDAO: ComputerDAO, val messagesApi: MessagesApi)(implicit userDAO: UserDAO, executionContext: ExecutionContext) extends ControllerWithNoAuthRequired
