package model

abstract class StateRef(val id: Int)

/**
  * Created by camilo on 22/05/16.
  */
case class State(
                  override val id: Int,
                  code: String)
  extends StateRef(id)

case class Connected() extends StateRef(1)

case class NotConnected() extends StateRef(2)

case class AuthFailed() extends StateRef(3)

case class UnknownError() extends StateRef(4)
