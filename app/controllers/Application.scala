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
    Ok( views.html.index( "" ) )
  }

  def create( entity: String ) = Action( parse.json ) { implicit request ⇒
    Async {
      val id = BSONObjectID.generate.stringify
      val json = id ++ request.body.as[ JsObject ]
      collection( entity ).insert( json ).map { lastError ⇒
        if ( lastError.inError )
          InternalServerError( lastError.toString )
        else
          Ok( json )
      } recover {
        case e ⇒
          InternalServerError( e.getMessage )
      }
    }
  }

  def find( entity: String, id: String ) = Action { implicit request ⇒
    Async {
      collection( entity ).find[ JsObject ]( id )
        .cursor[ play.api.libs.json.JsObject ]
        .toList.map{ ( f: List[ JsObject ] ) ⇒
          if ( f.isEmpty )
            NoContent
          else
            Ok( f.head )
        }
    }
  }

  def findAll( entity: String, criteria: String ) = Action { implicit request ⇒
    Async {
      collection( entity ).find( Json.parse( criteria ) )
        .cursor[ play.api.libs.json.JsObject ]
        .toList.map{ ( f: List[ JsObject ] ) ⇒
          if ( f.isEmpty )
            NoContent
          else
            Ok( JsArray( f ) )
        }
    }
  }

  def edit( entity: String, id: String ) = Action( parse.json ) { implicit request ⇒
    Async {
      val json = request.body.as[ JsObject ]
      collection( entity ).update[ JsObject, JsObject ]( id, json ).map { lastError ⇒
        if ( lastError.inError )
          InternalServerError( lastError.toString )
        else
          Ok( json )
      } recover {
        case e ⇒
          InternalServerError( e.getMessage )
      }
    }
  }

  def delete( entity: String, id: String ) = Action {
    Async {
      collection( entity ).remove[ JsObject ]( id ).map { lastError ⇒
        if ( lastError.inError )
          InternalServerError( lastError.toString )
        else
          Ok( "" )
      } recover {
        case e ⇒
          InternalServerError( e.getMessage )
      }
    }
  }
}