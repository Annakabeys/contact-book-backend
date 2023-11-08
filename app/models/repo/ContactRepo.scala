package models.repo

import javax.inject._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.ExecutionContext
import models.domain.Contact
import java.util.UUID

@Singleton
final class ContactRepo @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider,
  implicit val ec: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import slick.jdbc.PostgresProfile.api._

  protected class ContactTable(tag: Tag) extends Table[Contact](tag, "CONTACTS") {
    def id = column[UUID]("ID", O.PrimaryKey)
    def firstName = column[String]("FIRST_NAME")
    def middleName = column[Option[String]]("MIDDLE_NAME")
    def lastName = column[Option[String]]("LAST_NAME")
    def phoneNumber = column[String]("PHONE_NUMBER")
    def email = column[Option[String]]("EMAIL")
    def group = column[Option[String]]("GROUP")

    def * = (id, firstName, middleName, lastName, phoneNumber, email, group).mapTo[Contact]
  }

  val contacts = TableQuery[ContactTable]

  def createContactsTable() = db.run(contacts.schema.createIfNotExists)

  def getAllContacts() = db.run(contacts.result)

  def addContact(contact: Contact) = db.run(contacts += contact)

  def deleteContact(id: UUID) = db.run(contacts.filter(_.id === id).delete)

  def updateContact(id: UUID, updatedContact: Contact) = db.run(contacts.filter(_.id === id).update(updatedContact))

  def searchByGroup(group: String) = db.run(contacts.filter(_.group === Some(group)).result)
}

