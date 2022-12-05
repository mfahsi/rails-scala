package com.winova.rails.graph

import com.winova.core.graph.{DistanceFunction, EdgeW, GPath, GraphError}
import com.winova.rails.domain.RailNetwork
import com.winova.rails.domain.RailNetwork.{RailConnection, RailNetworkGraph, RailPath, RailStation}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

/**
 * source to destination shortest path algorithm (A* search).
 * this is not in core.graph package as it is specific to railways domain data types.
 * it can be done for any weighted graph and moved to core.
 */
object ShortestPathAlgo {

  given railDistance: DistanceFunction[RailStation, EdgeW[RailStation, Int]] = RailNetwork.railDistanceFunction

  val railPathOrdering: Ordering[RailPath] = (x, y) => railDistance.totalWeight(x).compareTo(railDistance.totalWeight(y))

  private val logger = com.typesafe.scalalogging.Logger(getClass)

  def shortestPath(a: RailStation, b: RailStation)(graph: RailNetworkGraph): Try[GPath[RailStation, RailConnection]] = {
    logger.info("shortestPath from {} to {}", a, b)
    val edgWOrdering: Ordering[EdgeW[RailStation, Int]] = (x, y) => -1 * x.weight.compareTo(y.weight)
    val visitedNodes = ListBuffer[RailStation]()
    val queue = mutable.PriorityQueue[EdgeW[RailStation, Int]]()(edgWOrdering)
    val distanceMap: mutable.Map[RailStation, EdgeW[RailStation, Int]] = mutable.Map[RailStation, EdgeW[RailStation, Int]]()
    val startEdges = graph.outConnections(a).filterNot(c => visitedNodes.contains(c.toVertex)) //.map(_.copy(weight=999))
    queue.addAll(startEdges)
    startEdges.foreach(e => distanceMap.put(e.toVertex, e))
    visitedNodes.addOne(a)
    while (!queue.isEmpty) {
      val nextInOrder = queue.dequeue()
      val oldCost = nextInOrder.weight
      visit(nextInOrder.toVertex, nextInOrder)
    }

    //TODO @tailrec
    def visit(current: RailStation, edge: EdgeW[RailStation, Int]): Unit = {
      logger.debug("visit {} via {} and weight={}", current, edge, edge.weight)
      val out: List[RailConnection] = graph.outConnections(current)
      out.map(e => {
        val newDistance: Int = edge.weight + e.weight
        val candidateVia: EdgeW[RailStation, Int] = e.copy(weight = newDistance)
        val exisitingPathCost = distanceMap.get(e.toVertex)
        if (exisitingPathCost.isDefined) {
          //we don't update (we forget this path)
          if (exisitingPathCost.get.weight <= candidateVia.weight) {
            logger.debug("IGNORE {} to {} cost {} via {}", a, candidateVia.toVertex, candidateVia.weight, candidateVia.fromVertex)
            logger.debug("KEEP {} to {} cost {} via {}", a, exisitingPathCost.get.toVertex, exisitingPathCost.get.weight, exisitingPathCost.get.fromVertex)
          } else {
            distanceMap.put(e.toVertex, candidateVia)
            logger.debug("ADD {} to {} cost {} via {}", a, candidateVia.toVertex, candidateVia.weight, candidateVia.fromVertex)
            logger.debug("REM {} to {} cost {} via {}", a, exisitingPathCost.get.toVertex, exisitingPathCost.get.weight, exisitingPathCost.get.fromVertex)
          }
        } else {
          distanceMap.put(candidateVia.toVertex, candidateVia)
          logger.debug("new path to " + candidateVia + " cost {}", candidateVia.weight)
          queue.addOne(e)
          visitedNodes.addOne(e.toVertex)
        }
      })
      ()
    }

    logger.info("final distance map {}", distanceMap)
    val shortestPath = pathFromList(a, b, distanceMap.values.toList, graph)
    logger.info("shortestPath from {} to {} is  {}", a,b, shortestPath)
    shortestPath
  }

  def pathFromList(from: RailStation, toStn: RailStation, edges: List[RailConnection], graph: RailNetworkGraph): Try[GPath[RailStation, RailConnection]] = {

    def recFindBestOrigine(from: RailStation, toStn: RailStation, accumulator: List[RailConnection]): List[RailConnection] = {
      logger.info("REC {} {} {}", from, toStn, accumulator)
      val bestIncoming = edges.find(_.toVertex.equals(toStn))
      if (from.equals(toStn)) {
        accumulator
      } else {
        bestIncoming match {
          case Some(e) => recFindBestOrigine(from, e.fromVertex, e :: accumulator)
          case None => Nil
        }
      }
    }

    val bestIncoming = edges.find(_.toVertex.equals(toStn))
    val result = bestIncoming match {
      case Some(e) => recFindBestOrigine(from, e.fromVertex, List(e))
      case _ => List()
    }

    val backToGraph = result.flatMap(e => graph.findEdge(e.fromVertex, e.toVertex))
    logger.debug("Path {}", backToGraph)
    if(!backToGraph.isEmpty){
      Success(GPath[RailStation, RailConnection](backToGraph))
    }else{
      Failure(GraphError.NoSuchRoute)
    }

  }
}
