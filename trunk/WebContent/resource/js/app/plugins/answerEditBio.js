AnswerShowBioPlugin = Class.extend({
	init: function()	{
		this.name = "AnswerShowBioPlugin";
	},
	
	onBegin: function()	{
		this.catches = undefined;
	},
	
	onShowBio: function(eventData)	{
		var memcached = SingletonFactory.getInstance(Memcached);
		var keyToStore = "question.catchword";
		if (eventData.qid != this.qid)	{
			this.catches = undefined;
		}
		memcached.clear(keyToStore);
		this.qid = eventData.qid;
		var obj = this;
		$('.extension-point[extensionName="AnswerProfileName"]').each(function(index, value) {
			if ($(value).find('[flag="AnswerShowBio"]').length > 0)	{
				return;
			}
			$(value).append('<span flag="AnswerShowBio"></span>');
			var uid = $(value).attr('extensionId');
			if (obj.catches == undefined)	{
				obj.onAjax('question', 'get-all-catchwords-question', {'qid': obj.qid}, 'GET', {
					'onSuccess': function(ret)	{
						memcached.store(keyToStore, ret);
						obj.catches = ret;
						obj.renderBio(value, uid);
					}
				}, true, 300000);
			} else {
				obj.renderBio(value, uid);
			}
		});
	},
	
	renderBio: function(value, uid)	{
		var obj = this;
		this.onAjax('ajax', 'get-all-expertises', {'id': uid}, 'GET', {
			'onSuccess': function(ret)	{
				for(var i in obj.catches)	{
					var cat = obj.catches[i];
					var catId = cat.id;
					for (var j in ret)	{
						var retJ = ret[j];
						var retJContent = retJ.content;
						for (var k in retJContent)	{
							var retJContentK = retJContent[k];
							if (catId == retJContentK.id)	{
								var explanation = retJContentK.expertiseExplanation;
								$(value).prepend(tmpl('AnswerShowBioTmpl', {'content': explanation}));
								return;
							}
						}
					}
				}
			}
		}, true, 300000);
	}
}).implement(PluginInterface).implement(AjaxInterface);

