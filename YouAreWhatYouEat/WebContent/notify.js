/*
 * separates messaging functionality from front end
 */

$('head').append('<script type="text/javascript" src="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>');
$('head').append( '<meta name="viewport" content="width=device-width">' );
$('head').append( '<meta charset="utf-8">' );



$('head').append('<link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css" />');
$('head').append('<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-notify/0.2.0/css/bootstrap-notify.min.css" />');
$('head').append('<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-notify/0.2.0/css/styles/alert-bangtidy.min.css" />');
$('head').append('<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-notify/0.2.0/css/styles/alert-blackgloss.min.css" />');

$('head').append('<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-notify/0.2.0/js/bootstrap-notify.min.js"></script>');

document.body.innerHTML += '<div class="notifications bottom-right"></div>';

$(window).load(function(){
	$(document).ready(function () {
	    $('.bottom-right').notify({
        message: {
            text: 'Aw yeah, It works!'
        }
    }).show();
});
});
