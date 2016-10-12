import org.scalatest._

import scalax.collection.GraphEdge.DiEdge
import Dependency.TopologySorter

class TopologySorterTests extends FlatSpec with Matchers {
  it should "Sort numbers" in {
    val pairs = Seq((1, 2), (2, 3), (3, 4), (4, 5), (5, 6), (6, 7), (0, 7), (1, 6), (7, 1))
    val edges = pairs.map(x => new DiEdge[Int](x._1, x._2))
    val topology = TopologySorter.topologicalSortDeCycle(edges.toList) match {
      case topology: List[Int] =>
        topology
    }
    topology should contain theSameElementsInOrderAs Seq(0, 1, 2, 3, 4, 5, 6, 7)
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
    topology should contain theSameElementsInOrderAs Seq("railcraft-addons", "ae2-industrial", "ic2", "ae2", "railcraft", "forge", "minecraft", "opengl", "opencl")
  }
}
