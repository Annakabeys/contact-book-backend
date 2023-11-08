package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import models.repo.ContactRepo
import play.api.libs.json._
import java.util.UUID
import models.domain.Contact
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport

@Singleton
class HomeController @Inject()(
  val contactRepo: ContactRepo,
  val cc: ControllerComponents,
  implicit val ec: scala.concurrent.ExecutionContext
  ) extends AbstractController(cc) with I18nSupport {

  val contactForm: Form[Contact] = Form(
    mapping(
      "id" -> ignored(UUID.randomUUID()),
      "firstName" -> nonEmptyText,
      "middleName" -> optional(text),
      "lastName" -> optional(text),
      "phoneNumber" -> nonEmptyText,
      "email" -> optional(email),
      "group" -> optional(text)
    )(Contact.apply)(Contact.unapply)
  )

  def index() = Action.async { implicit request: Request[AnyContent] =>
    contactRepo.createContactsTable().map { _ => Ok("Contacts table created!")}
  }

  def getAllContacts() = Action.async { implicit request: Request[AnyContent] =>
    contactRepo.getAllContacts().map { contacts => Ok(Json.toJson(contacts))}
  }

  def addContact() = Action.async { implicit request: Request[AnyContent] =>
    contactForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest)
      },
      contacts => {
        contactRepo.addContact(contacts.copy(id = UUID.randomUUID())).map { _ => Ok(Json.toJson("Contact added!"))}
      }
    )
  }

  def deleteContact(id: String) = Action.async { implicit request =>
    contactRepo.deleteContact(UUID.fromString(id)).map { _ => Ok(Json.toJson("Contact deleted!"))}
  }

  def editContact(id: String) = Action.async { implicit request =>
    contactForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest)
      },
      contacts => {
        contactRepo.updateContact(UUID.fromString(id), contacts.copy(id = UUID.fromString(id))).map { _ => Ok(Json.toJson("Contact updated!"))}
      }
    )
  }

  def searchByGroup(group: String) = Action.async { implicit request: Request[AnyContent] =>
    contactRepo.searchByGroup(group).map { contacts => Ok(Json.toJson(contacts))}
  }
}
