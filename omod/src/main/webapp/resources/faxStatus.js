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


    