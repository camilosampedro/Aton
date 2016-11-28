package config

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * This class contains information about actors. Actually there's only one actor: Computer Checker.
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
class ActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bindActor[ComputerChecker]("computerChecker")
  }
}
