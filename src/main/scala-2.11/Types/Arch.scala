package Types

import com.gilt.gfc.semver.SemVer

sealed trait Arch {
  def name: String
  def version: SemVer
}

object Arch {
  trait Minecraft extends Arch {
    val name = "Minecraft"
    def version: SemVer
  }
  case object Minecraft1_7_10 extends Minecraft { val version:SemVer = SemVer.apply("1.7.10") }
  case object Minecraft1_9_4 extends Minecraft { val version:SemVer = SemVer.apply("1.9.4") }
}
