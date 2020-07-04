var stompClient = null;
var wsConnected = false;
const websocketRequests = new Array();
 

function sendToWebsocket(url, requestObject){
	if(!wsConnected){
		console.info("Connecting");
		return false;
	}
	stompClient.send(url, {}, JSON.stringify(requestObject));
	return true;
}

function addWebsocketRequest(subscribeUrl, callback){
	
	const wsRequest = {
			subscribeUrl: subscribeUrl,
			callback: callback
	};
	
	websocketRequests.push(wsRequest);
}

/**
 * 
 * @param wsRequest
 *            video call
 * @returns
 */
function connectToWebsocket() {

	const requestIdElement = document.getElementById("request-id");
	 
	if(!websocketUrl){
		alert("websocketUrl is not defined");
		return;
	}
	
	var socket = new SockJS(websocketUrl);
	const stompClients = Stomp.over(socket);
	stompClients.connect({}, function(frame) {
		wsConnected = true;
		// setConnected(true);
		console.log('Connected -> ' + frame, stompClients.ws._transport.ws.url);

		// document.getElementById("ws-info").innerHTML =
		// stompClients.ws._transport.ws.url;
		for(let i =0;i<websocketRequests.length;i++){
			const wsRequest = websocketRequests[i];
			
			if(wsRequest){
				console.debug("set subscribeUrl: ", wsRequest.subscribeUrl)
				stompClients.subscribe(wsRequest.subscribeUrl, function(response) {
					 
					console.log("Websocket from ",wsRequest.subscribeUrl, " Updated...");
					
					var respObject = JSON.parse(response.body);
					 
					wsRequest.callback(respObject);
					 
				});
			}
		}

	});

	this.stompClient = stompClients;
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	// wsConnected = (false);
	console.log("Disconnected");
}
 
