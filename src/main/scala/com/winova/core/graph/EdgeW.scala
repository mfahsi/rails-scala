package com.winova.core.graph

case class EdgeW[N, W](fromVertex: N, toVertex: N, weight: W) extends Edge[N] {
  override def hashCode(): Int = fromVertex.hashCode() + toVertex.hashCode()

  override def equals(obj: Any): Boolean = if (obj.isInstanceOf[Edge[N]]) {
    val ed = obj.asInstanceOf[Edge[N]]
    ed.fromVertex.equals(fromVertex) && ed.toVertex.equals(toVertex)
  } else {
    false
  }

  override def toString: String = s"$fromVertex-$toVertex$weight"
}
