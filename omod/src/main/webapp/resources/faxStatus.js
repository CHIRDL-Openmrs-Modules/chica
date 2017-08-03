$(function() {
	
	$j("#exitButton").button();
	
	$j("#viewImageDialog").dialog({
	    resizable: true,
	    autoOpen:false,
	    modal: true,
        width: $(window).width() * 0.75,
        height: $(window).height() * 0.75,
	    open: function(event, ui){
	    },
	    buttons: {
		    Cancel: function() {
		   	 $j(this).dialog('close');
		    } //end cancel button
	    }//end buttons
	    
    });
	
	$j("#datepickerStart").datepicker({
    	changeMonth:true,
    	changeYear:true,
    	appendText: "(mm/dd/yyyy)",
    	yearRange: "-3:+0"
    });
	$j("#datepickerStop").datepicker({
    	changeMonth:true,
    	changeYear:true,
    	appendText: "(mm/dd/yyyy)",
    	yearRange: "-3:+0"
    });

});

$(document).ready(function() {
	
	var statusTable = $('#faxStatusTable').dataTable(
			{ 
				
				
			"columns": [ 
	            {"mData": "transmitTimeAsString"},
	            {"mData": "patientMRN"},
	            {"mData": "patientLastName"},
	            {"mData": "patientFirstName"},
	            {"mData": "recipientName"},
	            {"mData": "locationName"},
	            {"mData": "faxNumber"},
	            {"mData": "subject"},
	            {"mData": "statusText"},
	       	    {"mData": "idTag"},
	            {"mData": "image"},
	            {"mData": "uniqueJobID"}
	            ],
			       

			"columnDefs" : [
   				 	{	"targets": 'image',
   				 	   	"render": renderImageLocation,
   				 	},
   				 	{
   		                "targets": [9],
   		                "visible": false,
   		                "searchable": false
   		            },
   			],
   				 		
			"jQueryUI": true, 
			"width" : "80%",
			"pagingType": "full_numbers",
			"pageLength": 50,
			"filter": true,
			"sProcessing" : "Loading...",
			"oLanguage": {
				"sLengthMenu": "Display _MENU_ records per page",
				"sInfo": "Showing _START_ to _END_ of _TOTAL_ records",
				"sInfoEmtpy": "Showing 0 to 0 of 0 records",
				"sEmptyTable": "No data available"
			},
			 "order": [[ 0, "desc" ]]
			}).api();

	
	$('#faxStatusTable tbody tr').on( 'click', 'input',  function () {
		
		$j('#imageDisplay').attr('src', this.id);
    	$j('#viewImageDialog').dialog('open');

    } );
	
	
});

function backToAdminPage()
{
	window.location = '${pageContext.request.contextPath}/admin/index.htm';
};

//Create the image button
function renderImageLocation(data, type, full, meta)
{
	idTag = full.idTag;
	image = full.image;
	
	if (image == ""){
		return '<p> ' + idTag + ' </p>';
	}
	image = ctx + image;
    return '<input id="' +image+ '" type = "button" name="' + image + '" value="' + idTag + '"</input>';

};



    