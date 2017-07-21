package com.hypertino.exampleservice

import com.hypertino.exampleservice.api._
import com.hypertino.hyperbus.Hyperbus
import com.hypertino.hyperbus.model.{Accepted, Created, EmptyBody, ErrorBody, NoContent, NotFound, Ok}
import com.hypertino.service.control.api.{Console, Service}
import monix.execution.Ack.Continue
import monix.execution.Scheduler

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Success

class ExampleService (console: Console, hyperbus: Hyperbus, implicit val scheduler: Scheduler) extends Service {
  console.writeln("ExampleService started!")

  var todoList = TodoList("This is my todo list", List(
    TodoItem("1", "Make exercise"),
    TodoItem("2", "Buy milk")
  ))
  var counter = 2

  val handlers = Seq (
    hyperbus.commands[TodoListItemsGet].subscribe { implicit command ⇒
      command.reply(Success(
        Ok(todoList)
      ))
      Continue
    },

    hyperbus.commands[TodoListItemsPost].subscribe { implicit command ⇒
      counter += 1
      todoList = todoList.copy(
        items = todoList.items :+ TodoItem(counter.toString, command.request.body.title)
      )
      command.reply(Success(
        Created(EmptyBody)
      ))
      Continue
    },

    hyperbus.commands[TodoListItemGet].subscribe { implicit command ⇒
      command.reply(
        todoList.items.find(_.id == command.request.id) match {
          case Some(item) ⇒ Success(Ok(item))
          case None ⇒ Success(NotFound(ErrorBody("item-not-found", Some(s"Item ${command.request.id} is not found"))))
        }
      )
      Continue
    },

    hyperbus.commands[TodoListItemDelete].subscribe { implicit command ⇒
      counter += 1
      todoList = todoList.copy(
        items = todoList.items.filterNot(_.id == command.request.id)
      )
      command.reply(Success(
        Accepted(EmptyBody)
      ))
      Continue
    }
  )

  def stopService(controlBreak: Boolean, timeout: FiniteDuration): Future[Unit] = Future {
    handlers.foreach(_.cancel())
    console.writeln("ExampleService stopped.")
  }
}
