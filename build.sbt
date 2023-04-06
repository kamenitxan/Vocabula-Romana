import sbtassembly.AssemblyPlugin.autoImport.assembly

val V = new {
	val Scala = "3.2.2"
  val jakon = "0.5.1"
	val circe = "0.14.1"
}

scalaVersion := V.Scala
organization := "cz.kamenitxan"
name := "vocabularomana"
version := V.jakon


ThisBuild / resolvers += Resolver.mavenLocal
ThisBuild / resolvers += "Artifactory" at "https://nexus.kamenitxan.eu/repository/jakon/"


val Dependencies = new {

	lazy val frontend = Seq(

	)

	lazy val backend = Seq(
		libraryDependencies ++=
			Seq(
				"cz.kamenitxan" %% "jakon" % V.jakon  excludeAll (
					ExclusionRule(organization = "com.sun.mail", name = "smtp"),
					ExclusionRule(organization = "javax.mail", name = "javax.mail-api")
				),
				"com.github.japgolly.scalacss" %% "core" % "1.0.0",
				"org.scalatest" %% "scalatest" % "3.2.15" % "test",
				"org.seleniumhq.selenium" % "htmlunit-driver" % "2.52.0" % "test"
			)
	)

	lazy val shared = Def.settings(

	)

	lazy val tests = Def.settings(
	)
}

lazy val root =
	(project in file(".")).aggregate(frontend, backend, shared.js, shared.jvm)

lazy val frontend = (project in file("modules/frontend"))
	.dependsOn(shared.js)
	.enablePlugins(ScalaJSPlugin)
	.settings(scalaJSUseMainModuleInitializer := true)
	.settings(
		Dependencies.frontend,
		Dependencies.tests,
		Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
	)
	.settings(
		commonBuildSettings,
		name := "vocabularomana-fe"
	)

lazy val backend = (project in file("modules/backend"))
	.dependsOn(shared.jvm)
	.settings(Dependencies.backend)
	.settings(Dependencies.tests)
	.settings(commonBuildSettings)
	.enablePlugins(JavaAppPackaging)
	.enablePlugins(DockerPlugin)
	.settings(
		name := "vocabularomana",
		Test / fork := true,
		Universal / mappings += {
			val appJs = (frontend / Compile / fullOptJS).value.data
			appJs -> ("lib/prod.js")
		},
		Universal / javaOptions ++= Seq(
			"--port 8080",
			"--mode prod"
		),
		Docker / packageName := "vocabularomana"
	)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
	.crossType(CrossType.Pure)
	.in(file("modules/shared"))
	.jvmSettings(Dependencies.shared)
	.jsSettings(Dependencies.shared)
	.jsSettings(commonBuildSettings)
	.jvmSettings(commonBuildSettings)
	.settings(
		name := "vocabularomana-shared"
	)

lazy val commonBuildSettings: Seq[Def.Setting[_]] = Seq(
	scalaVersion := V.Scala,
	organization := "cz.kamenitxan",
	name := "jakon",
	version := V.jakon,
	startYear := Some(2015)
)

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
ThisBuild / semanticdbEnabled := false
ThisBuild / scalacOptions += "-deprecation"
ThisBuild / assembly / assemblyMergeStrategy := {
	case PathList("module-info.class") => MergeStrategy.discard
	case x if x.endsWith("module-info.class") => MergeStrategy.discard
	case x if x.endsWith("log4j2.xml") => MergeStrategy.first
	case x =>
		val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
		oldStrategy(x)
}

Test / parallelExecution := false
Test / logBuffered := false

lazy val fastOptCompileCopy = taskKey[Unit]("")
val jsPath = "modules/backend/src/main/resources"
fastOptCompileCopy := {
	val source = (frontend / Compile / fastOptJS).value.data
	IO.copyFile(
		source,
		baseDirectory.value / jsPath / "dev.js"
	)
}

lazy val fullOptCompileCopy = taskKey[Unit]("")
fullOptCompileCopy := {
	val source = (frontend / Compile / fullOptJS).value.data
	IO.copyFile(
		source,
		baseDirectory.value / jsPath / "prod.js"
	)
}

addCommandAlias("runDev", ";fastOptCompileCopy; backend/reStart --mode dev")
addCommandAlias("runProd", ";fullOptCompileCopy; backend/reStart --mode prod")

val scalafixRules = Seq(
	"OrganizeImports",
	"DisableSyntax",
	"LeakingImplicitClassVal",
	"NoValInForComprehension"
).mkString(" ")

val CICommands = Seq(
	"clean",
	"backend/compile",
	"backend/test",
	"frontend/compile",
	"frontend/fastOptJS",
	"frontend/test",
	s"scalafix --check $scalafixRules"
).mkString(";")

val PrepareCICommands = Seq(
	s"scalafix $scalafixRules"
).mkString(";")

addCommandAlias("ci", CICommands)
addCommandAlias("preCI", PrepareCICommands)
addCommandAlias("githubTest", "project backend; coverageOn; coverage; test; coverageReport; coverageOff;")