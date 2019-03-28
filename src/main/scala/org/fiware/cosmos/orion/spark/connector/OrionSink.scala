//package org.fiware.cosmos.orion.spark.connector
//
//import org.apache.spark.sql.{ForeachWriter, Row}
//import org.apache.http.client.methods.HttpPatch
//import org.apache.http.client.methods.HttpPut
//import org.apache.http.client.methods.HttpPost
//import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
//
//import org.apache.http.entity.StringEntity
//import org.apache.http.impl.client.HttpClientBuilder
//import org.slf4j.LoggerFactory
//
///**
//  * Message type accepted by the Orion Sink
//  * @author @sonsoleslp
//  * @param content Content of the body of the message
//  * @param url URL to which the message will be sent
//  * @param contentType Type of content. It can be: ContentType.JSON or ContentType.Plain
//  * @param method HTTP Method. It can be: HTTPMethod.POST, HTTPMethod.PUT, HTTPMethod.PATCH
//  */
//case class OrionSenderObject(content: String, url: String, contentType: ContentType.Value, method: HTTPMethod.Value)
//
///**
//  * Content type of the HTTP message
//  */
//object ContentType extends Enumeration {
//  type ContentType = Value
//  val JSON = Value("application/json")
//  val Plain = Value("text/plain")
//}
//
///**
//  * HTTP Method of the message
//  */
//object HTTPMethod extends Enumeration {
//  type HTTPMethod = Value
//  val POST = Value("HttpPost")
//  val PUT = Value("HttpPut")
//  val PATCH = Value("HttpPatch")
//}
//
///**
//  * Sink for sending Flink processed data to the Orion Context Broker
//  */
//class OrionSender extends ForeachWriter
//
///**
//  * Singleton instance of OrionSink
//  */
//object OrionSink {
//
//  /**
//    * Function for adding the Orion Sink
//    * @param stream DataStream of the OrionSinkObject
//    */
//  def addSink( datasetOfString: DataStream[OrionSenderObject]): Unit  = {
//
//    datasetOfString.writeStream.foreach(
//      new ForeachWriter[String] {
//
//        def open(partitionId: Long, version: Long): Boolean = {
//          // Open connection
//
//        }
//
//        def process(record: String) = {
//          // Write string to connection
//          val httpEntity : HttpEntityEnclosingRequestBase= createHttpMsg(msg)
//          val client = HttpClientBuilder.create.build
//          val response = client.execute(httpEntity)
//        }
//
//        def close(errorOrNull: Throwable): Unit = {
//          // Close the connection
//          logger.error(errorOrNull.toString)
//        }
//      }
//    ).start()
//
//    stream.addSink( msg => {
//
//      val httpEntity : HttpEntityEnclosingRequestBase= createHttpMsg(msg)
//
//      val client = HttpClientBuilder.create.build
//
//      try {
//        val response = client.execute(httpEntity)
//        logger.info("POST to " + msg.url)
//      } catch {
//        case e: Exception => {
//          logger.error(e.toString)
//        }
//      }
//
//    })
//
//  }
//
//  /**
//    * Create the HTTP message from the specified params
//    * @param msg OrionSinkObject
//    * @return Built Http Entity
//    */
//  def createHttpMsg(msg: OrionSender) : HttpEntityEnclosingRequestBase= {
//    val httpEntity = getMethod(msg.method, msg.url)
//    httpEntity.setHeader("Content-type", msg.contentType.toString)
//    httpEntity.setEntity(new StringEntity((msg.content)))
//    httpEntity
//  }
//}