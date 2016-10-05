package model.form.data

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
case class RoomFormData(name: String,
                        audiovisualResources: Option[String],
                        basicTools: Option[String],
                        laboratoryID: Long)