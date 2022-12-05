package com.winova.rails.domain

import com.winova.core.graph.{EdgeW, GraphError}
import com.winova.rails.domain.RailNetwork.{RailConnection, RailNetworkGraph, Route}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.defined
import org.scalatest.matchers.should

class RailNetworkTest extends AnyFlatSpec with should.Matchers {

  behavior of "RailNetworkConfig"

  "A-B,A-C,B-D, B-E"
  val network = new RailNetworkGraph(Map(
    "A" -> List(EdgeW("A", "B", 1), EdgeW("A", "C", 2)),
    "B" -> List(EdgeW("B", "D", 3), EdgeW("B", "E", 1))
  ))

  it must "find direct connection" in {
    network.findEdge("A", "B") should not be (empty)
    network.findEdge("C", "B") should be(empty)
    network.findEdge("A", "B").get.weight should be(1)
  }

  it must "validateRoute test" in {
    val routeABD = List("A","B","D")
    network.validateRoute(routeABD).toOption.isDefined shouldBe(true)
    val routeABCD = List("A","B","C","D")
    network.validateRoute(routeABCD).toOption.isDefined shouldBe(false)
  }

}
