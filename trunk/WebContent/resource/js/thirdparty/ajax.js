/**
 * An interface for all ajax-based portlets or plugins
 * Provide the following methods:
 *  - onAjax(controller, action, params, type, callback)
 */
AjaxInterface = InterfaceImplementor.extend({
	implement: function(obj)	{
		var interfacePrivate = function(ret, success, fail)	{
			ret = $.parseJSON(ret);
			var result = ret.data;
			if (ret.status)	{
				if (success != undefined)
					success.call(undefined, result);
			} else if (result == 'exception.PrivilegeException') {
				var subject = SingletonFactory.getInstance(Subject);
				subject.notifyEvent('NotifyError', ret.msg);
			} else {
				if (fail != undefined)
					fail.call(undefined, ret.msg);
			}
		};
		obj.prototype.onAjax = obj.prototype.onAjax || function(controller, action, params, type, callbacks, cache, cacheTime)	{
			if (type == undefined)
				type = 'GET';
			var success = callbacks.onSuccess;
			var fail = callbacks.onFailure;
			var accessDenied = callbacks.onAccessDenied;
			
			var memcacheKey = 'ajax.'+controller+'.'+action;
			for(var k in params)	{
				var v = params[k];
				memcacheKey += '.'+k+'.'+v;
			}

			//try to get from mem cached
			if (type == 'GET' && cache == true)	{
				var memcache = SingletonFactory.getInstance(Memcached);
				var value = memcache.retrieve(memcacheKey);
				if (value != undefined)	{
					var now = new Date();
					var cacheTimestamp = value.timestamp;
					if ((now.getTime() - cacheTimestamp) < cacheTime)	{
						interfacePrivate(value.ret, success, fail);
						return;
					} else {
						memcache.clear(memcacheKey);
					}
				}
			}
			var root = SingletonFactory.getInstance(Application).getSystemProperties().get('host.root');
			var url = root+'/'+controller+'/'+action;
			var subject = SingletonFactory.getInstance(Subject);
			subject.notifyEvent('AjaxBegan');
			$.ajax({
				url: url,
				type: type,
				data: params,
				success: function(ret)	{
					subject.notifyEvent('AjaxFinished');
					if (ret != null)	{
						if (type == 'GET' && cache == true)	{
							//cache the result
							var memcache = SingletonFactory.getInstance(Memcached);
							var now = new Date();
							memcache.store(memcacheKey, {'ret': ret, 'timestamp': now.getTime()});
						}
						interfacePrivate(ret, success, fail);
					}
				},
				error: function(ret, textStatus)	{
					subject.notifyEvent('AjaxFinished');
				},
				statusCode: {
					403: function()	{
						//console.log('access denied at '+url);
						if (accessDenied != undefined)
							accessDenied.call(undefined);
					}
				}
			});
		};
	}
});