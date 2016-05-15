package model.form.data

/**
  * Created by camilo on 7/05/16.
  */
case class ComputerFormData(
                             ip: String,
                             name: Option[String],
                             SSHUser: String,
                             SSHPassword: String,
                             description: Option[String],
                             roomID: Option[Long]
                           )
