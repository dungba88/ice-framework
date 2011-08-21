/**
 * @author KhanhHH
 */
ListFollowersPortlet = AbstractFollowersPortlet.extend({
	init: function()	{
		this.name = "ListFollowersPortlet";
		this.key = 'id';
		this.controller = 'user-ajax';
		this.action = 'get-followers';
	},
	
	onFollowersChange: function()	{
		this.run();
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface);