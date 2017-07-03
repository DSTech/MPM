package Dependency

import scala.language.{higherKinds, implicitConversions, postfixOps}
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.GraphPredef._
import scalax.collection.immutable.Graph
import scala.annotation.tailrec
import scalax.collection.GraphPredef

case class DecycledDiGraph[T, E[X] <: GraphPredef.EdgeLikeIn[X]] private(graph: Graph[T, E]) {
  def isCyclic: Boolean = false

  def isAcyclic: Boolean = true

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
    require(graph.isCyclic)
    graph - graph.findCycle.head.edges.last
  }

  @tailrec
  private def deCycleDiGraph[T](graph: Graph[T, DiEdge]): DecycledDiGraph[T, DiEdge] =
    if (graph.isAcyclic) DecycledDiGraph(graph) else deCycleDiGraph(eliminateCycle(graph))

  def topologicalSortDeCycle[T](edges: List[DiEdge[T]]): List[T] = {
    implicit def mkOps[A](x: A)(implicit ord: math.Ordering[A]): ord.Ops = ord.mkOrderingOps(x)
    val g: DecycledDiGraph[T, DiEdge] = {
      val g = createDiGraph(edges)
      if (g.isCyclic) deCycleDiGraph(g) else DecycledDiGraph(g)
    }
    assert(g.isAcyclic)
    val originalOrderMap = edges
      .flatMap(_.seq)
      .zipWithIndex
      .groupBy(x => x._1)
      .map(x => x._1 -> x._2.map(_._2).min)
    g.graph.topologicalSort match {
      case Right(order) => order.toLayered
        .map(layer => (layer._1, layer._2.toSeq.sortBy(x => originalOrderMap.getOrElse(x, Int.MinValue)))) //Sort contents of layers by original order
        .flatMap(_._2.map(_.value)).to[List]
      case Left(_) => assert(assertion = false, "Cycles should not reach this point"); throw new IllegalStateException()
    }
  }

  def TopologicalSortEqEdges[T](dependencies: Seq[(T, T)]): List[T] = {
    topologicalSortDeCycle(dependencies.map(dep => new DiEdge[T](dep._1, dep._2)).to[List])
  }

  def TopologicalSortEdgesByKey[T, K](items: Seq[T])(idFor: T => K, getDependenciesForItem: T => Seq[K]): List[T] = {
    val idToItem: Map[K, T] = items.map(item => idFor(item) -> item)(collection.breakOut)
    val itemToId: Map[T, K] = { idToItem.map(_.swap)(collection.breakOut) : Map[T, K] }
    val dependencyPairs =
      for (item <- items; dependency <- getDependenciesForItem(item)) yield {
        assert(idToItem.contains(dependency))//Can't return items that don't exist in the input set
        (itemToId(item), dependency)
      }
    val order = TopologySorter.TopologicalSortEqEdges(dependencyPairs)
    order map idToItem
  }
}
