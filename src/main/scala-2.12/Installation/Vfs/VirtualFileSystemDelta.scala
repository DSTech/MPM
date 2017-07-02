package Installation.Vfs

import scala.annotation.tailrec

/**
  * A description of changes between two VFS states
  * @param differences A list of changes to individual files, represented as [[Installation.Vfs.VirtualFileDelta]]s
  */
case class VirtualFileSystemDelta(differences: List[VirtualFileDelta]) {

  //TODO: A rudimentary implementation could take advantage of toMap dropping all but the last entry on a key
  //TODO: Of course, this would leave orphaned deletes for files that were never added; Pair elimination is important
  /**
    * Removes duplicate creation/deletion pairs and creations
    * The remaining entry for any given path will be either an addition or a deletion
    * @return An optimised form of the delta
    */
  def optimised: VirtualFileSystemDelta = throw new NotImplementedError

}

object VirtualFileSystemDelta {

  def merge(deltas: VirtualFileSystemDelta*): VirtualFileSystemDelta = mergeAll(deltas)
  def mergeAll(deltas: Seq[VirtualFileSystemDelta]) = VirtualFileSystemDelta(deltas.flatMap(_.differences).toList)

  private def firstCommon[T](first: => Seq[T], second: List[T]): Option[T] = {
    lazy val fSet = first.toSet
    @tailrec
    def firstCommonInner(first: Set[T], second: List[T]): Option[T] = {
      second match {
        case x :: xs => if (first.contains(x)) Some(x) else firstCommonInner(first, xs)
        case Nil => None
      }
    }
    firstCommonInner(fSet, second)
  }

  def CreateFromLayers(initial: VirtualFileLayer, result: VirtualFileLayer): VirtualFileSystemDelta = {
    val (subtractions, additions) = (initial, result) match {
      case (i, r) if i == r => (List.empty, List.empty) //No reason to perform 2 layer changes just to reset to current
      case (i, r) => {
        val (startParents, destParents) = (r.parents, i.parents)
        firstCommon(startParents, destParents) match {
          case Some(commonParent: VirtualFileLayer) => {
            val subtractions = initial :: startParents.takeWhile(_ != commonParent) //Subtract nearest to start first
            val additions = (result :: destParents.takeWhile(_ != commonParent)).reverse //Add nearest to start first
            (subtractions, additions)
          }
          case None => (startParents, destParents.reverse) //No shared parent; full clear then apply of hierarchy
        }
      }
    }
    val steps = VirtualFileSystemDelta.mergeAll(subtractions.map(x => x.deltaRevert) ::: additions.map(_.deltaImplement))
    steps.optimised
  }

}
