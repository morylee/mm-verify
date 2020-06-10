//var converter = new showdown.Converter({
//	headerLevelStart: 3,
//	tables: true
//});
//
//$("#guideContent").keyup(function () {
//	var content = $("#guideContent").val();
//	var html = converter.makeHtml(content);
//	$("#guideDisplay").html(html);
//})

var headerHeight = $(".globalHeader nav").height();
var contentPaddingTop = parseInt($('.devCenterBox').css('paddingTop').replace('px', ''));
var titleHeight = parseInt($('.pageTitle').css('height').replace('px', ''));
var titleMarginTop = parseInt($('.pageTitle').css('marginTop').replace('px', ''));
var titleMarginBottom = parseInt($('.pageTitle').css('marginBottom').replace('px', ''));

function moveMenu() {
	var scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
	if (scrollTop > contentPaddingTop + titleHeight + titleMarginTop) {
		$('.menu_content').css('top', (headerHeight + titleMarginBottom) + 'px');
	} else if (scrollTop > contentPaddingTop) {
		$('.menu_content').css('top', (headerHeight + contentPaddingTop + titleHeight + titleMarginTop + titleMarginBottom - scrollTop) + 'px');
	} else {
		$('.menu_content').css('top', '');
	}
}

function resizeMenuHeight() {
	var windowHeight = $(window).outerHeight();
	var scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
	var menuTop = parseInt($('.menu_content').css('top').replace('px', ''));
	var footerHeight = parseInt($('.globalFooter').css('height').replace('px', ''));
	var footerOffsetHeight = $('.globalFooter').offset().top;
	
	if (windowHeight + scrollTop > footerOffsetHeight) {
		$("#guideMenu").css("height", (windowHeight - menuTop - titleMarginBottom - windowHeight - scrollTop - footerHeight + footerOffsetHeight) + "px");
	} else {
		$("#guideMenu").css("height", (windowHeight - menuTop - titleMarginBottom) + "px");
	}
}

moveMenu();
resizeMenuHeight();
$(window).scroll(function () {
	moveMenu();
	resizeMenuHeight();
	lookupMenu();
})

function resizeGuideContent() {
	var guideDisplayWidth = parseInt($('#guideDisplay').css('width').replace('px', ''));
	$("#guideMenu").css("maxWidth", (guideDisplayWidth / 4 - 30) + 'px');
	var guideMenuWidth = parseInt($('#guideMenu').css('width').replace('px', ''));
	$('#guideDisplay').css("paddingLeft", (guideMenuWidth + 30) + 'px');
}

resizeGuideContent();
$(window).resize(function () {
	resizeGuideContent();
	resizeMenuHeight();
})

$(".menu_list").on("click", ".menu_link", function () {
	$('.menu_link.current').removeClass('current');
	$(this).addClass('current');
	$('html, body').animate({scrollTop: $($(this).data('anchor-point')).offset().top - 50}, 500);
})

function lookupMenu() {
	var scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
	var menuItems = $('.menu_link');
	$('#guideDisplay .content_anchor_point').each(function (n, ct) {
		var tagTop = $(ct).offset().top;
		var distance = tagTop - scrollTop;
		if (distance >= 75) {
			$('.menu_link.current').removeClass('current');
			var ind = n === 0 ? n : n - 1;
			$(menuItems[ind]).addClass('current');
			return false;
		}
	})
}
$(".menu_content, pre").niceScroll({
	cursorwidth: 10,
});
