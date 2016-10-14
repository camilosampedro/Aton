package services.state

/**
  * Created by camilo on 13/10/16.
  */
sealed trait ActionState

object ActionCompleted extends ActionState
object Failed extends ActionState
object NotFound extends ActionState
object Empty extends ActionState
object NotChecked extends ActionState
case class OrderCompleted(result: String, exitCode: Int) extends ActionState