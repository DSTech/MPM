package Dependency
import Types._

class DependencyResolver {

  //An install specification containing semver ranges for dependencies
  //A ConfigurationSpec may produce a different Configuration when higher satisfying versions exist in the repo
  type ConfigurationSpec//TODO: stub

  //A list of all installed mods and their versions, with each mod specified as a concrete value
  type Configuration//TODO: stub

  //
  type BuildSpec//TODO: stub

  def Resolve(configurationSpec: ConfigurationSpec): Configuration = ???

  def Resolve(build: BuildSpec): Build = ???

}
