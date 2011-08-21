NotificationListPortlet = Class.extend({
	init: function()	{
		this.name = "NotificationListPortlet";
	},
	
	onBegin: function()	{
		this.registerObserver();
	},
	
	onSystemPropertyChanged: function(eventData)	{
		if (eventData == 'user.login')	{
			var loggedin = SingletonFactory.getInstance(Application).getSystemProperties().get('user.login');
			if (loggedin == 0)	{
				this.requestForEffectiveResource('Main').hide();
			} else {
				this.run();
				this.requestForEffectiveResource('Main').show();
			}
		}
	},
	
	onFetchAllNotification: function()	{
		var container = this.requestForEffectiveResource('Content');
		var obj = this;
		if (container.is(':visible'))	{
			container.slideToggle();
		} else {
			container.slideToggle();
			this.onAjax('user-ajax', 'get-full-notification', {}, 'GET', {
				'onSuccess': function(ret)	{
					if (ret.length == 0)	{
						container.html('Hiện tại bạn không có thông báo nào');
					} else {
						var notifs = Array();
						for(var i=0;i<ret.length;i++)	{
							var link = eval("("+ret[i].link+")");
							var href=Array();
							for(var j in link)	{
								var param = j;
								if (param == 'type')
									param = 'page';
								href.push(param+"/"+link[j]);
							}
							ret[i].link = "#"+href.join("/");
							if (ret[i].avatar == undefined)	{
								if (ret[i].type == 'QuestionAdded')	{
									ret[i].avatar = 'static/css/beta/images/icon/24/question.png';
								}
							} else {
								ret[i].avatar = 'avatar/'+ret[i].avatar;
							}
							notifs.push(ret[i]);
						}
						container.html(obj.renderView('List', {notifs: notifs}));
					}
				}
			}, true, 300000);
		}
	},
	
	run: function()	{
		var loggedin = SingletonFactory.getInstance(Application).getSystemProperties().get('user.login');
		if (loggedin == 1)	{
			this.getPortletPlaceholder().paintCanvas(this.render());
			var subject = SingletonFactory.getInstance(Subject);
			subject.notifyEvent('FetchLiveNotification');
		}
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	}
}).implement(PortletInterface).implement(RenderInterface).implement(ObserverInterface).implement(AjaxInterface);