package Extensions

object SetX {


  implicit class IterableXSet[T](iter: Iterable[T]) {
    def toSet: Set[T] = iter.toSeq.toSet
  }

  implicit class SeqXSet[T](seq: Seq[T]) {
    def toSet: Set[T] = Set(seq: _*)
  }

}
