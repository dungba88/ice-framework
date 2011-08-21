InterestedAreaPortlet = Class.extend({
	init: function()	{
		this.name = "InterestedAreaPortlet";
		this.useCache = true;
	},
	
	onBegin: function()	{
		this.model = {};
		this.registerObserver();
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	},
	
	onInterestedAreaChanged: function()	{
		this.useCache = false;
		this.run();
		this.useCache = true;
	},
	
	run: function()	{
		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		var obj = this;
		var id = this.getRequest().getParam('id');
		this.onAjax('ajax', 'get-all-interests', {'id': id}, 'GET', 
				{'onSuccess': function(ret)	{
					var rsContexts = ret;
					for (var i=0;i<rsContexts.length;i++)	{
						var ctx = rsContexts[i];
						ctx.image = root+ctx.image;
					}
				
					obj.model.interestTitle = "Lĩnh vực quan tâm";
					obj.model.interestContexts = rsContexts;
					obj.getPortletPlaceholder().paintCanvas(obj.render());
				}}, this.useCache, 300000);
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface);