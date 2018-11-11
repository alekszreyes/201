/***
 * ws.js
 *
 * By Alex Reyes
 *
 * handles the websocket
 * dependencies
 *
 *  - notify.js to display message
 *  **/
var socket;
	
/** call to load **/
function connectToWebsocket() {
    socket = new WebSocket("ws://localhost:8080/YouAreWhatYouEat/ss");
    
    socket.onopen = function(event){
	console.log("Connected to websocket");
    }
    socket.onmessage = function(event){
	notify(event.data);
    }
    socket.onclose = function(event){
	console.log("Disconnected");
    }
}
/** 
 * send message to back end through websocket
 * @param str message
 * **/
function socketSend(str) {
    socket.send(str);
}
