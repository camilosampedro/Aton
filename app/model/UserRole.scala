package model

sealed trait UserRole

/**
  * Created by camilo on 15/05/16.
  */
object UserRole {
  case object Administrator extends UserRole
  case object NormalUser extends UserRole

  def valueOf(value: String): UserRole = value match{
    case "Administrator" => Administrator
    case "NormalUser" => NormalUser
    case _ => throw new IllegalArgumentException
  }
}
