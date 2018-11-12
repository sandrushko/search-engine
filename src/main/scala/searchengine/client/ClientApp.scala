package searchengine.client

import com.typesafe.config.ConfigFactory
import scalaj.http._
import scala.io.StdIn

object ClientApp extends App {

  val config = ConfigFactory.load()
  val servers = config.getStringList("client.servers")

  var currentIndex: Int = 0
  def switchToNextServer() = {
    currentIndex += 1
    if(currentIndex >= servers.size)
      currentIndex = 0
  }

  def currentServer: String = servers.get(currentIndex)
  def currentServerBaseUrl = s"http://$currentServer/text"

  val actionRegexp = "^(\\w+)\\s*'(.*?)'(\\s*'(.*)')?$".r
  while(true) {
    print(">")
    val cmd = StdIn.readLine()
    val actionOpt = actionRegexp.unapplySeq(cmd)
    actionOpt match {
      case Some("put" :: key :: _ :: text :: _) =>
        val response = Http(currentServerBaseUrl + s"/$key").postData(text).asString
        println(response.body)
      case Some("get" :: key :: _) =>
        val response = Http(currentServerBaseUrl + s"/$key").asString
        println(response.body)
      case Some("search" :: text :: _) =>
        val response = Http(currentServerBaseUrl).param("search", text).asString
        println(response.body)
      case _ => println("Wrong action format")
    }
  }
}
