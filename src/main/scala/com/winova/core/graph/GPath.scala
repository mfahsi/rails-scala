package com.winova.core.graph

import scala.collection.IterableOnce.iterableOnceExtensionMethods
import scala.collection.MapView
import scala.collection.mutable.ListBuffer
import com.winova.core.graph.Edge

import scala.annotation.tailrec

/**
 * Mutable class Representing a list of connected edges
 * @param path list of connected edges
 * @tparam N  vertex type
 * @tparam E  edge type
 */
class GPath[N, E <: Edge[N]](val path: ListBuffer[E]) {
  def this(edges: List[E]) = {
    this(ListBuffer.from(edges))
    if (!path.isEmpty) {
      require(path.tail.zip(path).forall(c => c._1.fromVertex.equals(c._2.toVertex)), "edges of the path do not connect")
    }
  }

  def this(edges: E*) = {
    this(edges.toList)
  }

  /** convert path to List of stops including start and terminus
   * example A-B1,BC3 => List[A,B,C]
   **/
  def asStopList(): List[N] = {
    //TODO @tailrec
    def toListOfStops(list: List[E]): List[N] = {
      list match {
        case Nil => Nil
        case e :: Nil => List(e.fromVertex, e.toVertex)
        case h :: tail => List(h.fromVertex) ++ toListOfStops(tail)
      }
    }
    toListOfStops(path.toList)
  }

  def lastEdge(): E = if (!path.isEmpty) path.last else throw new IllegalArgumentException("empty path")

  def concatGpath(b: GPath[N, E]): GPath[N, E] = {
    require(terminus().equals(b.origin())) //must be the same station
    new GPath(path ++ b.path)
  }

  def terminus(): N = if (!path.isEmpty) path.last.toVertex else throw new IllegalArgumentException("empty path")

  def origin(): N = if (!path.isEmpty) path.head.fromVertex else throw new IllegalArgumentException("empty path")

  /**
   * sum of weights or cost function applied to edges forming tne path
   * @param distanceFunction
   * @return
   */
  def pathCost(using distanceFunction: DistanceFunction[N,E]): Double = distanceFunction.totalWeight(this)

  /**
   * adds an edge to the path
   * @param edge  to be added at the end
   * @return
   */
  def +:(edge: E): GPath[N, E] = {
    new GPath[N, E](path.appended(edge))
  }

  def length(): Int = path.size

  def contains(o: Any): Boolean = path.contains(o)

  override def toString(): String = toStringVia()

  def toStringVia(): String = {
    val intermediateStops = path.tail.map(_.fromVertex).mkString("-")
    s"${path.head.fromVertex}to${this.path.last.toVertex.toString} via[${intermediateStops}]"
  }

  override def hashCode(): Int = path.map(_.hashCode()).reduce(_ + _) + 171

  override def equals(obj: Any): Boolean = if (obj.isInstanceOf[GPath[_, _]]) {
    val other = obj.asInstanceOf[GPath[_, _]]
    if (other.length() == length()) {
      path.forall(other.contains(_))
    } else {
      false
    }
  } else {
    false
  }

}
