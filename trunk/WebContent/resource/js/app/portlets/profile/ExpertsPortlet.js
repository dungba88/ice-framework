ExpertsPortlet = Class.extend({
	init: function()	{
		this.name = "ExpertsPortlet";
	},
	
	onBegin: function()	{
		this.model = {};
	},
	
	run: function()	{
		var obj = this;
		this.onAjax('ajax', 'get-experts', {}, 'GET', {
			'onSuccess': function(ret)	{
				obj.model = Array();
				obj.model.users = ret;
				obj.getPortletPlaceholder().paintCanvas(obj.render());
			}
		});
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface);