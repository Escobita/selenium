$(document).ready(function() {
		$(".toggled > div").each(function(i){
			$(this).hide().before("<input type='button' class='toggler' value='"+$(this).attr("class").substring(10)+"'/>");
			});
		;
		$(".toggler").bind("click", function(){
			  $(this).next().slideToggle();
			  });
		});

