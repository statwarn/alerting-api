package controllers

import play.api.mvc.{Result, RequestHeader, Filter}
import play.api.Play.{configuration, current}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CORSFilter extends Filter {
  override def apply(nextFilter: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    nextFilter(rh) map {
      result => result.withHeaders(CORSFilter.headers.toSeq: _*)
    }
  }
}

object CORSFilter {
  val headers: Map[String, String] = Map(
    "Access-Control-Allow-Origin" -> configuration.getString("access_control.allow_origin"),
    "Access-Control-Expose-Headers" -> configuration.getString("access_control.expose_headers"),
    "Access-Control-Allow-Methods" -> configuration.getString("access_control.allow_methods"),
    "Access-Control-Allow-Headers" -> configuration.getString("access_control.allow_headers")
  ).collect({
    case (header, Some(value)) => (header, value)
  })
}