package com.mongodb

import scala.collection.JavaConversions._
import com.gu.management.{ Loggable, Timing, TimingMetric }
import com.mongodb.ConnectionStatus.Node

class TimingDBTCPConnector(private val targetConnector: DBTCPConnector, private val timingMetric: TimingMetric, mongo: Mongo)
    extends DBTCPConnector(mongo) with Loggable {

  override def requestStart = targetConnector.requestStart()

  override def requestDone = targetConnector.requestDone()

  override def requestEnsureConnection = targetConnector.requestEnsureConnection()

  override def getMaxBsonObjectSize = targetConnector.getMaxBsonObjectSize

  override def getDBPortPool(addr: ServerAddress) = targetConnector.getDBPortPool(addr)

  override def updatePortPool(addr: ServerAddress) = targetConnector.updatePortPool(addr)

  override def close() = targetConnector.close()

  override def debugString() = targetConnector.debugString()

  override def checkMaster(force: Boolean, failIfNoMaster: Boolean) = targetConnector.checkMaster(force, failIfNoMaster)

  override def _error(t: Throwable, slaveOk: Boolean) = targetConnector._error(t, slaveOk)

  override def getConnectPoint = targetConnector.getConnectPoint

  override def getReplicaSetStatus = targetConnector.getReplicaSetStatus

  override def getServerAddressList = targetConnector.getServerAddressList

  override def getAllAddress = targetConnector.getAllAddress

  override def getAddress = targetConnector.getAddress

  override def _checkClosed() = targetConnector._checkClosed()

  override def say(db: DB, m: OutMessage, concern: WriteConcern): WriteResult =
    Timing.debug(logger, "mongo db say()", timingMetric) {
      targetConnector.say(db, m, concern)
    }

  override def say(db: DB, m: OutMessage, concern: WriteConcern, hostNeeded: ServerAddress): WriteResult =
    Timing.debug(logger, "mongo db say()", timingMetric) {
      targetConnector.say(db, m, concern, hostNeeded)
    }

  override def call(db: DB, coll: DBCollection, m: OutMessage, hostNeeded: ServerAddress, decoder: DBDecoder): Response =
    Timing.debug(logger, "mongo db call()", timingMetric) {
      targetConnector.call(db, coll, m, hostNeeded, decoder)
    }

  override def call(db: DB, coll: DBCollection, m: OutMessage, hostNeeded: ServerAddress, retries: Int): Response =
    Timing.debug(logger, "mongo db call()", timingMetric) {
      targetConnector.call(db, coll, m, hostNeeded, retries)
    }

  override def call(db: DB, coll: DBCollection, m: OutMessage, hostNeeded: ServerAddress, retries: Int, readPref: ReadPreference, decoder: DBDecoder): Response =
    Timing.debug(logger, "mongo db call()", timingMetric) {
      targetConnector.call(db, coll, m, hostNeeded, retries, readPref, decoder)
    }

  override def start() { targetConnector.start }

  override def isOpen: Boolean = targetConnector.isOpen

  override def _checkWriteError(db: DB, port: DBPort, concern: WriteConcern) = targetConnector._checkWriteError(db, port, concern)

  override def getMyPort = targetConnector.getMyPort

  override def initDirectConnection() = targetConnector.initDirectConnection()

  override def setMaster(master: Node) = targetConnector.setMaster(master)

  override def isMongosConnection = targetConnector.isMongosConnection

  override def authenticate(credentials: MongoCredential) = targetConnector.authenticate(credentials)
}