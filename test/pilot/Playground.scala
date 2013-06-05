package pilot

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import play.api.libs.json._

@RunWith( classOf[ JUnitRunner ] )
class Playground extends Specification {
  "find note" in {
    val json = Json.obj( "name" -> "test" )
    println( json )

    println( json + ( "n2" -> JsString( "test2" ) ) )
  }

  "query" in {
    running( FakeApplication( additionalPlugins = Seq( "play.modules.reactivemongo.ReactiveMongoPlugin" ) ) ) {
      val findAll = route( FakeRequest( GET, "/service/entity/note?criteria=%7B%22tags%22%3A%7B%22%24in%22%3A%5B%22test1%22%2C%22test4%22%5D%7D%7D" ) ).get
      status( findAll ) must equalTo( OK )
      println( contentAsString( findAll ) )
    }
  }
}