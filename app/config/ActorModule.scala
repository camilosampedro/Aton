package config

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * This class contains information about actors. Actually there's only one actor: Computer Checker.
  * Created by Camilo Sampedro <camilo.sampedro@udea.edu.co> on 21/05/16.
  */
class ActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bindActor[ComputerChecker]("computerChecker")
  }
}
