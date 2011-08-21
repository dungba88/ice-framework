AutocompleteSearcherPlugin = Class.extend({
	init: function()	{
		this.name = "AutocompleteSearcherPlugin";
	},
	
	onChangeAutocompleteType: function(eventData)	{
		var type = eventData.type;
		var subject = SingletonFactory.getInstance(Subject);
		
		if (type == 'person')
			subject.notifyEvent('AttachFocusDetection', {'target': $('#SearcherPortlet-Input'), 'defaultValue': 'Nhập vào từ khóa cho người bạn muốn tìm'});
		else
			subject.notifyEvent('AttachFocusDetection', {'target': $('#SearcherPortlet-Input'), 'defaultValue': 'Nhập vào từ khóa cho câu hỏi bạn muốn tìm'});
		this.autocompleteEngine = SingletonFactory.getInstance(AutocompleteEngineFactory).build(type);
		$("#SearcherPortlet-Input").autocomplete(this.autocompleteEngine);
	},
	
	onHtmlRendered: function()	{
		var selected = $("#SearcherPortlet-Select option:selected");
		var id = selected.attr('value');
		
		var subject = SingletonFactory.getInstance(Subject);
		if (id == 1)	{
			subject.notifyEvent('ChangeAutocompleteType', {type: 'person'});
		} else {
			subject.notifyEvent('ChangeAutocompleteType', {type: 'question'});
		}
		
		//add click handler to friends div
		$("#friends").click(function(){
			//focus 'to' field
			$("#SearcherPortlet-Input").focus();
		});
		
		//add live handler for clicks on remove links
		$(".remove", document.getElementById("friends")).live("click", function(){
			//remove current friend
			$(this).parent().remove();
			
			//correct 'to' field position
			if($("#friends span").length === 0) {
				$("#SearcherPortlet-Input").css("top", 0);
			}
		});
	}
}).implement(PluginInterface);

AutocompleteEngineFactory = Class.extend({
	build: function(type)	{
		if (type == 'person')	{
			return new PersonAutocomplete();
		} else {
			return new QuestionAutocomplete();
		}
	}
});

PersonAutocomplete = Class.extend({
	source: function(req, add){
		var reservedTerm = req.term;
		var str = "";
		$("#friendsPerson span").each(function(){
			str += "," + $(this).contents().filter(function() { return this.nodeType == 3;}).text().trim();
		});
		req.filter = str.substring(1);
		req.term = req.term;
	
		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		//pass request to server
		$.getJSON(root+"ajax/autocomplete-person?", req, function(data) {
			//create array for response objects
			var suggestions = [];
			//process response
			$.each(data['result'], function(i, val){
				suggestions.push({label: val.person, value: val.userID, type: 'person', catchwords: val.expertises, avatar: val.avatar, ans: val.totalAns});
			});
			
			//pass array to callback
			add(suggestions);
		});
	},
	
	//define select handler
	select: function(e, ui) {
		onClickPerson(ui.item.id);
	},
	
	//define select handler
	change: function() {
		//prevent 'to' field being updated and correct position
		$("#toPerson").css("top", 2);
	}
});

QuestionAutocomplete = Class.extend({
	//define callback to format results
	source: function(req, add){
		var reservedTerm = req.term;
		var str = "";
		$("#friends span").each(function(){
			str += "," + $(this).contents().filter(function() { return this.nodeType == 3;}).text().trim();
		});
		req.filter = str.substring(1);
		req.term = req.term;

		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		//pass request to server
		$.getJSON(root+"ajax/autocomplete-question?", req, function(data) {
			//create array for response objects
			var suggestions = [];
			//process response
			$.each(data['result'], function(i, val){
				if (val.expertise != undefined)	{
					suggestions.push({label: val.expertise, value: val.id, type: 'expertise', follow: val.catch_follow});	
				}
				else if (val.question != undefined){
					suggestions.push({label: val.question, value: val.question_id, type: 'question', catchwords: val.catchwords});
				}
			});
			
			var askMsg = reservedTerm;
			suggestions.push({label: askMsg, value: askMsg, type: 'ask'});
			
			//pass array to callback
			add(suggestions);
		});
	},
	
	//define select handler
	select: function(e, ui) {
		//create formatted friend
		var friend = ui.item.value;
		if (ui.item.type === "expertise"){
			moveToTopic(ui.item.id);
		} else if (ui.item.type == 'ask') {
			makeQuestion(friend);
		} else {
			try {
				tmp = $("a[question='"+escape(friend)+"']");
				moveToAQuestion(tmp.attr("question_id"));
			} catch (e) {
			}
		}
	},
	
	//define select handler
	change: function() {
		//prevent 'to' field being updated and correct position
		$("#SearcherPortlet-Input").css("top", 2);
	}
});

function onClickPerson(id){
	var str = "";
	var subject = SingletonFactory.getInstance(Subject);
    var request = new Request("Profile", undefined,{id: id});
    subject.notifyEvent('RequestRoute', request);
}

function moveToAQuestion(question_id){
	var qid = question_id;
	var subject = SingletonFactory.getInstance(Subject);
    var request = new Request("Question","donganh",{"qid":qid});
    subject.notifyEvent('RequestRoute', request);
}

function moveToTopic(id){
	var subject = SingletonFactory.getInstance(Subject);
    var request = new Request("Topic", undefined, {'id':id});
    subject.notifyEvent('RequestRoute', request);
}

function makeQuestion(ques){
	var app = SingletonFactory.getInstance(Application);
	var subject = SingletonFactory.getInstance(Subject);
	var logined = app.getSystemProperties().get("user.login");
	
	var container = SingletonFactory.getInstance(PortletContainer);
	var portletMeta = container.getPortletMetaByName("AskNonTargetQuestionPortlet");
	portletMeta.active = true;
	var eventData = {};
	eventData.question = ques;
	portletMeta.portlet.showPortlet(eventData);
}