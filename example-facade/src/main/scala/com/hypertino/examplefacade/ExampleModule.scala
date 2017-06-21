package com.hypertino.examplefacade

import com.hypertino.hyperbus.transport.api.ServiceResolver
import com.hypertino.hyperbus.transport.resolvers.PlainResolver
import scaldi.Module

class ExampleModule extends Module {
  bind [ServiceResolver]          identifiedBy 'serviceResolver       to new PlainResolver(Map.empty)
}
