SearcherPortlet = Class.extend({
	init:function() {
		this.name = "SearcherPortlet";
	},
	
	onBegin: function()	{
		this.registerObserver();
	},
	
	onSearchInputKeyup: function(event)	{
		var keyup = event.keyCode;
		if (keyup == 13)	{
			var subject = SingletonFactory.getInstance(Subject);
			subject.notifyEvent('SearchButtonClick');
		}
	},
	
	onSearchButtonClick: function(eventData)	{
		var input = this.requestForEffectiveResource('Input');
		if (input.val().trim() == '')	{
			return;
		}
		var txt = input.val().trim();
		
		//knowledge or profile?
		var selected = this.requestForEffectiveResource("Select").find('option:selected');
		var id = selected.attr('value');
		var request = undefined;
		if (id == 1)	{
			request = new Request('Search', undefined, {type: 'person-search', query: txt});
		} else {
			request = new Request('Search', undefined, {type: 'question-search', query: txt});
		}
		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent('RequestRoute', request);
	},
	
	onSearcherTypeChange: function(eventData)	{
		var selected = this.requestForEffectiveResource("Select").find('option:selected');
		var id = selected.attr('value');
		var subject = SingletonFactory.getInstance(Subject);
		$('.customStyleSelectBoxInner').html(selected.html());
		
		if (id == 1)	{
			subject.notifyEvent('ChangeAutocompleteType', {type: 'person'});
		} else {
			subject.notifyEvent('ChangeAutocompleteType', {type: 'question'});
		}
	},
	
	onDisplayHeaderHelp:function(eventData){
		jQuery("#headerhelp").slideToggle();
        var closetimer;
        $("#headerhelp").mouseleave(function() {
            closetimer = setTimeout("jQuery(\"#homehelp\").fadeOut(); jQuery(\"#maphelp\").fadeOut();jQuery(\"#nearplace-help\").fadeOut();",8000);
        });
        $("#headerhelp").mouseover(function() {
            window.clearTimeout(closetimer);
            closetimer = null;
        });
	},
	
	run: function() {
		this.getPortletPlaceholder().drawToCanvas(this.render());
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	}
}).implement(RenderInterface).implement(PortletInterface).implement(ObserverInterface);