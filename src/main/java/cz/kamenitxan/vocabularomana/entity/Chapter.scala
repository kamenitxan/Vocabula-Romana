package cz.kamenitxan.vocabularomana.entity

import java.sql.{Connection, Statement}

import cz.kamenitxan.jakon.core.model.{JakonObject, Ordered}
import cz.kamenitxan.jakon.webui.ObjectSettings
import cz.kamenitxan.jakon.webui.entity.JakonField
import javax.persistence.Transient

/**
  * Created by TPa on 2019-08-24.
  */
class Chapter extends JakonObject(classOf[Chapter].getName) with Ordered {

	@JakonField
	var name: String = _


	@Transient
	@JakonField
	override var visibleOrder: Int = _
	@JakonField(shownInEdit = false, shownInList = false)
	override var objectOrder: Double = _

	override def createObject(jid: Int, conn: Connection): Int = {
		val sql = "INSERT INTO Chapter (id, name, objectOrder) VALUES (?, ?, ?)"
		val stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
		stmt.setInt(1, jid)
		stmt.setString(2, name)
		stmt.setDouble(3, objectOrder)
		executeInsert(stmt)
	}

	override def updateObject(jid: Int, conn: Connection): Unit = {
		val sql = "UPDATE Chapter SET name = ?, objectOrder = ? WHERE id = ?"
		val stmt = conn.prepareStatement(sql)
		stmt.setString(1, name)
		stmt.setDouble(2, objectOrder)
		stmt.setInt(3, id)
		stmt.executeUpdate()
	}

	override def getObjectOrder: Double = objectOrder

	override def setObjectOrder(objectOrder: Double): Unit = this.objectOrder = objectOrder

	override val objectSettings: ObjectSettings = null

	override def toString = s"Chapter($name)"
}
