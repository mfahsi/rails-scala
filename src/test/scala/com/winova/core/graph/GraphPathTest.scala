package com.winova.core.graph

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class GraphPathTest extends AnyFlatSpec with should.Matchers {

  behavior of "DirectedGraph"

  //A-B-C-D
  val emptyPath: GPath[String, EdgeW[String, Int]] = new GPath();
  val gpath: GPath[String, EdgeW[String, Int]] = new GPath(EdgeW("A", "B", 1), EdgeW("B", "C", 2), EdgeW("C", "D", 10));

  it must "to String via  style" in {
    gpath.toStringVia() shouldBe ("AtoD via[B-C]")
  }

  it must "asListNodes" in {
    gpath.asStopList() shouldBe (List("A", "B", "C", "D"))
    emptyPath.asStopList() shouldBe (List())
  }

  it must "count stops" in {
    gpath.length() shouldBe (3)
    emptyPath.length() shouldBe (0)
  }

  it must "not allow invalid path creation" in {
    assertThrows[IllegalArgumentException] {
      new GPath[String, EdgeW[String, Int]](EdgeW("A", "B", 1), EdgeW("A", "E", 2)) //second edge must start with B
    }
  }

  it must "origin is the start of the trip" in {
    gpath.origin() shouldBe ("A")
    assertThrows[IllegalArgumentException] {
      emptyPath.origin()
    }
  }

  it must "terminus is the last stop" in {
    gpath.terminus() shouldBe ("D")
    assertThrows[IllegalArgumentException] {
      emptyPath.terminus()
    }
  }

  it must "+: add a stop to a path" in {
    val newpath = gpath.+:(EdgeW[String, Int]("D", "E", 1))
    newpath.terminus() shouldBe ("E")
    gpath.terminus() shouldBe ("D")
    newpath.length() shouldBe 4
  }

  it must "concat of 2 paths is a path and must check connection at terminus of first path" in {
    val gpathDA: GPath[String, EdgeW[String, Int]] = new GPath(EdgeW("D", "A", 1));
    gpath.concatGpath(gpathDA).terminus() shouldBe ("A")

    assertThrows[IllegalArgumentException] {
      val gpathFA: GPath[String, EdgeW[String, Int]] = new GPath(EdgeW("F", "A", 1));
      gpath.concatGpath(gpathFA) // no connection possible at D
    }

  }
}
