package com.winova.rails.domain

import com.winova.core.graph.*

object RailNetwork {

  type RailConnection = EdgeW[RailStation, Int]
  type RailStation = String
  type Route = List[RailStation]
  type RailPath = GPath[RailStation, RailConnection]

  val railDistanceFunction: DistanceFunction[RailStation, EdgeW[RailStation, Int]] = new DistanceFunction[RailStation, EdgeW[RailStation, Int]] {
    def weight(e: EdgeW[RailStation, Int]): Double = e.weight.toDouble
  }


  class RailNetworkGraph(network: Map[RailStation, List[RailConnection]]) extends DirectedWeightedGraph[RailStation, Int](network) {

    def this(edges: List[RailConnection]) = this(Edge.edgeToMap[RailStation, RailConnection](edges))

    def validateRoute(route: Route): Either[GraphError, RailPath] = {
      route match {
        case Nil => Right(GPath()) //for math convenience, empty route is no route
        case n :: Nil => Left(GraphError.InvalidRoute) // a route is made of 2 stations or more
        case _ => {
          val edges = route.tail.zip(route).map(zip => findEdge(zip._2, zip._1))
          if (edges.forall(_.isDefined)) {
            val result = new GPath[RailStation, RailConnection](edges.map(_.get))
            Right(result)
          } else {
            Left(GraphError.NoSuchRoute)
          }
        }

      }

    }

  }
}
