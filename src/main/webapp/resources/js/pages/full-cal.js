const DAY_NAMES = [ "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu",
		"Ahad", ];
const MONTHS = [ {
	name : "Januari",
	day : 31
}, {
	name : "Februari",
	day : 28
}, {
	name : "Maret",
	day : 31
}, {
	name : "April",
	day : 30
}, {
	name : "Mei",
	day : 31
}, {
	name : "Juni",
	day : 30
}, {
	name : "Juli",
	day : 31
}, {
	name : "Agustus",
	day : 31
}, {
	name : "September",
	day : 30
}, {
	name : "Oktober",
	day : 31
}, {
	name : "November",
	day : 30
}, {
	name : "Desember",
	day : 31
}, ];
let MONTH_NOW = 7; // 0;
let YEAR_NOW = 1945;
let RUNNING_MONTH = MONTH_NOW;
let RUNNING_YEAR = YEAR_NOW;

// MUST BE SET
var detailFunc, addFunc;
var fillDateItem, loadMonth;

let begin = {
	week : 1,
	day : 3,
	dayCount : 31,
	info : ""
};
let begin_old = {
	week : 0,
	day : 0,
	dayCount : 0,
	info : ""
};

var calendarTable = document.getElementById("calendarTable");
var calendarInput = document.getElementById("cal-input-fields");

const input_month = document.createElement("select"); // input_month");
const input_year = document.createElement("input"); // "input_year");
const date_info = document.createElement("input"); // "date-info");

var filterDayId, filterMonthId, filterYearId;

function checkRequiredElement() {
	if (!calendarTable) {
		calendarTable = document.getElementById("calendarTable");
	}
	calendarTable.className = "table";

	if (!calendarInput) {
		calendarInput = document.getElementById("cal-input-fields");
	}

	calendarInput.style.tableLayout = "fixed";
	calendarInput.style.textAlign = "center";
	calendarInput.className = "table table-nonfluid";

}

function loadCalendar() {
	checkRequiredElement();
	createTable();
	begin_old = begin;
	begin = fillDay(MONTH_NOW, true, begin);
	fillInputMonth();
	initInputValues();
	setCalendar();
}

function initInputValues() {
	input_month.value = new Date().getMonth() + 1;
	input_year.value = new Date().getFullYear();
}

function fillInputMonth() {
	input_month.innerHTML = "";
	for (let i = 0; i < MONTHS.length; i++) {
		// console.log("option ", i, input_month);
		const opt = createHtml("option", MONTHS[i].name);
		opt.value = i + 1;
		input_month.appendChild(opt);
	}
}

function createInputFields() {
	calendarInput.innerHTML = "";

	const inputRow = createHtml("tr");

	input_month.className = "form-control";
	input_month.id = "input_month";

	input_year.className = "form-control";
	input_year.id = "input_year";

	const seacrhBtn = createHtml("button", "Go");
	seacrhBtn.className = "btn btn-default";
	seacrhBtn.onclick = function(e) {
		setCalendar();
	};

	inputRow.appendChild(wrapTd(input_month));
	inputRow.appendChild(wrapTd(input_year));
	inputRow.appendChild(wrapTd(seacrhBtn));

	const navRow = createHtml("tr");

	date_info.id = "date-info";
	date_info.setAttribute("readonly", "true");

	const prevBtn = createHtml("button", "&#10096;");
	prevBtn.className = "btn btn-default";
	prevBtn.onclick = function(e) {
		doPrevMonth(true);
	};

	const nextBtn = createHtml("button", "&#10097;");
	nextBtn.className = "btn btn-default";
	nextBtn.onclick = function(e) {
		doNextMonth(true);
	};
	navRow.appendChild(wrapTd(prevBtn));
	navRow.appendChild(wrapTd(date_info));
	navRow.appendChild(wrapTd(nextBtn));

	calendarInput.appendChild(inputRow);
	calendarInput.appendChild(navRow);
}

function wrapTd(child) {
	return wrapHtml("td", child);
}

