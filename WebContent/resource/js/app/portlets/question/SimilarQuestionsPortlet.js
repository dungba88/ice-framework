SimilarQuestionsPortlet = Class.extend({
	init: function()	{
		this.name = "SimilarQuestionsPortlet";
		this.questionID = null;
	},
	
	onBegin: function()	{
		var request = SingletonFactory.getInstance(Page).getRequest();
		var params = request.getParams();
		this.questionID = params['qid'];
	},
	
	run: function()	{
		this.getPortletPlaceholder().paintCanvas(this.render());
		this.fetch();
	},
	
	fetch: function()	{
		var obj = this;
		this.onAjax('ajax', 'get-similar-questions', {qid: obj.questionID}, 'GET', {
			'onSuccess': function(ret)	{
				obj.requestForEffectiveResource('SimilarQuestions').html(obj.renderView('SimilarQuestionsTmpl', {user: ret}));
			}
		});
	},
	
	onReloadPage: function(){
		this.init();
		this.onBegin();
		this.run();
	},
	
	onEnd: function()	{
	}
}).implement(PortletInterface).implement(AjaxInterface).implement(RenderInterface);