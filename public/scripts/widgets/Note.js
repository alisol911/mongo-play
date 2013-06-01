define([
    "dojo/_base/declare",
    "dijit/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dojo/request/xhr", 
    "dojo/json",
    "dojo/text!./templates/Note.htm"
], function(declare, _WidgetBase, _TemplatedMixin, xhr, json, template) {
return declare([_WidgetBase, _TemplatedMixin], {
    
    templateString: template,

    postCreate: function(){
        this.name.innerText = this.entity.name;
    },
    
    _remove: function() {
        var self = this;
        xhr(this.target + "/" + this.entity._id.$oid, {
            method: "DELETE"
        }).then(function(){
            self.destroy();
        });
    }
});});