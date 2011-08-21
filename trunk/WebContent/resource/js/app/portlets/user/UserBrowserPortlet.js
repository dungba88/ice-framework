UserBrowserPortlet = Class.extend({
	init: function()	{
		this.name = "UserBrowserPortlet";
	},
	
	onBegin: function()	{
		this.registerObserver();
	},
	
	onTopMenuActive: function(name)	{
		if (name != this.name)	{
			$('#UserBrowserPortlet-ContentPlaceholder').html('');
			return;
		}
		var obj = this;
		obj.getPortletPlaceholder().paintCanvas(obj.render());
		this.onAjax('ajax', 'get-by-max-score', {}, 'GET', {
			'onSuccess': function(ret)	{
				obj.model = {};
				obj.model.users = Array();
				for (var i in ret)	{
					obj.model.users.push(ret[i]);
				}

				obj.requestForEffectiveResource('ContentPlaceholder-MaxScore').html(obj.renderView('MaxScoreList', obj.model));
			}
		}, true, 300000);
		
		this.onAjax('ajax', 'get-by-top-rank', {}, 'GET', {
			'onSuccess': function(ret)	{
				obj.model = {};
				obj.model.users = Array();
				var map = Array();
				for (var i in ret)	{
					var id = ret[i].userID;
					if (map[id] == undefined)	{
						map[id] = i;
						ret[i].ranks = ret[i].catchWord;
					} else {
						var index = map[id];
						ret[index].ranks += ','+ret[i].catchWord;
						ret[i] = undefined;
					}
				}
				
				for(var i in ret)	{
					if (ret[i] != undefined)
						obj.model.users.push(ret[i]);
				}

				obj.requestForEffectiveResource('ContentPlaceholder-TopRank').html(obj.renderView('TopRankList', obj.model));
			}
		}, true, 300000);
	},
	
	onReloadPage: function()	{
		this.checkActive();
	},
	
	checkActive: function()	{
	},
	
	run: function()	{
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface);