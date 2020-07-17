/** ********* CONSTANTS ********** */
const tempComponent = document.createElement("div"); 
const monthNames = ["January", "February", "March", "April", "May", "June",
	  "July", "August", "September", "October", "November", "December"
	];

function _byId(id){
	if(id==null || id == ""){
		console.warn("ID IS EMPTY");
	}
	return document.getElementById(id);
}

const loadingDiv = createDiv('loading-div','loading_div');

function infoLoading() {
	 
	document.body.prepend(loadingDiv);
	
	loadingDiv.innerHTML = "";
	const loadingText = createHtmlTag({
		tagName:'div',
		id:'loading-txt',
		innerHTML:'Please Wait'
	}); 
	loadingDiv.appendChild(loadingText); 
	const imagePath = ctxPath+"/res/img/loading-disk.gif";
	const loadingImg = createHtmlTag({
		tagName: "img",
		src: imagePath,
		style: {height: '70px', width: '70px'},
		onload: function(){
			loadingText.innerHTML = "";
		}
	}); 
	
	loadingDiv.appendChild(loadingImg);
}

function infoDone() {
	try{
		loadingDiv.parentNode.removeChild(loadingDiv);
	}catch(e){
		
	}
}

/** ***************COMPONENT*************** */

function htmlToElement(html) {
    var template = document.createElement('template');
    html = html.trim(); // Never return a text node of whitespace as the result
    template.innerHTML = html;
    return template.content.firstChild;
}


function createAnchor(id, html, url){ 
	return createHtmlTag({tagName:"a", innerHTML: html, id: id, href: url}); 
}

function appendElements(parent, ...childs){
	for (var i = 0; i < childs.length; i++) {
		parent.appendChild(childs[i]);
	}
}
function appendElementsArray(parent, childs){
	for (var i = 0; i < childs.length; i++) {
		parent.appendChild(childs[i]);
	}
}

function createNavigationButton(id, html, callback){
	const btn= createAnchor(id,html, "#");
	btn.className = "page-link";
	if(callback != null)
		btn.onclick = function(){
			callback(id);
		}
	const li = createHtmlTag({tagName:"li", class: "page-item" });  
	li.append(btn);
	return li;
}

function createButton(id, html, onclick){
	return createHtmlTag({tagName:"button", class: "btn btn-default", innerHTML: html, id: id, onclick: onclick}); 
}
 
function createButtonWarning(id, html, onclick){
	const btn = createButton(id, html, onclick);
	btn.setAttribute('class', 'btn btn-warning');
	return btn;
}

function createButtonDanger(id, html, onclick){
	const btn = createButton(id, html, onclick);
	btn.setAttribute('class', 'btn btn-danger');
	return btn;
}

function clearElement(...elements){
	for (let i = 0; i < elements.length; i++) {
		let element = elements[i];
		if(!element){
			continue;
		}
		
		if(typeof(element) == "string"){
			if(_byId(element)){
				element = _byId(element);
			}
		}
		
		if(!element){
			continue;
		}
		
		if(element.tagName == "input" || element.tagName == "textarea"){
			element.value = "";
		}else{
			element.innerHTML = "";
		}
	}
}

function createCell(val){
	return createHtmlTag({tagName:"td", innerHTML: val}); 
}

function createRow(val){
	return createHtmlTag({tagName:"tr", innerHTML: val}); 
}

function createInputText(id, className){
	return createInput(id, className, "text"); 
}

function hide(id){
	_byId(id).style.display = "none";
}

function show(id){
	_byId(id).style.display = "block";
}
	 

function toDateInput(date){
	let dateStr  ="";
	let yearStr=date.getFullYear();
	let monthStr =( date.getMonth()+1) >=10?( date.getMonth()+1):"0"+( date.getMonth()+1);
	let dayStr =( date.getDate()+1) >=10?( date.getDate()+1):"0"+( date.getDate()+1);
	dateStr = yearStr+"-"+monthStr+"-"+dayStr;
	
	return dateStr;
	
};

function toBase64(file, callback){
	const reader = new FileReader();
    reader.readAsDataURL(file.files[0]);
    reader.onload = () => callback(reader.result);
    reader.onerror = error => {
    	alert("Error Loading File");
    }
}

function createEmptySpan(){
	return createHtmlTag({tagName: "span", innerHTML: ""});
}

