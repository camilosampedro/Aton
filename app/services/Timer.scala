package services

/**
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
trait Timer {
  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    play.Logger.debug("Elapsed time: " + (t1 - t0) / 60000000000L + "ns")
    result
  }
}
