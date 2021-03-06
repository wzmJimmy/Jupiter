(function() {

	/**
	 * Variables
	 */
	var user_id = '1111';
	var user_fullname = 'John';
	var lng = -122.08;
	var lat = 37.38;

	/**
	 * Initialize
	 */

	function init() {
		$('login-btn').addEventListener('click', login);
		$('nearby-btn').addEventListener('click', loadNearbyItems);
		$('fav-btn').addEventListener('click', loadFavoriteItems);
		$('recommend-btn').addEventListener('click', loadRecommendedItems);

		validateSession();
		var welcomeMsg = $('welcome-msg');
		welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;
	}

	/**
	 * Session
	 */
	function validateSession() {
		// The request parameters
		var url = './login';
		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Validating session...');

		// make AJAX call
		ajax('GET', url, req, function(res) {
			var result = JSON.parse(res);
			if (result.status === 'OK') {
				onSessionValid(result);
			}
		}, onSessionInvalid, true);
//		onSessionValid({user_id:"1111",name:"John"});
	}

	function onSessionValid(result) {
		user_id = result.user_id;
		user_fullname = result.name;

		var loginForm = $('login-form');
		var itemNav = $('item-nav');
		var itemList = $('item-list');
		var avatar = $('avatar');
		var welcomeMsg = $('welcome-msg');
		var logoutBtn = $('logout-link');

		welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;

		showElement(itemNav);
		showElement(itemList);
		showElement(avatar);
		showElement(welcomeMsg);
		showElement(logoutBtn, 'inline-block');
		hideElement(loginForm);

		initGeoLocation();
	}

	function onSessionInvalid() {
		var loginForm = $('login-form');
		var itemNav = $('item-nav');
		var itemList = $('item-list');
		var avatar = $('avatar');
		var welcomeMsg = $('welcome-msg');
		var logoutBtn = $('logout-link');

		hideElement(itemNav);
		hideElement(itemList);
		hideElement(avatar);
		hideElement(welcomeMsg);
		hideElement(logoutBtn);
		showElement(loginForm);
	}
	
	// -----------------------------------
	// Geolocation
	// -----------------------------------
	
	function initGeoLocation() {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(onPositionUpdated,
					onLoadPositionFailed, {maximumAge : 60000}
			);
			showLoadingMessage('Retrieving your location...');
		} else {
			onLoadPositionFailed();
		}
	}

	function onPositionUpdated(position) {
		lat = position.coords.latitude;
		lng = position.coords.longitude;
		// console.log("nav.loca:"+lat+","+lng); /#/ show geo-information.

		loadNearbyItems();
	}

	function onLoadPositionFailed() {
		console.warn('navigator.geolocation is not available');
		getLocationFromIP();
	}

	function getLocationFromIP() {
		// Get location from http://ipinfo.io/json
		var url = 'http://ipinfo.io/json'
		var req = null;
		ajax('GET', url, req, function(res) {
			var result = JSON.parse(res);
			if ('loc' in result) {
				//console.log("ip.loca:"+loc); /#/ show geo-information for default.
				var loc = result.loc.split(',');
				lat = loc[0];
				lng = loc[1];
			} else {
				console.warn('Getting location by IP failed.');
			}
			loadNearbyItems();
		});
	}

	// -----------------------------------
	// Login
	// -----------------------------------

	function login() {
		var username = $('username').value;
		var password = $('password').value;
		password = md5(username + md5(password));
		//console.log(username + " " + password); //#/ show user-information

		// The request parameters
		var url =  './login';
		var req = JSON.stringify({
			user_id : username,
			password : password,
		});

		ajax('POST', url, req, function(res) {
			var result = JSON.parse(res);
			//console.log(result); /#/ show login-result
			if (result.status === 'OK') {
				onSessionValid(result);
			}
		}, function() {
			showLoginError();
		},
		true);
	}

	function showLoginError() {
		$('login-error').innerHTML = 'Invalid username or password';
	}

	function clearLoginError() {
		$('login-error').innerHTML = '';
	}

	// -----------------------------------
	// Helper Functions
	// -----------------------------------

	/**
	 * A function that makes a navigation button active
	 * 
	 * @param btnId -
	 *            The id of the navigation button
	 */
	function activeBtn(btnId) {
		var btns = document.getElementsByClassName('main-nav-btn');

		// deactivate all navigation buttons
		for (var i = 0; i < btns.length; i++) {
			btns[i].className = btns[i].className.replace(/\bactive\b/, '');
		}

		// active the one that has id = btnId
		var btn = document.getElementById(btnId);
		btn.className += ' active';
	}
	
	/**
	 * Functions for showing message of loading items for main sections.
	 */

	function showLoadingMessage(msg) {
		var itemList = document.getElementById('item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> '
				+ msg + '</p>';
	}

	function showWarningMessage(msg) {
		var itemList = document.getElementById('item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> '
				+ msg + '</p>';
	}

	function showErrorMessage(msg) {
		var itemList = document.getElementById('item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> '
				+ msg + '</p>';
	}

	// -----------------------------------
	// Helper Functions
	// -----------------------------------
	/**
	 * API #1 Load the nearby items API end point: [GET]
	 * /Jupiter/search?user_id=1111&lat=37.38&lon=-122.08
	 */
	function loadNearbyItems() {
		console.log('loadNearbyItems'); // /#/ sign for calling this API
		activeBtn('nearby-btn');

		// The request parameters
		var url = './search';
		var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Loading nearby items...');

		// make AJAX call
		ajax('GET', url + '?' + params, req,
		// successful callback
		function(res) {
			var items = JSON.parse(res);
			if (!items || items.length === 0) {
				showWarningMessage('No nearby item.');
			} else {
				listItems(items);
			}
		},
		// failed callback
		function() {
			showErrorMessage('Cannot load nearby items.');
		});
	}

	  /**
     * API #2 Load favorite (or visited) items API end point: [GET]
     * /Jupiter/history?user_id=1111
     */
    function loadFavoriteItems() {
    	console.log('loadFavoriteItems'); // /#/ sign for calling this API
        activeBtn('fav-btn');
        
        // The request parameters
        var url = './history';
        var params = 'user_id=' + user_id;
        var req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Loading favorite items...');

        // make AJAX call
        ajax('GET', url + '?' + params, req, function(res) {
        	console.log(res);
            var items = JSON.parse(res);
            if (!items || items.length === 0) {
                showWarningMessage('No favorite item.');
            } else {
                listItems(items);
            }
        }, function() {
            showErrorMessage('Cannot load favorite items.');
        });
    }

    /**
     * API #3 Load recommended items API end point: [GET]
     * /Jupiter/recommendation?user_id=11111&lat=37.38&lon=-122.08
     */
    function loadRecommendedItems() {
    	console.log('loadRecommendedItems'); // /#/ sign for calling this API
        activeBtn('recommend-btn');

        // The request parameters
        var url = './recommendation';
        var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;

        var req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Loading recommended items...');

        // make AJAX call
        ajax(
            'GET',
            url + '?' + params,
            req,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                if (!items || items.length === 0) {
                    showWarningMessage('No recommended item. Make sure you have favorites.');
                } else {
                    listItems(items);
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot load recommended items.');
            });
    }

    /**
     * API #4 Toggle favorite (or visited) items
     * @param item_id - The item business id
     * API end point: [POST]/[DELETE]
     * /Jupiter/history request json data: {
     * user_id: 1111, 
     * visited: [a_list_of_business_ids]
     *  }
     */
    function changeFavoriteItem(item_id) {
		console.log('loadRecommendedItems'); // /#/ sign for calling this API
		// Check whether this item has been visited or not
		var li = $('item-' + item_id);
		var favIcon = $('fav-icon-' + item_id);
		var favorite = li.dataset.favorite !== 'true';

		// The request parameters
		var url = './history';
		var req = JSON.stringify({
			user_id : user_id,
			favorite : [ item_id ]
		});
		var method = favorite ? 'POST' : 'DELETE';

		ajax(method, url, req, function(res) {
			console.log(res);
			var result = JSON.parse(res);
			if (result.result === 'SUCCESS') {
				li.dataset.favorite = favorite;
				favIcon.className = favorite ? 'fa fa-heart' : 'fa fa-heart-o';
			}
		});
	}

	//
	// -------------------------------------
	// Create item list
	// -------------------------------------

	/**
	 * List items
	 * 
	 * @param items - An array of item JSON objects
	 */
	function listItems(items) {
		// Clear the current results
		var itemList = document.getElementById('item-list');
		itemList.innerHTML = '';

		for (var i = 0; i < items.length; i++) {
			addItem(itemList, items[i]);
		}
	}

	/**
	 * Add item to the list
	 * 
	 * @param itemList - The <ul id="item-list"> tag
	 * @param item - The item data (JSON object)
	 */
	function addItem(itemList, item) {
		var item_id = item.item_id;

		// create the <li> tag and specify the id and class attributes,
		// then set the data attribute.
		var li = $('li', {
			id : 'item-' + item_id,
			className : 'item'
		});
		li.dataset.item_id = item_id;
		li.dataset.favorite = item.favorite;

		// item image
		if (item.image_url) { li.appendChild($('img', {src : item.image_url}));}
		else {li.appendChild($('img',
				{src :'https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png'}));}
		
		
		// section-1
		var section = $('div', {});
		// title
		var title = $('a', {
			href : item.url,
			target : '_blank',
			className : 'item-name'
		});
		title.innerHTML = item.name;
		section.appendChild(title);
		// category
		var category = $('p', {
			className : 'item-category',
			innerHTML :'Category: ' + item.categories.join(', ')}
		);
		section.appendChild(category);
		// stars - depreciated by ticketmaster. || float rate is showing by nearest .5*.
		var stars = $('div', {className : 'stars'});
		for (var i = 0; i < item.rating; i++) {
			var star = $('i', {className : 'fa fa-star'});
			stars.appendChild(star);
		}
		if (('' + item.rating).match(/\.5$/)) {
			stars.appendChild($('i', {className : 'fa fa-star-half-o'}));
		}
		section.appendChild(stars);
		li.appendChild(section);

		
		// address
		var address = $('p', {className : 'item-address'});
		// /.../ g means replace every match.
		address.innerHTML = item.address.replace(/\n/g, '<br/>').replace(/\"/g,'');
		li.appendChild(address);

		// favorite link
		var favLink = $('p', {className : 'fav-link'});
		favLink.onclick = function() {changeFavoriteItem(item_id);};
		favLink.appendChild($('i', {
			id : 'fav-icon-' + item_id,
			className : item.favorite ? 'fa fa-heart' : 'fa fa-heart-o'
		}));
		li.appendChild(favLink);

		itemList.appendChild(li);
	}

	init();

})();