function createDiv(id, className){
	let div = createElement("div", id, className); 
	return div;
}

function createInput(id, className, type){
	const domObj = { tagName:"input",  id:id, class:className, type: type};
	return createHtmlTag(domObj);
}

function createOption(value, text){  
	const domObj = { tagName:"option",  innerHTML:text, value: value   };
	return createHtmlTag(domObj); 
}

function createLabel(text){
	const domObj = { tagName:"label",  innerHTML:text   };
	return createHtmlTag(domObj); 
}

function createHeading(tag ,id, className, html){
	const heading = createElement(tag,id, className);
	heading.innerHTML = html;
	return heading;
}

function createElement(tag, id, className){
	const domObj = {tagName:tag};
	if(className!=null)
		domObj.class = className;
	if(id != null)
		domObj.id  = id;
	
	return createHtmlTag(domObj);
}

function createImgTag(id, className, w, h, src){
	
	const domObj = {
		tagName:"img", 
		src:src, 
		width: w, 
		height:h, 
		id: id, 
		class: className
	};
	return createHtmlTag(domObj);
}

function createGridWrapper(cols, width){
	
	let gridTemplateColumns;
	
	if(width == null){
		gridTemplateColumns = "auto ".repeat(cols);
	}else{
		gridTemplateColumns = (width+" ").repeat(cols);
	}
	const domObj = {tagName:"div", style:{display:grid, gridTemplateColumns:gridTemplateColumns}}; 
	return createHtmlTag(domObj);
}

/**
 * 
 * @returns <br/>
 */
function createBreakLine(){
	return createHtmlTag({tagName:"br"});
}
 
/**
 * 
 * @param styleObject
 * @returns string of ';' joined style items
 */
function stringifyStyleObject(styleObject){
	const keyValueArrays = new Array();
	
	for(key in styleObject){
		const keyValue  = key+":"+styleObject[key];
		keyValueArrays.push(keyValue);
	}
	return keyValueArrays.join(';');
}

/**
 * 
 * @param object
 * @returns <DOM>
 */
function createHtmlTag(object){
	if(null == object){
		object = { tagName: 'span', innerHTML: 'invalid DOM info', style:{color: 'red'}};
	} 
	const tag = document.createElement(object.tagName);
	tag.innerHTML = object["innerHTML"] ? object["innerHTML"] : "";
	
	for(let key in object){
		if(key == 'innerHTML' || key == 'tagName'){
			continue;
		}  
		const value = object[key];
		const isNotNull = object[key] != null;
		const isStyle = key == "style";
		const isObject = isNotNull && typeof(value) ==  "object";
		const isHtmlElement = isNotNull && value instanceof HTMLElement;
		const isFunction = isNotNull && typeof(value) ==  "function";
		const isArray = isNotNull && Array.isArray(value);
		
		if(isHtmlElement){
			tag.appendChild(value);
//		}else if(isArray){
//			for (var i = 0; i < value.length; i++) {
//				const htmlTag = createHtmlTag(value[i]);
//				tag.appendChild(htmlTag);
//			}
		}else if(isObject && !isFunction){
			if(isStyle){
				tag.setAttribute(key, stringifyStyleObject(value));
			}else{ // Html DOM
				console.debug("will create HTML DOM of :", key);
				const htmlObject = value;
				const htmlTag = createHtmlTag(htmlObject);
				tag.appendChild(htmlTag);
			}
		}else if(isFunction){
			 
			tag[key] = function(e){ value(e) };
			 
		}else{
			tag.setAttribute(key, value);
		}
	}
	tag.setAttribute("dynamictag", object.tagName);
	return tag;
}


function domToString(dom){
	tempComponent.innerHTML = "";
	tempComponent.appendChild(dom);
	
	return tempComponent.innerHTML;
}

/** BEGIN ENTITY DETAIL* */
function createTableHeaderByColumns(columns, ignoreNumber){
	console.log("Headers", columns);
	
	let row = createElement("tr","th-header-detail",null);
	if(!ignoreNumber)
		row.append(createCell("<b>No</b>"));
	for (var i = 0; i < columns.length; i++) {
		var column = columns[i];
		column = column.toUpperCase();
		column = column.replace("."," ");
		row.append(createCell("<b>"+column+"</b>"));
	}
	
	return row;
}

