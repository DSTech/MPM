import scala.pickling.json._
import scala.pickling.Defaults._

object Main {

  //case class PickleMap[X,Y](map: Map[X, DiEdge[Y]]) extends AnyVal
  case class Person(name: String, age: Int)

  def main(args: Array[String]): Unit = {
    Console.println("Hehehe")
    //testNumericTopo()
    //testStringTopo()

    //val pkld = PickleMap(Map[String, DiEdge[Int]](("boop", 2 ~> 5), ("bleep", 3 ~> 3))).pickle
    //val pkld = Person("Zoey", 23).pickle
    //Console.println(pkld.value)
    //val unpik = pkld.unpickle[PickleMap[String, Int]]
    //val unpik = pkld.unpickle[Person]
    //Console.println(unpik)



    val i: Option[Int] = None
    i match {
      case Some(value: Int) => Console.println("i: " + i)
      case _ => Console.println("No value!")
    }
  }
}
