import org.openurp.parent.Settings._
import org.openurp.parent.Dependencies._
import org.beangle.tools.sbt.Sas

ThisBuild / organization := "org.openurp.edu.extern"
ThisBuild / version := "0.0.15-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/edu-extern"),
    "scm:git@github.com:openurp/edu-extern.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "chaostone",
    name  = "Tihua Duan",
    email = "duantihua@gmail.com",
    url   = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Std CreditBank"
ThisBuild / homepage := Some(url("http://openurp.github.io/edu-extern/index.html"))

val apiVer = "0.24.0"
val starterVer = "0.0.15"
val baseVer = "0.1.24"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer

lazy val root = (project in file("."))
  .aggregate(core,web,webapp)

lazy val core = (project in file("core"))
  .settings(
    name := "openurp-edu-extern-core",
    common,
    libraryDependencies ++= Seq(openurp_edu_api,beangle_ems_app,openurp_stater_web)
  )

lazy val web = (project in file("web"))
  .settings(
    name := "openurp-edu-extern-web",
    common,
    libraryDependencies ++= Seq(openurp_stater_web,openurp_base_tag,beangle_serializer_text),
  ).dependsOn(core)

lazy val webapp = (project in file("webapp"))
  .enablePlugins(WarPlugin)
  .settings(
    name := "openurp-edu-extern-webapp",
    common,
    libraryDependencies ++= Seq(openurp_stater_web,openurp_base_tag),
    libraryDependencies ++= Seq(Sas.Tomcat % "test")
  ).dependsOn(core,web)



