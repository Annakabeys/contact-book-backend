package models.domain

import java.util.UUID
import play.api.libs.json._

final case class Contact (
  id: UUID,
  firstName: String,
  middleName: Option[String],
  lastName: Option[String],
  phoneNumber: String,
  email: Option[String],
  group: Option[String]
)

object Contact:
  val tupled = (apply: (UUID, String, Option[String], Option[String], String, Option[String], Option[String]) => Contact).tupled
  def apply (firstName: String, middleName: Option[String], lastName: Option[String], phoneNumber: String, email: Option[String], group: Option[String]): Contact = apply(UUID.randomUUID(), firstName, middleName, lastName, phoneNumber, email, group)
  def unapply (contact: Contact): Option[(
    UUID,
    String,
    Option[String],
    Option[String],
    String,
    Option[String],
    Option[String]
  )] = {
    Some(
        contact.id,
        contact.firstName,
        contact.middleName,
        contact.lastName,
        contact.phoneNumber,
        contact.email,
        contact.group
      )}
  implicit val contactWrites: Writes[Contact] = Json.writes[Contact]