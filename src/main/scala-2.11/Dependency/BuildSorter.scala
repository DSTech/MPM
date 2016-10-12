package Dependency

import Types.Build

class BuildSorter {
  def SortBuilds(builds: Seq[Build]): List[Build] = TopoSortBuilds(builds)

  private def TopoSortBuilds(builds: Seq[Build]): List[Build] = {
    val buildDependencyPairs = builds.flatMap(build => build.dependencies.map(dep => (build.mod.name, dep._1.name)))
    val order = TopologySorter.TopologicalSortEqEdges(buildDependencyPairs)
    val nameMapping = builds.map(b => b.mod.name -> b)(collection.breakOut): Map[String, Build]
    order.map(nameMapping(_))
  }
}
