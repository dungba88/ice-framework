/**
 * @author Hung
 */
ArticleCommentPortlet=Class.extend({
	init : function() {
		this.name="ArticleCommentPortlet";
		this.articleID=null;
		this.articleDetail=null;
		this.model = {};
		this.root = SingletonFactory.getInstance(Application).getSystemProperties().get("host.root");
		this.uid = SingletonFactory.getInstance(Application).getSystemProperties().get("user.id");
		this.needMCE=true;
		this.activeCommentPage=1;
		this.totalCommentPage=0;
	},
	onBegin : function() {
		this.registerObserver();
		this.getArticleID();				
	},
	trim:function(s) {
			return s.replace(/^\s+/,"").replace(/\s+$/, "");
		},
	trimOfEditor:function(s){
		return s.replace(/^(&nbsp;)+/,"").replace(/(&nbsp;)+$/,"");
	},				
	convertHtmlDataToDisplay:function(s){
		return s.replace(/\\n/g,'').replace(/\\/g,'');
	},
	getArticleID : function(){
		var request = SingletonFactory.getInstance(Page).getRequest();
		var params = request.getParams();
		this.articleID = params['aid'];
		//alert(this.articleID);
		if(this.articleID == null || this.articleID == undefined){
			var subject = SingletonFactory.getInstance(Subject);
			var request = new Request("ErrorPage","donganh",{"code":"888", "msg":"Lỗi","content":"Câu hỏi không tồn tại trong hệ thống. Mong bạn thử lại"}); 
			subject.notifyEvent('RequestRoute', request);
		}
	},
	getAjax:function(afterOnSuccess){
		var curPlt=this;
		var curUrl = this.root + "article/get-article-from-id/articleID/"+this.articleID;
		//alert(curUrl);
		// $.ajax({url:curUrl, async:false , success: function(ret){
			// ret = $.parseJSON(ret);
			// curPlt.articleDetail=ret.result;					
			// //alert("abc");
			// //alert(curPlt.articleDetail.result.details.id);
		// }});
	//	alert(curPlt.articleDetail.result.details.id);
		//this.articleDetail=curPlt.articleDetail;
		this.onAjax('article','get-article-detail-to-display',{'articleID':this.articleID},'POST', 
				{'onSuccess': function(ret)	{
					curPlt.articleDetail=ret;		
					curPlt.totalCommentPage=parseInt(curPlt.articleDetail.commentsID.length/5);
					//alert(curPlt.totalCommentPage);	
						
					//window.location='#!page/Article';
					if (afterOnSuccess != null){
						//alert("sss");
						afterOnSuccess.call(curPlt);
					}
				},
				onFailure:function(message){
					alert("thất bại rồi!");
					
				}});
			//	alert(this.articleDetail.details.id);
	},
	initEditor : function()	{
		if(this.needMCE){
			this.requestForEffectiveResource('TextAreaContainer').html(this.renderView('MCE', {}));
			var curID = "HungPDArticleCommentPortlet-CommentTextEditor";
			//var sbj = SingletonFactory.getInstance(Subject);
			//sbj.notifyEvent("TinyEditorInit",{id : curID, content: ""});
		}
	},
	buildArticleTitle:function(){
		var objParams={};
		var templateID=this.name+"-title-template";
		objParams.articleID=this.articleDetail.details.id;
		objParams.authorUserID=this.articleDetail.details.userID;
		objParams.needToVote=this.articleDetail.needToVote;
		objParams.title=this.articleDetail.details.title;
		objParams.userID=this.articleDetail.details.userID;
		objParams.createdDate=this.articleDetail.details.createdDate;		
		objParams.userFullName=this.articleDetail.details.userFullName;
		objParams.catchwordsID=this.articleDetail.catchwordsID;
		objParams.catchwordsName=this.articleDetail.catchwordsName;
		objParams.totalVote=this.articleDetail.votesTotalNum||"0";
		var atype = this.articleDetail.details.typeID;
		if (atype == 1)	{
			objParams.atype = "Bài báo khoa học";
		} else if (atype == 2)	{
			objParams.atype = "Bài chia sẻ kinh nghiệm";
		} else if (atype == 3) {
			objParams.atype = "Câu hỏi hay";
		} else {
			objParams.atype = "Bài phỏng vấn";
		}
		
		var titleContent=tmpl(templateID,objParams);
		this.model.articleTitle=titleContent;
		
		//alert(titleContent);
	},
	buildArticleContent:function(){
		var objParams={};
		var templateID=this.name+"-content-template";
		objParams.articleID=this.articleDetail.details.id;
		objParams.content=this.convertHtmlDataToDisplay(this.articleDetail.details.content);
		var contentContent=tmpl(templateID,objParams);
		this.model.articleContent=contentContent;
	},
	buildArticleComments:function(){
		var objParams={};
		
		var templateID=this.name+"-articleComment-template";
		var commentContent="";
		var commentList=this.articleDetail.commentsUserID;
		if(commentList.length==0){
			this.model.articleComments="";
			return;
		}
		start=(this.activeCommentPage-1)*5;
		while((start<(this.activeCommentPage*5))&&(start<this.articleDetail.commentsID.length)){
			//alert(commentList.length+"--"+this.articleDetail.commentsContent[start]);
			objParams={};
			objParams.commentsID=this.articleDetail.commentsID[start];
			objParams.commentsUserID=this.articleDetail.commentsUserID[start];
			
			objParams.commentsUserFullName=this.articleDetail.commentsUserFullName[start];
			
			objParams.commentsCreatedDate=this.articleDetail.commentsCreatedDate[start];
			objParams.commentsContent=this.convertHtmlDataToDisplay(this.articleDetail.commentsContent[start]);
			commentContent+=tmpl(templateID,objParams);	
			start++;
			//alert(commentContent);		
		};
		this.model.articleComments=commentContent;
	},
	buildArticleCommentPagination:function(){
		var objParams={};
		objParams.activeCommentPage=this.activeCommentPage;
		objParams.totalCommentPage=this.totalCommentPage;
		if(this.totalCommentPage>1){			
		this.requestForEffectiveResource('NavigationLink').html(tmpl('HungPDArticleCommentPortlet-articleComment-SearchMoreButton-template',objParams));
		//alert(tmpl('HungPDArticleCommentPortlet-articleComment-SearchMoreButton-template',objParams));	
		}
	},
	onSubmitComment:function(eventData){
		var obj =this;// obj.name+"-CommentTextEditor";
        //var instance = obj.props.get("memcached.tinyeditor");
        var str=this.trim($("#"+this.name+"-CommentTextEditor").val());
        //"sds";//instance[mceID].e.body.innerHTML;
        if(this.trim(str).length>300){
        	alert("Rất tiếc! Hãy nhập đúng 300 từ");
        } else if(this.trim(str).length==0){
        	alert("Không nhập nội dung rỗng!");        	
        } else{
        	
        	this.onAjax('article','add-article-comment',{'articleID':this.articleID,'content':str},'POST', 
				{'onSuccess': function(ret)	{
					obj.run();					
				},
				onFailure:function(message){
					alert("thất bại rồi!");
					
				}});
        	
        };        
	},
	onDeleteArticleComment:function(eventData){
		var obj=this;
		var cid=eventData.commentID;
		//alert(cid);
		var result=window.confirm("Có chắc chắn xóa ?");
		if(!result){
			return;
		}
		this.onAjax('article','delete-article-comment',{'commentID':cid},'POST', 
		{'onSuccess': function(ret)	{
			obj.run();
		},
		onFailure:function(message){
			alert("thất bại rồi!");
			
			
		}});
	},
	onVoteSubmit:function(eventData){
		var obj=this;
		var userID=this.uid;
		var articleID=eventData.articleID;
		//alert(articleID);
		var result=window.confirm("Có chắc chắn vote ?");
		if(!result){
			return;
		}
		this.onAjax('article','add-article-vote',{'articleID':articleID,'userID':userID},'POST', 
		{'onSuccess': function(ret)	{
			obj.run();
		},
		onFailure:function(message){
			alert("thất bại rồi!");						
		}});
	},
	onUnVoteSubmit:function(eventData){
		var obj=this;
		
		var userID=this.uid;
		var articleID=eventData.articleID;
		//alert(cid);
		var result=window.confirm("Có chắc chắn bỏ vote ?");
		if(!result){
			return;
		}
		this.onAjax('article','delete-article-vote',{'articleID':articleID,'userID':userID},'POST', 
		{'onSuccess': function(ret)	{
			obj.run();
		},
		onFailure:function(message){
			alert("thất bại rồi!");
			
			
		}});
	},
	onUpdateCommentPage:function(eventData){
		if(this.activeCommentPage==1&&eventData.type==-1){
			return;
		}
		if(this.activeCommentPage==this.totalCommentPage&&eventData.type==1){
			return;
		}
		this.activeCommentPage+=eventData.type;
		this.run();
				
	},		
	run : function() {
		this.getAjax(this.renderArticle);
		/*this.getAjax();
		this.buildArticleTitle();
		this.getPortletPlaceholder().paintCanvas(this.render());
		*/
		
		//alert("sá");
				
	},			
	renderArticle: function ()	{
		this.buildArticleTitle();
		this.buildArticleContent();
		this.buildArticleComments();
		
		this.getPortletPlaceholder().paintCanvas(this.render());		
		this.initEditor();
		this.buildArticleCommentPagination();
		
		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent('CleanFont');
	}, 
	onReloadPage : function(){
		this.init();
		this.onBegin();
		this.run();
	},
	
	onEnd : function()	{
		this.unregisterObserver();
	}	
}).implement(RenderInterface).implement(PortletInterface).implement(ObserverInterface).implement(AjaxInterface);