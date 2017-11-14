//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/socket/");
webSocket.onmessage = function (msg) {
    var response = JSON.parse(msg.data)
    updateMessages(response.message);
};
webSocket.onclose = function () { alert("WebSocket connection closed") };

function updateMessages(data) {
    insert("messages", data.message);
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message+'<br/>');
}

function id(id) {
    return document.getElementById(id);
}