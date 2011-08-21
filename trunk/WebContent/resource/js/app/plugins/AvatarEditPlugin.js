AvatarEditPlugin = Class.extend({
	init: function()	{
		this.name = "AvatarEditPlugin";
	},
	
	onReloadPlugin: function()	{
		this.renderAvatar();
	},
	
	onAvatarInputChange: function()	{
		$('#AvatarUploaderContainer').submit();
	},
	
	renderAvatar: function()	{
		var obj = this;
		$('.extension-point[extensionName="avatar"]').each(function(index, value)	{
			if ($(value).find('div[flag="AvatarUploader"]').length>0)
				return;
			var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
			$(value).append(tmpl('AvatarUploader', {'root': root}));
			$('#AvatarUploaderContainer').submit(function() {
				var fr = $('#avatar-upload-frame');
				$(fr).one('load', function() {
					var response = fr.contents().find('body').html();
					$('AvatarUploaderContainer').find('input[type="submit"]').attr('disabled', '');
			    	var ret = $.parseJSON(response);
			    	$(value).find('#AvatarUploaderErrorPlaceholder').html('');
			    	if (ret.result == "error")	{
			    		errors = {};
			    		errors.errors = ret.message; 
			    		$(value).find('#AvatarUploaderErrorPlaceholder').html(tmpl('AvatarUploaderError', errors));
			    	} else {
			    		$('#BriefInfoEditPortlet-Avatar').find('img').remove();
			    		$('#BriefInfoEditPortlet-Avatar').html('<img style="width: 100px" src="avatar/'+ret.result+'" />');
		    			var subject = SingletonFactory.getInstance(Subject);
		    			subject.notifyEvent('NotifyGlobal', 'Bạn đã thay đổi avatar thành công. Nếu ảnh chưa hiện ra, hãy ấn F5 để thấy sự thay đổi');
			    	}
				});
			});
		});
	},
	
	onHtmlUpdated: function()	{
		this.renderAvatar();
	}
}).implement(PluginInterface);