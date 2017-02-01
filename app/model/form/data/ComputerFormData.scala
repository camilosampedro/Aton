package model.form.data

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class ComputerFormData(
                             ip: String,
                             name: Option[String],
                             SSHUser: String,
                             SSHPassword: String,
                             description: Option[String],
                             roomID: Option[Long]
                           )
