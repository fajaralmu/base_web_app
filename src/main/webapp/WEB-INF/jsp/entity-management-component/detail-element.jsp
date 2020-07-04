<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="modal fade" id="modal-entity-detail" tabindex="-1"
	role="dialog" aria-labelledby="Entity Detail Modal"
	aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="title-detail-modal">Detail</h5>
				<button type="button" class="close" data-dismiss="modal" onclick="$('#modal-entity-form').modal('show')"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body" style="width: 90%; height: 400px; margin: auto; overflow: scroll;">
				<table class="table" id="table-detail" style="layout: fixed">
				</table>
				<div style="text-align: center">
						<button class="btn btn-outline-success" onclick="loadMoreDetail()">More</button>
					</div>
			</div>		
		 <div class="modal-footer">
        <button type="button" class="btn btn-secondary" onclick="$('#modal-entity-form').modal('show')" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>