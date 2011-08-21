QuestionFollowPlugin = Class.extend({
	init: function()	{
		this.name = "QuestionFollowPlugin";
	},
	
	onUnfollowQuestion: function(eventData)	{
		var id = eventData.id;
		var obj = this;
		this.onAjax('question', 'unfollow-question', {'id': id}, 'POST', {
			'onSuccess': function(ret)	{
				$('#QuestionFollow-Container').html(tmpl('QuestionFollowTmpl-Follow', {id: id}));
				var subject = SingletonFactory.getInstance(Subject);
				subject.notifyEvent('QuestionFollowerChange');
			}
		});
	},
	
	onFollowQuestion: function(eventData)	{
		var id = eventData.id;
		var obj = this;
		this.onAjax('question', 'follow-question', {'id': id}, 'POST', {
			'onSuccess': function(ret)	{
				$('#QuestionFollow-Container').html(tmpl('QuestionFollowTmpl-Unfollow', {id: id}));
				var subject = SingletonFactory.getInstance(Subject);
				subject.notifyEvent('QuestionFollowerChange');
			}
		});
	},
	
	onQuestionBuilt: function(eventData)	{
		var id = eventData.id;
		var userID = eventData.userID;
		var props = SingletonFactory.getInstance(Application).getSystemProperties();
		var loggedin = props.get('user.login');
		if (loggedin == 0)
			return;
		var curUID = props.get('user.id');
		if (curUID == userID)	{
			return;
		}
		
		this.onAjax('question', 'is-user-following', {'id': id}, 'GET', {
			'onSuccess': function(ret)	{
				var follow = '';
				if (ret == true)	{
					follow = 'Unfollow';
				} else {
					follow = 'Follow';
				}
				$('.extension-point[extensionName="QuestionTask"]').each(function()	{
					if ($(this).find('[flag="QuestionFollow"]').length > 0)	{
						return;
					}
					$(this).append(tmpl('QuestionFollowTmpl', {}));
					$('#QuestionFollow-Container').html(tmpl('QuestionFollowTmpl-'+follow, {id: id}));
				});
			}
		});
	}
}).implement(PluginInterface).implement(AjaxInterface);