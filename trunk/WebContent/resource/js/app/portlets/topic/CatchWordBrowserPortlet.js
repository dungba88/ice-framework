CatchWordBrowserPortlet = Class.extend({
	init: function()	{
		this.name = "CatchWordBrowserPortlet";
	},
	
	onBegin: function()	{
		this.registerObserver();
	},
	
	onReloadPage: function()	{
		this.checkActive();
	},
	
	checkActive: function()	{
		var request = this.getRequest();
		var page = request.getParam('page');
		var curId = request.getParam('id');
		this.requestForEffectiveResource('ContentPlaceholder').find('[tid]').each(function(){
			var tid = $(this).attr('tid');
			if (tid == curId)	{
				$(this).addClass('active');
			} else {
				$(this).removeClass('active');
			}
		});
	},
	
	onTopMenuActive: function(name)	{
		if (name != this.name)	{
			$('#CatchWordBrowserPortlet-ContentPlaceholder').html('');
			return;
		}
		var obj = this;
		this.onAjax('catchword', 'browse-catch-word', {}, 'GET', {
			'onSuccess': function(ret)	{
				obj.model = {};
				obj.model.catchwords = Array();
				for (var i in ret)	{
					obj.model.catchwords.push(ret[i]);
				}

				obj.getPortletPlaceholder().paintCanvas(obj.render());
				obj.requestForEffectiveResource('ContentPlaceholder').find('ul:first').addClass('first');
				obj.requestForEffectiveResource('ContentPlaceholder').find('ul:last').addClass('last');
				obj.requestForEffectiveResource('ContentPlaceholder').find('ul:last').append('<li class="more"><a style="font-weight: normal; color: #069" href="#!page/AllTopics">Xem tất cả</a></li>');
				obj.checkActive();
			}
		}, true, 300000);
	},
	
	run: function()	{
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface);