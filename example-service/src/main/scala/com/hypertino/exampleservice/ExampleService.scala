package com.hypertino.exampleservice

import com.hypertino.exampleservice.api.{TodoItem, TodoList, TodoListGet}
import com.hypertino.hyperbus.Hyperbus
import com.hypertino.hyperbus.model.Ok
import com.hypertino.service.control.api.{Console, Service}
import monix.execution.Ack.Continue
import monix.execution.Scheduler
import scaldi.{Injectable, Injector}
import scala.concurrent.duration._
import scala.util.Success

class ExampleService (console: Console, implicit val injector: Injector) extends Service with Injectable {
  console.writeln("ExampleService started!")

  implicit val scheduler = inject[Scheduler]
  val hyperbus = inject[Hyperbus]

  val handlers = Set {
    hyperbus.commands[TodoListGet].subscribe { implicit command ⇒
      val result = TodoList("This is my todo list", List(
        TodoItem("1", "Make exercise"),
        TodoItem("2", "Buy milk")
      ))

      command.reply(Success(
        Ok(result)
      ))

      Continue
    }
  }

  def stopService(controlBreak: Boolean): Unit = {
    handlers.foreach(_.cancel())
    hyperbus.shutdown(10.seconds).runAsync
    console.writeln("ExampleService stopped.")
  }
}
