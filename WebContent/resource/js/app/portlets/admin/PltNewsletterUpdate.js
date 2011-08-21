NewsletterUpdatePortlet = Class.extend(
		{
			init : function() {
				this.name = "NewsletterUpdatePortlet";
				this.model = {};
				this.isLogin = false;
				this.registerObserver();
				this.root = SingletonFactory.getInstance(Application).getSystemProperties().get("host.root");
				this.editor = SingletonFactory.getInstance(Application).getSystemProperties().get("memcached.tinyeditor");
			},
			
			onBegin: function(){
			},
			
			run : function() {
				this.getPortletPlaceholder().paintCanvas(tmpl("NewsletterUpdatePortletView",{}));
				this.initEditor();
			},
			
			checkUserLogin : function() {
				var sbj = SingletonFactory.getInstance(Subject);
				sbj.notifyEvent("NeedLogin", {});
			},

			onLoginSuccess : function(eventData) {
				this.isLogin = true;
			},
			
			onReloadPage: function(){
				this.onBegin();
				this.run();
			},
			
			onSendTestingEmail: function(){
				this.editor = SingletonFactory.getInstance(Application).getSystemProperties().get("memcached.tinyeditor");
				var updates = this.editor['NewsletterUpdatePortlet-TextEditor'].e.body.innerHTML;
				var email =  $("#NewsletterUpdatePortlet-Email").val();
				var curUrl = this.root + "newsletter/updates";
				var obj = this;
				$.ajax({url:curUrl, async:true , data: {'email': email, 'updates': updates}, type: "POST", success: function(ret){
					ret = $.parseJSON(ret);
					ret = ret.result;
					if(ret == undefined || ret == "error"){
						return;
					}
					result = ret;
				}});
			},
			
			onSubmitAll: function(){
				if(!confirm("Bạn có chắc chắn không")){
					return false;
				}
				this.editor = SingletonFactory.getInstance(Application).getSystemProperties().get("memcached.tinyeditor");
				var updates = this.editor['NewsletterUpdatePortlet-TextEditor'].e.body.innerHTML;
				var curUrl = this.root + "newsletter/updates";
				var obj = this;
				$.ajax({url:curUrl, async:true , data: {'updates': updates, 'all':1}, type: "POST", success: function(ret){
					ret = $.parseJSON(ret);
					ret = ret.result;
					if(ret == undefined || ret == "error"){
						return;
					}
					result = ret;
				}});
			},
			
			initEditor: function()	{
				var curID = "NewsletterUpdatePortlet-TextEditor";
				var sbj = SingletonFactory.getInstance(Subject);
				sbj.notifyEvent("TinyEditorInit",{id : curID, content: ""});
			},
			
			onEnd: function()	{
				this.unregisterObserver();
			}
		}).implement(PortletInterface).implement(RenderInterface).implement(ObserverInterface).implement(AjaxInterface);