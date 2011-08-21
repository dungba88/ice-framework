EmailPasswdEditPortlet = Class.extend({
	init: function()	{
		this.name = "EmailPasswdEditPortlet";
	},
	
	onBegin: function()	{
		this.registerObserver();
	},
	
	onWhoIsSelectChanged: function()	{
		var whois = this.requestForEffectiveResource('WhoIs').find('option:selected').val();
		if (whois == 1)	{
			this.requestForEffectiveResource('OptionalArea').show();
			this.requestForEffectiveResource('CompoundStudentID').show();
		} else {
			this.requestForEffectiveResource('OptionalArea').hide();
			this.requestForEffectiveResource('CompoundStudentID').hide();
		}
	},
	
	onUserCPButtonClick: function()	{
		var curPass = this.requestForEffectiveResource('CurrentPasswd').val().trim();
		var pass = this.requestForEffectiveResource('Passwd').val().trim();
		var confirmPass = this.requestForEffectiveResource('ConfirmPasswd').val().trim();
		var email = this.requestForEffectiveResource('Email').val().trim();
		
		var whois = this.requestForEffectiveResource('WhoIs').find('option:selected').val();
		var studentID = this.requestForEffectiveResource('StudentID').val();
		if (studentID.length != 8 && studentID.length != 0)	{
			this.requestForEffectiveResource('StudentIDError').html('Bạn nhập không đúng mã số sinh viên.');
			return;
		}
		
		this.requestForEffectiveResource('Error').html('');
		var obj = this;
		this.onAjax('user-ajax', 'edit-email-password', {'currentPasswd': curPass, 'passwd': pass, 'confirmPassword': confirmPass, 'email':email, 'whois':whois, 'sid':studentID}, 'POST', {
			'onSuccess': function(ret)	{
				var subject = SingletonFactory.getInstance(Subject);
				subject.notifyEvent('NotifyGlobal', 'Bạn đã thay đổi thông tin cá nhân thành công!');
				subject.notifyEvent('RequestRoute', new Request('User'));
			},
			'onFailure': function(message)	{
				var err = {};
				err.errors = message;
				obj.requestForEffectiveResource('Error').html(obj.renderView('ErrorView', err));
			}
		});
	},
	
	run: function()	{
		var obj = this;
		this.getPortletPlaceholder().paintCanvas(this.render());
		this.onAjax('user-ajax', 'get-user-email', {}, 'GET', {
			'onSuccess': function(ret)	{
				obj.requestForEffectiveResource('Email').val(ret);
			}
		});
		this.onAjax('user-ajax', 'get-whois', {}, 'GET', {
			'onSuccess': function(ret)	{
				obj.requestForEffectiveResource('StudentID').val(ret.sid);
				obj.requestForEffectiveResource('WhoIs').val(ret.whois);
				var subject = SingletonFactory.getInstance(Subject);
				subject.notifyEvent('WhoIsSelectChanged');
			}
		});
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface);