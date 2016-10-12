package Dependency

import scala.language.{higherKinds, implicitConversions, postfixOps}
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.GraphPredef._
import scalax.collection.immutable.Graph
import scala.annotation.tailrec
import scalax.collection.GraphPredef

case class DecycledDiGraph[T, E[X] <: GraphPredef.EdgeLikeIn[X]] private(graph: Graph[T, E]) {
  def isCyclic: Boolean = graph.isCyclic
  def isAcyclic: Boolean = graph.isAcyclic
  implicit def toGraph: Graph[T, E] = graph
}
object DecycledDiGraph {
  implicit def DecycledDiGraphToGraph[T, E[X] <: GraphPredef.EdgeLikeIn[X]](decycledDiGraph: DecycledDiGraph[T, E]): Graph[T, E] = decycledDiGraph.graph
}

object TopologySorter {
  private def createDiGraph[T](edges: Iterable[DiEdge[T]]): Graph[T, DiEdge] = {
    val nodes = edges.toList.flatMap(x => List(x.e._1, x.e._2)).distinct
    Graph.from(nodes, edges)
  }

  private def eliminateCycle[T](graph: Graph[T, DiEdge]): Graph[T, DiEdge] = {
    assert(graph.isCyclic)
    val result = graph.findCycle match {
      case Some(cycle) => graph - cycle.edges.head
      case None => assert(assertion = false, "Must be cyclic to reach this point"); throw new IllegalStateException()
    }
    result
  }

  @tailrec
  private def deCycleDiGraph[T](graph: Graph[T, DiEdge]): DecycledDiGraph[T, DiEdge] = graph match {
    case g if g.isAcyclic => DecycledDiGraph(graph)
    case g => deCycleDiGraph(eliminateCycle(graph))
  }

  def topologicalSortDeCycle[T](edges: List[DiEdge[T]]): List[T] = {
    val g: DecycledDiGraph[T, DiEdge] = {
      val g = createDiGraph(edges)
      if (g.isCyclic) deCycleDiGraph(g) else DecycledDiGraph(g)
    }
    assert(g.isAcyclic)
    g.graph.topologicalSort match {
      case Right(order) => order.map(_.value).toList
      case Left(cycle) => assert(assertion = false, "Cycles should not reach this point"); throw new IllegalStateException()
    }
  }

  def TopologicalSortEqEdges[T](dependencies: Seq[(T, T)]): List[T] = {
    topologicalSortDeCycle(dependencies.map(dep => new DiEdge[T](dep._1, dep._2)).to[List])
  }
}
