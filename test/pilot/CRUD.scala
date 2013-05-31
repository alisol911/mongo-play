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
      val familyForUpdate = "test " + Random.nextInt
      val jsonForInsert = Json.obj( "name" -> nameForInsert )
      val jsonForUpdate = Json.obj( "name" -> nameForUpdate, "family" -> familyForUpdate )

      val createResult = route( FakeRequest( POST, "/service/entity/note", FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForInsert ) ).get
      status( createResult ) must equalTo( OK )
      val id = contentAsString( createResult )

      val findForInsertResult = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForInsertResult ) must equalTo( OK )
      val jsonFindForInsertResult = Json.parse( contentAsString( findForInsertResult ) )
      ( jsonFindForInsertResult \ "name" ).toString must equalTo( '"' + nameForInsert + '"' )

      val findAllForInsertResult = route( FakeRequest( GET, "/service/entity/note?criteria=%7B%22name%22%3A%22" + nameForInsert + "%22%7D" ) ).get
      status( findAllForInsertResult ) must equalTo( OK )
      val jsonFindAllForInsertResult = Json.parse( contentAsString( findAllForInsertResult ) ).as[ JsArray ]
      ( jsonFindAllForInsertResult( 0 ) \ "name" ).toString must equalTo( '"' + nameForInsert + '"' )

      val updateResult = route( FakeRequest( PUT, "/service/entity/note/" + id, FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForUpdate ) ).get
      status( updateResult ) must equalTo( OK )

      val findForUpdateResult = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForUpdateResult ) must equalTo( OK )
      val jsonFindForUpdateResult = Json.parse( contentAsString( findForUpdateResult ) )
      ( jsonFindForUpdateResult \ "name" ).toString must equalTo( '"' + nameForUpdate + '"' )
      ( jsonFindForUpdateResult \ "family" ).toString must equalTo( '"' + familyForUpdate + '"' )

      val deleteResult = route( FakeRequest( DELETE, "/service/entity/note/" + id ) ).get
      status( findForUpdateResult ) must equalTo( OK )
    }
  }
}	