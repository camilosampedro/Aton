package test

import play.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.inject.Injector

import scala.reflect.ClassTag

/**
  * Created by camilosampedro on 11/10/16.
  */
trait Inject {
  val injector: Injector
  /*def inject[T: ClassTag]: T = injector.instanceOf[T]*/
}
