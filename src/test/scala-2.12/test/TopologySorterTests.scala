package test

import Dependency.TopologySorter
import org.scalatest.{FlatSpec, Matchers}

import scalax.collection.GraphEdge.DiEdge

class TopologySorterTests extends FlatSpec with Matchers {
  it should "Sort numbers" in {
    val pairs = Seq(
      (1, 2),
      (2, 3),
      (3, 4),
      (4, 5),
      (5, 6),
      (6, 7),
      (0, 7),
      (1, 6))
    val edges = pairs.map(x => new DiEdge[Int](x._1, x._2))
    val topology = TopologySorter.topologicalSortDeCycle(edges.toList) match {
      case topology: List[Int] =>
        topology
    }
    topology should contain theSameElementsInOrderAs Seq(1, 0, 2, 3, 4, 5, 6, 7)
    for (pair <- pairs) topology should contain inOrder(pair._1, pair._2)
    for (byDependency <- pairs.groupBy(_._1)) topology should contain inOrderElementsOf byDependency._2.map(_._2)
  }

  it should "Handle cycles gracefully" in {
    val pairs = Seq(
      (1, 2),
      (2, 7),
      (7, 1),
      (8, 7))
    val edges = pairs.map(x => new DiEdge[Int](x._1, x._2))
    val topology = TopologySorter.topologicalSortDeCycle(edges.toList) match {
      case topology: List[Int] => topology
    }
    //topology should contain theSameElementsInOrderAs Seq(1, 0, 2, 3, 4, 5, 6, 7)
    for (pair <- pairs if pair != (7, 1)) topology should contain inOrder(pair._1, pair._2)
    for (byDependency <- pairs.filter(x => x != (7, 1)).groupBy(_._1)) topology should contain inOrderElementsOf byDependency._2.map(_._2)
  }

  it should "Sort strings" in {
    val pairs = Seq(
      ("forge", "minecraft"),
      ("minecraft", "opencl"),
      ("minecraft", "opengl"),
      ("ae2", "forge"),
      ("ae2-industrial", "ae2"),
      ("ae2-industrial", "ic2"),
      ("ae2-industrial", "railcraft"),
      ("ic2", "forge"),
      ("railcraft", "forge"),
      ("railcraft-addons", "railcraft"))
    val edges = pairs.map(x => new DiEdge[String](x._1, x._2))

    val topology = TopologySorter.topologicalSortDeCycle(edges.toList)
    for (pair <- pairs) topology should contain inOrder(pair._1, pair._2)
    for (byDependency <- pairs.groupBy(_._1)) topology should contain inOrderElementsOf byDependency._2.map(_._2)
    for (byDependency <- pairs.groupBy(_._1)) topology should contain inOrderElementsOf byDependency._2.map(_._2)
  }
}
