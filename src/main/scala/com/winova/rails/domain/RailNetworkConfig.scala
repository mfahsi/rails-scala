package com.winova.rails.domain

import com.winova.core.config.AppConfig
import com.winova.core.graph.EdgeW
import com.winova.rails.domain.RailNetwork.{RailConnection, RailNetworkGraph, RailStation}
import com.typesafe.config.Config

import java.util.MissingResourceException
import scala.util.{Failure, Success, Try}

object RailNetworkConfig {

  val appConfig: Config = AppConfig("winrails.conf").appConfig()

  def loadNetworkMapFromConfig(): Try[RailNetworkGraph] = {
    val configuredGraph = getRailNetworkMapConfig()
    configuredGraph.flatMap(strMap => {
      loadNetworkMapFromString(strMap)
    })
  }

  def getRailNetworkMapConfig(): Try[String] = {
    val graphKey = "winova.rails.networkGraph"
    val map = appConfig.getString(graphKey)
    if (map == null) {
      Failure(new NoSuchElementException(s"no configuration item $graphKey"))
    } else {
      Success(map)
    }
  }

  def loadNetworkMapFromString(strMap: String): Try[RailNetworkGraph] = {
    val connections: Array[Try[RailConnection]] = strMap.split(",").map(_.trim).map(parseEdge(_))
    if (connections.forall(_.isSuccess)) {
      val edges: List[RailConnection] = connections.map(_.get).toList
      val connectionsByDepartureStn: Map[RailStation, List[RailConnection]] = edges.groupBy(_.fromVertex)
      val graph = new RailNetworkGraph(edges)
      Success(graph)
    } else {
      println("failed to load network map")
      Failure(new Exception("failed to load network map. one or many issues encountered while parsing network map."))
    }
  }

  def parseEdge(str: String): Try[RailConnection] = {
    try
      Success(EdgeW(str.charAt(0).toString, str.charAt(1).toString, Integer.parseInt(str.charAt(2).toString)))
    catch {
      case e: Exception => Failure(e)
    }
  }


}