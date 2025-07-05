import controllers.RenderTest
import cz.kamenitxan.jakon.JakonInit
import cz.kamenitxan.jakon.core.Director
import cz.kamenitxan.jakon.core.configuration.Settings
import cz.kamenitxan.jakon.core.controller.PageController
import cz.kamenitxan.jakon.core.custom_pages.AbstractStaticPage
import cz.kamenitxan.jakon.core.database.DBHelper
import cz.kamenitxan.jakon.core.model.{Category, Page, Post}
import cz.kamenitxan.jakon.core.template.Pebble
import cz.kamenitxan.vocabularomana.controler.{IndexControler, WordsControler}
import cz.kamenitxan.vocabularomana.entity.{Chapter, Word}
import org.scalatest.{BeforeAndAfterAll, Suites}

import java.io.{File, IOException}

/**
 * Created by TPa on 27.08.16.
 */
class TestRunner extends Suites(
	new RenderTest
) with BeforeAndAfterAll {

	val config = "jakonConfig=jakon_config_test.properties"

	override def beforeAll(): Unit = {
		new File("jakonUnitTest.sqlite").delete()
		println("Before!")
		Director.init()
		Settings.setTemplateEngine(new Pebble)

		val app = new TestJakonApp()
		try {
			app.run(Array[String](config))
		} catch {
			case _: IOException =>
				app.run(Array[String](config, s"port=${(Settings.getPort + 1).toString} test_param"))
		}

		val staticPage = new AbstractStaticPage("staticPage", "static") {}
		Director.registerCustomPage(staticPage)

		Thread.sleep(1000)
		Director.render()
	}

	override protected def afterAll(): Unit = {
		super.afterAll()
		JakonInit.javalin.stop()
	}

}

class TestJakonApp extends JakonInit {
	
	override def daoSetup(): Unit = {
		super.daoSetup()
		DBHelper.addDao(classOf[Chapter])
		DBHelper.addDao(classOf[Word])

		Director.registerController(new IndexControler)
		Director.registerController(new WordsControler)
	}

	Director.registerController(new PageController)

	override def adminControllers(): Unit = {
		super.adminControllers()
	}
}
