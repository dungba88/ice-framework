AutocompletePlugin = Class.extend({
	init: function()	{
		this.name = "AutocompletePlugin";
	},
	
	onNeedAutocomplete: function(eventData)	{
		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		var subject = SingletonFactory.getInstance(Subject);
		var url = root+eventData.autocompleteSource;
		var suggestions = Array();
		var obj = eventData.autocompleteObject;
		var type = eventData.type;
		if (type == undefined)	{
			type = 'Question';
		}
		
		if (eventData.fetchSourceCallback != undefined)
			subject.notifyEvent(eventData.fetchSourceCallback);
		$(obj).autocomplete({
			source: function(req, add){
				var term = $(obj).val();
				req.term = term;
				$.getJSON(url, req, function(data) {
					var suggestions = [];
					$.each(data['result'], function(i, val){
						if (type == 'Question')
							suggestions.push({label:val.expertise, value:val.id, type: 'expertise', follow: val.catch_follow});
						else if (type == "RecommendPerson"){
							var askMsg = 'Không tìm thấy? Tiến cử qua email';
							suggestions.push({label: askMsg, value: askMsg, type: 'email'});
							suggestions.push({label: val.person, value: val.userID, type: 'person', catchwords: val.expertises, avatar: val.avatar, ans: val.totalAns});
						}
						else
							suggestions.push({label: val.person, value: val.userID, type: 'person', catchwords: val.expertises, avatar: val.avatar, ans: val.totalAns});
					});
					add(suggestions);
				});
			},
			focus:  function(e,ui){
				if (eventData.focusCallback != undefined)
					subject.notifyEvent(eventData.focusCallback, {'ui':ui});
			},
			select: function(e,ui){
				if (eventData.selectCallback != undefined)
					subject.notifyEvent(eventData.selectCallback, {'ui':ui});
			},
			result: function(e, ui)	{
				if (eventData.resultCallback != undefined)
					subject.notifyEvent(eventData.resultCallback, {'ui':ui});
			}
		});
	}
}).implement(PluginInterface);