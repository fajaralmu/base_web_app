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
			console.log("RESPONSE ", this.status, this);
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

function loadEntityList(url, requestObject, callback) {
	
	postReq(url, requestObject,
			function(xhr) {
				var response = (xhr.data);
				var entities = response.entities;
				if (entities != null && entities[0] != null) {
					callback(entities);

				} else {
					alert("data not found");
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
	if(!confirm(" Are you sure want to Delete: "+ entityId+"?")){
		return;
	}
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
					alert("success deleted");
					callback();
				} else {
					alert("error deleting");
				}
			});
}

function doSubmit(url, requestObject, callback){
	postReq(url,
			requestObject, function(xhr) {
				var response = (xhr.data);
				if (response != null && response.code == "00") {
					alert("SUCCESS");
					callback();
				} else {
					alert("FAILS");
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
					alert("data not found");
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
					alert("data not found");
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
					alert("data not found");
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