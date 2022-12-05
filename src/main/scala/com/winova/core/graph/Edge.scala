package com.winova.core.graph

trait Edge[N] {
  val fromVertex: N
  val toVertex: N
}

object Edge {

  def edgeToMap[N, E <: Edge[N]](list: List[E]): Map[N, List[E]] = {
    val connectionsByDepartureStn: Map[N, List[E]] = list.groupBy(_.fromVertex)
    val verticesWithNoOutBounds = list.map(_.toVertex).filterNot(connectionsByDepartureStn.keySet.contains(_))
    connectionsByDepartureStn.++(verticesWithNoOutBounds.map(v => (v, List[E]())))
  }
}
