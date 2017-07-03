package test

import Extensions.AbstractFileX._
import Extensions.PathX._

import Installation.Vfs
import Installation.Vfs.{VirtualFileAddition, VirtualFileRemoval}
import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.io
import io.AbstractFile
import io.{Path, VirtualFile}

class VfsTests extends FlatSpec with Matchers {
  def MockFile(path: Path): AbstractFile = {
    new VirtualFile(path.name, path.path)
  }

  it must "Be able to form a map to files" in {
    val a_b_c = MockFile(path"a/b/c")
    val layer = Vfs.VirtualFileLayer(Set(a_b_c), Set(), None)
    layer.addedPathsToFiles map {
      case (p, vfile) =>
        assert(p.name === "c")
        assert(p === path"a/b/c")
        assert(vfile.name === "c")
        assert(p.path === vfile.path)
        (p.path, vfile.path)
    }
    assert(layer(path"a/b/c").get === a_b_c)
  }

  it should "Layer additions" in {
    val layer = Vfs.VirtualFileLayer(Set(
      MockFile(path"a/b"),
      MockFile(path"c")
    ), Set(), None)

    val childLayer = Vfs.VirtualFileLayer(Set(MockFile(path"a/d")), Set(), Some(layer))

    assert(childLayer(path"a/d").nonEmpty)
    assert(childLayer(path"c").nonEmpty)
    assert(childLayer(path"a").isEmpty)
  }

  it should "Positively delta additions with parents" in {
    val a_c = MockFile(path"a/c")
    val layer = Vfs.VirtualFileLayer(Set(MockFile(path"a/b")), Set(), None)
    val childLayer = Vfs.VirtualFileLayer(Set(a_c), Set(), Some(layer))
    val delta = childLayer.deltaImplement
    delta.differences.find(d => d.path === path"a/c") should matchPattern {
      case Some(VirtualFileAddition(_, addition)) if addition === a_c =>
    }
  }

  it should "Not include parent additions in implementation deltas" in {
    val layer = Vfs.VirtualFileLayer(Set(MockFile(path"a/b")), Set(), None)
    val childLayer = Vfs.VirtualFileLayer(Set(MockFile(path"a/c")), Set(), Some(layer))
    val delta = childLayer.deltaImplement
    delta.differences.filter(d => d.path === path"a/b") shouldBe empty
  }

  it should "Not include parent additions in reverting deltas" in {
    val layer = Vfs.VirtualFileLayer(Set(MockFile(path"a/b")), Set(), None)
    val childLayer = Vfs.VirtualFileLayer(Set(MockFile(path"a/c")), Set(), Some(layer))
    val delta = childLayer.deltaRevert
    delta.differences.filter(d => d.path === path"a/b") shouldBe empty
  }

  it should "Not include removals in layers" in {
    val layer = Vfs.VirtualFileLayer(Set(MockFile(path"a/b")), Set(), None)
    val childLayer = Vfs.VirtualFileLayer(Set(), Set(path"a/b"), Some(layer))
    childLayer(path"a/b") shouldBe None
    childLayer.files shouldBe empty
    childLayer shouldBe empty
  }

  it should "Positively delta removals with parents" in {
    val a_b = MockFile(path"a/b")
    val layer = Vfs.VirtualFileLayer(Set(a_b), Set(), None)
    val childLayer = Vfs.VirtualFileLayer(Set(), Set(a_b.path), Some(layer))
    val delta = childLayer.deltaImplement
    all (delta.differences) should matchPattern {
      case VirtualFileRemoval(removedPath) if removedPath === a_b.toPath =>
    }
  }

  it should "Not include parent removals in implementation deltas" in {
    val layer = Vfs.VirtualFileLayer(Set(), Set(path"a/d"), None)
    val childLayer = Vfs.VirtualFileLayer(Set(MockFile(path"a/b")), Set(), Some(layer))
    val delta = childLayer.deltaImplement
    all (delta.differences) shouldNot matchPattern {
      case VirtualFileRemoval(removedPath) if removedPath === path"a/d" =>
      case VirtualFileAddition(addedPath, _) if addedPath === path"a/d" =>
    }
  }

  it should "Not include parent removals in reverting deltas" in {
    val layer = Vfs.VirtualFileLayer(Set(), Set(path"a/d"), None)
    val childLayer = Vfs.VirtualFileLayer(Set(MockFile(path"a/b")), Set(), Some(layer))
    val delta = childLayer.deltaRevert
    all (delta.differences) shouldNot matchPattern {
      case VirtualFileRemoval(removedPath) if removedPath === path"a/d" =>
      case VirtualFileAddition(addedPath, _) if addedPath === path"a/d" =>
    }
  }

  it should "Iterate to include all files from all parents" in {
    val a = MockFile("a")
    val b = MockFile("b")
    val c = MockFile("c")
    val la = Vfs.VirtualFileLayer(Set(a), Set(), None)
    val lb = Vfs.VirtualFileLayer(Set(b), Set(), Some(la))
    val lc = Vfs.VirtualFileLayer(Set(c), Set(), Some(lb))

    val filesSortedByPath = lc.files.toSeq.sortBy(e => e._1.path)
    val filesDistinctSortedByPath = lc.files.toList.groupBy(f => f._2).map(t => t._2.head).toSeq.sortBy(e => e._1.path)
    filesSortedByPath shouldEqual filesDistinctSortedByPath

    la.map(_._2).toSet should contain only a
    lb.map(_._2).toSet should contain only(a, b)
    lc.map(_._2).toSet should contain only(a, b, c)
  }

  it should "Not include paths deleted by parents" in {
    val a = MockFile("a")
    val b = MockFile("b")
    val c = MockFile("c")
    val la = Vfs.VirtualFileLayer(Set(a), Set(), None)
    val lb = Vfs.VirtualFileLayer(Set(b), Set(a.path), Some(la))
    val lc = Vfs.VirtualFileLayer(Set(c), Set(b.path), Some(lb))

    val filesSortedByPath = lc.files.toSeq.sortBy(e => e._1.path)
    val filesDistinctSortedByPath = lc.files.toList.groupBy(f => f._2).map(t => t._2.head).toSeq.sortBy(e => e._1.path)
    filesSortedByPath shouldEqual filesDistinctSortedByPath

    lb.map(_._2).toSet shouldNot contain(a)
    lc.map(_._2).toSet shouldNot contain(a)
    lc.map(_._2).toSet shouldNot contain(b)
  }

  it should "Allow readdition of paths deleted by parents" in {
    val a = MockFile("a")
    val b = MockFile("b")
    val la = Vfs.VirtualFileLayer(Set(a), Set(), None)
    val lb = Vfs.VirtualFileLayer(Set(b), Set(a.path), Some(la))
    val lc = Vfs.VirtualFileLayer(Set(a), Set(b.path), Some(lb))

    la.map(_._2).toSet should contain(a)
    lb.map(_._2).toSet shouldNot contain(a)
    lb.map(_._2).toSet should contain(b)
    lc.map(_._2).toSet shouldNot contain(b)
    lc.map(_._2).toSet should contain(a)
  }
  
  ignore should "Allow optimization of deltas" in {
    ???
  }
}
