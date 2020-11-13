function loadEntity(page) {
	this.limit = byId("select-limit").value;
	if (this.limit > 20 || this.limit < 0) {
		alert("Please select correct limit");
		this.limit = 10;
		return;
	}
	if (page < 0) {
		page = this.page;
	}
	var selectedOrder = selectOrder.value;
	if (selectedOrder != null && selectedOrder != "00") {
		this.orderBy = selectedOrder.split("-")[0];
		this.orderType = selectedOrder.split("-")[1];
	} else {
		this.orderBy = null;
		this.orderType = null;
	}

	var requestObject = {
		"entity" : "product",
		"filter" : {
			"limit" : this.limit,
			"page" : page,
			"orderBy" : orderBy,
			"orderType" : orderType,
			"fieldsFilter" : {
				"name" : nameFilter.value,
				"withStock" : chkBoxGetStock.checked
			}
		}
	};
	if (categoryFilter.value != "00") {
		requestObject["filter"]["fieldsFilter"]["category,id[EXACTS]"] = categoryFilter.value;
	}
	doLoadEntities(this.URL_GET_PRODUCT_PUBLIC, requestObject, function(
			response) {
		if (response.code != "00") {
			alert("Data Not Found");
			return;
		}
		const products = response.entities;
		totalData = response.totalData;
		this.page = response.filter.page;
		setProducts(products);
		updateCatalog();
		updateNavigationButtons();
	});

}

function loadDetail(code) {
	infoLoading();
	var requestObject = {
		"entity" : "product",
		"filter" : {
			"limit" : 1,
			"exacts" : true,
			"contains" : false,
			"fieldsFilter" : {
				"code" : code,
				"withStock" : true,
				"withSupplier" : true
			}
		}
	};
	doLoadEntities(URL_GET_PRODUCT_PUBLIC, requestObject, function(
			response) {
		const entities = response.entities;
		if (entities != null && entities.length > 0)
			populateDetail(entities[0]);
		else
			alert("Data Not Found");
		infoDone();
	});
}

function populateDetail(product) {
	selectedProductId = product.id;
	console.log("product", product);
	hide("catalog-content");

	// POPULATE

	// title, count, price
	productTitle.innerHTML = product.name;
	byId("product-stock").innerHTML = product.count;
	byId("product-price").innerHTML = beautifyNominal(product.price);
	productUnit.innerHTML = product.unit.name;
	productCategory.innerHTML = product.category.name;
	productDescription.innerHTML = product.description;
	// image
	carouselInner.innerHTML = "";
	carouselIndicator.innerHTML = "";

	let images = product.imageUrl.split("~");
	for (var i = 0; i < images.length; i++) {
		let imageUrl = images[i];

		// indicator
		let className = null;
		if (i == 0) {
			className = "active";
		}
		let li = createElement("li", "indicator-" + i, className);
		li.setAttribute("data-slide-to", "" + i);
		li.setAttribute("data-target", "#carouselExampleIndicators");
		carouselIndicator.append(li);

		// inner
		let innerDiv = createDiv("item-" + i, "carousel-item " + className);
		let src = this.IMAGE_PATH + imageUrl;
		let iconImage = createImgTag("icon-" + product.id + "-" + i,
				"d-block w-100  ", "300", "300", src);
		iconImage.setAttribute("alt", product.name + "-" + i);

		innerDiv.append(iconImage);
		carouselInner.append(innerDiv);
	}

	// suppliers
	let suppliers = product.suppliers;

	tableSupplierList.innerHTML = "";
	let tableHeader = createTableHeaderByColumns([ "name", "website", "address" ]);
	let bodyRows = createTableBody([ "name", "website", "address" ], suppliers);
	tableSupplierList.append(tableHeader);
	for (let i = 0; i < bodyRows.length; i++) {
		const row = bodyRows[i];
		tableSupplierList.append(row);
	}

	var slash = "";
	if (!window.location.href.endsWith("/"))
		slash = "/";
	if (defaultOption == "")
		window.history.pushState('detail-page', product.name,
				window.location.href + slash + product.code);
	defaultOption = "";
	show("detail-content");

}

function doLoadMoreSupplier(offset, productId) {
	doLoadEntities(this.URL_GET_SUPPLIER, {
		"filter" : {
			"page" : offset,
			"fieldsFilter" : {
				"productId" : productId
			}
		}
	}, function(response) {
		var entities = response.entities;
		if (entities != null && entities.length > 0) {
			let bodyRows = createTableBody([ "name", "website", "address" ],
					entities, (offset * 5));

			for (var i = 0; i < bodyRows.length; i++) {
				let row = bodyRows[i];
				tableSupplierList.append(row);
			}
		} else
			alert("Data Not Found");
		infoDone();
	});
}
