RecommendAnswerPortlet = Class.extend({
	init: function()	{
		this.name = "RecommendAnswerPortlet";
		this.usingFacebox = false;
	},
	
	onBegin: function()	{
		this.model = {};
		this.registerObserver();
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	},
	
	run: function()	{
		this.getPortletPlaceholder().paintCanvas(this.render());
	},
	
	showPortlet: function(eventData){
		var loggedIn = SingletonFactory.getInstance(Application).getSystemProperties().get('user.login');
		if (loggedIn != 1)	{
			var subject = SingletonFactory.getInstance(Subject);
			subject.notifyEvent("NeedLogin", {type: 'RecommendAnswer', event: eventData});
		} else {
			this.doShowPortlet(eventData);
		}
	},

	onLoginSuccess: function(eventData)	{
		if (eventData.type != 'RecommendAnswer')
			return;
		this.doShowPortlet(eventData.event);
	},
	
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
		subject.notifyEvent('ShowPopup', {id: 'RecommendAnswer', title: '<img src="static/css/beta/images/icon/24/question.png" alt="question"/>Tiến cử người trả lời', content: this.render()});
		$('#RecommendAnswerPorlet-LabelEmail').hide();
		$('#RecommendAnswerPortlet-AddEmailInputBox').hide();
		
		var profile = eventData.profile;
		if (profile != undefined)	{
		for(var i=0;i<profile.length && i<2;i++)	{
				this.appendPeople({value: profile[i].id, txt: profile[i].name});
			}
		}
		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent('RecommendPortletFetchAutocomplete');
	},
	
	onRecommendPortletFetchAutocomplete: function()	{	
		var addPeopleInputBox = this.requestForEffectiveResource("AddPeopleInputBox");
		var eventData = {};
		eventData.selectCallback = "TargetPeopleSelect";
		eventData.autocompleteObject = addPeopleInputBox;
		eventData.autocompleteSource = "ajax/autocomplete-person";
		eventData.type = "RecommendPerson";
		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent('NeedAutocomplete', eventData);
	},
	
		
	onTargetPeopleSelect: function(eventData)	{
		var ui = eventData.ui;
		var txt = ui.item.label;
		var value = ui.item.id;
		if (ui.item.type == 'email') {
			$("#RecommendAnswerPorlet-LabelPeople").hide();
			$("#RecommendAnswerPortlet-AddPeopleInputBox").hide();
			$("#RecommendAnswerPorlet-LabelEmail").show();
			$("#RecommendAnswerPortlet-AddEmailInputBox").show();
			return;
		} 
		this.appendPeople({value: value, txt: txt});
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
		
	checkEmail: function(strEmail) {
		var atpos = strEmail.indexOf("@");
		var dotpos = strEmail.lastIndexOf(".");
		if (atpos<1 || dotpos<atpos+2 || dotpos+2>=strEmail.length){
		  	return false;
	 	}
	 	return true;
	},

	onRecommendAnswerButtonClick: function(eventData)	{
		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		var obj = this;
				
		if( $("#RecommendAnswerPortlet-AddEmailInputBox").is(":visible") ) {
	 	   if( this.checkEmail($("#RecommendAnswerPortlet-AddEmailInputBox").val()) ){
	 	   		var emailtarget = $("#RecommendAnswerPortlet-AddEmailInputBox").val() 
	 	   		this.onAjax('question', 'add-recommend-email', {'question_id': this.question_id, 'emailtarget': emailtarget}, 'POST', 
				{
				'onSuccess': function(ret)	{
					var sbj = SingletonFactory.getInstance(Subject);
					sbj.notifyEvent("NotifyGlobal", obj.getLocalizedText("AskSuccess"));
					sbj.notifyEvent("PopupRemove", {id: 'RecommendAnswer'});
				},
				'onFailure': function(message)	{
					obj.requestForEffectiveResource("QuestionError").html(obj.getLocalizedText("RecommendError"));
				}
				});//end onAjax
	 	   				
	 	   }else{
	 	   		var sbj = SingletonFactory.getInstance(Subject);
	 	   		sbj.notifyEvent("NotifyGlobal", obj.getLocalizedText("AskEmailError"));
	 	   		return;
	 	   }//end if check email address
		}//end if recommend email
		else {
		    var selectedPeople = null;
			if (this.selectedPeople != undefined && this.selectedPeople.length > 0)	{
				selectedPeople = new Array();
				for (var i=0; i<this.selectedPeople.length; i++){
					selectedPeople.push({"target_id":this.selectedPeople[i]});
				}
			}//end 
			this.onAjax('question', 'add-new-recommend-answer', {'question_id': this.question_id, 'target_id': selectedPeople}, 'POST', 
				{'onSuccess': function(ret)	{
					this.selectedPeople = Array();
					var sbj = SingletonFactory.getInstance(Subject);
					sbj.notifyEvent("NotifyGlobal", obj.getLocalizedText("AskSuccess"));
					sbj.notifyEvent("PopupRemove", {id: 'RecommendAnswer'});
				},
				'onFailure': function(message)	{
					obj.requestForEffectiveResource("QuestionError").html(obj.getLocalizedText("AskError"));
				}
			});//end onAjax
		}//end if recommend user		
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface);