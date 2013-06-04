define([
    "dojo/_base/declare",
    "dijit/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dojo/request/xhr", 
    "dojo/json",
    "dojo/text!./templates/Command.htm"
], function(declare, _WidgetBase, _TemplatedMixin, xhr, json, template) {
return declare([_WidgetBase, _TemplatedMixin], {
    templateString: template,
    _defaultText: "Command",
    _run: function() {
        xhr("/service/entity/note", {
            method: "POST",
            data: json.stringify({ name: this.text.value }),
            headers: { 'Content-Type': 'application/json' }
        });
    },
    _focus: function(){
        
    },
    _blur: function(){
    }
});});