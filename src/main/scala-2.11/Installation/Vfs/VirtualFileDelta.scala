package Installation.Vfs

import scala.tools.nsc.io.Path

sealed trait VirtualFileDelta {
  def path: Path
}

case class VirtualFileAddition(path: Path, file: VirtualFile) extends VirtualFileDelta {
  def invert = VirtualFileRemoval(path)
}

case class VirtualFileRemoval(path: Path) extends VirtualFileDelta {
  def invert(file: VirtualFile) = VirtualFileAddition(path, file)
}
