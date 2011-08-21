ExpertisePortlet = Class.extend({
	init: function()	{
		this.name = "ExpertisePortlet";
	},
	
	onBegin: function()	{
		this.model = {};
		this.registerObserver();
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	},
	
	onReloadPage: function()	{
		this.run();
	},
	
	onExpertiseChanged: function()	{
		this.useCache = false;
		this.run();
		this.useCache = true;
	},
	
	run: function()	{
		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		var obj = this;
		var id = this.getRequest().getParam('id');
		this.onAjax('ajax', 'get-all-expertises', {'id': id}, 'GET', 
			{'onSuccess': function(ret)	{
				var rsContexts = ret;
				for (var i=0;i<rsContexts.length;i++)	{
					var ctx = rsContexts[i];
					ctx.image = root+ctx.image;
				}
	
				obj.model.expertiseTitle = "Kinh nghiệm";
				obj.model.expertiseContexts = rsContexts;
				obj.getPortletPlaceholder().paintCanvas(obj.render());
			}}, this.useCache, 300000);
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface);