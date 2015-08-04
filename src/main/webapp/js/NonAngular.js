$(document).ready(function(){
	
	$(window).on("beforeunload",function(){
		
		$.get("/discoveryStreaming/exit");
	});
});