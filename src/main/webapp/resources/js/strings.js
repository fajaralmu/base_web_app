function isUpperCase(_char) {
	var str = "";
	str += (_char);
	return str == (str.toUpperCase());
}

function extractCamelCase(camelCased) {

	var result = camelCased[0].toUpperCase();

	for (let i = 1; i < camelCased.length; i++) {
		const _char = camelCased[i];
		if (isUpperCase(_char)) {
			result += (" ");
		}
		result += (_char);
	}

	return result;
}

function h1(text, small) {
	return createHtmlTag({
		tagName : 'h1',
		innerHTML : text,
		ch1 : {
			tagName : small ? 'small' : 'span',
			innerHTML : small
		}
	})
}

function h2(text, small) {
	return createHtmlTag({
		tagName : 'h2',
		innerHTML : text,
		ch1 : {
			tagName : small ? 'small' : 'span',
			innerHTML : small
		}
	})
}

function h3(text, small) {
	return createHtmlTag({
		tagName : 'h3',
		innerHTML : text,
		ch1 : {
			tagName : small ? 'small' : 'span',
			innerHTML : small
		}
	})
}

function h4(text, small) {
	return createHtmlTag({
		tagName : 'h4',
		innerHTML : text,
		ch1 : {
			tagName : small ? 'small' : 'span',
			innerHTML : small
		}
	})
}

function h5(text, small) {
	return createHtmlTag({
		tagName : 'h5',
		innerHTML : text,
		ch1 : {
			tagName : small ? 'small' : 'span',
			innerHTML : small
		}
	})
}