package com.winova.rails.graph

import com.winova.core.graph.DepthFirstSearch.getClass
import com.winova.core.graph.{DistanceFunction, EdgeW}
import com.winova.rails.domain.RailNetwork.{RailNetworkGraph, RailStation}
import com.winova.rails.domain.{RailNetwork, RailNetworkConfig}
import com.winova.rails.graph.ShortestPathAlgo
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.util
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class ShortestPathAlgoTest extends AnyFlatSpec with should.Matchers {

  val winLandRailNetworkConfig: String = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7"
  val network = RailNetworkConfig.loadNetworkMapFromString(winLandRailNetworkConfig).get


  behavior of "ShortestPathAlgo A*"

  it must "path ordering used by Dijsktra must sort by distance" in {
    given railDistance : DistanceFunction[RailStation,EdgeW[RailStation,Int]] = RailNetwork.railDistanceFunction
    val routeABCDE = List("A","B","C","D","E")
    val routeADE = List("A","D","E")
    val pathABCDE = network.validateRoute(routeABCDE).toOption.get
    val pathADE = network.validateRoute(routeADE).toOption.get
    pathADE.pathCost should be(11)
    pathABCDE.pathCost should be(23)
    ShortestPathAlgo.railPathOrdering.gt(pathABCDE,pathADE) should be(true)
  }

  it must "shortest path from A to E" in {
    val shortPathAE = ShortestPathAlgo.shortestPath("A","E")(network)
    val pathStops : List[RailStation] = shortPathAE.get.asStopList()
    pathStops should contain theSameElementsAs (List("A","B","C","E"))
  }

  it must "shortest path from A to X does not exist" in {
    val network = RailNetworkConfig.loadNetworkMapFromString("AB5, BC4, XY3").get
    ShortestPathAlgo.shortestPath("A","X")(network).isFailure should be(true)
  }

  it must "shortest path from A to A does not exist" in {
    val network = RailNetworkConfig.loadNetworkMapFromString("AB5, BC4, XY3").get
    ShortestPathAlgo.shortestPath("A","A")(network).isFailure should be(true)
  }

}
