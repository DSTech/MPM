import scala.annotation.tailrec
import scala.language.postfixOps
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.GraphPredef._
import scalax.collection.immutable.Graph
import scala.pickling.json._
import scala.pickling.Defaults._

object Main {

  def createDiGraph[T](edges: Iterable[DiEdge[T]]): Graph[T, DiEdge] = {
    val nodes = edges.toList.flatMap(x => List(x.e._1, x.e._2)).distinct
    Graph.from(nodes, edges)
  }

  def eliminateCycle[T](graph: Graph[T, DiEdge]): Graph[T, DiEdge] = {
    assert(graph.isCyclic)
    val result = graph.findCycle match {
      case Some(cycle) => graph - cycle.edges.head
      case None => assert(assertion = false, "Must be cyclic to reach this point"); throw new IllegalStateException()
    }
    result
  }

  @tailrec
  def deCycleDiGraph[T](graph: Graph[T, DiEdge]): Graph[T, DiEdge] = graph match {
    case g if g.isAcyclic => graph
    case g => deCycleDiGraph(eliminateCycle(graph))
  }

  private def topologicalSortDeCycle[T](edges: List[DiEdge[T]]): List[T] = {
    val g: Graph[T, DiEdge] = {
      val g = createDiGraph(edges)
      if (g.isCyclic) deCycleDiGraph(g) else g
    }
    assert(!g.isCyclic)
    g.topologicalSort match {
      case Right(order) => order.map(_.value).toList
      case Left(cycle) => assert(assertion = false, "Cycles should not reach this point"); throw new IllegalStateException()
    }
  }

  def testNumericTopo(): Unit = {
    val pairs = Seq((1, 2), (2, 3), (3, 4), (4, 5), (5, 6), (6, 7), (0, 7), (1, 6), (7, 1))
    val edges = pairs.map(x => new DiEdge[Int](x._1, x._2))
    val topology = topologicalSortDeCycle(edges.toList) match {
      case topology: List[Int] =>
        Console.println(topology mkString ", ")
        topology
    }
    assert(topology == Seq(0, 1, 2, 3, 4, 5, 6, 7))
  }

  def testStringTopo(): Unit = {
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
    val topology = topologicalSortDeCycle(edges.toList)
    Console.println(topology mkString ", ")
    assert(topology == Seq("railcraft-addons", "ae2-industrial", "ae2", "ic2", "railcraft", "forge", "minecraft", "opengl", "opencl"))
  }

  //case class PickleMap[X,Y](map: Map[X, DiEdge[Y]]) extends AnyVal
  case class Person(name: String, age: Int)

  def main(args: Array[String]): Unit = {
    //testNumericTopo()
    //testStringTopo()

    //val pkld = PickleMap(Map[String, DiEdge[Int]](("boop", 2 ~> 5), ("bleep", 3 ~> 3))).pickle
    val pkld = Person("Zoey", 23).pickle
    Console.println(pkld.value)
    //val unpik = pkld.unpickle[PickleMap[String, Int]]
    val unpik = pkld.unpickle[Person]
    Console.println(unpik)
  }
}
