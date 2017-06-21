$(function() {
    $("#exitButton").button();
    $("#printLeftButton").button();
    $("#printRightButton").button();
});

function printSelection(node){

  var frame = document.getElementById(node); 
  frame.contentWindow.focus(); 
  frame.contentWindow.print();

}