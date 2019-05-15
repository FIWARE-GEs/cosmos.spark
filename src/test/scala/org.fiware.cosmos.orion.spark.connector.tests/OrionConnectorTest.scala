package org.fiware.cosmos.orion.spark.connector.tests

import java.net.InetSocketAddress

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{DefaultFullHttpRequest, HttpMethod, HttpVersion}
import io.netty.util.CharsetUtil
import org.apache.http.client.methods.{HttpPatch, HttpPost, HttpPut}
import org.fiware.cosmos.orion.spark.connector.test.SparkJobTest
import org.fiware.cosmos.orion.spark.connector._
import org.junit.{Assert, Test}
import org.mockito.Mockito.mock

import scala.math.Numeric.Implicits.infixNumericOps

object Utils {
  final val Port = 9001
  final val SleepTime = 10000
  final val SleepTimeShort = 2000
  final val ServerAddress = "http://localhost:9001"
  final val OrionAddress = "http://localhost:2026"
  final val ContentType = "Content-Type"
  final val ContentType2 = "Content-Type2"
  final val Content = "Content"
  final val Accept = "Accept"
  final val UserAgent = "User-Agent"
  final val Json = "application/json"
  final val Orion = "orion/0.10.0"
  final val UTF8 = "application/json; charset=utf-8"
  final val FiwareService = "Fiware-Service"
  final val FiwareServicePath = "Fiware-ServicePath"
  final val Demo = "demo"
  final val Test = "/test"
  final val BadContent = "BAD CONTENT"

  final val OtherUrl = "http://localhost:9102"


}

