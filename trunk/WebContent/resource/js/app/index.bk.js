var bkbootstrap = new BKBootstrap();
var app = SingletonFactory.getInstance(Application);
app.getSystemProperties().set("host.root", "/bkprofile/index.php/");
app.getSystemProperties().set("page.default", "Home");
app.setBootstrap(bkbootstrap);
Request.setProactive(true);

$(document).ready(function(){
	$.get('static/microtemplating/all.bk.txt', {}, function(ret)	{
		var useragent = navigator.userAgent;
		if (useragent.indexOf('MSIE') != -1)	{
			$('#Application-Main').html(ret);
		} else {
			document.getElementById('Application-Main').innerHTML = ret;
		}
		app.begin();
		
        $(window).bind("hashchange", function(){
            if (Request.getProactive != true) {
                var subject = SingletonFactory.getInstance(Subject);
                subject.notifyEvent('NeedAssembleRequest');
            }
            Request.setProactive(false);
        });
	});
});
