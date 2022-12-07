package com.winova.rails.service

import com.winova.core.graph.{DirectedWeightedGraph, DistanceFunction, EdgeW, GraphError}
import com.winova.rails.domain.RailNetwork.{RailConnection, RailNetworkGraph}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ItineraryServiceTest extends AnyFlatSpec with should.Matchers {

  behavior of "ItineraryService"

  "A-B,A-C,B-D, B-E"
  val network = new RailNetworkGraph(Map(
    "A" -> List(EdgeW("A", "B", 1), EdgeW("A", "C", 2)),
    "B" -> List(EdgeW("B", "D", 3), EdgeW("B", "E", 1)),
    //split graph, this is possible in real life
    "X" -> List(EdgeW("X", "Y", 1), EdgeW("X", "Z", 1))
  ))

  val network2 = new RailNetworkGraph(Map(
    "A" -> List(EdgeW("A", "B", 1), EdgeW("A", "C", 2)),
    "B" -> List(EdgeW("B", "D", 3), EdgeW("B", "E", 1)),
    "C" -> List(EdgeW("C", "D", 1)),
    "D" -> List(EdgeW("D", "A", 10)),
    //split graph, this is possible in real life
    "X" -> List(EdgeW("X", "Y", 1), EdgeW("X", "Z", 1))
  ))

  it must "calculate distanceof a valid route" in {
    ItineraryService.routeDistance(List("A", "B", "D"))(network) should be(Right(4))

  }

  it must "not calculate distance of a invalid route" in {
    ItineraryService.routeDistance(List("A", "B", "X"))(network) should be(Left(GraphError.NoSuchRoute))
    ItineraryService.routeDistance(List("A", "A"))(network) should be(Left(GraphError.NoSuchRoute))
  }

  it must "countPathWithExactLength" in {
    ItineraryService.countRoutesWithExactStops(network2,"A","E",2) should be(1)
    ItineraryService.countRoutesWithExactStops(network2,"A","E",4) should be(0)
  }

  it must "countPathWithMaxLength" in {
    ItineraryService.countRoutesWithMaxStops(network2,"A","D",4) should be(2)
  }

  it must "countRoutesWithDistanceStrictlyLessThan" in {
    ItineraryService.countRoutesWithDistanceStrictlyLessThan(network2,"A","D",15) should be(2)
   // ItineraryService.countRoutesWithDistanceStrictlyLessThan(network2,"A","E",15) should be(1)
  }


}