AnswerEditBioPlugin = Class.extend({
	init: function()	{
		this.name = "AnswerEditBioPlugin";
	},
	
	onBegin: function()	{
		this.catches = undefined;
	},
	
	onRenderBioEditLink: function(eventData)	{
		var qid = eventData.qid;
		$('.extension-point[extensionName="AnswerProfileName"]').each(function(index, value) {
			if ($(value).find('[flag="AnswerEditBio"]').length > 0)	{
				return;
			}
			var properties = SingletonFactory.getInstance(Application).getSystemProperties();
			var loggedIn = properties.get('user.login');
			if (loggedIn == 1)	{
				var userID = properties.get('user.id');
				var answerUID = $(value).attr('extensionID');
				if (userID == answerUID)	{
					$(value).append(tmpl('AnswerEditBioTmpl', {'qid':qid}));
				}
			}
		});
	},
	
	onEditBioClick: function(eventData)	{
		this.qid = eventData.qid;
		var obj = this;
		if (this.catches == undefined)	{
			var keyToGet = "question.catchword";
			var memcached = SingletonFactory.getInstance(Memcached);
			var catches = memcached.retrieve(keyToGet);
			if (catches != undefined)	{
				this.catches = catches;
				this.prepareShowingBioEditor();
			} else {
				obj.onAjax('question', 'get-all-catchwords-question', {'qid': obj.qid}, 'GET', {
					'onSuccess': function(ret)	{
						memcached.store(keyToGet, ret);
						obj.catches = ret;
						obj.prepareShowingBioEditor();
					}
				});
			}
		} else {
			this.prepareShowingBioEditor();
		}
	},
	
	prepareShowingBioEditor: function()	{
		var obj = this;
		var memcached = SingletonFactory.getInstance(Memcached);
		var currentExpertises = memcached.retrieve('user.expertises');
		this.currentExpertises = currentExpertises;
		if (currentExpertises == undefined)	{
			this.onAjax('ajax', 'get-all-expertises', {}, 'GET', {
				'onSuccess': function(ret)	{
					memcached.store('user.expertises', ret);
					obj.currentExpertises = ret;
					obj.showBioEditor();
				}
			}, true, 300000);
		} else {
			this.showBioEditor();
		}
	},
	
	showBioEditor: function()	{
		var obj = this;
		var expToShow = Array();
		var ret = obj.currentExpertises;
		for(var i in obj.catches)	{
			var found = false;
			var cat = obj.catches[i];
			var catId = cat.id;
			for (var j in ret)	{
				var retJ = ret[j];
				var retJContent = retJ.content;
				for (var k in retJContent)	{
					var retJContentK = retJContent[k];
					if (catId == retJContentK.id)	{
						if (retJContent[k].expertiseExplanation == undefined)
							retJContent[k].expertiseExplanation = "";
						var plhd = tmpl('AnswerBioEditorExplanationTmpl', {'expertiseName':retJContent[k].expertiseName, 'expertiseExplanation':retJContent[k].expertiseExplanation});
						expToShow.push({'id':retJContentK.id,'expertiseName': retJContent[k].expertiseName, 'expertisePlaceholder': plhd});
						found = true;
					}
				}
			}
			if (!found)	{
				var plhd = tmpl('AnswerBioEditorExplanationTmpl', {'expertiseExplanation': undefined, 'expertiseName':cat.catchword});
				expToShow.push({'id':cat.id,'expertiseName': cat.catchword, 'expertisePlaceholder': plhd});
			}
		}
		var subject = SingletonFactory.getInstance(Subject);
		if (expToShow.length == 0)	{
			subject.notifyEvent('NotifyMessage', 'Câu hỏi hiện tại không thuộc chủ đề nào. Do đó bạn chưa thể thay đổi chú thích ngay bây giờ');
		} else {
			subject.notifyEvent('ShowPopup', {id: 'AnswerEditBio', title: 'Sửa chú thích cho câu trả lời', content: tmpl('AnswerBioEditorTmpl', {'expertises':expToShow})});
		}
	},
	
	onBioEditorEditClick: function(event)	{
		var target = event.event.target;
		var mode = event.mode;
		var parent = $(target).parent();
		var oldHtml = $(parent).find('.explanationPlaceholder').html();
		$(parent).html(tmpl('AnswerBioEditorExplanationEditTmpl', {'content': oldHtml, 'mode':mode}));
		$(parent).find('textarea').focus();
	},
	
	onBioEditCancelClick: function(event)	{
		var target = event.target;
		var parent = $(target).parent();
		var textBox = $(parent).find('textarea');
		$(parent).html(tmpl('AnswerBioEditorExplanationTmpl', {'expertiseExplanation':$(textBox).attr('oldVal'), 'expertiseName':$(parent).attr('expertiseName')}));
	},
	
	onBioEditUpdateClick: function(event)	{
		var target = event.event.target;
		var mode = event.mode;
		var parent = $(target).parent();
		var textBox = $(parent).find('textarea');
		var newVal = $(textBox).val();
		var oldVal = $(textBox).attr('oldVal');
		if (newVal == oldVal)	{
			$(parent).html(tmpl('AnswerBioEditorExplanationTmpl', {'expertiseExplanation':oldVal, 'expertiseName':$(parent).attr('expertiseName')}));
		} else {
			var id = $(parent).attr('expertiseID');
			if (mode == 'update')	{
				this.onAjax('ajax', 'update-single-expertise', {'id': id, 'explanation':newVal}, 'POST', {});
			} else {
				this.onAjax('ajax', 'add-single-expertise', {'id': id, 'explanation':newVal}, 'POST', {});
			}
			$(parent).html(tmpl('AnswerBioEditorExplanationTmpl', {'expertiseExplanation':newVal, 'expertiseName':$(parent).attr('expertiseName')}));
		}
	}
}).implement(PluginInterface).implement(AjaxInterface);