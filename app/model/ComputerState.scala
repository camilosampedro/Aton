package model

/**
  * POJO with the basic Computer State information (Used by the ComputerState DAO, Service and Controller)
  */
case class ComputerState(
                          computerIp: String,
                          registeredDate: java.sql.Timestamp,
                          description: String
                        )
