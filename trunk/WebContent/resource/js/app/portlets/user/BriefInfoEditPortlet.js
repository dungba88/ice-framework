BriefInfoEditPortlet = Class.extend({
	init: function()	{
		this.name = "BriefInfoEditPortlet";
	},
	
	run: function()	{
		this.model = {};
		var obj = this;
		var props = SingletonFactory.getInstance(Application).getSystemProperties();
		this.onAjax('user-ajax', 'get-brief-info', {}, 'GET', {
			'onSuccess': function(ret)	{
				var avatar = ret.avatar;
				obj.model.page = obj.getRequest().getParam('page');
				obj.model.id = props.get('user.id');
				obj.model.name = ret.name;
				obj.model.avatar = "avatar/"+avatar;
				obj.getPortletPlaceholder().paintCanvas(obj.render());
			}
		});
	}
}).implement(PortletInterface).implement(AjaxInterface).implement(RenderInterface);