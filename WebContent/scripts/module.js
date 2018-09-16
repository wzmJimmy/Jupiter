/**
 * A helper function that creates a DOM element <tag options...>
 * get an Element or set attribute by key-value pair given by options.
 * @param tag
 * @param options
 * @returns
 */
function $(tag, options) {
	if (!options) {
		return document.getElementById(tag);
	}

	var element = document.createElement(tag);

	for ( var option in options) {
		if (options.hasOwnProperty(option)) {
			element[option] = options[option];
		}
	}

	return element;
}

/* helper function to hide or show element*/
function hideElement(element) {
	element.style.display = 'none';
}

function showElement(element, style) {
	var displayStyle = style ? style : 'block';
	element.style.display = displayStyle;
}

/**
 * AJAX helper
 * 
 * @param method -
 *            GET|POST|PUT|DELETE
 * @param url -
 *            API end point
 * @param callback -
 *            This the successful callback
 * @param errorHandler -
 *            This is the failed callback
 */
function ajax(method, url, data, callback, errorHandler, credentials) {
	var xhr = new XMLHttpRequest();

	xhr.open(method, url, true);

	xhr.onload = function() {
		if (xhr.status === 200) {
			callback(xhr.responseText);
		} else {
			errorHandler();
		}
	};

	// temp use for cross-website visit credential.
	// xhr.withCredentials = credentials;

	xhr.onerror = function() {
		console.error("The request couldn't be completed.");
		errorHandler();
	};

	if (data === null) {
		xhr.send();
	} else {
		xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
		xhr.send(data);
	}
}