/**
 * 
 * @param columns
 * @param entities
 * @param ignoreNumber
 * @returns list of
 *          <tr>
 */
function createTableBody(columns, entities ,ignoreNumber){
	 createTableBody(columns, entities, 0,ignoreNumber);
}

/**
 * 
 * @param rows
 *            list of
 *            <tr>
 * @param id
 * @returns <table>
 */
function createTableFromRows(rows, id){
	let table = createElement	("table", id, "table");
	for (var i = 0; i < rows.length; i++) {
		table.appendChild(rows[i]);
	}
	return table;
}

/**
 * 
 * @param columns
 * @param entities
 * @param beginNumber
 * @param ignoreNumber
 * @returns list of
 *          <tr>
 */
function createTableBody(columns, entities, beginNumber,ignoreNumber){
	if(beginNumber == null){
		beginNumber = 0;
		 
	}
	// let tbody = createElement("tbody", "tbody-detail", "tbody-detail");
	let rows = [];
	for (let j = 0; j < entities.length; j++) {
		let entity = entities[j];
		
		let row = createElement("tr","tr-body-detail-"+j,null);
		if(!ignoreNumber)
			row.append(createCell(beginNumber+1)); 
		beginNumber++;
		for (let i = 0; i < columns.length; i++) {
			let column = columns[i];
			let isUrl = false;
			if(column.includes("setting=")){
				let setting = column.split("setting=")[1];
				column = column.split("setting=")[0].trim();
				
				if(setting.includes("link")){
					isUrl = true;	
				}
			}
			let refField = column.split(".");
			let entityValue = entity[column];
			
			let cell = createCell("");
			cell.setAttribute("name",column); 
			
			if(refField.length>1 && entity[refField[0]] !=null){
			 	entityValue = entity[refField[0]][refField[1]];
			 	
				cell.setAttribute("name", refField[1]);
				 
			}
			let notNull = entityValue!=null;
			if(notNull && typeof(entityValue) == "number"){
		 		entityValue = beautifyNominal(entityValue);
		 	}
			isUrl = typeof (entityValue) == "string" && (entityValue.trim().startsWith("http://") || entityValue.trim().startsWith("https://"));
			
			if(notNull && isUrl){
				entityValue  ="<a href=\""+entityValue+"\">"+entityValue+"</a>";
			}
			cell.innerHTML = entityValue;
			row.append(cell);
		}
		rows.push(row);
	}
	return rows;
}

/** END ENTITY DETAIL* */
const inputFormClass = "filter-field form-control";
const TYPE_DAY = "day";
const TYPE_MONTH = "month";
const TYPE_YEAR = "year";

function createPeriodFilterInput(fieldName, type, callback){
	const id = "filter-" + fieldName + "-" + type;
	 
	const inputDay = createHtmlTag({
		'tagName': "input",
		'id': id, 
		'class': inputFormClass,
		'type': "text",
		'field': fieldName + "-"+ type,
		'style': "width: 30%"
	});
	inputDay.onkeyup = function() { callback(); }
	
	return inputDay;
}

function createFilterInputDate(fieldName, callback){
	const inputGroup = createDiv("input-group-"+fieldName,"input-group input-group-sm mb-3"); 
	// input day
	let inputDay = createPeriodFilterInput(fieldName, TYPE_DAY, callback); 
	// input month
	let inputMonth = createPeriodFilterInput(fieldName, TYPE_MONTH, callback); 
	// input year
	let inputYear = createPeriodFilterInput(fieldName, TYPE_YEAR, callback); 
	
	inputGroup.append(inputDay);
	inputGroup.append(inputMonth);
	inputGroup.append(inputYear);
	return inputGroup;
}
var index = 0;
function randomID(){
	 
	let string = "";
	string = new Date().getUTCMilliseconds();
	index++;
	return index + "-" + string;
}

/**
 * 
 * @param rowList
 * @returns <tbody>
 */
