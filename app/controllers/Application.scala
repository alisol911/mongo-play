package controllers

import play.api._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONInteger
import reactivemongo.bson.BSONObjectID
import play.api.libs.json._

object Application extends Controller with MongoController {

  def collection( collectionName: String ): JSONCollection = db.collection[ JSONCollection ]( collectionName )

  def index = Action {
    Ok( views.html.index( "Your new application is ready." ) )
  }

  def createNote = Action( parse.json ) { implicit request ⇒
    Async {
      val id = BSONObjectID.generate.stringify
      val json = JsObject( Seq( "_id" -> JsObject( Seq( "$oid" -> JsString( id ) ) ) ) ) ++
        request.body.as[ JsObject ]
      collection( "note" ).insert( json ).map { lastError ⇒
        if ( lastError.inError )
          InternalServerError( lastError.toString )
        else
          Ok( json \ "_id" \ "$oid" )
      } recover {
        case e ⇒
          InternalServerError( JsString( "exception %s".format( e.getMessage ) ) )
      }
    }
  }

  def findNote( name: String ) = Action { implicit request ⇒
    Async {
      val cursor = collection( "note" ).find( Json.obj( "name" -> name ) ).cursor[ play.api.libs.json.JsObject ]
      cursor.toList.map( ( f: List[ JsObject ] ) ⇒ Ok( JsArray( f ) ) )
    }
  }
}