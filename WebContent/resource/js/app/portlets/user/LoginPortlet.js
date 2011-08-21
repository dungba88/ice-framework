LoginPortlet = Class.extend({
	
	init: function()	{
		this.name = "LoginPortlet";
		this.model = {};
	},
	
	onBegin: function()	{
		this.registerObserver();
	},
	
	onNeedLogin: function(eventData)	{
		this.currentEventData = eventData;
		//check login?
		var properties = SingletonFactory.getInstance(Application).getSystemProperties();
		var root = properties.get('host.root');
		var loggedIn = properties.get('user.login', undefined);
		var obj = this;
		//no evidence found
		if (loggedIn == undefined)	{
			//query to server
			this.onAjax('ajax', 'check-user-status', {}, 'GET', 
					{'onSuccess': function(ret)	{
						if (ret == 0)	{
							properties.set('user.login', 0, true);
						} else {
							properties.set('user.id', ret.userid, true);
							properties.set('user.name', ret.name, true);
							properties.set('user.login', 1, true);
							properties.set('user.type', ret.type, true);
						}
						obj.recheckLogin(eventData);
					}});
		} else {
			this.recheckLogin(eventData);
		}
	},
	
	recheckLogin: function(eventData)	{
		var subject = SingletonFactory.getInstance(Subject);
		var properties = SingletonFactory.getInstance(Application).getSystemProperties();
		var loggedIn = properties.get('user.login', undefined);
		if (loggedIn == 0)	{
			if (this.currentEventData.disableWarning == true)
				this.model.loginError = '';
			else 
				this.model.loginError = this.getLocalizedText('NeedLogin');
			subject.notifyEvent('ShowPopup', {'id': 'Login',title: 'Đăng nhập', 'content': this.render()});
			$('#LoginPortlet-Username').focus();
		} else {
			subject.notifyEvent('LoginSuccess', eventData);
		}
	},
	
	onPasswordTextBoxKeyUp: function(event)	{
		var keycode = event.keyCode;
		if (keycode == 13)	{
			this.onLoginButtonClick(event);
		}
	},
	
	onLoginButtonClick: function(eventData)	{
		var target = eventData.target;
		var form = $(target).parents('#LoginPortlet-Form:first');
		//ajax request
		var username = $(form).find('#LoginPortlet-Username').val();
		var password = $(form).find('#LoginPortlet-Password').val();
		var remember = $(form).find('#LoginPortlet-Persistent:checked').length;
		var loginError = $(target).parents('#LoginPortlet-Form:first').find('#LoginPortlet-LoginError');
		if (username == '' || password == '')	{
			$(loginError).html(this.getLocalizedText('LoginInsufficientInput'));
		}
		var obj = this;
		this.onAjax('ajax', 'login', {'username':username, 'password':password, 'persistent': remember}, 'POST', 
				{'onSuccess': function(ret)	{
					var properties = SingletonFactory.getInstance(Application).getSystemProperties();
					properties.set('user.id', ret.userid, true);
					properties.set('user.name', ret.name, true);
					properties.set('user.login', 1, true);
					properties.set('user.type', ret.type, true);
					var subject = SingletonFactory.getInstance(Subject);
					subject.notifyEvent('PopupRemove', {id: 'Login'});
					subject.notifyEvent('LoginSuccess', obj.currentEventData);
				},
				'onFailure': function(message)	{
					$(loginError).html(message);
				}});
	},
	
	run: function()	{
		this.model.loginError = '';
		this.getPortletPlaceholder().drawToCanvas(this.render());
	}
}).implement(RenderInterface).implement(PortletInterface).implement(ObserverInterface).implement(AjaxInterface);