function wrapHtml(tag, child) {
	const el = createHtml(tag);
	el.appendChild(child);
	return el;
}

function createDateCell(d, week) {
	const col = document.createElement("td");
	col.setAttribute("class", "date_element");
	col.setAttribute("day", d);
	col.setAttribute("week", week);
	col.style.wordWrap = "normal";
	return col;
}

function createDayNameHeaders() {
	calendarTable.innerHTML = "";
	const tHead = createHtml("thead");
	const tr = createHtml("tr");
	for (let i = 0; i < DAY_NAMES.length; i++) {
		const th = createHtml("th", DAY_NAMES[i]);
		tr.appendChild(th);
	}
	tHead.appendChild(tr);
	calendarTable.appendChild(tHead);
}

function createTable() {
	createInputFields();
	createDayNameHeaders();
	const tBody = createHtml("tbody");

	for (let rowNum = 1; rowNum <= 6; rowNum++) {
		const tr = createHtml("tr");
		for (let i = 1; i <= 7; i++) {
			const col = createDateCell(+i, +rowNum);
			tr.appendChild(col);
		}
		tBody.appendChild(tr);
	}
	calendarTable.className = "table table-bordered";
	calendarTable.style.tableLayout = "fixed";
	calendarTable.appendChild(tBody);
}

function setCalendar() {
	// loading();
	// cariAsync();
	doSetCalendar();
}

function doSetCalendar() {
	console.debug("==start==");

	RUNNING_MONTH = parseInt(input_month.value) - 1;
	RUNNING_YEAR = input_year.value;
	let diff_year = +Math.abs(RUNNING_YEAR - YEAR_NOW);

	let monthCount = 0;
	if (diff_year > 0) {
		let dummy;
		if (diff_year > 1) {
			dummy = (diff_year - 1) * 12;
		} else {
			dummy = 0;
		}
		monthCount = 11 - MONTH_NOW + dummy + RUNNING_MONTH;
	} else {
		monthCount = RUNNING_MONTH - MONTH_NOW;
	}

	let less = false;
	if (RUNNING_YEAR > YEAR_NOW) {
		less = false;
	} else if (RUNNING_YEAR < YEAR_NOW) {
		less = true;
	} else {
		if (RUNNING_MONTH > MONTH_NOW) {
			less = false;
		} else {
			less = true;
		}
	}
	monthCount = Math.abs(monthCount);

	let current_month = MONTH_NOW;
	let endMonth = monthCount + MONTH_NOW;

	if (monthCount <= 0) {
		return;
	}

	if (!less) {
		// //console.log("month now",MONTH_NOW,"diff_year",monthCount,"to",to);
		for (let m = MONTH_NOW + 1; m <= endMonth + 1; m++) {
			if (current_month > 11) {
				current_month = 0;
			}
			MONTH_NOW = current_month;
			let end = nextMonth();
			if (end) {
				break;
			}
			// //console.log("month",current_month,"RUNNING_YEAR",YEAR_NOW);
			current_month++;
		}
	} else if (less) {
		let dummy;
		if (diff_year > 1) {
			dummy = (diff_year - 1) * 12;
		} else {
			dummy = 0;
		}

		let pastMonthCount = MONTH_NOW + dummy + (11 - RUNNING_MONTH);
		endMonth = pastMonthCount + MONTH_NOW;
		// console.log("month now", MONTH_NOW, "diff_year", monthCount, "from",
		// to);
		let begin_month = MONTH_NOW;

		for (let b = endMonth + 1; b >= begin_month + 1; b--, current_month--) {
			if (current_month < 0) {
				current_month = 11;
			}
			MONTH_NOW = current_month;
			let end = prevMonth();
			if (end) {
				break;
			}
		}
	}

	fillInfo();
	console.debug("==end==");
}

function fillInfo() {
	date_info.value = MONTHS[MONTH_NOW].name + " " + YEAR_NOW;
}

function clearDateFilter() {
	console.info("Clear");
	detail(null, null, null);
}

