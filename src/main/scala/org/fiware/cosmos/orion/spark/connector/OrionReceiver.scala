package org.fiware.cosmos.orion.spark.connector

import org.apache.spark.internal.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver


class OrionReceiver(entities: List[String] = List(),
                    cbUrl: String = "",
                    description: String = "",
                    callbackHost: String =  "",
                    callbackPort: Int = 0)
  extends Receiver[NgsiEvent](StorageLevel.MEMORY_AND_DISK_2) with Logging {

  /*
  def this(entity: String,
           cbUrl: String,
           description: String,
           callbackHost: String,
           callbackPort: Int) {
      this(List(entity),cbUrl,description,callbackHost, callbackPort)
  }
  */

  def this(port: Int) {
      this(List(),"","","", port)
  }

  private var server: OrionHttpServer =_
  private var subscriptionId : String = ""

  def onStart() = {
    server = new OrionHttpServer(store, entities, cbUrl, description, callbackHost, callbackPort)
    server.start(callbackPort, None)
  }

  def onStop(): Unit = {
    server.close()
  }



}