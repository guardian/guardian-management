package com.gu.management.mongodb

import com.mongodb.casbah.MongoConnection
import scala.collection.JavaConversions._
import java.lang.reflect.{ Modifier, Field }
import com.mongodb.{ DBTCPConnector, TimingDBTCPConnector }
import com.gu.management.Loggable

trait MongoManagement extends Loggable {

  def wireInTimingMetric(connection: MongoConnection) {
    try {
      //this code manages to wire in a timing metric into the mongodb client.
      //it uses reflection to wrap the DBTCPConnector (which is the object that handles the actual operations that
      //connect to mongo) with some timing metric. The wrapper had to be a sub class of DBTCPConnector rather than
      //just implementing the DBConnector
      val connectorField = classOf[com.mongodb.Mongo].getDeclaredField("_connector")

      val modifiersField = classOf[Field].getDeclaredField("modifiers")
      modifiersField.setAccessible(true)
      modifiersField.setInt(connectorField, connectorField.getModifiers() & ~Modifier.FINAL)

      connectorField.setAccessible(true)
      connectorField.set(
        connection.underlying,
        new TimingDBTCPConnector(
          connectorField.get(connection.underlying).asInstanceOf[DBTCPConnector],
          MongoRequests,
          connection.underlying
        )
      )
      connectorField.setAccessible(false)
    } catch {
      case ex: Throwable => logger.error("Failed to wire timing metrics into mongo db", ex)
    }
  }

}