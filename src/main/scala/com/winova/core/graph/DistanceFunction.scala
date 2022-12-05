package com.winova.core.graph

trait DistanceFunction[N,E <: Edge[N]]{
  def weight(e:E) : Double
  def totalWeight(aPath:GPath[N,E]) : Double = {
    val weights : List[Double] = aPath.path.map(e=>weight(e)).toList
    weights.foldLeft(0d)(_+_)
  }
}
