import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*

ThisBuild / organization := "org.openurp.edu.extern"
ThisBuild / version := "0.1.5-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/edu-extern"),
    "scm:git@github.com:openurp/edu-extern.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Edu Extern"
ThisBuild / homepage := Some(url("http://openurp.github.io/edu-extern/index.html"))

val apiVer = "0.39.1"
val starterVer = "0.3.32"
val baseVer = "0.4.24"
val eduCoreVer = "0.2.6"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_edu_core = "org.openurp.edu" % "openurp-edu-core" % eduCoreVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer

lazy val webapp = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin, UndertowPlugin)
  .settings(
    name := "openurp-edu-extern-webapp",
    common,
    libraryDependencies ++= Seq(openurp_edu_api, openurp_stater_web, openurp_edu_core),
    libraryDependencies ++= Seq(openurp_stater_web, openurp_base_tag)
  )

