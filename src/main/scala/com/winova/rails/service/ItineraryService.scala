package com.winova.rails.service

import com.winova.core.graph.*
import com.winova.core.graph.DepthFirstSearch.{depthFirstEdgeVisitorWithTerminationPredicate, getClass}
import com.winova.rails.domain.RailNetwork
import com.winova.rails.domain.RailNetwork.{RailNetworkGraph, *}

object ItineraryService {
  private val logger = com.typesafe.scalalogging.Logger(getClass)

  def findRoute(fromStn: RailStation, toStn: RailStation)(graph: RailNetworkGraph): Option[RailConnection] = {
    // val graph: DirectedGraph[RailStation, Int] = network // to help the compiler
    graph.network.get(fromStn).flatMap(_.find(toStn == _.toVertex))
  }

  def routeDistance(route: Route)(graph: RailNetworkGraph): Either[GraphError, Int] = {
    graph.validateRoute(route).map(RailNetwork.railDistanceFunction.totalWeight(_).toInt)
  }

  def countRoutesWithExactStops[N, W](graph: DirectedWeightedGraph[N, W], start: N, end: N, exactStops: Int): Int = {
    val collectFunction: GPath[N, EdgeW[N, W]] => Option[Int] = p => if (p.terminus().equals(end) && p.length() == exactStops) Some(1) else None
    val routes = DepthFirstSearch.depthFirstEdgeVisitUntilMaxLength(graph,start, collectFunction, exactStops)
    routes.sum
  }

  def findRoutesWithMaxStops[N, W](graph: DirectedWeightedGraph[N, W], start: N, end: N, maxStops: Int): List[GPath[N, _]] = {
    val collectFunction: GPath[N, EdgeW[N, W]] => Option[GPath[N, EdgeW[N, W]]] = p => if (p.terminus().equals(end) && p.length() <= maxStops) Some(p) else None
    val routes = DepthFirstSearch.depthFirstEdgeVisitUntilMaxLength(graph,start,  collectFunction, maxStops)
    routes
  }

  def countRoutesWithMaxStops[N, W](graph: DirectedWeightedGraph[N, W], start: N, end: N, maxStops: Int): Int = {
    val collectFunction: GPath[N, EdgeW[N, W]] => Option[Int] = p => if (p.terminus().equals(end) && p.length() <= maxStops) Some(1) else None
    val routes = DepthFirstSearch.depthFirstEdgeVisitUntilMaxLength(graph,start, collectFunction, maxStops)
    routes.sum
  }

  def countRoutesWithDistanceStrictlyLessThan(graph: RailNetworkGraph, start: RailStation, terminus: RailStation, maxDistance: Int): Int = {
    given railDistance : DistanceFunction[RailStation,EdgeW[RailStation,Int]] = RailNetwork.railDistanceFunction
    val result = DirectedWeightedGraph.countRoutesWithDistanceStrictlyLessThan(graph,start,terminus,maxDistance)
    logger.info("countRoutesWithDistanceStrictlyLessThan from {} to {} = {}",start,terminus,result)
    result
  }
}
