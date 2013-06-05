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

  private def collection( collectionName: String ): JSONCollection = db.collection[ JSONCollection ]( collectionName )

  private implicit def stringToObjectID( id: String ): play.api.libs.json.JsObject = {
    JsObject( Seq( "_id" -> JsObject( Seq( "$oid" -> JsString( id ) ) ) ) )
  }

  private def operationWraper( op: () ⇒ scala.concurrent.Future[ reactivemongo.core.commands.LastError ],
                               result: () ⇒ Result ): Result = {
    Async {
      op().map { lastError ⇒
        if ( lastError.inError )
          InternalServerError( lastError.toString )
        else
          result()
      } recover {
        case e ⇒
          InternalServerError( e.getMessage )
      }
    }
  }

  private def findWraper( op: () ⇒ scala.concurrent.Future[ List[ play.api.libs.json.JsObject ] ],
                          result: ( List[ JsObject ] ) ⇒ Result ): Result = {
    Async {
      op().map{ ( l: List[ JsObject ] ) ⇒
        if ( l.isEmpty )
          NoContent
        else
          result( l )
      }
    }

  }

  def index = Action {
    Ok( views.html.index( "" ) )
  }

  def create( entity: String ) = Action( parse.json ) { implicit request ⇒
    val id = BSONObjectID.generate.stringify
    val json = id ++ request.body.as[ JsObject ]
    operationWraper( () ⇒ collection( entity ).insert( json ), () ⇒ Ok( json ) )
  }

  def find( entity: String, id: String ) = Action { implicit request ⇒
    findWraper( () ⇒ collection( entity ).find[ JsObject ]( id )
      .cursor[ play.api.libs.json.JsObject ].toList, ( l ) ⇒ if ( l.isEmpty ) NoContent else Ok( l.head ) )
  }

  def findAll( entity: String, criteria: String ) = Action { implicit request ⇒
    findWraper( () ⇒ collection( entity ).find( Json.parse( criteria ) )
      .cursor[ play.api.libs.json.JsObject ]
      .toList, ( l ) ⇒ if ( l.isEmpty ) NoContent else Ok( JsArray( l ) ) )
  }

  def edit( entity: String, id: String ) = Action( parse.json ) { implicit request ⇒
    val json = request.body.as[ JsObject ]
    operationWraper( () ⇒ collection( entity ).update[ JsObject, JsObject ]( id, Json.obj( "$set" -> json ) ), () ⇒ Ok( json ) )
  }

  def delete( entity: String, id: String ) = Action {
    operationWraper( () ⇒ collection( entity ).remove[ JsObject ]( id ), () ⇒ NoContent )
  }
}