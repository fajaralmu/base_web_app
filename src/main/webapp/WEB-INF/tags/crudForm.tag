<%@ tag body-content="tagdependent" isELIgnored="false" %>
<%-- <%@ attribute name="entityProperty" required="true" %>
<%@ attribute name="singleRecord" required="true" %> --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<div class="modal fade" id="modal-entity-form" tabindex="-1"
	role="dialog" aria-labelledby="Entity Form Modal" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="exampleModalCenterTitle">${entityProperty.alias }</h5>
				<c:if test="${singleRecord == false }">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</c:if>
			</div>
			<div class="modal-body" style="height: 400px; overflow: scroll;">
				<div id="entity-form">

					<!-- ///////////////ELEMENTS////////////////// -->
					<c:forEach var="element" items="${entityProperty.elements}">

						<div id="form_input_${element.id }"
							groupName="${element.inputGroupname }"
							class="entity-input-form ${element.isGrouped() == 'true' ? 'grouped' : '' }"
							style="grid-template-columns:  ${entityProperty.getGridTemplateColumns()}">
							<div class="entity-input-label">
								<label>${element.lableName }</label>
							</div>
							<div class="entity-input-field">
								<c:choose>
									<c:when test="${  element.type == 'fixedlist'}">
										<select class="input-field form-control" id="${element.id }"
											required="${element.required }"
											identity="${element.identity }"
											itemValueField="${element.optionValueName}"
											itemNameField="${element.optionItemName}"
											${element.multipleSelect?'multiple':'' }
											>

										</select>
										<script>
											managedEntity["valueField_${element.id}"] = "${element.optionValueName}";
											managedEntity["itemField_${element.id}"] = "${element.optionItemName}";
											var optionJsonString = "${element.getJsonListString(true)}";

											fixedListOptionValues["${element.id}"] = JSON
													.parse(optionJsonString);
											for (let i = 0; i < fixedListOptionValues["${element.id}"].length; i++) {
												var optionItemName = managedEntity["itemField_${element.id}"];
												var toDisplay; 
												const optionItem = fixedListOptionValues["${element.id}"][i];
												
												if(optionItemName.includes(".")){
													var raw = optionItemName.split(".");
													toDisplay = optionItem[raw[0]][raw[1]];
												}else{
													toDisplay = optionItem["${element.optionItemName}"];
												}
												
												const option = createHtmlTag({
													tagName : 'option',
													value : optionItem["${element.optionValueName}"],
													innerHTML : toDisplay
												});

												byId("${element.id }").append(
														option);
											}
										</script>
									</c:when>
									<c:when test="${element.type == 'dynamiclist'}">
										<input onkeyup="loadListForDynamicListInput(this)" name="${element.id }"
											id="input-${element.id }" class="form-control" type="text" />
										<br />
										<select style="width: 200px" class="input-field form-control"
											id="${element.id }" required="${element.required }"
											multiple="multiple" identity="${element.identity }"
											itemValueField="${element.optionValueName}"
											itemNameField="${element.optionItemName}"
											dynamic-list="true"
											name="${element.entityReferenceClass}"
											 
											>

										</select>
										<script>
											managedEntity["valueField_${element.id}"] = "${element.optionValueName}";
											managedEntity["itemField_${element.id}"] = "${element.optionItemName}";
										</script>
									</c:when>
									<c:when test="${  element.type == 'plainlist'}">
										<select class="input-field form-control" id="${element.id }"
											required="${element.required }"
											identity="${element.identity }" 
											plainlist="true"
											${element.multipleSelect?'multiple':'' }>
											<c:forEach var="val" items="${element.plainListValues }">
												<option value="${val }">${val }</option>
											</c:forEach>

										</select>
									</c:when>
									<c:when test="${  element.type == 'textarea'}">
										<textarea class="input-field form-control" id="${element.id }"
											type="${element.type }" ${element.required?'required':'' }
											identity="${element.identity }">
									</textarea>
									</c:when>
									<c:when test="${  element.showDetail}">
										<input detailfields="${element.detailFields}"
											showdetail="true" class="input-field" id="${element.id }"
											type="hidden" name="${element.optionItemName}"
											disabled="disabled" />

										<button id="btn-detail-${element.id }" class="btn btn-info"
											onclick="showDetail('${element.id }','${element.optionItemName}' )">Detail</button>
									</c:when>
									<c:when
										test="${ element.type=='img' && element.multiple == false}">
										<input class="input-field form-control" id="${element.id }"
											type="file" ${element.required?'required':'' }
											identity="${element.identity }" />

										<button id="${element.id }-file-ok-btn"
											class="btn btn-primary btn-sm"
											onclick="addImagesData('${element.id}')">ok</button>

										<button id="${element.id }-file-cancel-btn"
											class="btn btn-warning btn-sm"
											onclick="cancelImagesData('${element.id}')">cancel</button>
										<div>
											<img id="${element.id }-display" width="50" height="50" />
										</div>
									</c:when>
									<c:when
										test="${ element.type=='img' && element.multiple == true}">
										<div id="${element.id }" name="input-list" class="input-field">
											<div id="${element.id }-0-input-item"
												class="${element.id }-input-item">

												<input class="input-file" id="${element.id }-0" type="file"
													${element.required?'required':'' }
													identity="${element.identity }" />

												<button id="${element.id }-0-file-ok-btn "
													class="btn btn-primary btn-sm"
													onclick="addImagesData('${element.id}-0')">ok</button>

												<button id="${element.id }-0-file-cancel-btn"
													class="btn btn-warning btn-sm"
													onclick="cancelImagesData('${element.id}-0')">cancel</button>

												<button id="${element.id }-0-remove-list"
													class="btn btn-danger btn-sm"
													onclick="removeImageList('${element.id }-0')">Remove</button>

												<div>
													<img id="${element.id }-0-display" width="50" height="50" />
												</div>
											</div>
										</div>
										<button id="${element.id }-add-list"
											onclick="addImageList('${element.id }')" class="btn btn-secondary btn-sm">Add</button>
									</c:when>
									<c:when test="${ element.identity}">
										<input class="input-field form-control" disabled="disabled"
											id="${element.id }" type="text"
											${element.required?'required':'' }
											identity="${element.identity }" />
									</c:when>
									<c:otherwise>
										<input class="input-field form-control" id="${element.id }"
											type="${element.type }" ${element.required?'required':'' }
											identity="${element.identity }" />
									</c:otherwise>
								</c:choose>
							</div>
							<c:if test="${element.hasPreview }">
								<div class="input-preview" preview-link="${element.previewLink }" name="${element.id}" id="preview-${element.id }"></div> 
							</c:if>
						</div>
					</c:forEach>
				</div>

				<!-- </div> -->
			</div>
			<div class="modal-footer">
				<c:if test="${entityProperty.editable == true }">
					<button id="btn-submit" class="btn btn-primary">Save
						Changes</button>
					<c:if test="${singleRecord == false }">
						<button class="btn btn-warning" id="btn-clear">Clear</button>
					</c:if>
				</c:if>
				<c:if test="${singleRecord == false }">
					<button type="button" class="btn btn-secondary"
						data-dismiss="modal">Close</button>
				</c:if>
				<c:if test="${singleRecord == true }">
					<a role="button" class="btn btn-secondary"
						href="<spring:url value="/admin/home" />">Back</a>
				</c:if>
			</div>
		</div>
	</div>
</div>