class OrionConnectorTest extends  BaseTest{
  def createMockFullHttpRequest(str: String = simulatedNotification.notification()): DefaultFullHttpRequest ={
    val bytes = str.getBytes(CharsetUtil.UTF_8)
    val content = Unpooled.copiedBuffer(bytes)
    val fhr = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, Utils.ServerAddress, content)
    fhr.headers().set(Utils.ContentType, Utils.UTF8)
    fhr.headers().set(Utils.ContentType2, Utils.UTF8)
    fhr.headers().set(Utils.Accept, Utils.Json)
    fhr.headers().set(Utils.UserAgent, Utils.Orion)
    fhr.headers().set(Utils.FiwareService, Utils.Demo)
    fhr.headers().set(Utils.FiwareServicePath, Utils.Test)
    fhr
  }

  def createMockFullHttpRequestGet(): DefaultFullHttpRequest ={
    val fhr = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, Utils.ServerAddress)
    //  val headers = new HttpHeaders(Utils.ContentType, "application/json; charset=utf-8")
    fhr.headers().set(Utils.ContentType, Utils.Json)
    fhr.headers().set(Utils.ContentType2, Utils.Json)
    fhr.headers().set(Utils.Accept, Utils.Json)
    fhr.headers().set(Utils.UserAgent, Utils.Orion)
    fhr.headers().set(Utils.FiwareService, Utils.Demo)
    fhr.headers().set(Utils.FiwareServicePath, Utils.Test)
    fhr
  }

  @Test def correctNotification: Unit = {
    //val sc  =  new DummySourceContext()
    val ohh = new OrionHttpHandler(NgsiEvent => Unit)
    val req = createMockFullHttpRequest()
    val mockCtx = mock(classOf[ChannelHandlerContext])
  //  ohh.channelRead(mockCtx, req)
    val res = ohh.parseMessage(req)
    Assert.assertTrue(res.isInstanceOf[NgsiEvent])

  }

  @Test(expected=classOf[org.json4s.MappingException])
  def incorrectNotification: Unit = {

    val ohh = new OrionHttpHandler(NgsiEvent => Unit)
    val req = createMockFullHttpRequest("{}")
    val mockCtx = mock(classOf[ChannelHandlerContext])
    //  ohh.channelRead(mockCtx, req)
    var res = ohh.parseMessage(req)
  }

  @Test(expected=classOf[java.lang.Exception])
  def getNotification: Unit = {

    val ohh = new OrionHttpHandler(NgsiEvent => Unit)
    val req = createMockFullHttpRequestGet()
    val mockCtx = mock(classOf[ChannelHandlerContext])
    ohh.channelRead(mockCtx, req)
  }

  @Test def postNotification: Unit = {
   // val sc  =  new DummySourceContext()
    val ohh = new OrionHttpHandler(NgsiEvent => Unit)
    val req = createMockFullHttpRequest()
    val mockCtx = mock(classOf[ChannelHandlerContext])
    ohh.channelRead(mockCtx, req)

  }

  @Test def buildHttpPostSinkEntity : Unit = {
    val os = new OrionSinkObject(Utils.Content, Utils.OrionAddress,  ContentType.Plain, HTTPMethod.POST)
    val httpMsg = OrionSink.createHttpMsg(os)
    val content = scala.io.Source.fromInputStream(httpMsg.getEntity.getContent).mkString

    Assert.assertEquals(httpMsg.getHeaders(Utils.ContentType)(0).getValue, ContentType.Plain.toString())
    Assert.assertEquals(httpMsg.getMethod(), "POST")
    Assert.assertEquals(content, Utils.Content)
  }

  @Test def buildHttpPutSinkEntity : Unit = {
    val os = new OrionSinkObject(Utils.Content, Utils.OrionAddress,  ContentType.JSON, HTTPMethod.PUT)
    val httpMsg = OrionSink.createHttpMsg(os)
    val content = scala.io.Source.fromInputStream(httpMsg.getEntity.getContent).mkString

    Assert.assertEquals(httpMsg.getHeaders(Utils.ContentType)(0).getValue, ContentType.JSON.toString())
    Assert.assertEquals(httpMsg.getMethod(), "PUT")
    Assert.assertEquals(content, Utils.Content)
  }

  @Test def buildHttpPatchSinkEntity : Unit = {
    val os = new OrionSinkObject(Utils.Content, Utils.OrionAddress,  ContentType.JSON, HTTPMethod.PATCH)
    val httpMsg = OrionSink.createHttpMsg(os)
    val content = scala.io.Source.fromInputStream(httpMsg.getEntity.getContent).mkString

    Assert.assertEquals(httpMsg.getHeaders(Utils.ContentType)(0).getValue, ContentType.JSON.toString())
    Assert.assertEquals(httpMsg.getMethod(), "PATCH")
    Assert.assertEquals(content, Utils.Content)
  }

  @Test def getHTTPMethod : Unit = {
   Assert.assertTrue(OrionSink.getMethod(HTTPMethod.POST,"").isInstanceOf[HttpPost])
   Assert.assertTrue(OrionSink.getMethod(HTTPMethod.PUT,"").isInstanceOf[HttpPut])
   Assert.assertTrue(OrionSink.getMethod(HTTPMethod.PATCH,"").isInstanceOf[HttpPatch])
  }

  @Test (expected=classOf[java.lang.Exception]) def nettyServerCallbackUrl : Unit = {
    val os = new OrionHttpServer(NgsiEvent => Unit)
    Assert.assertEquals(os.startNettyServer(Utils.Port,Some("http://callback")).getPort(),Utils.Port)
    os.close()
  }

  @Test def nettyServerNoCallbackUrl : Unit = {
    val os : OrionHttpServer = new OrionHttpServer(NgsiEvent => Unit)
    new Thread(new Runnable {
      def run() {
        Thread.sleep(Utils.SleepTime)
        os.close()
      }
    }).run()
    val  currentAddr : InetSocketAddress = os.startNettyServer(Utils.Port,None)
    Assert.assertEquals(currentAddr.getPort(), Utils.Port)
    os.close()
  }

  @Test def orionSource() : Unit = {
    run(() =>SparkJobTest.main(Array()))
    Thread.sleep(Utils.SleepTimeShort*2)
    for ( x <- 0 to 10){
      val json = simulatedNotification.notification(10*x,x).toString
      sendPostRequest(Utils.OtherUrl,json)
      Thread.sleep(Utils.SleepTimeShort)
    }
    Thread.sleep(Utils.SleepTimeShort*2)
    Assert.assertTrue(simulatedNotification.maxTempVal > 50)
    Assert.assertTrue(simulatedNotification.maxPresVal > 5)
  }

  @Test def orionSourceBadRequest() : Unit = {
    run(() =>SparkJobTest.main(Array()))
    Thread.sleep(Utils.SleepTime)
    val originalValue = simulatedNotification.maxTempVal
    for ( x <- 0 to 10){
      sendPostRequest(Utils.OtherUrl,Utils.BadContent)
      Thread.sleep(Utils.SleepTimeShort)
    }
    Thread.sleep(Utils.SleepTimeShort)
    Assert.assertEquals(simulatedNotification.maxTempVal,originalValue,0)


  }
}
