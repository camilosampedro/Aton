package test

import play.api.cache.CacheApi

import scala.concurrent.duration.Duration

/**
  * This solves Cache Exceptions.
  * http://stackoverflow.com/questions/31041842/error-with-play-2-4-tests-the-cachemanager-has-been-shut-down-it-can-no-longe
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class FakeCache extends CacheApi {
  override def set(key: String, value: Any, expiration: Duration): Unit = {}

  override def get[T](key: String)(implicit evidence$2: ClassManifest[T]): Option[T] = None

  override def getOrElse[A](key: String, expiration: Duration)(orElse: => A)(implicit evidence$1: ClassManifest[A]): A = orElse

  override def remove(key: String): Unit = {}
}
