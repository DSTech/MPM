package Extensions

object SetX {

  implicit class SeqXSet[T](seq: Seq[T]) {
    def toSet: Set[T] = Set(seq: _*)
  }

}
