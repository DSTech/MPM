package Installation.Vfs

import scala.reflect.io.AbstractFile
import scala.tools.nsc.io.Path

sealed trait VirtualFileDelta {
  def path: Path
}

case class VirtualFileAddition(path: Path, file: AbstractFile) extends VirtualFileDelta {
  def invert = VirtualFileRemoval(path)
}

case class VirtualFileRemoval(path: Path) extends VirtualFileDelta {
  def invert(file: AbstractFile) = VirtualFileAddition(path, file)
}
