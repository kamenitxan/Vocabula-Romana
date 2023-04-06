package controllers

import org.scalatest.DoNotDiscover
import test.TestBase

import scala.io.Source

/**
  * Created by TPa on 27.08.16.
  */
@DoNotDiscover
class RenderTest extends TestBase {

	test("render index") { _ =>
		val fileContents = Source.fromFile("out/index.html").getLines.mkString
		assert(fileContents.contains("<title>Familia Romana</title>"))
		assert(fileContents.contains("FRcls.init();"))
		assert(fileContents.contains("<div id=\"chapters\">"))
		assert(fileContents.contains("<button id=\"random\">Random</button>"))
	}


}
