package utils

import okhttp3.{OkHttpClient, Request, RequestBody, Response}
import java.security.cert.X509Certificate
import javax.net.ssl.{SSLContext, TrustManager, X509TrustManager}

object HttpClient {
  private val SERVER_URL = "https://127.0.0.1:8443"

  private val client: OkHttpClient = OkHttpClient.Builder()
    .sslSocketFactory(createSslContext().getSocketFactory, createTrustManager())
    .hostnameVerifier((_, _) => true)
    .build()

  def post(endpoint: String, body: String, authToken: Option[String] = None): Response = {
    val request = Request.Builder()
      .url(s"$SERVER_URL/$endpoint")
      .header("Content-Type", "application/json")
      .post(RequestBody.create(body.getBytes()))

    if (authToken.orNull() != null) {
      request.header("Authorization", s"Bearer ${authToken.get}")
    }

    client.newCall(request.build()).execute()
  }

  def delete(endpoint: String, authToken: Option[String]): Response = {
    val request = Request.Builder()
      .url(s"$SERVER_URL/$endpoint")
      .delete()

    if (authToken.orNull != null) {
      request.header("Authorization", s"Bearer ${authToken.get}")
    }


    client.newCall(request.build()).execute()
  }

  def get(endpoint: String, authToken: Option[String]): Response = {
    val request = Request.Builder()
      .url(s"$SERVER_URL/$endpoint")
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
