import org.openurp.parent.Settings._
import org.openurp.parent.Dependencies._

ThisBuild / organization := "org.openurp.edu.extern"
ThisBuild / version := "0.1.2-SNAPSHOT"

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

val apiVer = "0.30.0"
val starterVer = "0.2.0"
val baseVer = "0.2.0"
val eduCoreVer="0.0.1"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_edu_core = "org.openurp.edu" % "openurp-edu-core" % eduCoreVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer

lazy val root = (project in file("."))
  .aggregate(web, webapp)

lazy val web = (project in file("web"))
  .settings(
    name := "openurp-edu-extern-web",
    common,
    libraryDependencies ++= Seq(openurp_stater_web, openurp_base_tag, openurp_edu_core,beangle_serializer_text),
  )

lazy val webapp = (project in file("webapp"))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "openurp-edu-extern-webapp",
    common,
    libraryDependencies ++= Seq(openurp_stater_web, openurp_base_tag)
  ).dependsOn(web)

publish / skip := true
