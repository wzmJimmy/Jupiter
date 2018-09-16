(function() {

	/**
	 * Initialize
	 */

	function init() {
		$('signIn-btn').addEventListener('click', signIn);
	}

	// -----------------------------------
	// Login
	// -----------------------------------

	function signIn() {
		var username = $('username').value;
		var pwd = $('password').value;
		var first_name = $('first_name').value;
		var last_name = $('last_name').value;
		password = md5(username + md5(pwd));
		//console.log(username + "+" + password + "+" + first_name + "," + last_name); //#/ show user-information

		
		// The request parameters
		var url =  '/Jupiter/sign';
		var req = JSON.stringify({
			user_id : username,
			pwd : pwd,
			password : password,
			first_name : first_name,
			last_name : last_name,
		});

		ajax('POST', url, req, function(res) {
			var result = JSON.parse(res);
			//console.log(result); /#/ show signIn-result
			if (result.status === 'OK') {
				$('signIn-error').innerHTML = 'Sign in successfully!';
				window.location.href = "/Jupiter";	
			}else{
				showsignInError(result.message);
			}
		}, function() {},
		true);
	}

	function showsignInError(msg) {
		$('signIn-error').innerHTML = msg;
	}

	function clearsignInError() {
		$('signIn-error').innerHTML = '';
	}


	init();

})();
