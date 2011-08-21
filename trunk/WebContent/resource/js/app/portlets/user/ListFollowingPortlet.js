ListFollowingPortlet = ListFollowersPortlet.extend({
	init: function()	{
		this._super();
		this.name = "ListFollowingPortlet";
		this.action = "get-following";
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface);