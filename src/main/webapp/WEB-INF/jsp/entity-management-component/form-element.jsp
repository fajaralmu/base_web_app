<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag" %> 
 
 <tag:crudForm  ></tag:crudForm>
 <script>
	const groupedInputs = getGroupedInputs();
	const entityForm = byId('entity-form');
	var groupNames = "${entityProperty.groupNames}";
	const inputPreviews = document.getElementsByClassName("input-preview");
	
	function arrangeInputs() {
		if(!groupedInputs) return;
		
		entityForm.innerHTML = "";
		const groupNameArray = groupNames.split(",");
		for (var i = 0; i < groupNameArray.length; i++) {
			const groupName = groupNameArray[i];
			
			console.debug("Now group name: ", groupName);
			
			const groupHeader = getGroupName(i+1, groupName);
			const elements =  getElementsByGroupName(groupName);
			const sectionContent = createHtmlTag({
				tagName:'div',
				id: 'section-'+groupName,
				className : 'form-section',
				ch1: groupHeader, 
				style: {
					padding: '5px',
					margin: '5px', 
				}
			})
			
			console.debug("Elements length: ", elements.length);
			for (var e= 0; e < elements.length; e++) {
				sectionContent.appendChild(elements[e]);
			}
			//appendElementsArray(sectionContent, elements);
			entityForm.appendChild(sectionContent);
			
		}
	}
	
	function getElementsByGroupName(groupName){
		const result = new Array();
		
		for (var i = 0; i < groupedInputs.length; i++) {
			const input = (groupedInputs[i]);
			if(input.getAttribute('groupName') == groupName){
				result.push(input);
			}
		}
		
		return result;
	}
	
	function initInputPreviewsEvent(){
		console.debug("Input previews: ", inputPreviews.length);
		for (var i = 0; i < inputPreviews.length; i++) {
			const inputPreview = inputPreviews[i];
			const name = inputPreview.getAttribute("name");
			 
			byId(name).onchange = function(e){
				const previewLink = "<spring:url value="/api/component/" />"+ inputPreview.getAttribute("preview-link");
				
				postReqHtmlResponse(previewLink+"/"+e.target.value, {}, function(xhr){
					inputPreview.innerHTML = xhr.data;
				});
			}
		}
		 
		
	 
		
	}
	
	function getGroupedInputs(){
		const inputs = document.getElementsByClassName('grouped');
		const result = new Array();
		if(null == inputs || inputs.length == 0){ return null; }
		
		for (var i = 0; i < inputs.length; i++) {
			const cloned = inputs[i].cloneNode();
			cloned.innerHTML = inputs[i].innerHTML;
			result.push(cloned);
		}
		return result;
	} 
	
	function getSectionBordersCount(){
		try{
			return document.getElementsByClassName("section-border").length;
		}catch (e) {
			return 0; 
		}
	} 
	
	function getGroupName(section, groupName){
		const h3 = createHtmlTag({
			tagName: 'h3',
			innerHTML: section + '  ' + groupName,
			className: 'section-border'
		});
		return h3;
	}

	if (groupedInputs && groupedInputs.length > 1 ) {
		arrangeInputs();
	}
	
	initInputPreviewsEvent();
</script>