function createTBodyWithGivenValue(rowList){
	const tbody = createElement("tbody",randomID(),null);
	
	for (var i = 0; i < rowList.length; i++) {
		const columns = rowList[i];
		const row = createElement("tr");
		
		for (var j = 0; j < columns.length; j++) {
			let cell = columns[j];
			const column = createElement("td");
			
			if(null!=cell && typeof(cell) == "string" && cell.includes("setting=")){
				var setting = cell.split("setting=")[1];
				// colspan
				if(setting.includes("<colspan>")){
					var collspan = setting.split("<colspan>")[1];
					column.setAttribute("colspan",collspan.split("</colspan>")[0]);
				}
				// style
				if(setting.includes("<style>")){
					var style = setting.split("<style>")[1].split("</style>")[0];
					column.setAttribute("style",style);
				}
				
				cell = cell.split("setting=")[0];
			}
			if(cell!=null && typeof(cell) == "number"){
				cell = beautifyNominal(cell);
			}
			column.innerHTML = cell;
			row.append(column);
		}
		tbody.append(row);
	}
	return tbody;
	
}

function beautifyNominal(val) {
	let nominal = ""+val;
	let result = "";
	if (nominal.length > 3) {
		let zero = 0;
		for (let i = nominal.length - 1; i > 0; i--) {
			zero++;
			result = nominal[i] + result;
			if (zero == 3) {
				result = "." + result;
				zero = 0;
			}

		}
		result = nominal[0] + result;
	} else {
		result = val;
	}
	return result;
}

/** ******NAVIGATION******** */
function createNavigationButtons(navigationPanel, currentPage, totalData, limit, buttonClickCallback) {
	navigationPanel.innerHTML = "";
	
	var buttonCount = Math.ceil(totalData / limit);
	let prevPage = getPreviousPage(currentPage, buttonCount);
	// prev and first button
	const buttonFirstPage = createNavigationButton(0, "|<",buttonClickCallback);
	const buttonPrevPage = createNavigationButton(prevPage, "<",buttonClickCallback);
	
	appendElements(navigationPanel, buttonFirstPage, buttonPrevPage);

	/* DISPLAYED BUTTONS */
	const displayed_buttons = getDisplayedButtonIndexes(currentPage); 
	
	let firstSeparated = false;
	let lastSeparated = false;

	for (let i = 0; i < buttonCount; i++) {
		let buttonValue = i * 1 + 1;
		let included = false;
		for (let j = 0; j < displayed_buttons.length; j++) {
			if (displayed_buttons[j] == i && !included) {
				included = true;
			}
		}
		if (!lastSeparated && currentPage < i - 2 && (i * 1 + 1) == (buttonCount - 1)) {
			// console.log("btn id",btn.id,"MAX",max,"LAST",(jumlahTombol-1));
			lastSeparated = true;
			const lastSeparator = createEmptySpan();
	// navigationPanel.appendChild(lastSeparator);

		}
		if (!included && i != 0 && !firstSeparated) {
			firstSeparated = true;
			var firstSeparator = createEmptySpan();
		// navigationPanel.appendChild(firstSeparator);

		}
		if (!included && i != 0 && i != (buttonCount - 1)) {
			continue;
		}

		const button = createNavigationButton(i, buttonValue, buttonClickCallback);
		if (i == page) {
			button.className = button.className.replace("active", "");
			button.className = button.className + " active ";
		}
		navigationPanel.append(button);
	}

	let nextPage = getNextPage(currentPage, buttonCount);
	// next & last button
	const buttonNextPage = createNavigationButton(nextPage, ">",buttonClickCallback);
	const buttonLastPage = createNavigationButton(buttonCount - 1, ">|",buttonClickCallback);

	appendElements(navigationPanel, buttonNextPage, buttonLastPage); 
	return navigationPanel;
}

function getDisplayedButtonIndexes( currentPage){
	const displayed_buttons = new Array();
	let min = currentPage - 2;
	let max = currentPage + 2;
	
	for (let i = min; i <= max; i++) {
		displayed_buttons.push(i);
	}
	return displayed_buttons;
}

function getPreviousPage(currentPage, buttonCount){
	const currentPageIsFirstPage = currentPage == 0;
	return currentPageIsFirstPage ? 0 : currentPage - 1;
}

function getNextPage(currentPage, buttonCount){
	const currentPageIsLastPage =  currentPage == buttonCount - 1;
	return currentPageIsLastPage ? currentPage : currentPage + 1;
}

function isOneOfInputFieldEmpty(...inputfields ){
	
	for (var i = 0; i < inputfields.length; i++) {
		const input = inputfields[i];
		if(input.value == null || input.value.trim() == ""){
			return true;
		}
		
	}
	
	return false;
	
}

