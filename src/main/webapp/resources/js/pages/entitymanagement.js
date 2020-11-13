function isTooLong(val) {
	return typeof (val) == "string" && val.length > 35
}

function isColorValue(val) {
	return typeof (val) == "string" && val.startsWith("#")
			&& val.trim().length == 7;
}

function isURLValue(val) {
	return val != null
			&& (val.trim().startsWith("http://") || val.trim()
					.startsWith("https://"));
}

function isNumeric(val){
	return val!= null && typeof (val) == "number";
}

function isObject(val){
	return val!= null && typeof (val) == "object";
}