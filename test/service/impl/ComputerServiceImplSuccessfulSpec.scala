package service.impl

import java.sql.Timestamp

import services.impl.ComputerServiceImpl
import services.state
import org.scalatest._
import Matchers._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by camilosampedro on 6/11/16.
  */
class ComputerServiceImplSuccessfulSpec extends ComputerServiceImplSpec {
  val sSHOrderService = mockSSHOrderService(state.ActionCompleted)
  val computerDAO = mockComputerDAO(state.ActionCompleted)

  val computerService = new ComputerServiceImpl(sSHOrderService, computerDAO)(executionContext)

  "Computer Service on successful operations" should {
    "return ActionCompleted on adding a new computer" in {
      val result = computerService.add(computer1)
      assertState(result, state.ActionCompleted)
    }

    "return ActionCompleted on editing a computer" in {
      val result = computerService.edit(computer1)
      assertState(result, state.ActionCompleted)
    }

    "return a sequence of (Computer, computer state, list of connected users) when listing all" in {
      val result = waitFor(computerService.listAll)
      assert(result.nonEmpty)
    }

    "return the latest computer state of computers when listing all" in {
      val result = waitFor(computerService.listAll)
      val firstRow = result.head
      val firstComputer = firstRow._1
      if (firstComputer.ip == computer1ip) {
        firstRow._2 shouldBe defined
        firstRow._2 match {
          case Some(state) =>
            assert(state._1.registeredDate.toString === stateDate12)
            assert(state._2.nonEmpty)
          case _ =>
        }
      } else {
        assert(firstRow._1.ip === computer2ip)
        firstRow._2 shouldBe defined
        firstRow._2 match {
          case Some(state) =>
            assert(state._1.registeredDate.compareTo(Timestamp.valueOf(stateDate22)) === 0)
            assert(state._2.nonEmpty)
          case _ =>
        }
      }
    }

    "return a single computer with its latest status when accessing get method" in {
      val result = waitFor(computerService.get(computer1ip))
      result shouldBe defined
      result match {
        case Some(computerWithStatus) =>
          assert(computerWithStatus._1 === computer1)
          computerWithStatus._2 shouldBe defined
          computerWithStatus._2 match {
            case Some(status) =>
              assert(status._1.registeredDate.toString === stateDate12)
              assert(status._2.nonEmpty)
            case _ =>
          }
        case _ =>
      }
    }

    "return a single computer when accessing getSingle method" in {
      val result = waitFor(computerService.getSingle(computer1ip))
      result shouldBe defined
      result match {
        case Some(computer) =>
          assert(computer.ip === computer1ip)
        case _ =>
      }
    }

    "return several computers when accessing getSeveral method" in {
      val result = waitFor(computerService.getSeveral(List(computer1ip,computer2ip)))
      assert(result.nonEmpty)
      assert(result.head === computer1)
      assert(result.last === computer2)
    }

    "return ActionCompleted on sending a message to a computer" in {
      val result = computerService.sendMessage(computer1ip, "Hello!")
      assertState(result, state.ActionCompleted)
    }

    "return ActionCompleted on deleting a computer" in {
      val result = computerService.delete(computer1ip)
      assertState(result, state.ActionCompleted)
    }

    "return ActionCompleted on shutting down a computer" in {
      val result = computerService.shutdown(computer1ip)
      assertState(result, state.ActionCompleted)
    }

    "return ActionCompleted on shutting down several computers" in {
      val result = computerService.shutdown(List(computer1ip,computer2ip))
      assertState(result, state.ActionCompleted)
    }

    "return OrderCompleted with stdout and exit code of the execution when installing a package in a computer" in {
      val result = computerService.installAPackage(computer1ip,"mysql")
      assertStateWithResult(result,state.OrderCompleted("installed", 0),"installed", 0)
    }

    "return OrderCompleted with stdout and exit code of the execution when upgrading a computer" in {
      val result = computerService.upgrade(computer1ip)
      assertStateWithResult(result,state.OrderCompleted("upgraded", 0),"upgraded", 0)
    }

    "return OrderCompleted with stdout and exit code of the execution when unfreezing a computer" in {
      val result = computerService.unfreeze(computer1ip)
      assertStateWithResult(result,state.OrderCompleted("unfreezed", 0),"unfreezed", 0)
    }

    "return ActionCompleted when blocking a page on a computer" in {
      val result = computerService.blockPage(computer1ip, urlForTesting)
      assertState(result, state.ActionCompleted)
    }
  }
}
