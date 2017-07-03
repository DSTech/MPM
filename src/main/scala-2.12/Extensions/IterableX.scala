package Extensions

object IterableX {

  final implicit class IterableXSet[T](val iter: Iterable[T]) extends AnyVal {
    def toSet: Set[T] = iter.toSeq.toSet
  }

  final implicit class TraversableX[T](val iter: Iterable[T]) extends AnyVal {
    def breakoutToMap[K, V](toPair: T => (K, V)): Map[K, V] = iter.map(toPair)(collection.breakOut)
  }

}
