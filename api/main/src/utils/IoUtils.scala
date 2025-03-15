package utils

import java.nio.charset.StandardCharsets
import scala.io.Source
import scala.util.Using

def readFile(path: String): String = {
  val transformedPath = path
    .replace("~", "$HOME")
    .replace("$HOME", System.getProperty("user.home"))

  Using(Source.fromFile(transformedPath)) { source =>
    source.getLines().mkString("\n")
  }.get
}

def readFileBytes(path: String): Array[Byte] = {
  readFile(path).getBytes(StandardCharsets.UTF_8)
}