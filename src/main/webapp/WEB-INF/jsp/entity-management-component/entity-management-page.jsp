
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<script type="text/javascript">
	var entityName = "${entityProperty.entityName}";
	var page = 0;
	var limit = 5;
	var totalData = 0;

	var imgElements = ${ entityProperty.imageElementsJson };
	var currencyElements = ${ entityProperty.currencyElementsJson };
	var dateElements = ${ entityProperty.dateElementsJson };
	var multipleSelectElements = ${ entityProperty.multipleSelectElementsJson };

	var fieldNames = ${ entityProperty.fieldNames };
	var optionElements = ${ options };
	var fixedListOptionValues = {};
	var imagesData = {};
	var idField = "${entityProperty.idField}";
	var editable = ${ entityProperty.editable };
	var singleRecord = ${ singleRecord == null || singleRecord == false ? false : true }
	var entityIdValue = "${entityId}";
	var managedEntity = {};
	var fullImagePath = "${host}/${contextPath}/${imagePath}/";
	//var entityPropJson = ${entityPropJson};
</script>

<%-- <c:if test="${entityProperty.editable == true }"> --%>
<!-- DETAIL ELEMENT -->
<jsp:include page="../entity-management-component/detail-element.jsp"></jsp:include>

<!-- INPUT FORM -->
<jsp:include page="../entity-management-component/form-element.jsp"></jsp:include>
<%-- </c:if> --%>
<!-- CONTENT -->
<div class="content">
	<h2>${entityProperty.alias }</h2>
	<p></p> 

	<!-- PAGINATION -->
	<div class="input-group mb-3" style="width: 40%">
	
		<c:if test="${entityProperty.editable == true }">
			<button type="btn-show-form" class="btn btn-secondary" 
			data-toggle="modal" data-target="#modal-entity-form">Show Form</button>
		<!-- <button id="btn-show-form" class="btn btn-info" onclick="show('modal-entity-form')">Show
			Form</button> -->
		</c:if>
	
		<input class="form-control" value="Page" disabled="disabled">
		<input class="form-control" type="number" placeholder="page" value="1" id="input-page" style="-moz-appearance:textField" />
		<div class="input-group-append">
			<button class="btn btn-primary" id="btn-filter-ok"
				onclick="setPage()"><i class="fa fa-search" aria-hidden="true"></i></button>
			<button class="btn btn-info" id="btn-printExcel"
				onclick="printExcel()"><i class="fa fa-print" aria-hidden="true"></i> Excel Report</button>
		</div>

	</div>
	<nav>
		<ul class="pagination" id="navigation-panel"></ul>
	</nav>

	<!-- DATA TABLE -->
	<div style="overflow: scroll; width: 100%; border: solid 1px #cccccc">
		<table class="table" id="list-table" style="layout: fixed">
			<thead id="entity-th">
			</thead>
			<tbody id="entity-tb">
			</tbody>
		</table>
	</div>
	<div class="alert alert-secondary" style="margin-top: 5px;" role="alert">
	  <p>Total Record: <span id="info-total-data"></span></p>
	</div>
</div>

