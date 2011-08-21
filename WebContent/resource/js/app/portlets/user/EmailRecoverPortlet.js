EmailRecoverPortlet = Class.extend({
	init: function()	{
		this.name = "EmailRecoverPortlet";
	},
	
	onBegin: function()	{
		this.registerObserver();
	},
	
	onRecoverButtonClick: function()	{
		var obj = this;
		var captchaResponse = $('input#recaptcha_response_field').val();
		var captchaChallenge = $('input#recaptcha_challenge_field').val();
		var email = this.requestForEffectiveResource('Email').val();
		this.requestForEffectiveResource('Email').attr('disabled', 'true');
		this.requestForEffectiveResource('Error').html('<img src="static/images/ajax-loader-1.gif">');
		
		this.onAjax('user-ajax', 'request-new-password', 
				{'recaptcha_challenge_field': captchaChallenge, 'recaptcha_response_field': captchaResponse,
					'email': email}, 'POST', {
						'onSuccess': function()	{
							var subject = SingletonFactory.getInstance(Subject);
							subject.notifyEvent('NotifyGlobal', 'Chúng tôi đã gửi hướng dẫn phục hồi mật khẩu đến email của bạn! Hướng dẫn có giá trị trong vòng 24h');
							subject.notifyEvent('RequestRoute', new Request('Home'));
						},
						
						'onFailure': function(message)	{
							obj.requestForEffectiveResource('Email').removeAttr('disabled');
							obj.requestForEffectiveResource('Error').html(message);
						}
					});
	},
		
	run: function()	{
		this.getPortletPlaceholder().paintCanvas(this.render());
		Recaptcha.create("6LeF-MISAAAAAKXfmjbbhb-EO5wSfJK3qkRBfGD_",
			    this.name+"-Recaptcha",
			    {
			      theme: "red",
			      callback: Recaptcha.focus_response_field
			    }
			  );
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface);