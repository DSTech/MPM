package Types

import sbt.VersionNumber

//TODO: Add a [[BuildSpec]] type for specifying a range of properties, to act as a filter on dependency resolution

/**
  * @param mod          [[Types.Mod]] to which this build belongs
  * @param arch         [[Types.Arch]] Minecraft Version
  * @param platform     [[Types.Platform]] Windows/Linux/Mac
  * @param version      Version of this build of the mod
  * @param dependencies Mods with exact versions of dependencies
  * @note Architecture Bitness is assumed to be 64.
  */
case class Build(mod: Mod, arch: Arch, platform: Platform, version: VersionNumber, dependencies: Array[(Mod, VersionNumber)])
