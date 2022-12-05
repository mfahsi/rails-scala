package com.winova.rails.service

import com.winova.core.graph.*
import com.winova.rails.domain.RailNetwork.{RailNetworkGraph, RailStation}
import com.winova.rails.domain.{RailNetwork, RailNetworkConfig}
import com.winova.rails.graph.ShortestPathAlgo
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.concurrent.TimeLimitedTests
import org.scalatest.time.Span
import scala.collection.mutable.ListBuffer
import org.scalatest.time.SpanSugar._
import scala.language.postfixOps

/**
 * we will do a simle and quick check that system works with 10 times current input data.
 */

class CapacityAndPerfCheckTest extends AnyFlatSpec with should.Matchers with TimeLimitedTests {

  val timeLimit = 8000 millis

  //mo than 4 times the prod network
  val bigNetworkStr = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7,MN5, NO4, OP8, PO8, PQ6, MP5, NQ2, QN3, MQ7, EF9, JO9, NA11, PC11,BM6,XC6, XY2,XT4,XI9,WG2"

  val railNetwork = RailNetworkConfig.loadNetworkMapFromString(bigNetworkStr).get

  behavior of "Basic Performance and Memory Check"


  it must "complete countRoutesWithMaxStops 10 stops C to P, C to  without failure within memory limit" in {
      ItineraryService.countRoutesWithMaxStops(railNetwork, "C", "F", 10) should be(88)
      ItineraryService.countRoutesWithMaxStops(railNetwork, "C", "P", 10) should be(79)
  }

  it must "complete countRoutesWithExactStops A to P in 10 stops" in {
    ItineraryService.countRoutesWithExactStops(railNetwork, "A", "P", 10) should be(70)
  }

  it must "complete shortestPath A to P" in {
    RailNetwork.railDistanceFunction.totalWeight(ShortestPathAlgo.shortestPath("A","P")(railNetwork).get) should be(16)
  }

  it must "countRoutesWithDistanceStrictlyLessThan C to C less than 60" in {
    ItineraryService.countRoutesWithDistanceStrictlyLessThan(railNetwork,"C","C", 60) should be(1293)
  }
}
