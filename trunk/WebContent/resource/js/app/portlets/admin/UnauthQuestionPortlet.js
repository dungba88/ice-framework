/**
 * Trunghieu 
 */

UnauthQuestionPortlet = Class.extend({
	init : function()	{
		this.name = "UnauthQuestionPortlet";
		this.expertises = new Array();
		this.selectedExpertises = new Array();		
		this.model = {};
		this.data = new Array();
	},
	
	onBegin: function()	{
		this.registerObserver();
	},
	
	onReloadPage: function(){
		this.getPortletPlaceholder().getCanvas().access().html("");
		this.run();
	},
	
	onApproveQuestion: function(eventData)	{
		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		var obj = this;
		var subject = SingletonFactory.getInstance(Subject);
		var request = new Request("Home",null);
		this.onAjax('question', 'add-auth-question', {'questionId':eventData.id}, 'POST', 
				{'onSuccess': function(ret)	{
					subject.notifyEvent('RequestRoute', request);
				}});
	},
	
	onDeleteUnAuthQuestion: function(eventData)	{
		var i = eventData.id;
		var obj = {}; 
		obj.msg = "Bạn có thực sự muốn xóa dòng có id="+i+" không?";
		obj.func = "generateEvent('ConfirmDeleteUnAuthQuestion',{'id':"+i+"});";
		var str = tmpl(this.name+"-MsgBox",obj);
		$.facebox(str);
	},
	
	onConfirmDeleteUnAuthQuestion: function(eventData)	{
		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		var obj = this;
		var subject = SingletonFactory.getInstance(Subject);
		var request = new Request("Home",null);
		this.onAjax('ajax-admin', 'delete-unauth-question', {'id':eventData.id}, 'POST', 
				{'onSuccess': function(ret)	{
					subject.notifyEvent('RequestRoute', request);
				}});
	},
	
	findQuestionById: function(id)	{
		var i;
		var result;
		for( i=0;i<this.data.length;i++ ){
			if(this.data[i].id == id){
				result = this.data[i];
				break;
			}
		}
		return result;
	},
	
	findLabelById: function(id)	{
		var i;
		for ( i=0; i < this.expertises.length; i++ )	{
			if(this.expertises[i].id == id){
				return this.expertises[i].label;
			}
		}
	},
	
	onEditUnAuthQuestion: function(eventData)	{
		var id = eventData.id;
		var question = {};
		var tmp = this.findQuestionById(id);
		var base = this;
		question.question = tmp.title;
		question.questionContext = tmp.content;
		question.id = id;
		question.tags = tmp.tags;
		this.selectedExpertises = new Array();
		$.facebox(tmpl(this.name+"-QuestionEditor" , question));
		var container = $("#"+this.name+"-ExpertisesContainer" );
		for( var i=0;i<question.tags.length;i++ )	{
			var catchWordID = question.tags[i].catchWordID;
			var lbl = this.findLabelById(catchWordID);
			this.selectedExpertises.push(catchWordID);
			var a = $("<span class='uiToken'>").html(lbl);
			var lbl = $("<label class='btnStl delToken' cid='"+catchWordID+"'>").html("X");
			lbl.bind("click",function(){
				var m=0;
				for( var k = 0; k < base.selectedExpertises.length; k++ ){
					if(base.selectedExpertises[k] == $(this).attr("cid")){
						m = k;
						break;
					}
				}
				base.selectedExpertises.splice(m,1);
				$(this).parent().remove();
			});
			$(a).append(lbl);
			container.append(a);
		}
		var suggestions = new Array();
		for( var i=0; i<base.expertises.length; i++){
			suggestions.push({label:base.expertises[i].label, value: base.expertises[i].id, id: base.expertises[i].id});
		}
		$("#"+base.name+"-Expertises").autocomplete({
			source: suggestions,
			focus:  function(e, ui)	{
				
			},
			select: function(e, ui)	{
				var existed = false;
				var id = ui.item.value;
				for( var i=0;i<base.selectedExpertises.length;i++ ){
					if(base.selectedExpertises[i] == id){
						existed = true;
						break;
					}
				}
				if(!existed){
					base.selectedExpertises.push(id);
					var a = $("<span class='uiToken'>").html(ui.item.label);
					a.append($("<label class='btnStl delToken'>").html("X").bind("click",function(){
						var j=0;
						for( var i=0;i<base.selectedExpertises.length;i++ ){
							if(base.selectedExpertises[i] == id){
								j = i;
								break;
							}
						}
						base.selectedExpertises.splice(j,1);
						$(this).parent().remove();
					}));
					container.append(a);
				}
			},
			result: function(e, ui)	{
			}
		});
	},
	
	onConfirmEditUnAuthQuestion: function(eventData)	{
		var title = $("#"+this.name+"-Question").val();
		var content = $("#"+this.name+"-Context").val();
		var tags = this.selectedExpertises;
		var dataToSend = 
		{"data": 
			[
				{
					"id":eventData.id,
					"data": 
						{
							'title': title, 
							'content': content
						},
					"tags": tags
				}
			]
		};
		
		this.onAjax("ajax-admin","edit-unauth-question",dataToSend,"POST",
				{"onSuccess":function(ret){
					var subject = SingletonFactory.getInstance(Subject);
					var request = new Request("Home",null);
					$.facebox.close();
					subject.notifyEvent("RequestRoute",request);
				}});
	},
	
	loadExpertises: function()	{
		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		var base = this;
		this.onAjax('catchword', 'autocomplete-all-catchwords', {}, 'POST', 
				{'onSuccess': function(ret)	{
					base.expertises = ret;
					base.drawScreen();
				}});
	},
	
	drawScreen: function()	{
		var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
		var obj = this;
		this.onAjax('ajax-admin', 'list-all-unauth-question', {}, 'POST', 
				{'onSuccess': function(ret)	{
					var ret;
					var objToSend = {};
					var i;
					txt = "";
					obj.data = new Array();
					for( i=0; i<ret.length; i++ ){
						obj.data[i] = new Array();
						obj.data[i].id = ret[i].id;
						obj.data[i].title = ret[i].title; 
						obj.data[i].content = ret[i].content;
						obj.data[i].tags = ret[i].tags;
					} 
					objToSend.ret = ret;
					objToSend.expertises = obj.expertises;
					obj.model = objToSend;
					obj.getPortletPlaceholder().drawToCanvas(obj.render());
				}});
	},
	
	run: function(){
		this.loadExpertises();
	}
}).implement(RenderInterface).implement(PortletInterface).implement(ObserverInterface).implement(AjaxInterface);