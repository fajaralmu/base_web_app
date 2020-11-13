function postReq(url, requestObject, callback, blob) {
	infoLoading();
	var request = new XMLHttpRequest();
	var param = JSON.stringify(requestObject);
	request.open("POST", url, true);
	request.setRequestHeader("Content-type", "application/json");
	request.setRequestHeader("requestToken", document.getElementById("token-value").value);
	request.setRequestHeader("requestId", document.getElementById("request-id").value);
	if(blob == true){
		request.responseType = "blob";
	}
	request.onreadystatechange = function() {
		
		if (this.readyState == this.DONE) {
			if(this.status != 200){
				alert("Server Error");
				infoDone();
				return;
			}
			console.debug("RESPONSE ", this.status, this);
			try {
				this['data'] = JSON.parse(this.responseText);
			} catch (e) {
				this['data'] = "{}";
			}
			callback(this);
			infoDone();
		}
		

	}
	request.send(param);
}

function postReqHtmlResponse(url, requestObject, callback ) {
	infoLoading();
	var request = new XMLHttpRequest();
	var param = JSON.stringify(requestObject);
	request.open("POST", url, true);
	request.setRequestHeader("Content-type", "application/json");
	request.setRequestHeader("requestToken", document.getElementById("token-value").value);
	request.setRequestHeader("requestId", document.getElementById("request-id").value);
	 
	request.onreadystatechange = function() {
		
		if (this.readyState == this.DONE) {
			if(this.status != 200){
				alert("Server Error");
				infoDone();
				return;
			}
			console.debug("RESPONSE ", this.status, this);
			try {
				this['data'] =  (this.responseText);
			} catch (e) {
				this['data'] = "{}";
			}
			callback(this);
			infoDone();
		}
		 
	}
	request.send(param);
}

function loadEntityList(url, requestObject, callback) {
	
	postReq(url, requestObject,
			function(xhr) {
				var response = (xhr.data);
				var entities = response.entities;
				if (entities != null && entities[0] != null) {
					callback(entities);

				} else {
					infoDialog("Data Not Found").then(function(e){ });
				}
				
			});
}

/**
 * extract file from xhrResponse
 * @param xhr
 * @returns
 */
function downloadFileFromResponse(xhr){
	let contentDisposition = xhr.getResponseHeader("Content-disposition");
	let fileName = contentDisposition.split("filename=")[1];
	let rawSplit = fileName.split(".");
	let extension = rawSplit[rawSplit.length - 1];
	let blob = new Blob([xhr.response], {type: extension}); 
	let url = window.URL.createObjectURL(blob); 
    let a = document.createElement("a"); 
    
    document.body.appendChild(a);  
     
    a.href = url;
    a.style = "display: none";
    a.download = fileName; 
    a.click(); 
      
    window.URL.revokeObjectURL(url);
}


/**CRUD OPERATION**/
function doDeleteEntity(url, entityName, idField, entityId, callback) {
//	if(!confirm(" Are you sure want to Delete: "+ entityId+"?")){
//		return;
//	}
	var requestObject = {
		"entity" : entityName,
		"filter" : { }
	};
	requestObject.filter.fieldsFilter = {};
	requestObject.filter.fieldsFilter[idField] = entityId;

	postReq(url, requestObject,
			function(xhr) {
				var response = (xhr.data);
				var code = response.code;
				if (code == "00") {
					infoDialog("Success deleting").then(function(e){
						callback();
					});
				} else {
					infoDialog("Error deleting").then(function(e){ });
				}
			});
}

function doSubmit(url, requestObject, callback){
	postReq(url,
			requestObject, function(xhr) {
				var response = (xhr.data);
				if (response != null && response.code == "00") {
					infoDialog("Success").then(function(e){
						callback();
					});
				} else {
					infoDialog("Failed").then(function(e){ });
				}
				
			});
}

function doGetDetail(url,requestObject, callback){
	postReq(
			url,
			requestObject,
			function(xhr) {
				var response = (xhr.data);
				var entities = response.entities;
				if (entities != null && entities[0] != null) {
					callback(entities);
				} else {
					infoDialog("Data Not Found").then(function(e){ });
				}
			});
}

//GET ONE
function doGetById(url, requestObject, callback){
	postReq(url, requestObject,
			function(xhr) {
				var response = (xhr.data);
				var entities = response.entities;
				if (entities != null && entities[0] != null) {
					callback(entities[0]);
				} else {
					infoDialog("Data Not Found").then(function(e){ });
				}
			});
}

function doLoadDropDownItems(url, requestObject, callback){
	postReq(url, requestObject,
			function(xhr) {
				var response = (xhr.data);
				var entities = response.entities;
				if (entities != null && entities[0] != null) {
					callback(entities);

				} else {
					infoDialog("Data Not Found").then(function(e){ });
				}
			});
}

function doLoadEntities(url, requestObject, callback){
	postReq(url, requestObject,
			function(xhr) {
				var response = (xhr.data);
				callback(response);
			});
}