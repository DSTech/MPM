package Extensions

object IterableX {

  implicit class IterableXSet[T](iter: Iterable[T]) {
    def toSet: Set[T] = iter.toSeq.toSet
  }

  implicit class TraversableX[T](iter: Iterable[T]) {
    def breakoutToMap[K, V](toPair: T => (K, V)): Map[K, V] = iter.map(toPair)(collection.breakOut)
  }

}
