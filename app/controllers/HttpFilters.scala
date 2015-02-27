package controllers

import javax.inject.Inject

import play.api.mvc.EssentialFilter

class HttpFilters @Inject() (corsFilter: CORSFilter) extends play.api.http.HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(corsFilter)
}