<script type="text/javascript">
	//element list
	var fields = document.getElementsByClassName("input-field");
	var filterFields = document.getElementsByClassName("filter-field");
	const SEARCH_FILTER = {};

	var entityTBody = byId("entity-tb");
	var entityTableHead = byId("entity-th");
	var entitiesTable = byId("list-table");

	var filterField = byId("filter-field");
	var filterValue = byId("filter-value");

	var navigationPanel = byId("navigation-panel");
	var orderBy = null;
	var orderType = null;

	//detail
	var currentDetailEntityName = "";
	var currentDetailFieldName = "";
	var currentDetailOffset = 0;
	var detailTable = byId("table-detail");
	/*
		add single image
	 */
	function addImagesData(id) {
		const imageTag = byId(id + "-display");
		toBase64(byId(id), function(result) {
			const imageData = {
				id : result
			};
			imageTag.src = result;
			imagesData[id] = result;
			console.log("Images Data", imagesData);
		});
	}

	/*
		cancel single image
	 */
	function cancelImagesData(id) {
		byId(id).value = null;
		const imageTag = byId(id + "-display");
		imageTag.src = imageTag.getAttribute("originaldata");
		//remove from imagesData object
		imagesData[id] = null;
	}

	//load dropdown list for multiple select
	function loadListForDynamicListInput(inputElement) {

		const element = byId(inputElement.name);
		element.innerHTML = "";
		//converter field
		const itemField = element.getAttribute("itemNameField");
		//foreign key field
		const valueField = element.getAttribute("itemValueField");
		const filterValue = inputElement.value;
		var requestObject = {
			"entity" : element.name,
			"filter" : {
				"page" : 0,
				"limit" : 10
			}
		};
		requestObject.filter.fieldsFilter = {};
		requestObject.filter.fieldsFilter[itemField] = filterValue;

		doLoadDropDownItems("<spring:url value="/api/entity/get" />",
				requestObject, function(entities) {
					for (let i = 0; i < entities.length; i++) {
						const entity = entities[i];
						const option = createHtmlTag({
							tagName : 'option',
							value : entity[valueField],
							innerHTML : entity[itemField],
							onclick : function() {
								inputElement.value = option.innerHTML;
							}
						});

						element.append(option);
					}
				});

	}

	function getById(entityId, callback) {
		var requestObject = {
			"entity" : entityName,
			"filter" : {
				"limit" : 1,
				"page" : 0,
				"exacts" : true,
				"contains" : false

			}
		};
		requestObject.filter.fieldsFilter = {};
		requestObject.filter.fieldsFilter[this.idField] = entityId;

		doGetById("<spring:url value="/api/entity/get" />", requestObject,
				callback);

	}

	function setPage() {
		const selectedPage = byId("input-page").value - 1;

		if (selectedPage < 0) {
			infoDialog("Invalid Page : " + selectedPage + "!!");
			byId("input-page").value = 1;
			return;
		}

		setSelectedPage(selectedPage);
		loadEntity(this.page);
	}
	
	function setSelectedPage(selectedPage){
		this.page = selectedPage;
	}

	function loadEntity(page) {
		if (page < 0) {
			page = this.page;
		}

		console.log("Goto Page: ", page);

		const requestObject = buildRequestObject(page);
		doLoadEntities("<spring:url value="/api/entity/get" />", requestObject,
				function(response) {

					var entities = response.entities;
					if (entities == null) {
						infoDialog("Server Error!");
						return;
					}
					setTotalData(response.totalData); 
					setSelectedPage(response.filter.page);
					populateTable(entities);
					updateNavigationButtons();
				});

	}
	
	function setTotalData(_totalData) {
		totalData = _totalData;
		byId("info-total-data").innerHTML = totalData;
	}

	function printExcel() {
		confirmDialog("Do you want to download excel file from row page "+ this.page + "?")
			.then(function(confirmed){
					if (confirmed) {
						doPrintExcel();
					}
				});
	
	}
			
	function doPrintExcel(){
		const app = this;
		const requestObject = buildRequestObject(this.page);
		promptDialog("Please input count of record", "number").then(function(data){
			if(data.ok){
				const limit = data.value;
				if (limit == null) { return; }
				
				requestObject.filter.limit = limit;
				postReq("<spring:url value="/api/report/entity" />", requestObject,
						function(xhr) { 
							downloadFileFromResponse(xhr); 
							infoDone();
						}, true); 
			}
		})
	}

	function buildRequestObject(page) {
		var requestObject = {
			"entity" : this.entityName,
			"filter" : {
				"limit" : this.limit,
				"page" : page,
				"orderBy" : this.orderBy,
				"orderType" : this.orderType

			}
		};
		requestObject.filter.fieldsFilter = SEARCH_FILTER;
		return requestObject;
	}

	function updateNavigationButtons() {
		createNavigationButtons(this.navigationPanel, this.page,
				this.totalData, this.limit, this.loadEntity);
	}

	//is image field
	function isImage(id) {
		for (var i = 0; i < imgElements.length; i++) {
			var array_element = imgElements[i];
			if (id == array_element) {
				return true;
			}
		}
		return false;
	}

	function existInArray(val, array) {
		for (var i = 0; i < array.length; i++) {
			var array_element = array[i];
			if (val == array_element) {
				return true;
			}
		}
		return false;
	}

	//is multiple select field
	function isMultipleSelect(id) {
		return existInArray(id, multipleSelectElements);

	}

	//is currency field
	function isCurrency(id) {
		return existInArray(id, currencyElements);
	}

	//is date field
	function isDate(id) {
		return existInArray(id, dateElements);

	}

	//populate data table
	function populateTable(entities) {
		entityTBody.innerHTML = "";

		//CONTENT
		for (let i = 0; i < entities.length; i++) {
			const entity = entities[i];
			populateRow(entityTBody, entity, i);
		}
	}

	function populateRow(entityTBody, entity, index) {
		const row = document.createElement("tr");
		row.setAttribute("valign", "top");
		row.setAttribute("class", "entity-record");

		const number = index * 1 + 1 + page * limit;
		row.append(createCell(number));

		for (let j = 0; j < fieldNames.length; j++) {
			const rawValue = entity[fieldNames[j]];
			const fieldName = fieldNames[j];
			const finalValue = getEntityFinalValueForDataTable(rawValue, fieldName);

			row.append(createCell(finalValue));
		}
		const optionCell = createCell("");
		const btnOptionGroup = getButtonOptionGroup(entity, index);

		optionCell.append(btnOptionGroup);
		row.append(optionCell);
		entityTBody.append(row);
	}

	function getButtonOptionGroup(entity, index) {
		//button edit
		const buttonEdit = createButton("btn-edit-" + index, editable ? "<i class=\"fas fa-edit\"></i>"
				: "Detail");
		buttonEdit.className = "btn btn-warning";
		const _idField = this.idField;
		buttonEdit.onclick = function() {
			confirmDialog("will Edit: " + entity[_idField]).then(function(ok){
				if(ok){
					getById(entity[_idField], function(entity) {
						populateForm(entity);
					});
				}
			});
			
		}
		/* row.onclick = function() {
			infoDialog("will Edit: " + entity[idField]);
			getById(entity[idField], function(entity) {
				populateForm(entity);
			});
		} */
		const btnOptionGroup = createDiv("btn-group-option-" + index,
				"btn-group btn-group-sm");
		btnOptionGroup.append(buttonEdit);

		//button delete
		if (editable) {
			const buttonDelete = createButton("delete_" + index, "<i class=\"fa fa-trash\"></i>");
			buttonDelete.className = "btn btn-danger";
			buttonDelete.onclick = function() {
				confirmDialog("will Delete: " + entity[_idField]+"?").then(function(ok){
						if(ok){
							deleteEntity(entity[_idField]);
						}
					});
				} 
			 
			btnOptionGroup.append(buttonDelete);
		}
		return btnOptionGroup;

	}

	/**
		returns DOM element
	 */
	function getEntityFinalValueForDataTable(entityValue, fieldName) {
		//handle object type value  

		if (isObject(entityValue) && !Array.isArray(entityValue)) {
			console.log("TYPE ", typeof (entityValue), fieldName);
			var fieldToDisplay = managedEntity["itemField_" + fieldName];
			if(fieldToDisplay.includes(".")){
				const raw = fieldToDisplay.split(".");
				entityValue = entityValue[raw[0]][raw[1]];
			}else{
				entityValue = entityValue[fieldToDisplay];
			}
			
		} 
		else if(Array.isArray(entityValue)){

			const fieldNameToDisplay = managedEntity["itemField_"+fieldName];
			const stringOfValues = new Array();
			for (var i = 0; i < entityValue.length; i++) {
				const val = entityValue[i];
				stringOfValues.push(val[fieldNameToDisplay]);
			}
			
			entityValue = stringOfValues.join();
		}  
		else if (isDate(fieldName)) {
			entityValue = new Date(entityValue);
		} 
		else if (isNumeric(entityValue)) {
			const dom = createHtmlTag({
				tagName : "span",
				style : { 'font-family' : 'consolas' },
				innerHTML : beautifyNominal(entityValue)
			});
			entityValue = domToString(dom);//"<span style=\"font-family:consolas\">"+ beautifyNominal(entityValue) +"</span>";
		}
		//handle image type value
		else if (isImage(fieldName)) {
			if (entityValue.split("~") != null) {
				entityValue = entityValue.split("~")[0];
			}
			const dom = createHtmlTag({
				tagName : "img",
				width : 30,
				height : 30,
				src : "${host}${contextPath}/${imagePath}/" + (entityValue.endsWith(".ico") ? "ICON/"+entityValue: entityValue)
			});
			entityValue = domToString(dom);
			//"<img width=\"30\" height=\"30\" src=\"${host}/${contextPath}/${imagePath}/" + (entityValue) + "\" />";
		}
		//regular value
		else if (entityValue != null) {

			const isUrl = isURLValue(entityValue);
			const isColor = isColorValue(entityValue); 
			 
			if (isTooLong(entityValue) && !isUrl) {
				entityValue = entityValue.substring(0, 35) + "...";
				
			}else if (isUrl) {
				const anchor = createAnchor(null, entityValue, entityValue);
				entityValue = domToString(anchor);
				
			} else if (isColor) {
				const span = createHtmlTag({
					tagName : 'span',
					style : { 'color' : entityValue, 'font-size' : '1.3em' },
					ch1 : { tagName : 'b', innerHTML : entityValue }
				});
				entityValue = domToString(span);

			}
		}

		return entityValue;
	} 

	function createDataTableInputFilter(fieldName) {
		const inputGroup = createDiv("input-group-" + fieldName,
				"input-group input-group-sm mb-3");
		const input = createInputText("filter-" + fieldName,
				"filter-field form-control");
		input.setAttribute("field", fieldName);
		  
		inputGroup.appendChild(input);
		return inputGroup;
	}

	function createSortingButton(fieldName) {
		//sorting button
		const btnSortGroup = createDiv("btn-group-sort-" + fieldName,
				"btn-group btn-group-sm");
		const ascButton = createButton("sort-asc-" + fieldName, "&#8593;");
		const descButton = createButton("sort-desc-" + fieldName, "&#8595;");

		ascButton.className = "btn btn-outline-secondary btn-sm";
		descButton.className = "btn btn-outline-secondary btn-sm";
		descButton.onclick = function() {

			orderType = "desc";
			orderBy = fieldName;
			loadEntity(page);
		}
		ascButton.onclick = function() {

			orderType = "asc";
			orderBy = fieldName;
			loadEntity(page);
		}
		btnSortGroup.append(ascButton);
		btnSortGroup.append(descButton);

		return btnSortGroup;
	}

	function createExactCheckBox(fieldName) {
		const wrapper = createHtmlTag({
			tagName : "div"
		});
		const checkBoxExact = createElement("input", "checkbox-exact-"
				+ fieldName, "none");
		checkBoxExact.type = "checkbox";
		const checkBoxInfo = createElement("span", "cb-info-" + fieldName,
				"none");
		checkBoxInfo.innerHTML = "Exact Search";
		checkBoxInfo.setAttribute("style", "font-size:0.7em");
		appendElements(wrapper, checkBoxExact, checkBoxInfo);
		return wrapper;
	}

	function createDataTableHeader() {
		/////////HEADER//////////
		this.entityTableHead.innerHTML = "";
		const row = document.createElement("tr");
		row.append(createCell("No"));
		for (let i = 0; i < fieldNames.length; i++) {

			const fieldName = fieldNames[i];
			const isDateField = isDate(fieldName);
			const cell = createCell('<h5>' + extractCamelCase(fieldName)
					+ '</h5>');
			cell.setAttribute('class', 'nowrap');

			var filterInputGroup;

			if (isDateField) {
				filterInputGroup = createFilterInputDate(fieldName, null);

			} else {
				filterInputGroup = createDataTableInputFilter(fieldName);
			}

			cell.append(filterInputGroup);

			//sorting button
			const btnSortGroup = createSortingButton(fieldName);
			cell.append(btnSortGroup);

			//checkbox is exacts
			//let inputGroupExact = createDiv("input-group-exact-"+fieldName,"input-group-text");
			const checkBoxExact = createExactCheckBox(fieldName);
			cell.append(createBreakLine());
			cell.append(checkBoxExact);

			row.append(cell);
		}
		row.append(createCell("Option"));
		entityTableHead.append(row);
	}

	function populateForm(entity) {
		clear();
		for (let j = 0; j < fieldNames.length; j++) {
			const fieldName = fieldNames[j];
			setFieldOfEntity(entity, fieldName);

		}
		//show("modal-entity-form");
		$('#modal-entity-form').modal('show');
	}

	function isShowDetail(elementField) {
		return elementField.getAttribute("showdetail") == "true";
	}

	function isDynamicList(elementField) {
		return elementField.nodeName == "SELECT"
				&& elementField.getAttribute("dynamic-list") == "true";
	}
	
	

	function setFieldOfEntity(entity, fieldName) {
		var entityValue = entity[fieldName];
		var entityValueAsObject = entityValue;
		//element
		const elementField = byId(fieldName);  

		//handle object type value
		if (isObject(entityValue) ) {

			
			//handle multiple select
			if (isDynamicList(elementField)) {
				var objectValueName = managedEntity["valueField_" + fieldName]
				entityValue = entityValueAsObject[objectValueName];
				//foreign key field name
				objectValueName = elementField.getAttribute("itemvaluefield");

				//converter field name
				const objectItemName = elementField
						.getAttribute("itemnamefield");

				const option = document.createElement("option");
				option.value = entityValueAsObject[objectValueName];
				option.innerHTML = entityValueAsObject[objectItemName];
				option.selected = true;

				elementField.append(option);
				//set input value same as converter field name
				const inputField = byId("input-" + fieldName);
				inputField.value = entityValueAsObject[objectItemName];
			}
			else if(isMultipleSelect(fieldName)){
				console.debug(fieldName, " is multiple select. value: ", entityValue);
				
				if(null != entityValue && Array.isArray(entityValue)){
					console.debug("is array");
					
					objectValueName = elementField.getAttribute("itemvaluefield");
					const optionsTag = elementField.options;
					for (var i = 0; i < optionsTag.length; i++) {
						for (var j = 0; j < entityValue.length; j++) {
							if(optionsTag[i].value == entityValue[j][objectValueName].toString()){
								optionsTag[i].setAttribute("selected", "true");
							}
						}
					}
				}
			}
			//handle regular select
			else {
				var objectValueName = managedEntity["valueField_" + fieldName]
				entityValue = entityValueAsObject[objectValueName];
				elementField.value = entityValue;
			}
		}
		//handle image type value
		else if (isImage(fieldName)) {
			const displayElement = byId(fieldName + "-display");
			const url = fullImagePath;

			if (displayElement == null && entityValue != null) {
				elementField.innerHTML = "";
				const entityValues = entityValue.split("~");

				//console.log(fieldName, "values", entityValues);
				for (let i = 0; i < entityValues.length; i++) {
					const array_element = entityValues[i];
					doAddImageList(fieldName, url + array_element,
							array_element);
				}
			} else {
				const resourceUrl = url + (entityValue.endsWith(".ico")? "ICON/"+ entityValue : entityValue);
				displayElement.src = resourceUrl;
				
				displayElement.setAttribute("originaldata", resourceUrl);
				displayElement.setAttribute("originalvalue", entityValue);
			}
		}
		//handle regular value
		else if (!isDynamicList(elementField)) {
			//datefield
			if (isDate(fieldName)) {
				const date = new Date(entityValue);
				entityValue = toDateInput(date);
			}
			//has detail values
			else if (isShowDetail(elementField)) {
				const nameAttr = elementField.getAttribute("name");
				entityValue = entity[nameAttr];
				elementField.setAttribute(nameAttr, entityValue);
			}
			elementField.value = entityValue;

		}
	}

	function clear() {
		const fields = document.getElementsByClassName("input-field");
		for (let i = 0; i < fields.length; i++) {
			let id = fields[i].id;
			let element = byId(id);
			if (element.nodeName == "SELECT"
					&& element.getAttribute("multiple") == "multiple") {
				element.innerHTML = "";
				byId("input-" + id).value = "";
			} else {
				element.value = null;
				element.value = "";
			}
		}
		imagesData = [];
	}

	function addImageList(id) {
		doAddImageList(id, null, null);
	}

	//add image to image list
	function doAddImageList(id, src, originalvalue) {
		const listParent = byId(id);//+"-input-list");
		//current item list elements
		const itemLists = document.getElementsByClassName(id + "-input-item");
		let length = 0;
		if (itemLists != null)
			length = itemLists.length;

		let index = length;
		if (index < 0) {
			index = 0;
		}

		//begin create new list item element
		const elmentIdAndIndex = id + "-" + index;
		//create list item
		const listItem = createDiv(elmentIdAndIndex + "-input-item", id
				+ "-input-item");

		//create file input for choosing image
		const input = createInput(elmentIdAndIndex, "input-file", "file");
		//create image tag for displaying image
		const imgTag = createImgTag(elmentIdAndIndex + "-display", null, "50",
				"50", src);
		if (src != null) {

			imgTag.setAttribute("originaldata", src); //image name with full path 
			imgTag.setAttribute("originalvalue", originalvalue); //image name only
		}

		const btnAddData = createButtonAddImage(elmentIdAndIndex); //button SET selected image
		const btnCancelData = createButtonCancelImage(elmentIdAndIndex); //button CANCEL selectedImage 
		const btnRemoveListItem = createButtonRemoveImage(elmentIdAndIndex); //button REMOVE list item

		//append file input
		listItem.append(input);

		//append buttons
		appendElements(listItem, btnAddData, btnCancelData, btnRemoveListItem);

		//append image display
		const wrapperDiv = createDiv(elmentIdAndIndex + "-wrapper-img",
				"wrapper");
		wrapperDiv.append(imgTag);
		listItem.append(wrapperDiv);

		listParent.append(listItem);

	}

	function createButtonRemoveImage(elmentIdAndIndex) {
		const btnRemoveListItem = createButton(elmentIdAndIndex
				+ "-remove-list", "remove");
		btnRemoveListItem.className = "btn btn-danger btn-sm";
		btnRemoveListItem.onclick = function() {
			removeImageList(elmentIdAndIndex);
		}

		return btnRemoveListItem;
	}

	function createButtonAddImage(elmentIdAndIndex) {
		const btnAddData = createButton(elmentIdAndIndex + "-file-ok-btn", "ok");
		btnAddData.className = "btn btn-primary btn-sm";
		btnAddData.onclick = function() {
			addImagesData(elmentIdAndIndex);
		}

		return btnAddData;
	}

	function createButtonCancelImage(elmentIdAndIndex) {
		const btnCancelData = createButton(elmentIdAndIndex
				+ "-file-cancel-btn", "cancel");
		btnCancelData.className = "btn btn-warning btn-sm";
		btnCancelData.onclick = function() {
			cancelImagesData(elmentIdAndIndex);
		}

		return btnCancelData;
	}

	function removeImageList(id) {
		if (!confirm("Are you sure want to remove this item?"))
			return;
		let element = byId(id);
		element.parentNode.remove(element);
	}

	function loadMoreDetail() {
		this.currentDetailOffset++;
		var requestObject = {
			'entity' : this.currentDetailEntityName,
			'filter' : {
				'limit' : 5,
				'page' : currentDetailOffset,
				'orderBy' : null,
				'contains' : false,
				'exacts' : true,
				'orderType' : null,
				"fieldsFilter" : {}
			}
		};
		const detailElement = byId(this.currentDetailEntityName);

		requestObject.filter.fieldsFilter[entityName] = detailElement
				.getAttribute(this.currentDetailFieldName);
		const detailFields = detailElement.getAttribute("detailfields").split(
				"~");

		console.log("request more detail", requestObject);

		doGetDetail("<spring:url value="/api/entity/get" />", requestObject,
				function(entities) {
					const bodyRows = createTableBody(detailFields, entities,
							this.currentDetailOffset * 5);

					for (var i = 0; i < bodyRows.length; i++) {
						const row = bodyRows[i];
						detailTable.append(row);
					}
				});

	}

	function showDetail(elementId, field) {
		this.currentDetailEntityName = elementId;
		this.currentDetailFieldName = field;
		this.currentDetailOffset = 0;
		var requestObject = {
			'entity' : elementId,
			'filter' : {
				'limit' : 5,
				'page' : 0,
				'orderBy' : null,
				'contains' : false,
				'exacts' : true,
				'orderType' : null,
				"fieldsFilter" : {}
			}
		};
		requestObject.filter.fieldsFilter[entityName] = document
				.getElementById(elementId).getAttribute(field);
		const detailFields = byId(elementId).getAttribute("detailfields")
				.split("~");
		console.log("request", requestObject);
		detailTable.innerHTML = "";

		doGetDetail("<spring:url value="/api/entity/get" />", requestObject,
				function(entities) {
					populateDetailModal(entities, detailFields);
				});

	}

	function populateDetailModal(entities, detailFields) {
		//table detail header
		let tableHeader = createTableHeaderByColumns(detailFields);
		//table detail body
		let bodyRows = createTableBody(detailFields, entities);
		detailTable.append(tableHeader);
		for (var i = 0; i < bodyRows.length; i++) {
			var row = bodyRows[i];
			detailTable.append(row);
		}
		$("#modal-entity-form").modal('hide');
		$('#modal-entity-detail').modal('show');
	}

	function setDefaultOption() {
		if (optionElements == null) {
			return;
		}
		for ( let optionElement in optionElements) {
			if (byId("filter-" + optionElement) == null)
				continue;
			byId("filter-" + optionElement).value = optionElements[optionElement];
			SEARCH_FILTER[optionElement] = optionElements[optionElement];
		}

	}

	function init() {

		if (singleRecord) {
			getById(this.entityIdValue, function(entity) {
				populateForm(entity);
			});
		} else {

			createDataTableHeader();
			setDefaultOption();
			loadEntity(page);
		}
	}
	init();
