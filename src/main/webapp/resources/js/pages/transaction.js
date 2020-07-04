var ENTITY_GET_URL;
function receiptFooterRow(summaryPrice) {
	return createHtmlTag({
		"tagName" : "tr",
		"ch1" : {
			"tagName" : "td",
			"style" : {
				"text-align" : "right"
			},
			"colspan" : 7,
			"innerHTML" : "Total : " + beautifyNominal(summaryPrice)
		}
	});
}

function receiptHeaderRow2() {
	return createHtmlTag({
		"tagName" : "tr",
		"ch1" : {
			"tagName" : "td",
			"style" : {
				"text-align" : "center"
			},
			"colspan" : 7,
			"ch1" : {
				"tagName" : "h3",
				"innerHTML" : "Products"
			}
		}
	});
}

function receiptHeaderRow(summaryPrice) {
	return createHtmlTag({
		tagName : 'tr',
		ch1 : {
			tagName : 'td',
			innerHTML : 'Transaction Amount'
		},
		ch2 : {
			tagName : 'td',
			style : {
				'text-align' : 'left'
			},
			colspan : 2,
			ch1 : {
				tagName : 'u',
				innerHTML : beautifyNominal(summaryPrice)
			}
		}
	});
}


function loadStakeHolderList(entityDropDown, entityName, entityFieldName, filterValue, onOptionClick) {
	if(ENTITY_GET_URL == null){
		alert("ENTITY_GET_URL not defined!");
	}
	
	clearElement(entityDropDown);
	var requestObject = {
		"entity" : entityName,
		"filter" : {
			"page" : 0,
			"limit" : 10
		}
	};
	requestObject.filter.fieldsFilter = {};
	requestObject.filter.fieldsFilter[entityFieldName] = filterValue;

	loadEntityList(
			ENTITY_GET_URL,
			requestObject,
			function(entities) {
				for (let i = 0; i < entities.length; i++) {
					const entity = entities[i];
					const option = createHtmlTag({
						tagName: 'option',
						value: entity["id"],
						innerHTML:  entity[entityFieldName],
						onclick :  function() {
							onOptionClick(entity);
						}
					});
					entityDropDown.append(option);
				}
			});
}

function removeFromProductFlowsById(ID) {
	if(productFlows == null){
		alert("productFlows is not defined!");
		return;
	}
	if(productFlowTable == null){
		alert("productFlowTable is not defined!");
		return;
	}
	
	productFlowTable.innerHTML = "";
	for (let i = 0; i < productFlows.length; i++) {
		const productFlow = productFlows[i];
		if (productFlow.id == ID)
			productFlows.splice(i, 1);
	}
}

function containsNull(...objects){
	for (var i = 0; i < objects.length; i++) {
		if(null == objects[i]){
			return true;
		}
	}
	return false;
}

function doPopulateProductFlow(productFlows, rowCreationFunction) {
	if(containsNull(productFlowTable, productFlows, totalPriceLabel)){
		alert("One of variables: productFlowTable, productFlows, totalPriceLabel is null!!");
		return;
	}
	
	productFlowTable.innerHTML = "";
	let totalPrice = 0;
	for (let i = 0; i < productFlows.length; i++) {
		const productFlow = productFlows[i];
		const row = document.createElement("tr");
		rowCreationFunction(index, productFlow, row); 
		
		const optionCell = createCell(""); 
		const btnEdit = createButtonWarning("edit-" + productFlow.id, "edit", function() {
			setCurrentProductFlow(productFlow);
		});
		const btnDelete = createButtonDanger("delete-" + productFlow.id, "delete", function() {
			if (!confirm("Are you sure wnat to delete?")) {
				return;
			}
			productFlows.splice(i, 1);
			populateProductFlow(productFlows);
		});
		 
		optionCell.append(btnEdit);
		optionCell.append(btnDelete);
		row.append(optionCell);
		productFlowTable.append(row);

		totalPrice = totalPrice*1+(productFlow.price * productFlow.count);

	}

	totalPriceLabel.innerHTML = beautifyNominal(totalPrice);
//	_byId("total-price-label").value = totalPrice;
}

function processReceipt(transaction){
	const requestDetailFlows = {
		    "entity": "productFlow",
		    "filter": {
		        "limit": 0, 
		        "contains": false,
		        "exacts": true, 
		        "fieldsFilter": {
		            "transaction":transaction.code
		        }
		    }
		};
	doGetDetail(ENTITY_GET_URL ,requestDetailFlows, populateReceiptProductDetail); 
	
	show("content-receipt");
	hide("content-form");
}

function populateReceiptProductDetail(entities){
	 
	const tableColumns = [];
	const detailFields = ["NO","Product","ID","Expiry Date","Qty","Unit","Price","Total Price"];
	
	tableColumns.push(detailFields);
	var summaryPrice = 0;
	for (let i = 0; i < entities.length; i++) {
		const productFlow = entities[i];
		const totalPrice = productFlow.count*1 * productFlow.price*1;
		const columns = [
			i+1,
			productFlow.product.name, productFlow.id, productFlow.expiryDate, productFlow.count,
			productFlow.product.unit.name, productFlow.price, totalPrice
			];
		summaryPrice += totalPrice;
		tableColumns.push(columns);
	}
	const tbody  = createTBodyWithGivenValue(tableColumns);
	
	//clearElement(tableReceipt);
	tableReceipt.appendChild(receiptHeaderRow(summaryPrice));
	tableReceipt.appendChild(receiptHeaderRow2());
	tableReceipt.appendChild(tbody);
	tableReceipt.appendChild(receiptFooterRow(summaryPrice));
}