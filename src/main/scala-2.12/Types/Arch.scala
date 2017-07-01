package Types

import sbt.VersionNumber

sealed trait Arch {
  def name: String
  def version: VersionNumber
}

object Arch {
  trait Minecraft extends Arch {
    val name = "Minecraft"
    def version: VersionNumber
  }
  case object Minecraft1_7_10 extends Minecraft { val version:VersionNumber = VersionNumber.apply("1.7.10") }
  case object Minecraft1_9_4 extends Minecraft { val version:VersionNumber = VersionNumber.apply("1.9.4") }
}