</script>
<c:if test="${entityProperty.editable == true }">
	<script type="text/javascript">
		function commonFieldRequired(field) {
			return field.required && (field.value == "" || field.value == null)
					&& field.getAttribute("identity") != "true";
		}

		function isIdentityFieldAndNotNull(field) {
			return field.getAttribute("identity") == "true"
					&& field.value != "" && field.value != null;
		}
		
		function submit(){
			$("#modal-entity-form").modal('hide');
			 confirmDialog("Are You Sure ?").then(function (ok){
				 if(ok){
					 _doSubmit();
				 }else{}
				 $("#modal-entity-form").modal('show');
			 });
		}

		function _doSubmit() {
			 
			var requestObject = {};
			var entity = {};
			var endPoint = "add";
			var isNew = true;
			for (var i = 0; i < fields.length; i++) {
				var field = fields[i];
				var fieldId = field.id;

				console.log("FIELD ", field);
				if (commonFieldRequired(field)) {
					alert("Field " + field.id + " must be filled! ");
					return;
				}
				//check if it is update or create operation
				if (isIdentityFieldAndNotNull(field)) {
					isNew = false;
				}

				const finalValue = getFinalValueForSubmit(field);
				entity[fieldId] = finalValue;

			}

			requestObject[entityName] = entity;
			requestObject.entityObject = entity;
			requestObject.entity = entityName;

			console.log("request object", requestObject);

			if (!isNew) {
				endPoint = "update";
			}
			doSubmit("<spring:url value="/api/entity/" />" + endPoint,
					requestObject, function() {
						if (singleRecord) {

						} else {
							$("#modal-entity-form").modal('hide');
							loadEntity(this.page);
							clear();
						}
					});
		}

		function selectNodeAndNotPlainList(field) {
			return field.nodeName == "SELECT"
					&& field.getAttribute("plainlist") == null
		}

		function isInputList(field) {
			return field.getAttribute("name") == "input-list";
		}

		function getFinalValueForSubmit(field) {
			var fieldId = field.id;
			var finalValue = field.value;

			if (selectNodeAndNotPlainList(field)) { //handle select element
				const _idField = field.getAttribute("itemValueField");
				
				if(isMultipleSelect(field.id)){
					finalValue = [];
					const selectedOptions = field.selectedOptions;
					for (var i = 0; i < selectedOptions.length; i++) {
						const selectedOption = selectedOptions[i];
						const finalValueItem = {};
						finalValueItem[_idField] = selectedOption.value;
						finalValue.push(finalValueItem);
					}
				}else{
					finalValue = {};
					finalValue[_idField] = field.value;
				}
			
				
			} else if (isImage(fieldId)) { //handle image element

				//handle multiple images
				if (isInputList(field)) {
					const itemLists = document.getElementsByClassName(fieldId
							+ "-input-item");

					console.log(fieldId, "item list length", itemLists.length);

					if (itemLists == null || itemLists.length == 0) {
						return null;
					}
					const length = itemLists.length;
					finalValue = "";
					for (var j = 0; j < length; j++) {
						const elmentIdAndIndex = fieldId + "-" + j;
						const imgTag = byId(elmentIdAndIndex + "-display");

						//check original image
						const originalValue = imgTag
								.getAttribute("originalvalue");
						if (originalValue != null) {
							finalValue += "{ORIGINAL>>" + originalValue + "}";
						}

						//if current value has NOT been updated
						if (imagesData[fieldId + "-" + j] == null
								|| imagesData[fieldId + "-" + j].trim() == "") {
							finalValue += "~";
						} else {
							//if current value has been UPDATED
							finalValue += imagesData[elmentIdAndIndex] + "~";
						}
					}

				}
				// single image
				else {
					finalValue = imagesData[fieldId];
				}
			} else {//regular element not changed
				//finalValue = field.value;
			}

			return finalValue;
		}

		function deleteEntity(entityId) {
			doDeleteEntity("<spring:url value="/api/entity/delete" />",
					entityName, idField, entityId, function() {
						loadEntity(page);
					});
		}

		function initEvents() {
			byId("btn-submit").onclick = function(e) {
				submit();
			}
			if(byId("btn-clear")){
				byId("btn-clear").onclick = function(e) {
					clear();
				}
			}
			

		}
		
		

		initEvents();
	</script>
</c:if>
<script type="text/javascript">
function initFilterFieldsOnKeyup(){
	for (var i = 0; i < filterFields.length; i++) {
		const filterField = filterFields[i];
		
		filterField.onkeyup = function(e){
			var fieldName = e.target.getAttribute("field");
			const filterValue = e.target.value;
			if (filterValue != "") {
				const checkBoxExact = byId("checkbox-exact-" + fieldName);
				if (checkBoxExact != null && checkBoxExact.checked) {
					fieldName = fieldName + "[EXACTS]";
				}
				SEARCH_FILTER[fieldName] = filterValue;
			} else {
				SEARCH_FILTER[fieldName]  = null;
			}
			
			loadEntity(-1);
			
		}
	}
}
initFilterFieldsOnKeyup();
</script>