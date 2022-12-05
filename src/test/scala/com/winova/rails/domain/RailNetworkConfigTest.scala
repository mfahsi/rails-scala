package com.winova.rails.domain

import com.winova.core.graph.EdgeW
import com.winova.rails.domain.RailNetwork.RailNetworkGraph
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.util.Try

class RailNetworkConfigTest extends AnyFlatSpec with should.Matchers {

  behavior of "RailNetworkConfig"

  it must "null and empty String is not a valid network in configuration (but valide in core library as mathematical concept)" in {
    RailNetworkConfig.parseEdge("").isFailure should be(true)
    RailNetworkConfig.loadNetworkMapFromString("").isFailure should be(true)
    RailNetworkConfig.parseEdge(null).isFailure should be(true)
  }

  it must "parse single edge in string config" in {
    RailNetworkConfig.parseEdge("AB1").get should be(EdgeW("A", "B", 1))
    val maybeGaph: Try[RailNetworkGraph] = RailNetworkConfig.loadNetworkMapFromString("AB1")
    maybeGaph.isSuccess
    maybeGaph.get.edges.size should be(1)
    maybeGaph.get.edges.head should be(EdgeW("A", "B", 1))
  }

  it must "parse edges in string config with multiple edges" in {
    val maybeGaph: Try[RailNetworkGraph] = RailNetworkConfig.loadNetworkMapFromString("AB1,BC2,AD3")
    maybeGaph.isSuccess
    maybeGaph.get.findEdge("B", "C").get.weight should be(2)
  }

  it can "handle malformed configuration" in {
    RailNetworkConfig.loadNetworkMapFromString("AB1 + BC2,AD3").isFailure
  }

  behavior of "connected graph"
  it must "load correctly" in {
    RailNetworkConfig.loadNetworkMapFromString("AB1,BC2,AD3").isSuccess
  }
  it can "handle malformed configuration" in {
    RailNetworkConfig.loadNetworkMapFromString("AB1 + BC2,AD3").isFailure
  }

  behavior of "rail config"
  it must "load config" in {
    val winLandRailNetworkConfig: String = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7"
    val network = RailNetworkConfig.loadNetworkMapFromString(winLandRailNetworkConfig).get
    val network2 = RailNetworkConfig.loadNetworkMapFromConfig()
    network2.isSuccess should be(true)
    network2.get.edges should contain theSameElementsAs network.edges
  }

}
