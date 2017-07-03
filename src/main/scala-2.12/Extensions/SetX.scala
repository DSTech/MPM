package Extensions

object SetX {

  final implicit class SeqXSet[T](val seq: Seq[T]) extends AnyVal {
    def toSet: Set[T] = Set(seq: _*)
  }

}
