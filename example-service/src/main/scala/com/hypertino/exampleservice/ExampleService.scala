package com.hypertino.exampleservice

import com.hypertino.exampleservice.api._
import com.hypertino.hyperbus.Hyperbus
import com.hypertino.hyperbus.model.{Accepted, Created, EmptyBody, ErrorBody, NoContent, NotFound, Ok, ResponseBase}
import com.hypertino.hyperbus.subscribe.Subscribable
import com.hypertino.service.control.api.{Console, Service}
import monix.eval.Task
import monix.execution.Ack.Continue
import monix.execution.Scheduler

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Success

class ExampleService (console: Console, hyperbus: Hyperbus, implicit val scheduler: Scheduler) extends Service with Subscribable {
  console.writeln("ExampleService started!")

  var todoList = TodoList("This is my todo list", List(
    TodoItem("1", "Make exercise"),
    TodoItem("2", "Buy milk")
  ))
  var counter = 2
  val handlers = hyperbus.subscribe(this)

  def onTodoListItemsGet(implicit r: TodoListItemsGet) = Task.now[ResponseBase] {
    Ok(todoList)
  }

  def onTodoListItemsPost(implicit r: TodoListItemsPost) = {
    counter += 1
    todoList = todoList.copy(
      items = todoList.items :+ TodoItem(counter.toString, r.body.title)
    )
    Task.now[ResponseBase](Created(EmptyBody))
  }

  def onTodoListItemGet(implicit r: TodoListItemGet) = Task.now[ResponseBase] {
    todoList.items.find(_.id == r.id) match {
      case Some(item) ⇒ Ok(item)
      case None ⇒ NotFound(ErrorBody("item-not-found", Some(s"Item ${r.id} is not found")))
    }
  }

  def onTodoListItemDelete(implicit r: TodoListItemDelete) = Task.now[ResponseBase] {
    counter += 1
    todoList = todoList.copy(
      items = todoList.items.filterNot(_.id == r.id)
    )
    Accepted(EmptyBody)
  }

  def stopService(controlBreak: Boolean, timeout: FiniteDuration): Future[Unit] = Future {
    handlers.foreach(_.cancel())
    console.writeln("ExampleService stopped.")
  }
}
