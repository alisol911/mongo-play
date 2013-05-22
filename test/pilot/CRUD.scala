package pilot

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith( classOf[ JUnitRunner ] )
class CRUD extends Specification {

  "create simple note" in {
    running( FakeApplication( additionalPlugins = Seq( "play.modules.reactivemongo.ReactiveMongoPlugin" ) ) ) {
      val json = Json.obj( "name" -> "test 1" )

      val result = route( FakeRequest( POST, "/note", FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), json ) ).get

      status( result ) must equalTo( OK )
    }
  }
}	