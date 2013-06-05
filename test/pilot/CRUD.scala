package pilot

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import scala.util.Random
import play.api.libs.json.JsArray

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith( classOf[ JUnitRunner ] )
class CRUD extends Specification {

  "create simple note" in {
    running( FakeApplication( additionalPlugins = Seq( "play.modules.reactivemongo.ReactiveMongoPlugin" ) ) ) {
      val nameForInsert = "test" + Random.nextInt
      val nameForUpdate = "test" + Random.nextInt
      val familyForUpdate1 = "test " + Random.nextInt
      val familyForUpdate2 = "test " + Random.nextInt
      val jsonForInsert = Json.obj( "name" -> nameForInsert )
      val jsonForUpdate1 = Json.obj( "name" -> nameForUpdate, "family" -> familyForUpdate1 )
      val jsonForUpdate2 = Json.obj( "family" -> familyForUpdate2 )

      val createResult = route( FakeRequest( POST, "/service/entity/note",
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForInsert ) ).get
      status( createResult ) must equalTo( OK )
      val id = ( Json.parse( contentAsString( createResult ) ) \ "_id" \ "$oid" ).as[ String ]

      val findForInsertResult = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForInsertResult ) must equalTo( OK )
      val jsonFindForInsertResult = Json.parse( contentAsString( findForInsertResult ) )
      ( jsonFindForInsertResult \ "name" ).as[ String ] must equalTo( nameForInsert )

      val findAllForInsertResult = route( FakeRequest( GET,
        "/service/entity/note?criteria=%7B%22name%22%3A%22" + nameForInsert + "%22%7D" ) ).get
      status( findAllForInsertResult ) must equalTo( OK )
      val jsonFindAllForInsertResult = Json.parse( contentAsString( findAllForInsertResult ) ).as[ JsArray ]
      ( jsonFindAllForInsertResult( 0 ) \ "name" ).as[ String ] must equalTo( nameForInsert )

      val updateResult1 = route( FakeRequest( PUT, "/service/entity/note/" + id,
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForUpdate1 ) ).get
      status( updateResult1 ) must equalTo( OK )

      val findForUpdateResult1 = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForUpdateResult1 ) must equalTo( OK )
      val jsonFindForUpdateResult1 = Json.parse( contentAsString( findForUpdateResult1 ) )
      ( jsonFindForUpdateResult1 \ "name" ).as[ String ] must equalTo( nameForUpdate )
      ( jsonFindForUpdateResult1 \ "family" ).as[ String ] must equalTo( familyForUpdate1 )

      val updateResult2 = route( FakeRequest( PUT, "/service/entity/note/" + id,
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForUpdate2 ) ).get
      status( updateResult2 ) must equalTo( OK )

      val findForUpdateResult2 = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForUpdateResult2 ) must equalTo( OK )
      val jsonFindForUpdateResult2 = Json.parse( contentAsString( findForUpdateResult2 ) )
      ( jsonFindForUpdateResult2 \ "name" ).as[ String ] must equalTo( nameForUpdate )
      ( jsonFindForUpdateResult2 \ "family" ).as[ String ] must equalTo( familyForUpdate2 )

      val deleteResult = route( FakeRequest( DELETE, "/service/entity/note/" + id ) ).get
      status( deleteResult ) must equalTo( NO_CONTENT )
    }
  }
}	