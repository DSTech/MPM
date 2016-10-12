package Types

/**
  * Architecture Bitness is assumed to be 64.
  * @param mod Mod to which this build belongs
  * @param arch Arch
  * @param platform : Windows/Linux/Mac
  */
case class Build(mod: Mod, arch: Arch, platform: Platform) {
}
