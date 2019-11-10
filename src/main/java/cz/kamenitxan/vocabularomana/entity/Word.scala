package cz.kamenitxan.vocabularomana.entity

import java.sql.{Connection, Statement}

import cz.kamenitxan.jakon.core.model.JakonObject
import cz.kamenitxan.jakon.validation.validators.NotEmpty
import cz.kamenitxan.jakon.webui.ObjectSettings
import cz.kamenitxan.jakon.webui.entity.JakonField
import javax.persistence.ManyToOne

/**
  * Created by TPa on 2019-08-24.
  */
class Word extends JakonObject(classOf[Word].getName) {

	@ManyToOne
	@NotEmpty
	@JakonField(searched = true)
	var chapter: Chapter = _
	@NotEmpty
	@JakonField(searched = true)
	var latin: String = _
	@NotEmpty
	@JakonField(searched = true)
	var cz: String = _
	@JakonField(searched = true, required = false)
	var en: String = _

	override def createObject(jid: Int, conn: Connection): Int = {
		val sql = "INSERT INTO Word (id, chapter_id, latin, cz, en) VALUES (?, ?, ?, ?, ?)"
		val stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
		stmt.setInt(1, jid)
		stmt.setInt(2, chapter.id)
		stmt.setString(3, latin)
		stmt.setString(4, cz)
		stmt.setString(5, en)
		executeInsert(stmt)
	}

	override def updateObject(jid: Int, conn: Connection): Unit = {
		val sql = "UPDATE Word SET chapter_id = ?, latin = ?, cz = ?, en = ? WHERE id = ?"
		val stmt = conn.prepareStatement(sql)
		stmt.setInt(1, chapter.id)
		stmt.setString(2, latin)
		stmt.setString(3, cz)
		stmt.setString(4, en)
		stmt.setInt(5, id)
		stmt.executeUpdate()
	}


	override val objectSettings: ObjectSettings = null
}
