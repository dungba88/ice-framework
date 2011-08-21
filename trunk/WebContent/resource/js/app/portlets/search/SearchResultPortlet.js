SearchResultPortlet = Class.extend({
	init : function() {
		this.name = "SearchResultPortlet";
		this.model = {};
		this.pageType = "";//Profile; Knowledge; 
		this.query = "";
		this.configData = null;
		this.pltParams = null;
		this.dataResJSON = null;
		this.highlightResJSON = null;
		this.numDocs = null;
		this.start = null;
		this.rows = null;
		this.root = SingletonFactory.getInstance(Application).getSystemProperties().get("host.root");
		this.searchCore = "";
		this.isShuffleResult = false;
	},
	
	onBegin: function()	{
		this.registerObserver();
		this.prepareInitialConfig();
		this.prepareData();
	},
	
	prepareInitialConfig: function()	{
		this.pltParams = this.getInitParameters();
		var params = this.getRequest().getParams();
		this.core = this.pltParams['type'] || params['type'] || "notification-filter";
		this.page = this.pltParams['page'] || params['page'];
		this.isShuffleResult = this.pltParams['shuffle'] || false;
		this.rows = this.pltParams['rows'] || params['rows'] || 10;
		this.shuffleNo = this.pltParams['shuffleNo'] || this.rows;
	},
	
	// done
	prepareData: function(){
		var request = this.getRequest();
		var params = request.getParams();
		this.query = params['query'] || "*:*";
		this.expertises = params['expertises'];
		this.profileExcluded = params['profileExcluded'];
		
		// fixed
		switch(this.core){
			case "person-search" :
			case "person-best" :
			case "person-hardcore" :
			case "person-similar" :
			case "person-recommendation":
				this.pageType = "Person";
				break;
			case "question-flagged":
				this.query = this.getRequest().getParam('id', SingletonFactory.getInstance(Application).getSystemProperties().get("user.id"));
				this.pageType = "KnowledgeFlagged";
				break;
			case "question-search" :
			case "question-latest" :
			case "notification-filter" :
			case "question-best" :
			case "question-open" :
				this.pageType = "Notification";
				break;
			case "catchword-filter" :
			case "question-asked" :
			case "question-asking" :
			case "answer" :
				this.pageType = "Notification";
				this.query = this.getRequest().getParam('id', this.query);
				break;
			default:
				this.pageType = "Knowledge";
				break;
		}
		
		this.searchPage = params['search-page'] || this.pltParams['start'] || 1;
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
		var params = {'search-page': this.searchPage, 'rows': this.rows, 'type': this.core.toLowerCase()};
		// for hardcore person search
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
		
		this.onAjax('ajax', 'get-search-dynamic', params, 'GET', {
			'onSuccess': function(ret)	{
				try {
					ret = $.parseJSON(ret);
					currentPorlet.start = ret.response.start;
					currentPorlet.numDocs = ret.response.numFound;
					currentPorlet.dataResJSON = ret.response.docs;
					currentPorlet.highlightResJSON = ret.highlighting;
				} catch (err)	{
					currentPorlet.start = 0;
					currentPorlet.numDocs = 0;
					currentPorlet.dataResJSON = {};
					currentPorlet.highlightResJSON = {};
				}

				obj.numDocs = currentPorlet.numDocs;
				obj.dataResJSON = currentPorlet.dataResJSON;
				obj.highlightResJSON = currentPorlet.highlightResJSON;
				obj.shuffleResult();
				obj.buildSearchItems();
				obj.getPortletPlaceholder().paintCanvas(obj.render());
				sbj.notifyEvent("CheckMoreLessContent");
				obj.buildSearchNavigation();
				obj.buildSearchHeader();
				if (currentPorlet.numDocs == 0 || currentPorlet.dataResJSON.length - currentPorlet.start >= currentPorlet.numDocs)	{
					sbj.notifyEvent("NoMoreResult");
				}
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
			var data = eval("(" + tmpl(id,{}) + ")");
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
					// get loop
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
		return tmpl(id, objParam);
	},
	
	// done
	buildSearchItemTemplate: function(templateName, curRes){
		var objParam = {};
		var templ = this.configData['template'];
		var templateKey = this.name + "-Item" + templ + "-" + templateName;
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
		return tmpl(templateKey, objParam);
	},
	
	// Lấy cái con cấp 2; Chắc chắn chỉ có 2 cấp
	// Cái này chuyên để lấy những thứ như id đi kèm vs cái j đó
	// để lấy link chẳng hạn.
	buildSearchItemMultipleHelper: function(templateName, curRes, curObjVal){
		var ret = $("<div></div>");
		var templ = this.configData['template'];
		var id = this.name + "-Item" + templ + "-" + templateName;
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
	
	// done
	run : function() {
		this.getDataFromServer();
	},
	
	onReloadPage: function(){
		this.onBegin();
		this.run();
	},
	
	onGetMoreResultRows: function()	{
		if (this.rows == undefined)
			this.rows = this.getRequest().getParam('rows') || 10;
		this.rows += 10;
		this.run();
	},
	
	onSearchResultChangeType: function(eventData)	{
		this.core = eventData.type || this.core || "notification-filter";
		this.page = eventData.page || this.page || 0;
		this.isShuffleResult = eventData.shuffle || this.isShuffleResult || false;
		this.rows = eventData.rows || this.rows || 10;
		this.shuffleNo = eventData.shuffleNo || this.rows;
		
		this.prepareData();
		this.run();
	},
	
	onSearchResultChangeScope: function(eventData)	{
		this.scope = eventData.scope || this.scope || "all";
		this.page = eventData.page || this.page || 0;
		this.isShuffleResult = eventData.shuffle || this.isShuffleResult || false;
		this.rows = eventData.rows || this.rows || 10;
		this.shuffleNo = eventData.shuffleNo || this.rows;
		
		this.prepareData();
		this.run();
	},
	
	onEnd: function()	{
		this.unregisterObserver();
	}
}).implement(RenderInterface).implement(PortletInterface).implement(ObserverInterface).implement(AjaxInterface);