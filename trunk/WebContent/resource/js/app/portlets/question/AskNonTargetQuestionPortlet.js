AskQuestionPortlet = Class.extend({
	init: function()	{
		this.name = "AskQuestionPortlet";
		this.selectedTopics = new Array();
		this.usingFacebox = false;
	},
	
	requestForEffectiveResource: function(targetName){
		if(this.usingFacebox){
			return $("#"+this.name+"-"+targetName);
		}else{
			return this._super.requestForEffectiveResource(targetName);
		}
	},
	
	onMakeNewQuestion: function(eventData)	{
		this.showPortlet(eventData);
	},

	showPortlet: function(eventData){
		var loggedIn = SingletonFactory.getInstance(Application).getSystemProperties().get('user.login');
		if (loggedIn != 1)	{
			var subject = SingletonFactory.getInstance(Subject);
			subject.notifyEvent("NeedLogin", {type: 'AskNonTarget', event: eventData});
		} else {
			this.doShowPortlet(eventData);
		}
	},
	
	onLoginSuccess: function(eventData)	{
		if (eventData.type != 'AskNonTarget')
			return;
		this.doShowPortlet(eventData.event);
	},
	
	// Mar21
	doShowPortlet: function(eventData)	{
		var app = SingletonFactory.getInstance(Application);
		var subject = SingletonFactory.getInstance(Subject);
		this.usingFacebox = true;
		this.selectedPeople = Array();
		this.selectedTopics = Array();
		
		this.model.optionalMsg = $("#"+this.name+"-TextOptionalMessage").html();
		this.model.optionalDetailMsg = $("#"+this.name+"-TextOptionalDetailMessage").html();
		this.model.askMsg = $("#"+this.name+"-TextAskMessage").html();
		this.model.question = eventData.question;
		subject.notifyEvent('ShowPopup', {id: 'AskNonTargetQuestion', title: 'Đặt câu hỏi', content: this.render()});
		$('#AskNonTargetQuestionPortlet-TextBox').focus();
		var target = this.requestForEffectiveResource('TextAreaExtended');
		subject.notifyEvent('ExpandTextArea', {target: target, min: 30, max: 200});
		
		var profile = eventData.profile;
		if (profile != undefined)	{
			for(var i=0;i<profile.length && i<2;i++)	{
				this.appendPeople({value: profile[i].id, txt: profile[i].name});
			}
		}
		
		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent('AskNonTargetQuestionPortletFetchAutocomplete');
	},
	
	onAskNonTargetQuestionPortletFetchAutocomplete: function()	{
		//alert("onAskNonTargetQuestionPortletFetchAutocomplete");
		var addTopicInputBox = this.requestForEffectiveResource("AddTopicInputBox"); 
		//FIXME: data should be loaded only once !addTopicInputBox
		var eventData = {};
		eventData.selectCallback = "AskNonTargetQuestionTopicSelect";
		eventData.autocompleteObject = addTopicInputBox;
		eventData.autocompleteSource = "ajax/autocomplete-catchwords";
		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent('NeedAutocomplete', eventData);
		
		var addPeopleInputBox = this.requestForEffectiveResource("AddPeopleInputBox");
		var eventData = {};
		eventData.selectCallback = "AskNonTargetQuestionPeopleSelect";
		eventData.autocompleteObject = addPeopleInputBox;
		eventData.autocompleteSource = "ajax/autocomplete-person";
		eventData.type = "person";
		subject.notifyEvent('NeedAutocomplete', eventData);
	},
	
	appendPeople: function(obj)	{
		var txt = obj.txt;
		var value = obj.value;
		
		var topicContainer = this.requestForEffectiveResource("AddPeopleContainer");
		if (this.selectedPeople == undefined)
			this.selectedPeople = Array();
		if (this.selectedPeople.length >= 2)
			return;
		for( i=0; i<this.selectedPeople.length;i++ ){
			if(this.selectedPeople[i] === value){
				return;
			}
		}
		var textToken = $("<label class='tokenContent'>"+txt+"</label>");
		this.selectedPeople.push(value);
		
		// UI ONLY
		var selectedPeople = this.selectedPeople;
		var span = $("<span>").addClass("uiToken").append(textToken);
		var a = $("<span>").addClass("btnStl").addClass("delToken").bind("click",function(event){
			for( start=0; start<selectedPeople.length; start++ ){
				if(selectedPeople[start] === value){
					break;
				}
			}
			selectedPeople.splice(start, 1);
			$(event.target).parent().remove();
		}).html("X").css("text-align","center").appendTo(span);
		topicContainer.append(span);
	},
	
	onAskNonTargetQuestionPeopleSelect: function(eventData)	{
		var ui = eventData.ui;
		var txt = ui.item.label;
		var value = ui.item.id;
		this.appendPeople({value: value, txt: txt});
	},
	
	onBegin: function()	{
		this.model = {};
		this.registerObserver();
	},
	
	run: function()	{
		this.model.optionalMsg = this.getLocalizedText("OptionalMessage");
		this.model.optionalDetailMsg = this.getLocalizedText("OptionalDetailMessage");
		this.model.askMsg = this.getLocalizedText("AskMessage");
		this.getPortletPlaceholder().drawToCanvas(this.render());
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	},
	
	// Chọn topic
	onAskNonTargetQuestionTopicSelect: function(eventData)	{
		var ui = eventData.ui;
		var i = 0;
		var txt = ui.item.label;
		var value = ui.item.id;
		
		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent('AskNonTargetQuestionPortletAppend', {'txt': txt, 'value': value});
	},
	
	onAskNonTargetQuestionPortletAppend: function(eventData)	{
		var txt = eventData.txt;
		var value = eventData.value;
		var topicContainer = this.requestForEffectiveResource("AddTopicContainer");
		var addTopicInputBox = this.requestForEffectiveResource("AddTopicInputBox");
		if (this.selectedTopics == undefined)
			this.selectedTopics = Array();
		for( i=0; i<this.selectedTopics.length;i++ ){
			if(this.selectedTopics[i] === value){
				return;
			}
		}
		var textToken = $("<label class='tokenContent'>"+txt+"</label>");
		this.selectedTopics.push(value);
		
		// UI ONLY
		var selectedTopics = this.selectedTopics;
		var span = $("<span>").addClass("uiToken").append(textToken);
		var a = $("<span>").addClass("btnStl").addClass("delToken").bind("click",function(event){
				var start = 0;
				for( start=0; start<selectedTopics.length; start++ ){
					if(selectedTopics[start] === value){
						break;
					}
				}
				selectedTopics.splice(start, 1);
				$(event.target).parent().remove();
			}).html("X").css("text-align","center").appendTo(span);
		topicContainer.append(span);
	},
	
	onAskNonTargetTextBoxFocus: function(eventData)	{
		this.requestForEffectiveResource("Extended").show();
	},
	
	onAskNonTargetButtonClick: function(eventData)	{
		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		var question = this.requestForEffectiveResource("TextBox").val();
		var extended = this.requestForEffectiveResource("TextAreaExtended").val();
		
		if (question == undefined || question.trim() == '')	{
			this.requestForEffectiveResource('QuestionError1').html('Câu hỏi không được để trống');
			return;
		}
		
		var obj = this;
		obj.requestForEffectiveResource("QuestionInfo").html("");
		obj.requestForEffectiveResource("QuestionError").html("");
		obj.requestForEffectiveResource("QuestionError1").html("");
		var selectedTopics = null;
		if(this.selectedTopics.length > 0){
			selectedTopics = new Array();
			for( var i=0; i<this.selectedTopics.length; i++ ){
				selectedTopics.push({"catchword_id":this.selectedTopics[i],"position":0}); 
			}
		}
		var target_id = undefined;
		var target_next_id = undefined;
		if (this.selectedPeople != undefined && this.selectedPeople.length > 0)	{
			target_id = this.selectedPeople[0];
			if (this.selectedPeople.length > 1)	{
				target_next_id = this.selectedPeople[1];
			}
		}
		this.onAjax('question', 'add-unauth-question', {'title': question, 'content': extended, 'catch': selectedTopics, 'target_id': target_id, 'target_next_id': target_next_id}, 'POST', 
			{'onSuccess': function(ret)	{
				this.selectedTopics = Array();
				var sbj = SingletonFactory.getInstance(Subject);
				sbj.notifyEvent("NotifyGlobal", obj.getLocalizedText("AskSuccess"));
				sbj.notifyEvent("PopupRemove", {id: 'AskNonTargetQuestion'});
			},
			'onFailure': function(message)	{
				obj.requestForEffectiveResource("QuestionError").html(obj.getLocalizedText("AskError"));
			}
		});
	}
}).implement(PortletInterface).implement(RenderInterface).implement(ObserverInterface).implement(AjaxInterface);