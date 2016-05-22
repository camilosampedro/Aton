package config

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext

import scala.concurrent.duration._

/**
  * Created by camilo on 21/05/16.
  */
@Singleton
class MainAplication @Inject()(@Named("computerChecker") computerChecker: ActorRef, actorSystem: ActorSystem)(implicit ec: ExecutionContext) {
  play.Logger.debug("Computer Checker configured.")
  actorSystem.scheduler.schedule(0.microseconds,5.minutes, computerChecker,"Execute")
}
