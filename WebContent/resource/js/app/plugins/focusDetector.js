FocusDetectorPlugin = Class.extend({
	init: function()	{
		this.name = "FocusDetectorPlugin";
	},
	
	onAttachFocusDetection: function(eventData)	{
		var target = eventData.target;
		var defaultValue = eventData.defaultValue;
		if ($(target).val() == '' || $(target).hasClass('joo-non-focus'))	{
			$(target).val(defaultValue);
			$(target).addClass('joo-non-focus');
		}
		$(target).unbind('blur');
		$(target).unbind('focus');
		$(target).blur(function() {
			if ($(target).val() == '')	{
				$(target).val(defaultValue);
				$(target).addClass('joo-non-focus');
			}
		});
		$(target).focus(function() {
			if ($(target).hasClass('joo-non-focus'))	{
				$(target).val('');
				$(target).removeClass('joo-non-focus');
			}
		});
	}
}).implement(PluginInterface);