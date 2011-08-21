(function($) {
    
    $.timeFormat = function( timestamp ) {
        var time = handleDate( timestamp, extractDateHour );
        return time;
    };
    
    $.timeFormatSolr = function ( timestr )	{
    	timestr = timestr.replace(/[TZ]/g, ' ').trim();
    	var dt = timestr.split(' ');
    	var d = dt[0].split('-');
    	var t = dt[1].split(':');
    	var date = new Date(d[0], d[1]-1, d[2], t[0], t[1], t[2]);
    	
    	var timestamp = $.format.date(date, "HH:mm dd/MM/yyyy");
    	var time = handleDate( timestamp, extractDateHourSolr );
    	return time;
    };
    
    function extractDateHourSolr(timestamp)	{
    	var arr = timestamp.split(' ');
    	var d = arr[1].split('/');
    	var h = arr[0].split(':');
    	var timestamp = Date.UTC(d[2],d[1]-1,d[0],h[0],h[1],0,0)/1000;
    	return timestamp;
    }
    
    function extractDateHour(timestamp)	{
    	var arr = timestamp.split(' ');
    	var d = arr[0].split('-');
    	var h = arr[1].split(':');
    	var timestamp = Date.UTC(d[0],d[1]-1,d[2],h[0],h[1],h[2],0)/1000;
    	return timestamp;
    }
    
    function handleDate( timestamp, func ) {
        var n=new Date(), t, ago = " ";
        if( timestamp ) {
        	var timestamp = func(timestamp);
        	//420 min = UTC+7
        	t =   Math.round( (n.getTime()/1000 - timestamp)/60 + 420 );
        	ago += handleSinceDateEndings( t, timestamp );
        } else {
            ago += "";
        }
        return ago;
    }
    
    function handleSinceDateEndings( t, original_timestamp ) {
        var ago = " ", date;
        date = new Date( parseInt( original_timestamp )*1000 );
        if( t <= 1 ) {
            ago += "Vài giây trước";
        } else if( t<60) {
            ago += t + " phút trước";
        } else if( t>= 60 && t<= 120) {
            ago += Math.floor( t / 60 ) + " giờ trước";
        } else if( t<1440 ) {
            ago += Math.floor( t / 60 )  + " giờ trước";
        } else if( t< 2880) {
            ago +=  "1 ngày trước lúc "+ date.getUTCHours()+'h:'+date.getUTCMinutes();
        } else if( t > 2880  && t < 4320 ) {
            ago +=  "2 ngày trước lúc "+ date.getUTCHours()+'h:'+date.getUTCMinutes();
        } else {
            ago += "ngày " + date.getDate() + "-" + months[ date.getMonth() ] + "-"+ (1900+date.getYear()) +" lúc "+ date.getUTCHours()+'h:'+date.getUTCMinutes();
        }
        return ago;
    }
    
    var months = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"];
})(jQuery);