function detail(d, m, y) {
	console.log("DETAIL", d, m, y);
	if (this.detailFunc) {
		this.detailFunc(d, m, y);
	}
}

function prevMonth() {
	return doPrevMonth(false);
}

function doPrevMonth(prev) {
	MONTH_NOW--;
	if (prev) {
		RUNNING_MONTH--;
	}
	if (MONTH_NOW < 0) {
		MONTH_NOW = 11;
		YEAR_NOW--;
		if (prev) {
			RUNNING_MONTH = 11;
			RUNNING_YEAR--;
		}
	}
	let begin_prev = findBeginning(begin_old, begin_old.dayCount);

	begin_old = copyMonthProp(begin_prev);
	let switch_ = fillDay(MONTH_NOW, false, begin_prev);
	begin = copyMonthProp(switch_);
	return switch_.info == "NOW";
}

function nextMonth() {
	return doNextMonth(false);
}

function doNextMonth(next) {
	MONTH_NOW++;
	if (next) {
		RUNNING_MONTH++;
	}
	if (MONTH_NOW > 11) {
		MONTH_NOW = 0;
		YEAR_NOW++;
		if (next) {
			RUNNING_MONTH = 0;
			RUNNING_YEAR++;
		}
	}

	let switch_ = fillDay(MONTH_NOW, true, begin);
	begin_old = copyMonthProp(begin);
	begin = copyMonthProp(switch_);

	return switch_.info == "NOW";
}

function copyMonthProp(monthProp) {
	return {
		week : monthProp.week,
		day : monthProp.day,
		dayCount : monthProp.dayCount,
	};
}

function createMonthProp(week, day, dayCount) {
	return {
		week : week,
		day : day,
		dayCount : dayCount,
	};
}

function findBeginning(begin_old_, totalday) {
	let M = MONTH_NOW - 1;
	if (M < 0) {
		M = 11;
	}
	let day = begin_old_.day;
	let week = 6;
	let begin_prev_ = createMonthProp(0, 0, MONTHS[M].day);

	for (let D = totalday; D >= 0; D--, day--) {
		if (day <= 0) {
			day = 7;
			week--;
		}
	}
	begin_prev_.week = week;
	begin_prev_.day = day + 1;
	return begin_prev_;
}

function matchToday(d, m, y) {
	const date = new Date();
	return (date.getDate() == d && date.getMonth() == m && date.getYear() + 1900 == y);
}

function createDateString(d, m, y) {
	return addZero(d, 10).concat("-").concat(addZero(m, 10)).concat("-")
			.concat(y);
}

function checkIfToday(dateElem, day) {
	let bgColor;
	if (matchToday(day, MONTH_NOW, YEAR_NOW)) {
		bgColor = "yellow";
		console.debug("Day NOW", day);
	} else {
		bgColor = "white";
		console.debug("Day NOT NOW", day);
	}
	dateElem.style.backgroundColor = bgColor;
}

function getDateElem(day, week) {
	let dates = document.getElementsByClassName("date_element");

	for (let i = 0; i < dates.length; i++) {
		let cek = dates[i].getAttribute("day") == day;
		let cek2 = dates[i].getAttribute("week") == week;

		if (cek && cek2) {
			return dates[i];
		}
	}
	return null;
}

function setElementByAttr(d, w, day) {
	const dateElem = getDateElem(d, w);
	const id = "date-" + day + "-" + (MONTH_NOW + 1);
	if (dateElem) {
		if(document.getElementById(id)){
			document.getElementById(id).id= "temp-"+id;
		}
		dateElem.innerHTML = "";
		dateElem.id = id;

		checkIfToday(dateElem, day);
		fillDateElem(dateElem, day, +MONTH_NOW + 1, YEAR_NOW);
	}

}

