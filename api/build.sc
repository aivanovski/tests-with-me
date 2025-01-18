import mill._, scalalib._

object main extends ScalaModule {
  def scalaVersion = "3.6.2"

  def ivyDeps = Agg(
    ivy"io.circe::circe-core:0.14.10",
    ivy"io.circe::circe-generic:0.14.10",
    ivy"io.circe::circe-parser:0.14.10",
    ivy"com.squareup.okhttp3:okhttp:4.7.2"
  )
}
