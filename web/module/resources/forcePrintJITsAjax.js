$(function() {
    var options = []; 
    for (i = 0; i < json.powerDropDownItems.length; i++) {
        options.push("<option value='" + json.powerDropDownItems[i] + "'>" + json.powerDropDownItems[i] + "</option>");
    }
    //append after populating all options
    $('#powerSelect')
        .append(options.join(""))
        .selectmenu();
});