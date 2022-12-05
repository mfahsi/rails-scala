package com.winova.core.config

import com.typesafe.config.*

case class AppConfig(val componentNames: String*) {

  private lazy val config = loadConfigFiles(componentNames)

  def appConfig(): Config = config

  def loadConfigFiles(componentNames: Seq[String]): Config = {
     ConfigFactory.load().withFallback(componentNames.map(ConfigFactory.parseResources(_)).reduce((a,b)=>a.withFallback(b))).resolve()
  }

}
