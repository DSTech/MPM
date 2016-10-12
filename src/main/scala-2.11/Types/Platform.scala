package Types

object Platform {

  case object Linux extends Platform {
    val name = "Linux"
  }

  case object Windows extends Platform {
    val name = "Windows"
  }

  case object Mac extends Platform {
    val name = "Mac"
  }

}

sealed trait Platform {
  def name: String
}