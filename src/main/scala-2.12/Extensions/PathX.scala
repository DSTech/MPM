package Extensions

import scala.reflect.io.Path
import java.io.{File => JFile}

object PathX {

  final implicit class PathCompanionX(val pathCompanion: Path.type) extends AnyVal {
    def currentDirectory: Path = pathCompanion(".")
    def systemSeparator: Char = PathX.systemSeparator
    def systemSeparatorStr: String = PathX.systemSeparatorStr
    def buildFromStrings(parts: String*): Path = pathCompanion(parts.mkString(this.systemSeparatorStr))
  }

  final implicit class StringPather(val sc: StringContext) extends AnyVal {
    def path(args: Any*): Path = Path.buildFromStrings(sc.s(args:_*).split("/"):_*)
  }

  lazy val systemSeparator: Char = JFile.separatorChar
  lazy val systemSeparatorStr: String = JFile.separator

}
