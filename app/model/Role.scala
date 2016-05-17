package model



/**
  * Created by camilo on 15/05/16.
  */
case class Role(id: Int, description: String)

object Role {
  val Administrator = 1
  val NormalUser = 2
}
