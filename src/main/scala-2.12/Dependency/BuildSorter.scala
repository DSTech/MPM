package Dependency

import Types.Build

class BuildSorter {
  //TODO: Add test for this ensuring builds are sorted by dependency
  def SortBuilds(builds: Seq[Build]): List[Build] = TopoSortBuilds(builds)

  private def TopoSortBuilds(builds: Seq[Build]): List[Build] = {
    TopologySorter.TopologicalSortEdgesByKey[Build, String](builds)(b => b.mod.name, b => b.dependencies.map(dep => dep._1.name))
  }
}
