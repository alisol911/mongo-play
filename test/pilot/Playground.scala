package pilot

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import play.api.libs.json._

@RunWith( classOf[JUnitRunner] )
class Playground extends Specification {
  "find note" in {
    val json = Json.obj( "name" -> "test" )
    println( json )

    println( json + ( "n2" -> JsString( "test2" ) ) )

  }
}