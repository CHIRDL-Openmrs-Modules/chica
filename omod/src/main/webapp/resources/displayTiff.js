$(function() {
    $("#exitButton").button();
    $("#printLeftButton").button();
    $("#printRightButton").button();
    
    loadPDFs();
});

function printSelection(node){

  var content=node.innerHTML
  var pwin=window.open('','print_content','width=300,height=300');

  pwin.document.open();
  pwin.document.write('<html><body onload="window.print()">'+content+'</body></html>');
  pwin.document.close();
 
  setTimeout(function(){pwin.close();},1000);

}

function loadPDFs() {
	var leftHtml = $("#leftHtmlLength").val();
	if (leftHtml <= 0) {
		var leftTiff = $("#leftImageLocation").val();
		var action = "action=convertTiffToPDF&tiffFileLocation=" + encodeURIComponent(leftTiff) + "#view=fit&navpanes=0";
		var url = ctx + "/moduleServlet/chica/chica?";
		var obj = $("#left_pdf_display");
		var container = obj.parent();
		var newUrl = url + action;
		var newobj = obj.clone();
		obj.remove();
		newobj.attr("data", newUrl);
		newobj.attr("type", "application/pdf");
//		newobj.on("load", function () {
//			$(".force-print-form-loading").hide();
//			$(".force-print-form-container").show();
//	    });
		
		container.append(newobj);
	}
	
	var rightHtml = $("#rightHtmlLength").val();
	if (rightHtml <= 0) {
		var rightTiff = $("#rightImageLocation").val();
		var action = "action=convertTiffToPDF&tiffFileLocation=" + encodeURIComponent(rightTiff) + "#view=fit&navpanes=0";
		var url = ctx + "/moduleServlet/chica/chica?";
		var obj = $("#right_pdf_display");
		var container = obj.parent();
		var newUrl = url + action;
		var newobj = obj.clone();
		obj.remove();
		newobj.attr("data", newUrl);
		newobj.attr("type", "application/pdf");
//		newobj.on("load", function () {
//			$(".force-print-form-loading").hide();
//			$(".force-print-form-container").show();
//	    });
		
		container.append(newobj);
	}
}

