  jQuery(function($) {
	$("#message").delay(5000).slideUp(300);
});
var closeButtons = $('.fa-solid');
closeButtons.on('click', function() {
  $(this).parent().hide();
});
