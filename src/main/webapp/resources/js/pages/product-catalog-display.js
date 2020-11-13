/////////////////PRODUCT CARD/////////////////

function createProductDisplayCard(product) {
	// create col
	const colDiv = createDiv("card-wrapper-" + product.id, "col-md-3");
	// create card
	const cardDiv = createHtmlTag({
		tagName : "div",
		id : "card-" + product.id,
		className : "card",
		style : {
			'width' : '100%',
			'background-color' : product.color,
			'color' : product.color
		}
	});

	const iconImage = createProductIconElement(product);
	const categoryTag = createCategoryTag(product);
	const cardBody = createDiv("card-body-" + product.id, "card-body");
	/* <div class="card-body"> */
	// card title
	const cardTitle = createProductCardTitle(product);

	// /LIST GROUP///
	const listGroup = createListGroup(product);

	// ////////LIST ITEMS//////////

	// const productDetailLink = createProductDetailLink(product);
	const listItemCount = createListItemCount(product);
	const listItemPrice = createListItemPrice(product);

	// listGroup.append(productDetailLink);
	listGroup.append(listItemCount);
	listGroup.append(listItemPrice);

	if (this.addAdditionalLink) {
		this.addAdditionalLink(product, listGroup);
	}

	// populate cardbody
	cardBody.append(cardTitle);
	cardBody.append(listGroup);
	cardBody.append(categoryTag);
	// populate overall card
	cardDiv.append(iconImage);
	cardDiv.append(cardBody);

	colDiv.append(cardDiv);
	return colDiv;
}

function createListItemPrice(entity) {
	const id = "list-item-price-" + entity.id;
	const className = "list-group-item d-flex justify-content-between align-items-center";
	const html = createHtmlTag({
		tagName : 'li',
		id : id,
		className : className,
		ch1: {
			tagName: 'span', 
			className: 'fas fa-tags'
		},
		ch2 : {
			tagName : 'span', 
			id : "product-price-" + entity.id,
			innerHTML : beautifyNominal(entity.price)
		}

	});
	return html;
}

function createCategoryTag(entity) {
	const html = createHtmlTag({
		tagName : 'h5',
		id : 'category-' + entity.id,
		ch1 : {
			tagName : 'span',
			className : 'badge badge-secondary',
			innerHTML : entity.category.name
		}
	});
	return html;
}

function createProductIconElement(entity) {
	const imageUrl = entity.imageUrl;
	const src = imageUrl ? IMAGE_PATH + imageUrl.split("~")[0] : IMAGE_PATH+"/Default.BMP";
	const elementId = "icon-" + entity.id;

	const iconImage = createImgTag(elementId, "card-img-top", "100", "200", src);
	iconImage.setAttribute("alt", entity.name);

	return iconImage;
}

function createListItemCount(entity) {
	const listClass = "list-group-item d-flex justify-content-between align-items-center";
	const listItem = createHtmlTag({
		tagName : "li",
		id : "list-item-count-" + entity.id,
		className : listClass,
		ch1 : {
			tagName : "span",
			innerHTML : "Stock"
		},
		ch2 : {
			tagName : "br"
		},
		ch3 : {
			tagName : "span",
			className : "badge badge-primary badge-pill",
			innerHTML : entity.count,
			id : "product-count-" + entity.id
		}
	});

	return listItem;
}

function createProductCardTitle(product) {
	const html = createHtmlTag({
		tagName : 'h5',
		id : 'title-' + product.id,
		innerHTML : product.name,
		style : { color : '#000000' },
		className : 'clickable',
		ch1 : {
			tagName : 'small',
			style : { 'background-color' : 'rgb(224,224,224)' },
			className : 'text-muted',
			innerHTML : (product.newProduct ? "(NEW)" : "")
		},
		onclick : function() {
			loadDetail(product.code);
		}
	});
	/*
	 * html.onclick = function() { loadDetail(product.code); }
	 */
	return html;
}

function createListGroup(entity) {
	return createHtmlTag({
		tagName : "ul",
		id : "list-group-" + entity.id,
		className : "list-group",
		style : {
			color : '#000000'
		}
	});
}

// ///////////////PRODUCT LIST/////////////////
function createProductDisplayList(product) {
	const rowDiv = createDiv("ROW-" + product.id, "row clickable");
	rowDiv.style.margin = '5px';
	rowDiv.style.border = 'solid 1px #cccccc';
	rowDiv.onclick = function(e) {
		loadDetail(product.code);
	}
	// ICON
	const imageUrl = product.imageUrl;
	const src = imageUrl ? IMAGE_PATH + imageUrl.split("~")[0] : IMAGE_PATH+"/Default.BMP";
	const imgDiv = createHtmlTag({
		tagName : "div",
		className : "col-md-2",
		
		ch1 : {
			tagName : "img",
			src : src,
			width: '100',
			height: '100'
		}
	});

	// name
	const nameDiv = createDiv(null, 'col-md-2', product.name);
	//category
	const categoryDiv = createDiv(null, 'col-md-2', product.category.name);
	// price
	const priceDiv = createDiv(null, 'col-md-2', beautifyNominal(product.price));
	// stock
	const countDiv = createDiv(null, 'col-md-2', beautifyNominal(product.count));

	appendElements(rowDiv, imgDiv, nameDiv, categoryDiv, priceDiv, countDiv);
	return rowDiv;
}

function generateProductCatalogListHeaders(){
	const mainHeader = createDiv("product-catalog-list-header", "row");
	mainHeader.style.fontSize = '1.2em'; 
	
	const imgDiv = createDiv(null, 'col-md-2 center-aligned', "<h5>Preview</h5>");
	const nameDiv = createDiv(null, 'col-md-2 center-aligned', "<h5>Name</h5>"); 
	const categoryDiv = createDiv(null, 'col-md-2 center-aligned', "<h5>Category</h5>"); 
	const priceDiv = createDiv(null, 'col-md-2 center-aligned', "<h5>Price</h5>"); 
	const countDiv = createDiv(null, 'col-md-2 center-aligned', "<h5>Stock</h5>");
	appendElements(mainHeader, imgDiv, nameDiv, categoryDiv, priceDiv, countDiv);
	return mainHeader;
}
