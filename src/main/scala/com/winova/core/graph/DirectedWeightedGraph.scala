package com.winova.core.graph

import com.winova.core.graph.DepthFirstSearch.{depthFirstEdgeVisitorWithTerminationPredicate, logger}
import com.winova.core.graph.*

import scala.collection.IterableOnce.iterableOnceExtensionMethods
import scala.collection.MapView
import scala.collection.mutable.ListBuffer
import com.winova.core.graph.{Edge, EdgeW}
import com.winova.rails.service.ItineraryService.logger

class DirectedWeightedGraph[N, W](val network: Map[N, List[EdgeW[N, W]]]) {
  type NumWeight = Int | Double

    def this(edges: List[EdgeW[N, W]]) = this(Edge.edgeToMap[N, EdgeW[N, W]](edges))

    def edges: List[EdgeW[N, W]] = network.values.flatMap(_.toList).toList

    def findVertex(predicate: N => Boolean) = vertices.find(predicate)

    def vertices: List[N] = network.keys.toList

    def outConnections(vertex: N): List[EdgeW[N, W]] = network.get(vertex).map(_.toList).getOrElse(List())

    /** a direct route is an edge * */
    def findEdge(fromStn: N, toStn: N): Option[EdgeW[N, W]] = {
      network.get(fromStn).flatMap(_.find(_.toVertex.equals(toStn)))
    }
  }

  object DirectedWeightedGraph {

    def maxLengthReachedPredicateGen[N,W](maxLength:Int) : (p:GPath[N,EdgeW[N,W]]) => Boolean = (p:GPath[N,EdgeW[N,W]]) => p.length() == maxLength

    def countPathWithExactLength[N, W](graph: DirectedWeightedGraph[N, W], start: N, end: N, exactLength: Int): Int = {
      val terminalPredicate = maxLengthReachedPredicateGen[N,W].apply(exactLength)
      val collectFunction: GPath[N, EdgeW[N, W]] => Option[Int] = p => if (p.terminus().equals(end) && p.length() == exactLength) Some(1) else None
      val routes = DepthFirstSearch.depthFirstEdgeVisitorWithTerminationPredicate(graph, start, collectFunction, terminalPredicate)
      routes.sum
    }

    //For testing
    def findPathWithMaxLength[N, W](graph: DirectedWeightedGraph[N, W], start: N, end: N, maxLength: Int): List[GPath[N, _]] = {
      val terminalPredicate = maxLengthReachedPredicateGen[N,W].apply(maxLength)
      val collectFunction: GPath[N, EdgeW[N, W]] => Option[GPath[N, EdgeW[N, W]]] = p => if (p.terminus().equals(end) && p.length() <= maxLength) Some(p) else None
      val routes = DepthFirstSearch.depthFirstEdgeVisitUntilMaxLength(graph, start, collectFunction, maxLength)
      routes
    }

    def countPathWithMaxLength[N, W](graph: DirectedWeightedGraph[N, W], start: N, end: N, maxLength: Int): Int = {
      val terminalPredicate = maxLengthReachedPredicateGen[N,W].apply(maxLength)
      val collectFunction: GPath[N, EdgeW[N, W]] => Option[Int] = p => if (p.terminus().equals(end) && p.length() <= maxLength) Some(1) else None
      val routes = DepthFirstSearch.depthFirstEdgeVisitUntilMaxLength(graph, start, collectFunction, maxLength)
      routes.sum
    }

    def countRoutesWithDistanceStrictlyLessThan[N, W](graph: DirectedWeightedGraph[N, W], start: N, end: N, maxDistance: Double)(using cost: DistanceFunction[N, EdgeW[N, W]]): Int = {
      val terminalPredicate = (p: GPath[N, EdgeW[N, W]]) => cost.totalWeight(p) >= maxDistance
      val collectFunction: GPath[N, EdgeW[N, W]] => Option[Int] = p => if (p.terminus().equals(end) && cost.totalWeight(p) < maxDistance) Some(1) else None
      val routes = depthFirstEdgeVisitorWithTerminationPredicate(graph, start, collectFunction, terminalPredicate)
      routes.sum
    }

  }

