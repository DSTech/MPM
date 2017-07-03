package Extensions

import scala.reflect.io.{Path, AbstractFile}

object AbstractFileX {
  final implicit class AbstractFileX(val abstractFile: AbstractFile) extends AnyVal {
    def toPath: Path = Path(abstractFile.path)
    def separator: Char = toPath.separator
    def separatorStr: String = toPath.separatorStr
  }
}
