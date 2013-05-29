package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.api.collections._
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.JsObject
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID

object Application extends Controller with MongoController {

  def collection( collectionName: String ): JSONCollection = db.collection[JSONCollection]( collectionName )

  def index = Action {
    Ok( views.html.index( "Your new application is ready." ) )
  }

  def createNote = Action( parse.json ) { implicit request ⇒
    Async {
      val id = BSONObjectID.generate.stringify
      val json = request.body.as[JsObject] + ( "_id" -> JsObject( Seq( "$oid" -> JsString( id ) ) ) )
      collection( "note" ).insert( json ).map( lastError ⇒ Ok( if ( lastError.inError ) "error " + lastError else lastError.elements.mkString( "," ) ) )
    }
  }

  def findNote( name: String ) = Action { implicit request ⇒
    Async {
      val cursor = collection( "note" ).find( Json.obj( "name" -> name ) ).cursor[play.api.libs.json.JsObject]
      cursor.toList.map( ( f: List[JsObject] ) ⇒ Ok( JsArray( f ) ) )
    }
  }
}