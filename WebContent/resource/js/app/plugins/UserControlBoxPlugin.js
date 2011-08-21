UserControlBoxPlugin = Class.extend({
	init: function()	{
		this.name = "UserControlBoxPlugin";
	},
	
	onSystemPropertyChanged: function(eventData)	{
		if (eventData == 'user.login')	{
			this.setupUserBox();
		}
	},
	
	onUpdateUserStatus: function(eventData)	{
		this.onBegin();
	},
	
	onPageBegan: function()	{
		this.checkActiveMenu();
	},
	
	checkActiveMenu: function()	{
		var request = SingletonFactory.getInstance(Page).getRequest();
		var page = request.getName();
		$('#UserControlBoxPlugin a').each(function()	{
			var p = $(this).attr('page');
			if (p == page)	{
				$(this).addClass('active');
			} else {
				$(this).removeClass('active');
			}
		});
	},
	
	onBegin: function(eventData)	{
		var properties = SingletonFactory.getInstance(Application).getSystemProperties();
		var loggedIn = properties.get('user.login');
		var obj = this;
		this.onAjax('ajax', 'check-user-status', {}, 'GET', 
			{'onSuccess': function(ret)	{
				if (ret == 0)	{
					properties.set('user.login', 0, true);
				} else {
					properties.set('user.login', 1, true);
					properties.set('user.id', ret.userid, true);
					properties.set('user.name', ret.name, true);
					properties.set('user.type', ret.type, true);
				}
				obj.setupUserBox();
			}});
	},

	setupUserBox: function()	{
		var systemProperties = SingletonFactory.getInstance(Application).getSystemProperties();
		var loggedIn = systemProperties.get('user.login');
		var obj = {};
		if (loggedIn == true)	{
			obj.login = true;
			obj.name = systemProperties.get('user.name');
		} else {
			obj.login = false;
		}
		if ($('#UserControlBoxPlugin').length <= 0)	{
			$('div.extension-point[extensionName="TopRight"]').append(tmpl('UserControlBoxPluginContainer', {}));
		}
		var type = systemProperties.get('user.type');
		if (type == 'person'){
			obj.userPage = 'User/id/'+systemProperties.get('user.id');;
		} else if (type == 'partner'){
			obj.userPage = 'PartnerProfile/id/'+systemProperties.get('user.id');
		}
		 
		$('#UserControlBoxPlugin').html(tmpl('UserControlBoxPluginTmpl', obj));
//		$("li.cutdi").blur(function () {
//	         $('#navAccount').addClass('_hidden');
//	    });
		this.checkActiveMenu();
	},
	
	onToggleAccountNav: function()	{
		if($('#navAccount').is("._hidden")) $('#navAccount').removeClass("_hidden");
		else $('#navAccount').addClass("_hidden");
	},
	
	onEnd: function()	{
		if ($('#UserControlBoxPlugin').length > 0)
			$('#UserControlBoxPlugin').remove();
	}
}).implement(PluginInterface).implement(AjaxInterface);

LiveNotificationPlugin = Class.extend({
	init: function()	{
		this.name = "LiveNotificationPlugin";
	},
	
	onBegin: function()	{
		this.startInterval(300000, this.fetch);
	},
	
	onFetchLiveNotification: function()	{
		this.fetch();
	},
	
	fetch: function()	{
		var loggedin = SingletonFactory.getInstance(Application).getSystemProperties().get('user.login');
		if (loggedin == 1)	{
			this.onAjax('user-ajax', 'get-live-notification', {}, 'GET', {
				'onSuccess': function(ret)	{
					$('#NotificationListPortlet-Number').html(ret.num);
				}
			});
		}
	}
}).implement(PluginInterface).implement(AjaxInterface).implement(IntervalTimerInterface);

UserLogoutPlugin = Class.extend({
	init: function()	{
		this.name = "UserLogoutPlugin";
	},
	
	onPageBegan: function()	{
		this.onAjax('ajax', 'logout', {}, 'POST', {
			'onSuccess': function(ret)	{
				var systemProperties = SingletonFactory.getInstance(Application).getSystemProperties();
				systemProperties.set('user.login', 0, true);
				systemProperties.set('user.id', undefined, true);
				var request = new Request("Home");
				var subject = SingletonFactory.getInstance(Subject);
				subject.notifyEvent("RequestRoute", request);
			}
		});
	}
}).implement(PluginInterface).implement(AjaxInterface);