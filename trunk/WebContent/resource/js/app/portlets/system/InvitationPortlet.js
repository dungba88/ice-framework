GmailInvitation = Class.extend({
	
	init: function()	{
		var obj = this;
		this.scope = "https://www.google.com/m8/feeds";
		this.contactsService = new google.gdata.contacts.ContactsService('BKProfile-GmailContactFeed-1.0');
	},
	
	setCallback: function(callback)	{
		this.callback = callback;
	},
	
	checkLogin: function()	{
		return (google.accounts.user.checkLogin(this.scope));
	},
	
	logMeOut: function(){
		google.accounts.user.logout(function() {}, this.scope);
	},
	
	authenticate: function()	{
//		if (google.accounts.user.checkLogin(this.scope))	{
//			this.onAuthReady();
//			return;
//		}
		var w = 600;
		var h = 450;
		var left = (screen.width/2)-(w/2);
		var top = (screen.height/2)-(h/2);
		var popup = window.open('invite.html', 'authWindow', "status = 0, height = "+h+", width = "+h+", resizable = 0, left = "+left+", top = "+top);
	},
	
	feedContact: function(maximum)	{
		var contactsFeedUri = 'https://www.google.com/m8/feeds/contacts/default/full';
		var query = new google.gdata.contacts.ContactQuery(contactsFeedUri);
		  
		// Set the maximum of the result set to be 10
		query.setMaxResults(maximum);
		
		var obj = this;
		this.contactsService.getContactFeed(query, function(result) {obj.callback(result)}, function() {});
	}
});

InvitationPortlet = Class.extend({
	init: function()	{
		this.name = "InvitationPortlet";
	},
	
	onBegin: function()	{
		this.registerObserver();
		this.currentMaxContacts = 50;
		this.invitationImpl = new GmailInvitation();
		this.invitationImpl.setCallback(function(result) {
			var subject = SingletonFactory.getInstance(Subject);
			subject.notifyEvent('FeedContacts', result);
		});
	},
	
	onSendInviteButtonClick: function(eventData)	{
		var email = eventData.email;
		var origin = eventData.origin.target;
		var plhd = $(origin).parent();
		plhd.html('<img class="invite-ajax-loader" src="static/images/ajax-loader-1.gif">');
		this.onAjax('invitation', 'send-invitation', {'email': email}, 'POST', {
			'onSuccess': function()	{
				$(plhd).html('<strong class="InvitationSent">Đã gửi lời mời</strong>')
			},
			
			'onFailure': function()	{
				$(plhd).html('<div class="errorMsg">Gửi thất bại!</div>');
			}
		});
	},
	
	onFeedContacts: function(result)	{
		var entries = result.feed.entry;
		var contacts = Array();
		var emails = Array();
		for (var i = 0; i < entries.length; i++) {
			var contact = {};
			var contactEntry = entries[i];
			var photoLink = contactEntry.getContactPhotoLink();
			if (photoLink != undefined)	{
				photo = photoLink.getHref();
			}
			else
				photo = "static/images/unknown.png";
			if (contactEntry.getTitle() && contactEntry.getTitle().getText())	{
				contact.name = contactEntry.getTitle().getText();
			} else {
				contact.name = "";
			}
			contact.photo = photo;
			contact.emails = Array();
		    var emailAddresses = contactEntry.getEmailAddresses();
		    if (emailAddresses.length == 0)
		    	continue;
		    var emailAddress = emailAddresses[0].address;
		    if (emailAddress == this.currentEmail)	{
		    	continue;
		    }
		    emails.push(emailAddress);
		    contact.email = emailAddress;
		    contacts.push(contact);
		}
		var existing = Array();
		var pendingArr = Array();
		var obj = this;
		this.onAjax('invitation', 'get-invite-users-from-emails', {'emails': emails}, 'GET', {
			'onSuccess': function(ret)	{
				var exist = ret.existing;
				var pending = ret.pending;
				
				for(var i=0;i<exist.length;i++)	{
					var email = exist[i].email;
					//remove contacts which exists in our system
					for(var j in contacts)	{
						var contact = contacts[j];
						if (contact.email == email)	{
							contacts.splice(j, 1);
							break;
						}
					}
					existing.push(exist[i]);
				}
				
				for(var i=0;i<pending.length;i++)	{
					var email = pending[i].email;
					//remove contacts which exists in our system
					for(var j in contacts)	{
						var contact = contacts[j];
						if (contact.email == email)	{
							contacts.splice(j, 1);
							break;
						}
					}
					pendingArr.push(pending[i]);
				}
				
				obj.currentContacts = contacts;
				obj.requestForEffectiveResource('PendingContactsFeed').html(obj.renderView('PendingContacts', {'contacts': pendingArr}));
				obj.requestForEffectiveResource('ExistingContactsFeed').html(obj.renderView('ExistingContacts', {'contacts': existing}));
				obj.requestForEffectiveResource('ContactsFeed').html(obj.renderView('Contacts', {'contacts': contacts}));
				obj.requestForEffectiveResource('MoreFeed').show();
				var target = obj.requestForEffectiveResource('EmailFilter');
				var subject = SingletonFactory.getInstance(Subject);
				subject.notifyEvent('AttachFocusDetection', {'target': target, 'defaultValue': 'Nhập tên/email để tìm kiếm'});
				
				//filtering function
				$(target).keyup(function()	{
					var val = $(target).val();
					for(var i=0;i<obj.currentContacts.length;i++)	{
						if (obj.currentContacts[i].name.indexOf(val) == -1 && obj.currentContacts[i].email.indexOf(val) == -1)	{
							$('div[contactID="'+i+'"]').hide();
						} else {
							$('div[contactID="'+i+'"]').show();
						}
					}
				});
			}
		});
	},
	
	feed: function()	{
		this.invitationImpl.feedContact(this.currentMaxContacts);
		this.currentMaxContacts += 20;
	},
	
	onFeedMoreContacts: function()	{
		this.feed();
	},
	
	onInviteButtonClicked: function()	{
		this.feed();
	},
	
	onAuthSubAuthenticateButtonClick: function()	{
		this.invitationImpl.authenticate();
	},
	
	run: function()	{
		this.model = {};
		this.getPortletPlaceholder().paintCanvas(this.render());
		
		if (!this.invitationImpl.checkLogin())	{
			this.startInterval(500, function() {
				if (this.invitationImpl.checkLogin())	{
					this.requestForEffectiveResource('AuthenButton').html('Cho phép thành công');
					this.requestForEffectiveResource('AuthenButton').addClass('disable');
					this.requestForEffectiveResource('FeedMe').removeClass('disable');
					this.stopInterval();
				}
			});
			this.requestForEffectiveResource('FeedMe').addClass('disable');
			this.requestForEffectiveResource('AuthStep').html(this.renderView('Authenticate', {}));
		}
		var loggedIn = SingletonFactory.getInstance(Application).getSystemProperties().get('user.login');
		if (loggedIn == 1)	{
			var obj = this;
			this.onAjax('user-ajax', 'get-user-email', {}, 'GET', {
				'onSuccess': function(ret)	{
					obj.currentEmail = ret;
				}
			});
		}
	},
	
	onEnd: function()	{
		this.unregisterObserver();
		this.stopInterval();
	}
}).implement(PortletInterface).implement(RenderInterface).implement(AjaxInterface).implement(ObserverInterface).implement(IntervalTimerInterface);