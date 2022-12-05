package com.winova.core.graph

import com.winova.rails.domain.RailNetwork
import com.winova.rails.domain.RailNetwork.{RailNetworkGraph, RailStation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.List

class DirectedWeightedGraphTest extends AnyFlatSpec with should.Matchers {

  behavior of "DirectedGraph"

  "1-2, 2-3, 3-4, 4-5, 2-5"
  val network = new DirectedWeightedGraph(List(EdgeW(1, 2, 1), EdgeW(2, 3, 2), EdgeW(2, 5, 10), EdgeW(3, 4, 1), EdgeW(4, 5, 1)))

  it must "find direct connection" in {
    network.findEdge(2, 5) should not be (empty)
    network.findEdge(2, 4) should be(empty)
    network.findEdge(2, 5).get.weight should be(10)
  }

  it must "explore routes with max length" in {
    val network2 = new DirectedWeightedGraph(List(EdgeW(1, 2, 10), EdgeW(2, 3, 7), EdgeW(3, 2, 5)))
    DirectedWeightedGraph.findPathWithMaxLength(network2,1, 3, 5).map(_.asStopList()) should be(List(List(1, 2,3),List(1, 2,3,2,3)))
    DirectedWeightedGraph.findPathWithMaxLength(network2,1, 3, 2).map(_.asStopList() ) should be(List(List(1, 2,3)))
  }

  it must "nodes return vertices" in {
    network.vertices should contain theSameElementsAs (List(1, 2, 3, 4, 5))
  }

  it must "outConnections all ajascent edges " in {
    network.outConnections(2).size should be(2)
  }

  it must "countPathWithExactLength" in {
    DirectedWeightedGraph.countPathWithExactLength(network,1,5,4) should be(1)
    DirectedWeightedGraph.countPathWithExactLength(network,1,5,3) should be(0)
    DirectedWeightedGraph.countPathWithExactLength(network,1,5,2) should be(1)
  }

  it must "countPathWithMaxLength" in {
    DirectedWeightedGraph.countPathWithMaxLength(network,1,5,4) should be(2)
  }

  it must "countRoutesWithDistanceStrictlyLessThan (1)" in {
    given railDistance : DistanceFunction[Int,EdgeW[Int,Int]] = e => e.fromVertex + e.toVertex
    DirectedWeightedGraph.countRoutesWithDistanceStrictlyLessThan(network,1,3,7) should be(0)
  }

  it must "countRoutesWithDistanceStrictlyLessThan (2)" in {
    given railDistance2 : DistanceFunction[Int,EdgeW[Int,Int]] = e => e.weight
    DirectedWeightedGraph.countRoutesWithDistanceStrictlyLessThan(network,1,3,7) should be(1)
  }
}
