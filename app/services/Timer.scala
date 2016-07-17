package services

/**
  * Created by camilo on 16/07/16.
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
