AnswerPortlet = Class.extend({
	init : function() {
		this.name = "AnswerPortlet";
		this.model = {};
		this.question = null;
		this.topics = null;
		this.topicsQuestion = null;
		this.mce = $("");
		this.answers = null;
		this.questionID = null;
		this.needMCE = true;
		this.answerNum = 0;
		this.root = SingletonFactory.getInstance(Application).getSystemProperties().get("host.root");
		this.uid = SingletonFactory.getInstance(Application).getSystemProperties().get("user.id");
		this.quid = "";
	},

	onBegin : function() {
		this.registerObserver();
		this.getQuestionID();
		this.model.qid = this.questionID;
		this.model.qvoteup = "...";
		this.model.qvotedown = "...";
		this.model.avaiTopics = {};
		this.model.topics = {};
		this.model.mce = {};
		this.model.children = {};
	},
	
	getQuestionID: function(){
		var request = SingletonFactory.getInstance(Page).getRequest();
		var params = request.getParams();
		this.questionID = params['qid'];
		if(this.questionID == null || this.questionID == undefined){
			var subject = SingletonFactory.getInstance(Subject);
			var request = new Request("ErrorPage","donganh",{"code":"888", "msg":"Lỗi","content":"Câu hỏi không tồn tại trong hệ thống. Mong bạn thử lại"}); 
			subject.notifyEvent('RequestRoute', request);
		}
	},
	
	getAjax: function(){
		var curPlt = this;
		var curUrl = this.root + "question/detail-question/questionid/" + this.questionID + "/page/0/rows/10";
		$.ajax({url:curUrl, async:false, success: function(ret){
			ret = $.parseJSON(ret);
			ret = ret.result;
			if(ret == undefined || ret == "error"){
				var subject = SingletonFactory.getInstance(Subject);
				var request = new Request("ErrorPage","",{"code":"888", "msg":"Lỗi","content":"Câu hỏi không tồn tại trong hệ thống. Mong bạn thử lại"}); 
				subject.notifyEvent('RequestRoute', request);
				return;
			}
			curPlt.question = ret.question;
			//curPlt.answers = ret.answers;
			
			var owner = ret.question.user_id;
			curPlt.ownerAvatar = ret.question.user.avatar;
			curPlt.questionOwner = owner;
			if (owner == curPlt.uid)	{
				//check if current user is the question owner
				curPlt.getAjaxAllTopics();
			}
		}});
	},
	
	onReloadAnswers: function()	{
		this.getAjaxAnswer();
	},
	
	getAjaxAnswer: function(){
		var curPlt = this;
		this.onAjax('question', 'get-question-answer', {questionid: this.questionID, page: 0, rows: 10}, 'GET', {
			'onSuccess': function(ret){
				curPlt.answers = ret.answers;
				curPlt.buildAnswer(ret.answers);
			},
			'onFailure': function(msg)	{
				var subject = SingletonFactory.getInstance(Subject);
				var request = new Request("ErrorPage","",{"code":"888", "msg":"Lỗi","content":"Câu hỏi không tồn tại trong hệ thống. Mong bạn thử lại"});
				subject.notifyEvent('RequestRoute', request);
				return;
			}
		});
	},
	
	getAjaxAllTopics: function()	{
		var curPlt = this;
		var curUrl = this.root + "catchword/get-all-catchwords/";
		// $.ajax({url:curUrl, async:true , success: function(ret){
		// 	ret = $.parseJSON(ret);
		// 	ret = ret.result;
		// 	if(ret == undefined || ret == "error"){
		// 		return;
		// 	}
		// 	curPlt.topics = ret;
		// 	//build all topic when done
		// 	curPlt.buildAllTopics(ret);
		// 	SingletonFactory.getInstance(Application).getSystemProperties().set("memcached.topics",ret);
		// }});
	},
	
	getAjaxTopicQuestion: function(){
		var curPlt = this;
		var curUrl = this.root + "question/get-all-catchwords-question/qid/" + this.questionID;
		$.ajax({url:curUrl, async:true, success: function(ret){
			ret = $.parseJSON(ret);
			ret = ret.result;
			if(ret == undefined || ret == "error"){
				return;
			}
			curPlt.topicsQuestion = ret;
			curPlt.buildTopics(ret);
		}});
	},
	
	buildQuestion: function(){
		this.model.qcontent = this.question.title;
		this.model.qdetail = this.question.content.trim().replace(/\\n/g, '<br>');
		this.model.qdate = this.question.created_date;
		this.model.quser = "";
		this.model.qusertarget = "";
		this.model.avatar = this.ownerAvatar;
		if(this.question.user['name'] != undefined && this.question.user['name']!=""){
			this.quid = this.question.user['id'];
			var objParam = {};
			objParam.quserid = this.question.user['id'];
			objParam.qusername = this.question.user['name'];
			this.model.quser = this.renderView("UserLink",objParam);
		}
		
		if(this.question.target_name != undefined && this.question.target_name !=""){
			var objParam = {};
			objParam.quserid = this.question.targetId;
			objParam.qusername = this.question.target_name;
			this.model.qusertarget = this.renderView("UserLink",objParam);
		}
	},
	
	buildTopics: function(topicsQuestion){
		var topicAdded = Array();
		var topicArea = this.requestForEffectiveResource('TopicArea');
		for ( var key in topicsQuestion) {
			var objParam = {};
			var curObj = topicsQuestion[key];
			objParam.tid = generateId("tt");
			objParam.content = curObj['catchword'];
			objParam.cid = curObj['id'];
			objParam.uid = this.quid;
			topicAdded[curObj['id']] = objParam.tid;
			var newTp = this.renderView("Topic",objParam);
			topicArea.append(newTp);
		}
		// fetch to cache
		var sysProp = SingletonFactory.getInstance(Application).getSystemProperties();
		sysProp.set("memcached.addedTopics", topicAdded);
		
		var sbj = SingletonFactory.getInstance(Subject);
		sbj.notifyEvent("CheckTopicEditable");
	},
	
	buildAllTopics: function(topics){
		var result = $("<div><div></div></div>");
		for ( var key in topics) {
			var curObj = topics[key];
			var newTp = $("<option value='"+curObj['id']+"'>"+curObj['catchWord']+"</option>")
			result.children().append(newTp);
		}
		
		// changed to ajax oriented
		$("#bkp-topic-list").append(result.children().html());
		//this.model.avaiTopics = result.children();
	},

	buildAnswer : function(answers) {
		var result = $("<div></div>");
		var hidden = $("<div></div>");
		var hiddenNo = 0;
		
		var checkUserAnswered = Array();
		for ( var key in answers) {
			this.answerNum ++;
			var objParam = {};
			var curObj = answers[key];
			objParam.uid = curObj['userID'];
			checkUserAnswered.push(curObj['userID']);
			// TODO: neu uid == this user => no need for mce
			if(curObj['userID'] == this.uid){
				this.needMCE = false;
			}
			objParam.aid = curObj['id'];
			objParam.uimglink = curObj['user'].avatar;
			objParam.uname= curObj['user'].name;
			objParam.avote = curObj['vote'];
			objParam.adate = curObj['created_date'];
			objParam.acontent = curObj['content'];
			objParam.negatives = Array();
			
			//negative comments
			for(var i in curObj['negativeTypes'])	{
				var type = curObj['negativeTypes'][i];
				switch (type['type'])	{
					case '1':
						objParam.negatives.push('Câu trả lời này cần phải đúng trọng tâm câu hỏi hơn');
						break;
					case '2':
						objParam.negatives.push('Câu trả lời này cần đưa ra ý kiến mới so với những câu trả lời trước đó');
						break;
					case '3':
						objParam.negatives.push('Câu trả lời này cần thêm giải thích');
						break;
					case '4':
						objParam.negatives.push('Câu trả lời này nên đưa vào phần bàn luận cho câu trả lời khác');
						break;
					case '5':
						objParam.negatives.push('Chứa nội dung từ nguồn khác nhưng không có chú thích');
						break;
					case '6':
						objParam.negatives.push('Không lấy từ kinh nghiệm bản thân');
						break;
				}
			}
			
			if (curObj.hidden == 1)	{
				var template = this.renderView("HiddenItem", objParam);
				hidden.append(template);
				hiddenNo++;
			}
			else {
				var template = this.renderView("Item", objParam);
				result.append(template);
			}
		}
		
		// Used in plugin
		SingletonFactory.getInstance(Application).getSystemProperties().set("memcached.answeredID",checkUserAnswered);
		this.requestForEffectiveResource('AnswersList').html($(result).html());
		this.requestForEffectiveResource('HiddenAnswersList').html($(hidden).html());
		this.requestForEffectiveResource('HiddenAnswerNo').html(hiddenNo);
		if (hiddenNo == 0)	{
			this.requestForEffectiveResource('HiddenAnswers').hide();
		}
		
		var sbj = SingletonFactory.getInstance(Subject);
		var checkImg = {};
		checkImg.zone = this.requestForEffectiveResource('AnswersList');
		sbj.notifyEvent("RequestProfileImageCheck", checkImg);
		checkImg.zone = this.requestForEffectiveResource('HiddenAnswersList');
		sbj.notifyEvent("RequestProfileImageCheck", checkImg);
		
		this.updateAnswerNum();
		this.initEditor(this.questionID);
		sbj.notifyEvent("BindingChangeAnswer");
		var qid = this.getRequest().getParam('qid');
		sbj.notifyEvent("ShowBio", {'qid': qid});
		sbj.notifyEvent("RenderBioEditLink", {'qid': qid});
		sbj.notifyEvent("AnswerBuilt", {'qid': qid});
		//clean style
		sbj.notifyEvent('CleanFont');
	},
	
	buildQuestionVote: function(){
		var result = null;
		var curUrl = this.root + "question/get-question-vote";
		var obj = this;
		$.ajax({url:curUrl, async:true , data: {qid: this.questionID}, type: "POST", success: function(ret){
			ret = $.parseJSON(ret);
			ret = ret.result;
			if(ret == undefined || ret == "error"){
				return;
			}
			result = ret;
			
			//changed to ajax oriented
			obj.requestForEffectiveResource('QVote').html(ret['up']);
		}});
		//this.model.qvoteup = result['up'];
		//this.model.qvotedown = result['down'];
	},
	
	updateAnswerNum: function(){
		// update count
		var countPage = parseInt($("#answer-more-btn").attr("page"));
		var sum = this.answerNum + countPage;
		$("#answer-more-btn").attr("page",sum);
	},
	
	initEditor: function(qid)	{
		if(this.needMCE){
			this.requestForEffectiveResource('TextAreaContainer').html(this.renderView('MCE', {'qid': qid}));
			var curID = "AnswerPortlet-AnswerTextEditor";
			var sbj = SingletonFactory.getInstance(Subject);
			sbj.notifyEvent("TinyEditorInit",{id : curID, content: ""});
		}
	},
	
	run : function() {
		this.getAjax();
		this.buildQuestion();

		this.getPortletPlaceholder().paintCanvas(this.render());
		
		var sbj = SingletonFactory.getInstance(Subject);
		sbj.notifyEvent('QuestionBuilt', {id: this.questionID, userID: this.questionOwner});
		
		if (this.questionOwner != this.uid)	{
			this.requestForEffectiveResource('ChangeTopic').hide();
		}
		
		this.getAjaxAnswer();
		
		this.getAjaxTopicQuestion();
		this.buildQuestionVote();
	},
	
	onSubmitQuestionContent: function()	{
		var qid = this.getRequest().getParam('qid');
		var subject = SingletonFactory.getInstance(Subject);
		var title = this.requestForEffectiveResource('QContent').find('input').val();
		var content = this.requestForEffectiveResource('QDetail').find('textarea').val();
		if (title == this.contentOld && content == this.detailOld)	{
			subject.notifyEvent('CancelSubmitQuestionEdit');
			return;
		}
		var obj = this;
		this.onAjax('question', 'edit-question', {title: title, content: content, id: qid}, 'POST', {
			'onSuccess': function(ret)	{
				obj.requestForEffectiveResource('QContent').html(title);
				obj.requestForEffectiveResource('QDetail').html(content);
				obj.requestForEffectiveResource('QEditControl').html('');
				obj.editState = 0;
			}
		});
	},
	
	onCancelSubmitQuestionEdit: function()	{
		this.requestForEffectiveResource('QContent').html(this.contentOld);
		this.requestForEffectiveResource('QDetail').html(this.detailOld);
		this.requestForEffectiveResource('QEditControl').html('');
		this.editState = 0;
	},
	
	onChangeTopicButtonClick: function()	{
		if (this.editState == 1)
			return;
		this.editState = 1;
		var oldContent = this.requestForEffectiveResource('QContent').html();
		this.requestForEffectiveResource('QContent').html(this.renderView('QContentEdit', {oldContent: oldContent}));
		this.contentOld = oldContent;
		oldContent = this.requestForEffectiveResource('QDetail').html();
		this.requestForEffectiveResource('QDetail').html(this.renderView('QDetailEdit', {oldContent: oldContent}));
		this.detailOld = oldContent;
		this.requestForEffectiveResource('QEditControl').html(this.renderView('QEditControlTmpl', {}));
		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent('ExpandTextArea', {min: 45, max: 200, target: this.requestForEffectiveResource('QDetail').find('textarea')});
	},
	
	onReloadPage: function(){
		this.init();
		this.onBegin();
		this.run();
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	}
}).implement(RenderInterface).implement(PortletInterface).implement(ObserverInterface).implement(AjaxInterface);
