StatsPortlet = Class.extend({
	init: function()	{
		this.name = "StatsPortlet";
		this.interval = 300000;
	},
	
	onBegin: function()	{
		this.model = {};
	},
	
	run: function()	{
		this.getPortletPlaceholder().drawToCanvas(this.render());
		this.fetch();
		this.startInterval(this.interval, this.fetch);
	},
	
	fetch: function()	{
		var obj = this;
		this.onAjax('ajax', 'count-stats', {}, 'GET', {
			'onSuccess': function(ret)	{
				obj.requestForEffectiveResource('UserTotal').html(ret.user);
				obj.requestForEffectiveResource('QuestionTotal').html(ret.question);
				obj.requestForEffectiveResource('AnswerTotal').html(ret.answer);
				obj.requestForEffectiveResource('CatchwordTotal').html(ret.catchword);
			}
		});
	},
	
	onEnd: function()	{
		this.stopInterval();
	}
}).implement(PortletInterface).implement(AjaxInterface).implement(RenderInterface).implement(IntervalTimerInterface);