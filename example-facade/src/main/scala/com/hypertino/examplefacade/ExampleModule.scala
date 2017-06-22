package com.hypertino.examplefacade

import com.hypertino.hyperbus.transport.api.ServiceResolver
import com.hypertino.hyperbus.transport.resolvers.{PlainEndpoint, PlainResolver}
import scaldi.Module

class ExampleModule extends Module {
  bind [ServiceResolver]          identifiedBy 'serviceResolver       to new PlainResolver(Map(
    "hb://todo-service" → Seq(PlainEndpoint("localhost", Some(10050)))
  ))
}
