$(function() {

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

});
    