package com.hypertino.exampleservice

import com.hypertino.hyperbus.Hyperbus
import com.hypertino.hyperbus.transport.api.ServiceResolver
import com.hypertino.hyperbus.transport.resolvers.{PlainEndpoint, PlainResolver}
import com.hypertino.service.config.ConfigLoader
import com.hypertino.service.control.{ConsoleServiceController, RuntimeShutdownMonitor, StdConsole}
import com.hypertino.service.control.api.{Console, Service, ServiceController, ShutdownMonitor}
import com.typesafe.config.Config
import monix.execution.Scheduler
import scaldi.Module

object EntryPoint extends Module {
  bind [Config] to ConfigLoader()
  bind [ServiceResolver]        identifiedBy 'serviceResolver       to new PlainResolver(Map("example-service" → Seq(PlainEndpoint("loclalhost", Some(10050)))))
  bind [Scheduler] identifiedBy 'scheduler to monix.execution.Scheduler.Implicits.global
  bind [HyperbusFactory] identifiedBy 'hbFactory to new HyperbusFactory(inject[Config], injector)
  bind [Hyperbus] identifiedBy 'hyperbus to inject[HyperbusFactory].hyperbus
  bind [Console]                identifiedBy 'console              toNonLazy injected[StdConsole]
  bind [ServiceController]      identifiedBy 'serviceController    toNonLazy injected[ConsoleServiceController]
  bind [ShutdownMonitor]        identifiedBy 'shutdownMonitor      toNonLazy injected[RuntimeShutdownMonitor]
  bind [Service] to injected [ExampleService]

  def main(args: Array[String]): Unit = {
    inject[ServiceController].run()
  }
}
