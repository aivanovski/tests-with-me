package utils

import okhttp3.{OkHttpClient, Request, RequestBody, Response}
import java.security.cert.X509Certificate
import javax.net.ssl.{SSLContext, TrustManager, X509TrustManager}

object HttpClient {

  private val client: OkHttpClient = OkHttpClient.Builder()
    .sslSocketFactory(createSslContext().getSocketFactory, createTrustManager())
    .hostnameVerifier((_, _) => true)
    .build()

  def post(url: String, body: String, authToken: Option[String] = None): Response = {
    val request = Request.Builder()
      .url(url)
      .header("Content-Type", "application/json")
      .post(RequestBody.create(body.getBytes()))

    if (authToken.orNull() != null) {
      request.header("Authorization", s"Bearer ${authToken.get}")
    }

    client.newCall(request.build()).execute()
  }

  def put(url: String, body: String, authToken: Option[String] = None): Response = {
    val request = Request.Builder()
      .url(url)
      .header("Content-Type", "application/json")
      .put(RequestBody.create(body.getBytes()))

    if (authToken.orNull() != null) {
      request.header("Authorization", s"Bearer ${authToken.get}")
    }

    client.newCall(request.build()).execute()
  }

  def delete(url: String, authToken: Option[String]): Response = {
    val request = Request.Builder()
      .url(url)
      .delete()

    if (authToken.orNull != null) {
      request.header("Authorization", s"Bearer ${authToken.get}")
    }


    client.newCall(request.build()).execute()
  }

  def get(url: String, authToken: Option[String]): Response = {
    val request = Request.Builder()
      .url(url)
      .header("Authorization", s"Bearer ${authToken.get}")
      .get()

    client.newCall(request.build()).execute()
  }

  private def createTrustManager(): X509TrustManager =
    new X509TrustManager {
      override def getAcceptedIssuers: Array[X509Certificate] = Array()

      override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

      override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {}
    }

  private def createSslContext(): SSLContext = {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, Array[TrustManager](createTrustManager()), new java.security.SecureRandom())
    sslContext
  }
}
