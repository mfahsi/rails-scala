package com.winova.core.graph

import com.winova.core.graph.DepthFirstSearch.{getClass}
import com.winova.rails.domain.RailNetwork.{RailNetworkGraph, RailStation}
import com.winova.rails.domain.RailNetworkConfig
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.util
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class DepthFirstSearchTest extends AnyFlatSpec with should.Matchers {
  private val logger = com.typesafe.scalalogging.Logger(getClass)

  val winLandRailNetworkConfig: String = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7"
  val network = RailNetworkConfig.loadNetworkMapFromString(winLandRailNetworkConfig).get

  behavior of "depthFirstWithCycles"

  it must "cycle until max length, no duplicates" in {
    val simple = List(EdgeW("A", "B", 1), EdgeW("B", "C", 1), EdgeW("C", "D", 1), EdgeW("D", "C", 2))
    val graph = new DirectedWeightedGraph[String, Int](simple)
    val collect = ListBuffer[String]()
    DepthFirstSearch.depthFirstEdgeVisitUntilMaxLength(graph,"A", (path: GPath[String, EdgeW[String, Int]]) => Some(path.terminus()), 6) should contain theSameElementsAs (List("B", "C", "D", "C", "D", "C"))

  }

  it must "depthFirstEdgeVisitUntilMaxLength collect terminus of each path visited (no target fixed)" in {
    val simple = List(EdgeW("A", "B", 1), EdgeW("B", "C", 1), EdgeW("C", "D", 1), EdgeW("D", "C", 2))
    val graph = new DirectedWeightedGraph[String, Int](simple)
    DepthFirstSearch.depthFirstEdgeVisitUntilMaxLength(graph,"A", (path: GPath[String, EdgeW[String, Int]]) => Some(path.terminus()), 6) should contain theSameElementsAs(List("B", "C", "D", "C", "D", "C"))
  }

  it must "depthFirstEdgeVisitUntilMaxLength collect paths terminating at C" in {
    val simple = List(EdgeW("A", "B", 1), EdgeW("B", "C", 1), EdgeW("C", "D", 1), EdgeW("D", "C", 2))
    val cycle = new GPath[String, EdgeW[String, Int]](EdgeW("C", "D", 1), EdgeW("D", "C", 2))
    val path1 = new GPath[String, EdgeW[String, Int]](EdgeW("A", "B", 1), EdgeW("B", "C", 1))
    val path2 = path1.concatGpath(cycle)
    val path3 = path2.concatGpath(cycle)
    val graph = new DirectedWeightedGraph[String, Int](simple)
    DepthFirstSearch.depthFirstEdgeVisitUntilMaxLength(graph,"A", (path: GPath[String, EdgeW[String, Int]]) => {
      if (path.terminus() == "C") Some(path) else None
    }, 6) should contain theSameElementsAs (List(path1, path2, path3))
  }

}
