package controllers

import play.api._
import play.api.mvc._

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object Application extends Controller with MongoController {
  
  def collection( collection: String ): JSONCollection = db.collection[ JSONCollection ]( collection )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def createNote = Action( parse.json ) { implicit request ⇒
    Async {
      collection( "note" ).insert( request.body ).map( lastError ⇒ Ok( if ( lastError.inError ) "error " + lastError else lastError.elements.mkString( "," ) ) )
    }
  }
  
}