GetAnnouncementPlugin = Class.extend({
	init: function()	{
		this.name = "GetAnnouncementPlugin";
	},
	
	onBegin: function()	{
		this.onGetAnnouncement();
		this.startInterval(300*1000, this.onGetAnnouncement);
	},

	onGetAnnouncement: function()	{
		var obj = this;
		this.onAjax('ajax', 'get-announcement', {}, 'GET', {
			'onSuccess': function(ret)	{
				if (ret == undefined || ret == "")
					return;
				//find if plugin already loaded
				var containerHTML = tmpl('GetAnnouncementPluginContainer', {});
				if ($('div#GetAnnouncementPlugin').length <= 0)	{
					$('div.extension-point[extensionName="PostTop"]:first').prepend(containerHTML);
				}
				$('div#GetAnnouncementPlugin').html(ret);
				$('div#GetAnnouncementPlugin').fadeOut(60000);
			}
		});
	},
	
	onEnd: function()	{
		this.stopInterval();
	}
}).implement(PluginInterface).implement(IntervalTimerInterface).implement(AjaxInterface);

GlobalNotificationPlugin = Class.extend({
	init: function()	{
		this.name = "GlobalNotificationPlugin";
	},
	
	onCloseGlobalNotify: function()	{
		$('div#NotifyGlobalPlugin').stop().animate({opacity: '100'}).hide();
	},
	
	onNotifyGlobal: function(eventData)	{
		var containerHTML = tmpl('NotifyGlobalPluginContainer', {});
		if ($('div#NotifyGlobalPlugin').length <= 0)	{
			$('div.extension-point[extensionName="PostTop"]:first').prepend(containerHTML);
		}
		$('div#NotifyGlobalPlugin').find('span.notify-content').html(eventData);
		$('div#NotifyGlobalPlugin').stop().animate({opacity: '100'});
		$('div#NotifyGlobalPlugin').show().fadeOut(30000);
	}
}).implement(PluginInterface).implement(IntervalTimerInterface).implement(AjaxInterface);

PopupPlugin = Class.extend({
	init: function()	{
		this.name = "PopupPlugin";
		this.popup = new Popup();
	},
	
	onPopupRemove: function(eventData)	{
		var id = eventData.id;
		this.popup.remove(id);
	},
	
	onShowPopup: function(eventData)	{
		this.popup.add(eventData.id, eventData.title, eventData.content);
	},
	
	onNotifyMessage: function(eventData)	{
		this.popup.msg(eventData);
	},
	
	onNotifyError: function(eventData)	{
		this.popup.error(eventData);
	}
}).implement(PluginInterface);

KeepAlivePlugin = Class.extend({
	init: function()	{
		this.name = "KeepAlivePlugin";
	},
	
	onBegin: function()	{
		this.onKeepAlive();
		this.startInterval(15*60*1000, this.onKeepAlive);
	},

	onKeepAlive: function()	{
		this.onAjax('ajax', 'index', {}, 'GET', {
			'onSuccess': function(ret)	{
				
			}
		});
	},
	
	onEnd: function()	{
		this.stopInterval();
	}
}).implement(PluginInterface).implement(IntervalTimerInterface).implement(AjaxInterface);