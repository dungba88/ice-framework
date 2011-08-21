ShareFacebookPortlet = Class.extend({
	init: function()	{
		this.name = "ShareFacebookPortlet";
		this.usingFacebox = false;
		
		this.answers = null;
		this.question = null;
		
		this.questionID = null;
		this.linkQuestion = null;
		this.strQuestionTitle = null;
		this.linkAction = null;
		this.avatarAsker = null;
		this.strCaption = 'www.bkprofile.com';
		this.strDefaultMessage = '';
		this.strActionName = null;
		this.strDesciption = null;

		this.root = SingletonFactory.getInstance(Application).getSystemProperties().get("host.root");
		
    	window.fbAsyncInit = function() {
		    FB.init({appId: '103055919791728', status: true, cookie: true, xfbml: false});
    	};
	  	(function() {
		    var e = document.createElement('script'); e.async = true;
		    e.src = document.location.protocol +
	      								'//connect.facebook.net/en_US/all.js';
		    $("#fb-root").append(e);
		}());
				
	},
	
	requestForEffectiveResource: function(targetName){
		if(this.usingFacebox){
			return $("#"+this.name+"-"+targetName);
		}else{
			return this._super.requestForEffectiveResource(targetName);
		}
	},
	
	onBegin: function()	{
		this.model = {};
		this.registerObserver();
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	},
	
	onShareFacebook: function(eventData)	{
		this.questionID = eventData.qid;
		
		var loggedIn = SingletonFactory.getInstance(Application).getSystemProperties().get('user.login');
		if (loggedIn != 1)	{
			var subject = SingletonFactory.getInstance(Subject);
			subject.notifyEvent("NeedLogin", {type: 'ShareFacebook', event: eventData});
		} else {
			this.publishSharing();
		}	
	},
	onLoginSuccess: function(eventData)	{
		if (eventData.type != 'ShareFacebook')
			return;
		this.publishSharing();
	},
	
	publishSharing: function(){			
		var curPlt = this;
		var curUrl = this.root + "question/detail-question/questionid/" + this.questionID + "/page/0/rows/10";
		$.ajax({url:curUrl, async: false, success: function(ret){
			ret = $.parseJSON(ret);
			ret = ret.result;
			if(ret == undefined || ret == "error"){
				var subject = SingletonFactory.getInstance(Subject);
				var request = new Request("ErrorPage","",{"code":"888", "msg":"Lỗi","content":"Câu hỏi không tồn tại trong hệ thống. Mong bạn thử lại"}); 
				subject.notifyEvent('RequestRoute', request);
				return;
			}	
			curPlt.question = ret.question;
			
		}});//end ajax question
		
		//get answer
		this.onAjax('question', 'get-question-answer', {questionid: this.questionID, page: 0, rows: 10}, 'GET', {
			'onSuccess': function(ret){
				curPlt.answers = ret.answers;
				curPlt.buidContent();
				curPlt.startShare();
			},
			'onFailure': function(msg)	{
				var subject = SingletonFactory.getInstance(Subject);
				var request = new Request("ErrorPage","",{"code":"888", "msg":"Lỗi","content":"Câu hỏi không tồn tại trong hệ thống. Mong bạn thử lại"});
				subject.notifyEvent('RequestRoute', request);
				return;
			}
		});//end ajax answer.
				
	},
	
	buidContent: function(){		
		var count=0;
		if(this.question.user.avatar.length == 0){ //assign default avatar of bkprofile
			this.avatarAsker = "http://bkprofile.com/avatar/fd058c62388594c31276382d48198c5f0f5e0d34";
		}else{
			this.avatarAsker = "http://bkprofile.com/avatar/" + this.question.user.avatar;	
		}
		this.strQuestionTitle = this.question.title;
		this.linkAction = "http://bkprofile.com/#!page/Question/qid/" + this.questionID;
		
		for ( var key in this.answers){count++;} //get length of answers.
		for ( var key in this.answers){
			var curObj = this.answers[key];
			this.strActionName = 'See Answer';
			this.strDescription = "Answer (1 of " + count + "):" + curObj['content'];
			break;
		}
		if(count == 0){
			this.strActionName = 'Answer Question';
			this.strDescription = this.question.content.trim().replace(/\\n/g, '<br>');
		}
		return;
	},
	
	startShare: function() {
		var curPlt = this;
		 FB.ui(
		   {
		     method: 'feed',
		     name: this.strQuestionTitle,
		     link: 'http://griever/bkprofile.com/',
		     picture: this.avatarAsker,
		     caption: this.strCaption,
		     description: this.strDescription,
		     message: this.strDefaultMessage,
		     actions: [
    				{ name: this.strActionName, link: this.linkAction }
  			 ],
		   },
		   function(response) {
		     if (response && response.post_id) {
		       var sbj = SingletonFactory.getInstance(Subject);
		       curPlt.onAjax('question', 'add-share-facebook', {'question_id': curPlt.questionID, 'session_id': response.post_id}, 'POST', 
				{'onSuccess': function(ret)	{
					var sbj = SingletonFactory.getInstance(Subject);
					sbj.notifyEvent("NotifyGlobal", curPlt.getLocalizedText("SetDataSuccess"));
				},
				'onFailure': function(message)	{
					var sbj = SingletonFactory.getInstance(Subject);
					sbj.notifyEvent("NotifyGlobal", curPlt.getLocalizedText("SetDataFailure"));
				}
				});    
			   sbj.notifyEvent("NotifyGlobal", curPlt.getLocalizedText("PublishSuccess"));
		     } else {
		       var sbj = SingletonFactory.getInstance(Subject);
			   sbj.notifyEvent("NotifyGlobal", curPlt.getLocalizedText("PublishFailure"));
		     }
		   }
 		);
	},

	run: function()	{
	},
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface);