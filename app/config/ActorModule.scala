package config

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Inject}
import play.api.libs.concurrent.AkkaGuiceSupport
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by camilo on 21/05/16.
  */
class ActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bindActor[ComputerChecker]("computerChecker")
  }
}
