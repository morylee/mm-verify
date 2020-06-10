var imgModal = $('#imgFullViewer');
var imgModalImgTag = imgModal.find("#modelImg");
var imgModalCaptionText = imgModal.find("#caption");
var targets = imgModal.data("targets");

$(targets).on("click", function () {
	var imgSrc = $(this).prop("src");
	if (validImgTag(imgSrc)) {
		imgModalImgTag.attr("src", imgSrc);
		imgModalImgTag.attr("alt", $(this).prop("alt"));
		imgModalCaptionText.html($(this).prop("alt"));
		imgModal.css("display", "block");
	}
});

function validImgTag(imgSrc) {
	var imgReg = /(.jpg|.jpeg|.png|.gif|.svg)/g;
	if (imgSrc != null && imgSrc != undefined && imgSrc != '' && imgReg.test(imgSrc)) {
		var img = new Image();
		img.src = imgSrc;
		if (img.complete) {
			adjustImgTagPosition(img.width, img.height);
		} else {
			img.onload = function () {
				adjustImgTagPosition(img.width, img.height);
			}
		}
		return true;
	}
	return false;
}

function adjustImgTagPosition(imgWidth, imgHeight) {
	if (imgWidth > 0 && imgHeight > 0) {
		var windowWidth = $(window).width();
		if (imgWidth > windowWidth * 0.8) {
			imgHeight = imgHeight * windowWidth * 0.8 / imgWidth;
			imgWidth = windowWidth * 0.8;
		}
		imgModalImgTag.addClass("modal-content-adjust");
		imgModalCaptionText.addClass("caption-adjust");
		imgModal.find(".close").addClass("close-adjust");
		imgModalImgTag.css("marginLeft", 0 - imgWidth / 2 + "px");
		imgModalImgTag.css("marginTop", 0 - imgHeight / 2 + "px");
		imgModalCaptionText.css("marginLeft", 0 - windowWidth * 0.4 + "px");
		imgModalCaptionText.css("marginTop", imgHeight / 2 + "px");
		imgModal.find(".close").css("marginRight", 0 - imgWidth / 2 - 35 + "px");
		imgModal.find(".close").css("marginTop", 0 - imgHeight / 2 + "px");
	}
	imgModal.find(".close").removeClass("hide");
}

function removeModalCss() {
	imgModal.find(".close").addClass("hide");
	imgModalImgTag.removeClass("modal-content-adjust");
	imgModalCaptionText.removeClass("caption-adjust");
	imgModal.find(".close").removeClass("close-adjust");
	imgModalImgTag.css("marginLeft", "");
	imgModalImgTag.css("marginTop", "");
	imgModalCaptionText.css("marginLeft", "");
	imgModalCaptionText.css("marginTop", "");
	imgModal.find(".close").css("marginRight", "");
	imgModal.find(".close").css("marginTop", "");
}

$(window).resize(function () {
	var imgSrc = imgModalImgTag.prop("src");
	validImgTag(imgSrc);
})
$(document).keyup(function (event) {
	if (event.keyCode == 27) {
		imgModal.css("display", "none");
		removeModalCss();
	}
});
imgModal.on("click", function () {
	imgModal.css("display", "none");
	removeModalCss();
});
imgModal.find(".close").on("click", function() {
	imgModal.css("display", "none");
	removeModalCss();
});
