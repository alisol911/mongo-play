package controllers

import play.api._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONInteger
import reactivemongo.bson.BSONObjectID
import play.api.libs.json._
import play.api.http.Writeable

object Application extends Controller with MongoController {

  def collection( collectionName: String ): JSONCollection = db.collection[ JSONCollection ]( collectionName )

  private implicit def stringToObjectID( id: String ): play.api.libs.json.JsObject = {
    JsObject( Seq( "_id" -> JsObject( Seq( "$oid" -> JsString( id ) ) ) ) )
  }

  def index = Action {
    Ok( views.html.index( "Your new application is ready." ) )
  }

  def create( entity: String ) = Action( parse.json ) { implicit request ⇒
    Async {
      val id = BSONObjectID.generate.stringify
      val json = id ++ request.body.as[ JsObject ]
      collection( entity ).insert( json ).map { lastError ⇒
        if ( lastError.inError )
          InternalServerError( lastError.toString )
        else
          Ok( id )
      } recover {
        case e ⇒
          InternalServerError( e.getMessage )
      }
    }
  }

  def find( entity: String, id: String ) = Action { implicit request ⇒
    Async {
      val cursor = collection( entity ).find( stringToObjectID( id ) ).cursor[ play.api.libs.json.JsObject ]
      cursor.toList.map( ( f: List[ JsObject ] ) ⇒ Ok( f.head ) )
    }
  }

  def edit( entity: String, id: String ) = Action( parse.json ) { implicit request ⇒
    Async {
      collection( entity ).update( stringToObjectID( id ), request.body.as[ JsObject ] ).map { lastError ⇒
        if ( lastError.inError )
          InternalServerError( lastError.toString )
        else
          Ok( id )
      } recover {
        case e ⇒
          InternalServerError( e.getMessage )
      }
    }
  }

  def delete( entity: String, id: String ) = Action {
    Async {
      collection( entity ).remove( stringToObjectID( id ) ).map { lastError ⇒
        if ( lastError.inError )
          InternalServerError( lastError.toString )
        else
          Ok( id )
      } recover {
        case e ⇒
          InternalServerError( e.getMessage )
      }
    }
  }
}