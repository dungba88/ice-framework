FeedbackPortlet = Class.extend({
	init: function()	{
		this.name = "FeedbackPortlet";
	},
	
	onBegin: function()	{
		this.model = {};
		this.registerObserver();
	},
	
	run: function()	{
		this.getPortletPlaceholder().drawToCanvas(this.render());
	},
	
	onFeedbackSubmit: function()	{
		var feedback = this.requestForEffectiveResource('TextInput').val().trim();
		var location = window.location.href;
		if (feedback == "")	{
			var obj = this.requestForEffectiveResource('Error');
			$(obj).val(this.getLocalizedText('FeedbackError'));
			return;
		}
		var email = this.requestForEffectiveResource('Email').val().trim();
		var obj = this;
		this.onAjax('ajax', 'simple-feedback', {'content': feedback, 'location': location, 'email': email}, 'POST', {
			'onSuccess': function(ret)	{
				var subject = SingletonFactory.getInstance(Subject);
				subject.notifyEvent('NotifyMessage', 'Cảm ơn bạn đã đóng góp cho hệ thống! Chúng tôi sẽ nghiên cứu phản hồi của bạn và cải thiện hệ thống cho phù hợp.');
				var request = new Request('Home');
				subject.notifyEvent('RequestRoute', request);
			},
			'onFailure': function(message)	{
				var error = obj.requestForEffectiveResource('Error');
				$(error).html(obj.getLocalizedText('FeedbackSubmitError'));
			}
		});
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	}
}).implement(PortletInterface).implement(RenderInterface).implement(ObserverInterface).implement(AjaxInterface);