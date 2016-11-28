package model


/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class Role(
                 id: Int,
                 description: String)

object Role {
  val Administrator = 1
  val NormalUser = 2
}
