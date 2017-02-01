package model

abstract class StateRef(val id: Int)

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class State(
                  override val id: Int,
                  code: String)
  extends StateRef(id)

case class NotCheckedYet() extends StateRef(0)

case class Connected() extends StateRef(1)

case class WithoutSudoRights() extends StateRef(2)

case class NotConnected() extends StateRef(3)

case class AuthFailed() extends StateRef(4)

case class UnknownError() extends StateRef(5)
