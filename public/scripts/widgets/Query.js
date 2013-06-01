define([
    "dojo/_base/declare",
    "dojo/_base/array",
    "dijit/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dojo/store/JsonRest", 
    "dojo/json",
    "./Note",
    "dojo/text!./templates/Query.htm"
], function(declare, array, _WidgetBase, _TemplatedMixin, JsonRest, json, Note, template) {
return declare([_WidgetBase, _TemplatedMixin], {
    
    templateString: template,
    
    target: "/service/entity/note",
    
    postCreate: function(){
        this.jsonRest = new JsonRest({target: this.target});
    },
    
    _run: function() {
        var self = this;
        this.jsonRest.query().then(function(data){
            array.forEach(data, function(item){
                var note = new Note({target: self.target, entity: item});
                self.list.appendChild(note.domNode);
                
            })
        })
        
    }
});});