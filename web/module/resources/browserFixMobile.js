// Fixes issues introduced in Chrome 43.  We had issues with scrolling, font size, and jumping pages
// This can be removed from JSP pages once we upgrade to jquery mobile 1.4.5 or higher.
$(document).on( "mobileinit", function() {
	$.fn.animationComplete = function(callback) {
       if ($.support.cssTransitions) {
         var superfy= "WebKitTransitionEvent" in window ? "webkitAnimationEnd" : "animationend";
         return $(this).one(superfy, callback);
       } else {
         setTimeout(callback, 0);
         return $(this);
       }
     };
});