package com.hypertino.exampleservice

import com.hypertino.hyperbus.Hyperbus
import com.hypertino.hyperbus.transport.api.ServiceResolver
import com.hypertino.hyperbus.transport.resolvers.{PlainEndpoint, PlainResolver}
import com.hypertino.service.config.ConfigModule
import com.hypertino.service.control.api.{Console, Service, ServiceController, ShutdownMonitor}
import com.hypertino.service.control.{ConsoleServiceController, RuntimeShutdownMonitor, StdConsole}
import monix.execution.Scheduler
import scaldi.{Injectable, Module}

import scala.concurrent.duration._

class ExampleModule extends Module {
  bind [ServiceResolver]        identifiedBy 'serviceResolver       to new PlainResolver(Map("example-service" → Seq(PlainEndpoint("loclalhost", Some(10050)))))
  bind [Scheduler]              identifiedBy 'scheduler             to monix.execution.Scheduler.Implicits.global
  bind [Hyperbus]               identifiedBy 'hyperbus              to injected[Hyperbus]
  bind [Console]                identifiedBy 'console               to injected[StdConsole]
  bind [ServiceController]      identifiedBy 'serviceController     to injected[ConsoleServiceController]
  bind [ShutdownMonitor]        identifiedBy 'shutdownMonitor       to injected[RuntimeShutdownMonitor]
  bind [Service] to injected [ExampleService]
}

object EntryPoint extends Injectable {
  def main(args: Array[String]): Unit = {
    implicit val injector = new ExampleModule :: ConfigModule()
    implicit val scheduler = inject[Scheduler]
    inject[ServiceController].run().andThen {
      case _ ⇒
        inject[Hyperbus].shutdown(10.seconds).runAsync
    }
  }
}
