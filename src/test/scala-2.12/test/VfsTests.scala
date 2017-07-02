package test

import java.io.{File, InputStream, OutputStream}

import util._
import Installation.Vfs
import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.io, io.AbstractFile, io.{Path, File, VirtualFile}

class VfsTests extends FlatSpec with Matchers {
  def MockFilePair(pathParts: Seq[String]): (Path, AbstractFile) = {
    (pathParts.mkString("/"), new VirtualFile(pathParts.last, pathParts.init.mkString("/")))
  }

  it must "Be able to form a map to files" in {
    Vfs.VirtualFileLayer(Map(Seq(MockFilePair(Seq("a", "b", "c"))):_*), Set(), None)
  }

  it should "Delta additions" in {
    ???
  }

  it should "Delta removals" in {
  }
}
