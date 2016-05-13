package model

/**
  * POJO with the basic SSH Order information (Used by the SSH Order DAO, Service and Controller)
  */
case class SSHOrder(
                     id: Long,
                     superUser: Boolean,
                     interrupt: Boolean,
                     command: String,
                     result: String,
                     exitCode: Int
                   )