function fillDateElem(dateElem, d, m, y) {
	const dateStr = createDateString(d, m, y);
	const addBtn = createAddBtn(d, m, y);
	const detailBtn = createDetailBtn(d, m, y);
	const span = createHtml("span", "<b>"+d+"</b>");
	const additional = createHtml("div", null);

	if (this.fillDateItem) {
		const elem = this.fillDateItem(d, m, y);
		if (elem) {
			additional.appendChild(elem);
		}

	}

	dateElem.appendChild(addBtn);
	dateElem.appendChild(detailBtn);
	dateElem.appendChild(span);
	dateElem.appendChild(additional);
}

function createHtml(tag, html) {
	const e = document.createElement(tag);
	if (html)
		e.innerHTML = html;
	return e;
}

function createAddBtn(d, m, y) {
	const addBtn = createHtml("code", "+");
	addBtn.className = "btn btn-default btn-sm";
	addBtn.style.cssFloat = "right";
	addBtn.style.border = "solid 1px gray";
	addBtn.setAttribute("data-toggle", "tooltip");
	// addBtn.setAttribute("title", "Add an event at " + dateStr + "!");
	addBtn.onclick = function(e) {
		addNewEvent(d, m, y);
	};
	return addBtn;
}

function createDetailBtn(d, m, y) {
	const detailBtn = createHtml("code", "&#10296;");
	detailBtn.className = "btn btn-default btn-sm";
	detailBtn.style.cssFloat = "right";
	detailBtn.style.border = "solid 1px gray";
	detailBtn.setAttribute("data-toggle", "tooltip");
	// detailBtn.setAttribute("title", "See events at " + dateStr + "!");
	detailBtn.onclick = function(e) {
		detail(d, m, y);
	};
	return detailBtn;
}

function addNewEvent(d, m, y) {
	const strDate = dateAcceptableForHtmlInput(d, m, y);
	console.log("strDate: ", strDate);
	if (this.addFunc) {
		this.addFunc(d, m, y);
	}
}

function dateAcceptableForHtmlInput(day, month, year) {
	return year + "-" + addZero(month, 10) + "-" + addZero(day, 10);
}

function addZero(Val, Min) {
	let N = new String(Val);
	const MinStr = new String(Min);
	const ValLength = N.length;
	const MinLength = MinStr.length;
	const Diff = MinLength - ValLength;

	for (let i = 1; i <= Diff; i++) {
		N = new String("0").concat(N);
	}

	return N;
}

function clear() {
	const dates = document.getElementsByClassName("date_element");
	for (let i = 0; i < dates.length; i++) {
		dates[i].innerHTML = "";
	}
	updateFebruary();
}

function updateFebruary() {
	MONTHS[1].day = 28 + (+YEAR_NOW % 4 == 0 ? 1 : 0);
}

function fillDay(current_month, next, begin) {

	clear();
	let begin_new = copyMonthProp(begin);
	let begin_old_ = copyMonthProp(begin);

	let week_ = begin_new.week;
	let begin_week = week_;
	if (begin_new.week > 1 && begin_new.day > 1) {
		week_ = 1;
		begin_week = 1;
	}
	let day_ = begin_new.day;
	let begin_day = day_;
	let isToday = RUNNING_MONTH == current_month && RUNNING_YEAR == YEAR_NOW;

	

	for (let d = 1; d <= MONTHS[current_month].day; d++) {
		if (day_ > 7) {
			day_ = 1;
			week_++;
		}
		if (isToday) {
			setElementByAttr(day_, week_, d);
		}
		day_++;
	}
	begin_new.week = week_ >= 5 ? 2 : 1;
	begin_new.day = day_;
	begin_new.dayCount = MONTHS[current_month].day;

	fillInfo();
	if (isToday) {
		detail(null, RUNNING_MONTH + 1, RUNNING_YEAR);
		begin_new.info = "NOW";
	} else {
		begin_new.info = "SOME-DAY";
	}
	//WHEN MONTH COMPONENT LOADED ALL
	if (isToday && this.loadMonth) {
		loadMonth(RUNNING_MONTH, RUNNING_YEAR);
	}
	return begin_new;
}