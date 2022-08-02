package cz.kamenitxan.vocabularomana.controler

import java.sql.Connection
import java.util
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.{Gson, GsonBuilder}
import cz.kamenitxan.jakon.core.configuration.{DeployMode, Settings}
import cz.kamenitxan.jakon.core.controller.IController
import cz.kamenitxan.jakon.core.database.DBHelper
import cz.kamenitxan.jakon.core.model.JakonObject
import cz.kamenitxan.jakon.core.template.TemplateEngine
import cz.kamenitxan.jakon.core.template.utils.TemplateUtils
import cz.kamenitxan.vocabularomana.entity.Word
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
 * Created by TPa on 2019-08-24.
 */
class WordsControler extends IController {
	private val logger: Logger = LoggerFactory.getLogger(this.getClass)

	private val template = "raw"
	val strategy: ExclusionStrategy = new ExclusionStrategy() {
		override def shouldSkipClass(clazz: Class[_]) = false

		override def shouldSkipField(field: FieldAttributes): Boolean = {
			field.getName != "id" &
				field.getName != "chapter" &
				field.getName != "latin" &
				field.getName != "cz" &
				field.getName != "en" &
				field.getName != "name"
		}
	}
	private val gson = if (Settings.getDeployMode == DeployMode.DEVEL) {
		new GsonBuilder().addSerializationExclusionStrategy(strategy).setPrettyPrinting().create
	} else {
		new GsonBuilder().addSerializationExclusionStrategy(strategy).create
	}

	private val ALL_WORDS_SQL = "SELECT * FROM Word JOIN Chapter ON Word.chapter_id = Chapter.id"

	def generate() {
		val e: TemplateEngine = TemplateUtils.getEngine
		implicit val conn: Connection = DBHelper.getConnection
		try {
			val stmt = conn.createStatement()
			val words = DBHelper.selectDeep(stmt, ALL_WORDS_SQL)(conn, classOf[Word])
			val context = Map("content" -> gson.toJson(words.asJava))
			e.render(template, "words.json", context)
		} catch {
			case ex: Exception => logger.error("Exception occurred while generation of words json", ex)
		} finally {
			conn.close()
		}
	}
}
