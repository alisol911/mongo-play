function create(){
    require(["dojo/query", "dojo/on", "dojo/topic", "dojo/request/xhr", "dojo/json",
            "dojo/NodeList-dom", "dojo/NodeList-manipulate", "dojo/parser", "dojo/ready", "dojo/domReady!"],
        function (query, on, topic, xhr, json) {
            xhr("/note", {method: "POST", data: json.stringify({ name: query("#text").val() }), headers: { 'Content-Type': 'application/json' }});
        }
    );
}
