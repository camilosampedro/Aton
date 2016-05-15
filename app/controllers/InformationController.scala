package controllers

import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller

/**
  * Created by camilo on 14/05/16.
  */
class InformationController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

}
