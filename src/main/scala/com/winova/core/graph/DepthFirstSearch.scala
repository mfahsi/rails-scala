package com.winova.core.graph

import com.winova.rails.domain.RailNetwork.RailStation

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object DepthFirstSearch {

  private val logger = com.typesafe.scalalogging.Logger(getClass)

  def depthFirstEdgeVisitUntilMaxLength[N, W, R](graph: DirectedWeightedGraph[N, W], start: N, visitAction: GPath[N, EdgeW[N, W]] => Option[R], maxLength: Int): List[R] = {
    val terminalPredicate = (p:GPath[N,EdgeW[N,W]]) => p.length() == maxLength
    depthFirstEdgeVisitorWithTerminationPredicate(graph, start, visitAction,terminalPredicate)
  }

  /**
   * Visit graph edges in depth first order allowing multiple visits and terminating based on a user predicate.
   * visit action collect any object using a function (input is the current path) for each path visited.
   * by closing over the graph anything is typically collectible : edge or node labels or values etc
   * @param graph
   * @param start  starting node
   * @param visitAction function to collect objects of type R
   * @param terminalPredicate determining when depth walk ends
   * @tparam N  vertex type
   * @tparam W  weight or label type (of edge)
   * @tparam R  type of collected objects
   * @return List of collected objects by depth first walk
   */
  def depthFirstEdgeVisitorWithTerminationPredicate[N, W, R](graph: DirectedWeightedGraph[N, W], start: N, visitAction: GPath[N, EdgeW[N, W]] => Option[R], terminalPredicate : GPath[N, EdgeW[N, W]] => Boolean): List[R] = {

    //TO DO @tailrec
    def depthFirstEdgeVisitorWithTerminationPredicateRec(currentPath: GPath[N, EdgeW[N, W]], visitAction: GPath[N, EdgeW[N, W]] => Option[R]): List[R] = {
      val currentEdge = currentPath.lastEdge()
      val unvisited: List[EdgeW[N, W]] = graph.outConnections(currentEdge.toVertex)
      logger.debug("DFS visiting edge {} via path {}", currentEdge, currentPath)
      if (unvisited.isEmpty || terminalPredicate.apply(currentPath)) {
        logger.debug("terminating at {}",currentPath)
        val R = visitAction.apply(currentPath)
        if (R.isDefined) { // Note : we are not ending at target to allow cycles
          ListBuffer(R.get).toList
        } else {
          ListBuffer().toList
        }
      } else {
        val r = visitAction.apply(currentPath)
        val result: List[R] = unvisited.map(outEdge => {
          val nextPath = currentPath.+:(outEdge)
          logger.debug("DFS currentPath {} nextPath = {} last edge {}", currentPath, nextPath, outEdge)
          depthFirstEdgeVisitorWithTerminationPredicateRec(nextPath, visitAction)
        }).flatten.toList
        if (r.isDefined) {
           result.prepended(r.get)
        } else {
          result
        }
      }
    }

    logger.info("--depthFirstWithCycleVisit {}--", start)
    val unvisited: List[EdgeW[N, W]] = graph.outConnections(start)
    logger.debug("edges of start {}", unvisited)
    if (unvisited.isEmpty) {
      List[R]()
    } else {
      val visitResults: List[R] = unvisited.map(edge => {
        val currentPath: GPath[N, EdgeW[N, W]] = new GPath[N, EdgeW[N, W]](edge)
        depthFirstEdgeVisitorWithTerminationPredicateRec(currentPath, visitAction)
      }).flatten
      logger.info("--depthFirstWithCycleVisit from {} Result ={}",start, visitResults)
      visitResults
    }
  }
}
