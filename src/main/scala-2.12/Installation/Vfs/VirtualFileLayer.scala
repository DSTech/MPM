package Installation.Vfs

import scala.annotation.tailrec
import scala.reflect.io.AbstractFile
import scala.tools.nsc.io.Path
import Extensions.AbstractFileX._

import scala.collection.IterableView

/**
  * A list of files in a filesystem; Layers are modifiers atop each other, with a list of additions and deletions
  *
  * @param additions     [[scala.collection.Map]]([[Path]], [[scala.reflect.io.VirtualFile]])Tracks direct contents of this filesystem
  * @param deletions [[[scala.collection.Set]]([[Path]]) files removed by the filesystem layer
  * @param parent    [[Installation.Vfs.VirtualFileLayer]] If provided, the layer above this one to which we defer when an unknown path is requested
  */
case class VirtualFileLayer(additions: Set[AbstractFile], deletions: Set[Path], parent: Option[VirtualFileLayer])
  extends Iterable[(Path, AbstractFile)] {
  lazy val addedPathsToFiles: Map[Path, AbstractFile] =
    { additions.map(f => (f.toPath, f))(collection.breakOut): Map[Path, AbstractFile] }

  lazy val additionPaths: Iterable[Path] = addedPathsToFiles.keys

  def files: Iterator[(Path, AbstractFile)] = {
    val thisLayerItr: Iterator[(Path, AbstractFile)] = addedPathsToFiles.iterator
    val parentLayerItr: Iterator[(Path, AbstractFile)] = parent.toSeq
      .flatMap(parent => parent
          .files
          .filterNot(f => deletions.contains(f._1))
      ).toIterator

    thisLayerItr ++ parentLayerItr
  }

  def apply(path: Path): Option[AbstractFile] = {
    addedPathsToFiles.get(path) match {
      case Some(virtualFile) => Some(virtualFile)
      case None => {
        if (deletions.contains(path)) {
          None
        } else {
          parent match {
            case Some(layer) => layer(path)
            case None => None
          }
        }
      }
    }
  }

  /**
    * Fetch the changes needed to install this layer to a VFS
    *
    * @return a delta of file changes needed to implement this layer on a filesystem
    */
  def deltaImplement: VirtualFileSystemDelta = {
    val additions = this.addedPathsToFiles.map(f => VirtualFileAddition(f._1, f._2))
    val deletions = parent match {
      case Some(_) => {
        this.deletions.filterNot(this.addedPathsToFiles.contains).map(VirtualFileRemoval)
      }
      case None => Seq()
    }
    VirtualFileSystemDelta((additions ++ deletions).toList)
  }

  /**
    * Fetch the changes needed to remove this layer from a VFS
    *
    * @return a delta of file changes needed to remove this layer from a filesystem
    */
  def deltaRevert: VirtualFileSystemDelta = {
    //Delete what we've added; add the parent version if it exists
    val deletionsAndReadditions = parent match {
      case Some(p) => {
        val dels = this.addedPathsToFiles.map(f => VirtualFileRemoval(f._1))
        val readds = dels.map(del => (del.path, p(del.path))).flatMap({
          case (path: Path, Some(file: AbstractFile)) => Seq((path, file))
          case (_, None) => Seq()
        }).map { case (path, file) => VirtualFileAddition(path, file) }
        dels ++ readds
      }
      case None => {
        //Delete everything
        this.addedPathsToFiles.map(f => VirtualFileRemoval(f._1))
      }
    }
    //And add what we've deleted, if the parent has it in its filesystem (Not just its files!)
    val additions = parent match {
      case Some(p) => {
        this.deletions.map(delPath => (delPath, p(delPath))).flatMap({
          case (path: Path, Some(file: AbstractFile)) => Seq((path, file))
          case (_, None) => Seq()
        }).map(f => VirtualFileAddition(f._1, f._2))
      }
      case None => Seq()//Can't add what we've deleted, there's nothing there
    }
    VirtualFileSystemDelta((deletionsAndReadditions ++ additions).toList)
  }

  /**
    * Returns a list containing the parents of this layer, but not this layer itself.
    *
    * @return A list of parents in ascending order of distance from this element.
    */
  def parents: List[VirtualFileLayer] = {
    this.parent match {
      case None => Nil
      case Some(l: VirtualFileLayer) =>
        @tailrec
        def listParentLayers(maybeLayer: Option[VirtualFileLayer], layers: List[VirtualFileLayer] = List.empty[VirtualFileLayer]): List[VirtualFileLayer] = maybeLayer match {
          case Some(l: VirtualFileLayer) => listParentLayers(l.parent, l :: layers)
          case None => layers
        }
        listParentLayers(Some(l))
    }
  }
  override def iterator: Iterator[(Path, AbstractFile)] = this.files
}
