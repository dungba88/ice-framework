/**
 * @author Hung
 */
ArticleSearchResultPortlet = Class.extend(
	{
		init : function() {
		this.name = "ArticleSearchResultPortlet";
		this.model = {};
		this.configData = null;
		this.pltParams = null;
		this.dataResJSON = null;
		this.highlightResJSON = null;
		this.numDocs = null;
		this.totalPage=null;
		
		this.root = SingletonFactory.getInstance(Application).getSystemProperties().get("host.root");
		this.isShuffleResult = false;
		// this.articleChangeOrder="theongaythang";
		// this.articleTypeID=null;
		// this.typeToChange=0;
		// this.oldTypeToChange=-1;
		this.pageType = null;//"Knowledge";
		
		this.page = null;
		this.rows = 10;
		this.isSelected=1;
		this.searchFilter=null;
		this.articleType=null;
		this.articleOrderBy=null;
		this.queryArr={};
		
	},
	
	onBegin: function()	{
		this.registerObserver();		
		this.prepareInitialConfig();
		this.prepareData();
	},
	
	prepareInitialConfig: function()	{
		this.pltParams = this.getInitParameters();
		var params = this.getRequest().getParams();
		//kiểu cuối cùng owner-filter
		this.searchFilter = this.pltParams['type'] || params['type'] || "notification-filter";
		this.model.authorID=params['authorID']||SingletonFactory.getInstance(Application).getSystemProperties().get('user.id')||-1;
		//alert(topicID);
		this.pageType = "Notification";
	
		this.page = 1;
		this.totalPage=1;
		this.rows = 10;
		this.articleType=this.pltParams['articleTypeID']||0;
		this.articleOrderBy=0;
				
		//alert(this.core);
		this.isShuffleResult = this.pltParams['shuffle'] || false;
		this.shuffleNo = this.pltParams['shuffleNo'] || this.rows;
	},
	
	// done
	prepareData: function(){
		var request = this.getRequest();
		var params = request.getParams();
		this.queryArr={};
		// fixed
		switch(this.searchFilter){
			case "user-filter":
				//var userid=SingletonFactory.getInstance(Application).getSystemProperties().get('user.id');				
				//alert(this.model.authorID);
				this.queryArr.authorID=this.model.authorID;
				this.pageType="UserManagement";
				//alert(this.pageType);
				break;
			case "catchword-filter" :
				var topicID = params['topicID']||null;				
				this.queryArr.topicID=topicID;
				break;
			case "notification-filter":
			default:
				break;
		}
		//this.searchPage = params['search-page'] || this.pltParams['start'] || 1;
		this.configData = this.getConfigData();
		
	},

	processQuery: function(query){
		if(this.core == "catchword-filter"){
			query = '"' + query + '"';
		}
		
		return query;
	},
	
	// good
	getDataFromServer: function(){
		var currentPorlet = {};
		var obj = this;
		//alert(this.query);
		var params = {'searchFilter':this.searchFilter,'isSelected':this.isSelected,'page':this.page,'rows':this.rows,'articleType':this.articleType,'articleOrderBy':this.articleOrderBy,'queryArr':this.queryArr};
		// for hardcore person search
		/*
		if(this.expertises != null && this.expertises != ""){
			params.expertises = this.expertises;
		}
		// for hardcore similar search
		if(this.profileExcluded != null && this.profileExcluded != ""){
			params.profileExcluded = this.profileExcluded;
		}
		params.query = this.processQuery(this.query);
		var scope = this.scope;
		if (scope == 'all' || scope == undefined)	{
			params.all = 1;
		}
		
		var sbj = SingletonFactory.getInstance(Subject);
		sbj.notifyEvent("BeginSearching");
		*/
		//this.onAjax('ajax', 'get-search-dynamic', params, 'GET', {
		this.onAjax('article', 'get-search-articles', params, 'GET', {
			'onSuccess': function(ret)	{
				try {
					ret = $.parseJSON(ret);
					currentPorlet.start = ret.response.start;
					currentPorlet.numDocs = ret.response.numFound;
					obj.totalPage=Math.ceil(ret.response.numFound/obj.rows);
					//alert("--"+ret.response.numFound+"--");
					currentPorlet.dataResJSON = ret.response.docs;
					currentPorlet.highlightResJSON = ret.highlighting;
					//alert(currentPorlet.dataResJSON);
				} catch (err)	{
					currentPorlet.start = 0;
					currentPorlet.numDocs = 0;
					currentPorlet.dataResJSON = {};
					currentPorlet.highlightResJSON = {};
				}
				obj.numDocs = currentPorlet.numDocs;
				obj.dataResJSON = currentPorlet.dataResJSON;
				obj.highlightResJSON = currentPorlet.highlightResJSON;
				//obj.shuffleResult();
				obj.buildSearchItems();
				//lenh in vao man hinh
				obj.getPortletPlaceholder().paintCanvas(obj.render());
				obj.buildResultNavigation();
				//sbj.notifyEvent("CheckMoreLessContent");
				//obj.buildSearchNavigation();
				//obj.buildSearchHeader();
				
				/*if (currentPorlet.numDocs == 0 || currentPorlet.dataResJSON.length - currentPorlet.start >= currentPorlet.numDocs)	{
					sbj.notifyEvent("NoMoreResult");
				}*/
			}
		});
		
	},
	shuffleResult: function(){
		//watch out for this
		var count = 0;
		var result = Array();
		if(this.isShuffleResult){
			while(count < this.shuffleNo){
				var rand = Math.floor(Math.random()*this.dataResJSON.length);
				var tmp = this.dataResJSON;
				result.push(tmp[rand]);
				this.dataResJSON.splice(rand,1);
				count++;
			}
			this.dataResJSON = result;
		}
	},
	
	// done
	// return json object
	getHighlight: function(profileID, fieldName){
		if(this.highlightResJSON[profileID] == undefined || this.highlightResJSON[profileID][fieldName] == undefined){
			return null;
		}
		return this.highlightResJSON[profileID][fieldName];
	},
	
	// done
	getConfigData: function(){
		try{
			var id = this.name + "-ItemConfig" + this.pageType;
			//alert(id);
			var data = eval("(" + tmpl(id,{}) + ")");			
			//alert(tmpl(id,{}));
			// iterate through json sent from server and build template arcording to view type
			var hashConfig = Array();
			// item order
			for(var key in data){
				hashConfig[key] = data[key];
			}
			
			return hashConfig;
		}
		catch(ex){
		}
		return null;
	},
	
	buildSearchItems: function(){
		var searchItems = Array();
		if((this.numDocs == 0 || this.dataResJSON.length == 0)){
			if(this.configData['notShowNoResult']== undefined || this.configData['notShowNoResult']== false) {
				this.numDocs = 0;
				searchItems.push(tmpl("CommonPortlet-NoResult",{}));
				this.model.children = searchItems;
				return;
			}
		}
		for(var key in this.dataResJSON){
			
			var curRes = this.dataResJSON[key];
					
			// 1 search result
			var objParam = {};
			var uiFields = this.configData['uifields'];
			for(var keyConfig in uiFields){
				for(var field in uiFields[keyConfig]){
					var templateName = uiFields[keyConfig][field];
					// cai templateName dau tien chinh la content-template
					//alert(templateName);	
					objParam[field] = this.buildSearchItemTemplate(templateName, curRes);
					
				}
			}
			var searchItem = $(this.buildSearchItem(objParam));
			var sbj = SingletonFactory.getInstance(Subject);
			sbj.notifyEvent("RequestCheckAnswerContent",searchItem);
			searchItems.push(searchItem);
		}
		this.model.children = searchItems;
	},
	
	// done
	buildSearchItem: function(objParam){
		var templ = this.configData['template'];
		var id = this.name + "-Item"+ templ + "-Main";
		//alert(tmpl(id, objParam));
		return tmpl(id, objParam);
	},
	
	// done
	buildSearchItemTemplate: function(templateName, curRes){
		var objParam = {};
		var templ = this.configData['template'];
		var templateKey = this.name + "-Item" + templ + "-" + templateName;
		//alert(templateKey);
		var fieldMaps = this.configData[templateName].data;
		
		for(var fieldMap in fieldMaps){
			// placeholderName => fieldMap
			var currentFieldMap = fieldMaps[fieldMap]; 
			
			var fieldRes = currentFieldMap.field;
			var type = currentFieldMap.type;
			var templateName = currentFieldMap.template;
			
			var curObjVal = curRes[fieldRes];
			var ret = null;
			switch(type){
				case "multiple":
					// Có nhiệm vụ lấy thông tin kiểu link, có cả id, cả content.
					// Cần truyền vào curObjVal để nó biết là cần duyệt ở cột nào.
					// array của dataJSON
					ret = this.buildSearchItemMultipleHelper(templateName, curRes, curObjVal);
					break;
				case "advanced":
					// có chữ cái cuối cùng == _1; _3...
					ret = this.buildSearchItemAdvancedHelper(templateName, curRes, fieldRes);
					break;
				case "single":
				default:
					// Chỉ lấy thông tin dạng đơn 
					// text value
					ret = curObjVal;
					break;
			}
			objParam[fieldMap] = ret;
			
		}
		//alert(tmpl(templateKey, objParam));
		return tmpl(templateKey, objParam);
	},
	
	// Lấy cái con cấp 2; Chắc chắn chỉ có 2 cấp
	// Cái này chuyên để lấy những thứ như id đi kèm vs cái j đó
	// để lấy link chẳng hạn.
	buildSearchItemMultipleHelper: function(templateName, curRes, curObjVal){
		var ret = $("<div></div>");
		var templ = this.configData['template'];
		var id = this.name + "-Item" + templ + "-" + templateName;
		//alert(templateName);
		for (var pos in curObjVal){
			var fieldMaps = this.configData[templateName].data;
			var objParam = {};
			for(var fieldMap in fieldMaps){
				try {
					// placeholderName => fieldMap
					var fieldRes = fieldMaps[fieldMap].field;
					if(curRes[fieldRes] == undefined){
						continue;
					}
					if(curRes[fieldRes][pos] == undefined){
						objParam[fieldMap] = curRes[fieldRes];
					} else {
						// REMEMBER: At first, it only has this
						objParam[fieldMap] = curRes[fieldRes][pos];
					}
				} catch (e) {
//							console.error(e);
				}
			}
			ret.append(tmpl(id, objParam));
		}
		return ret.html();
	},
	
	buildSearchItemAdvancedHelper: function(templateName, curRes, fieldRes){
		var ret = $("<div></div>");
		var templ = this.configData['template'];
		var id = this.name + "-Item" + templ + "-" + templateName;
		// đầu tiên phải chỉ định trường id là trường nào.
		var idCol = fieldRes + "_id"; 
		// lấy id của trường đó. => quy định hẳn là tên trường + _id
		var curObjVal = curRes[fieldRes];
		for (var pos in curObjVal){
			var curID = curRes[idCol][pos];
			var fieldMaps = this.configData[templateName].data;
			var objParam = {};
			for(var fieldMap in fieldMaps){
				var fieldCheck = fieldMaps[fieldMap].field;
				var curField = (fieldCheck == idCol || fieldCheck == fieldRes) ?  fieldCheck : fieldCheck + "_" + curID;
				objParam[fieldMap] = curRes[curField][pos] == null ? "" : curRes[curField][pos];
			}
			ret.append(tmpl(id, objParam));
		}
		//alert(ret.html());
		return ret.html();
	},
	
	buildSearchNavigation: function(){
		if(this.pltParams["showNavigator"] != undefined && !this.pltParams["showNavigator"]){
			return;
		}
		if(this.configData["showNavigator"] == undefined || this.configData["showNavigator"]){
			var objParam = {};
			objParam.numDocs = this.numDocs;
			objParam.rows = this.rows;
			objParam.searchPage = this.searchPage;
			objParam.dataLength = this.dataResJSON.length;
			objParam.query = this.query;
			objParam.core = this.core;
			objParam.searchController = this.page || "Search";
			var sbj = SingletonFactory.getInstance(Subject);
			sbj.notifyEvent("FetchNavigationLink", objParam);
		}
	},
	
	buildSearchHeader: function(){
		if(this.pltParams["showSearchResult"] != undefined && !this.pltParams["showSearchResult"]){
			return;
		}
		if(this.configData["showSearchResult"] == undefined || this.configData["showSearchResult"]){
			var objParam = {};
			objParam.numDocs = this.numDocs;
			var sbj = SingletonFactory.getInstance(Subject);
			sbj.notifyEvent("FetchSearchResultNumber", objParam);
		}
	},
	
	setHighlightKeywords: function(element){
		element.find("em").addClass("bkp_search_highlight");
	},
		
	// done
	normalizeKeyword:function(keyword){
		var ret = keyword.replace("_indexed","");
		ret = ret.replace("_unindexed","");
		return ret;
	},
	buildResultNavigation:function(){
		//alert("assaasdzxczxcxzc");
		var objParams={};
		objParams.page=this.page;
		objParams.totalPage=this.totalPage;
		//alert(tmpl('HungPDArticleSearchResultDynamicPortlet-SearchMoreButton-template',objParams));
		if(this.totalPage>1){			
		this.requestForEffectiveResource('NavigationLink').html(tmpl('HungPDArticleSearchResultDynamicPortlet-SearchMoreButton-template',objParams));
		} else{
			this.requestForEffectiveResource('NavigationLink').html("");
		}
	},
	// done
	run : function() {
		this.getDataFromServer();
		
		//var obj=this;
		//obj.getPortletPlaceholder().paintCanvas(obj.render());
		//alert("sasasa");
	
	},

	onCheckWriteNewArticleClick:function(eventData){
		var loggedIn = SingletonFactory.getInstance(Application).getSystemProperties().get('user.login');
		var userid=SingletonFactory.getInstance(Application).getSystemProperties().get('user.id');
		//alert(userid);
		//so cua Hung va tami
		if ((loggedIn != 1)){//||(userid!=133&&userid!=217))	{
			var subject = SingletonFactory.getInstance(Subject);
			subject.notifyEvent("NeedLogin", {type: 'WriteNewArticle', event: eventData});
		} else {
			//this.doShowPortlet(eventData);
			window.location="#!page/InsertArticle";
		}
		//alert('goi di');
	},
	//cap nhat lai row
	onArticleGetMoreResultRows: function()	{
		//alert("Kho hieu");
		if (this.rows == undefined)
			this.rows = this.getRequest().getParam('rows') || 10;
		this.rows += 10;
		this.run();
	},
	onNavigateArticleSearchRows:function(eventData){
		if(this.page==1&&eventData.type==-1){
			return;
		}
		if(this.page==this.totalPage&&eventData.type==1){
			return;
		}
		this.page+=eventData.type;
		this.run();
	},
	onReloadPage: function(){
		//alert("reloadPage");
		//alert(this.oldTypeToChange+"--"+this.typeToChange);
		//if((this.typeToChange!=this.oldTypeToChange)&&this.typeToChange==1){
			//this.typeToChange=1;
			//this.typeToChange=1;
			
		//} 
		//alert("reload");
		this.onBegin();		
		this.run();
		
	},
	onArticleTopicChange:function(eventData){
		//alert(eventData.tid);	
	//	this.onReloadPage();		
		//this.run();
		//alert("onArticleChangeTopic");
		//window.location='#!page/ArticleTopic/id/'+eventData.tid;
		var params = this.getRequest().getParams();
		//kiểu cuối cùng owner-filter
		this.searchFilter = this.pltParams['type'] || params['type'] || "notification-filter";
		//var topicID = params['topicID']||null;
		this.isSelected=1;				
		this.queryArr={};
		this.queryArr.topicID=eventData.tid;
		//alert(this.queryArr.topicID);			
		this.run();
		//this.onReloadPage();
		return;
	},
	onArticleChangeType:function(eventData){
		//alert("onArticleChangeType");
		// if(this.typeToChange!=2){
			// this.oldTypeToChange=this.typeToChange;
			// //alert(this.oldTypeToChange);
			// this.typeToChange=2;
		// }
		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent("ArticleSearchResultChangeType", {type:eventData.typeName});
		//this.core = "catchword-filter";//Profile; Knowledge; 
		//this.query = eventData.tid;
		//alert(this.query);
		this.page=1;
		this.articleType=eventData.articleTypeID;			
		this.run();
		//this.run();
		//window.location='#!page/ArticleTopic/id/'+eventData.tid;
		
	},
	onIsSelectedArticleChange:function(eventData){
		//alert("onArticleChangeType");
		// if(this.typeToChange!=2){
			// this.oldTypeToChange=this.typeToChange;
			// //alert(this.oldTypeToChange);
			// this.typeToChange=2;
		// }
		var subject = SingletonFactory.getInstance(Subject);
		
		//this.core = "catchword-filter";//Profile; Knowledge; 
		//this.query = eventData.tid;
		//alert(this.query);
		this.page=1;
		this.isSelected=eventData.isSelected;
		this.model.isSelected=eventData.isSelected;			
		this.queryArr.isSelected=eventData.isSelected;		
		//alert(this.queryArr.isSelected);
			
		this.run();
		subject.notifyEvent("IsSelectedArticleChangeType", {type:eventData.typeName});
		//this.run();
		//window.location='#!page/ArticleTopic/id/'+eventData.tid;
		
	},
	
	onArticleChangeOrder:function(eventData){
		//alert("eventData.tid");
		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent("ArticleSearchResultChangeScope", {scope:eventData.typeName});
		//alert(eventData.typeName);
		//this.articleChangeOrder=eventData.typeName;	
		if(eventData.typeName=="theongaythang"){
			this.articleOrderBy=0;
		} else{
			this.articleOrderBy=1;
		}
		this.page=1;	
		this.run();
		//window.location='#!page/ArticleTopic/id/'+eventData.tid;
		
	},
	onRefreshArticle:function(eventData){
		//this.onBegin();
	//	this.oldTypeToChange=this.typeToChange;
		//this.typeToChange=1;
		//alert("aaa");
		// if(this.typeToChange!=1){
			// this.oldTypeToChange=this.typeToChange;
			// this.typeToChange=1;	
		// }
//		this.onBegin();
		

		var subject = SingletonFactory.getInstance(Subject);
		subject.notifyEvent("ArticleSearchResultChangeType", {type:"tatca"});
		subject.notifyEvent("ArticleSearchResultChangeType", {type:"dadang"});
		subject.notifyEvent("ArticleSearchResultChangeScope", {scope:"theongaythang"});
	},	
	onEnd: function()	{
		this.unregisterObserver();
	}
}).implement(RenderInterface).implement(PortletInterface).implement(ObserverInterface).implement(AjaxInterface);