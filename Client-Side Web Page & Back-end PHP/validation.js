
$(document).ready(function() {
  $("#clear").click(formClear);

	$.validator.setDefaults({
		submitHandler: function(form) {
			initialPage();
            // form.submit();
		},
        debug: true,
	});

	//don't use this.optional(element), otherwise error
	$.validator.addMethod("myregex", function(value, element) {
	   return Number(value) >= Number($('#minprice').val()) || value == '';}//,;},
	);

  $("#register-form").validate({
	  // Specify the validation rules
	  rules: {
	  	keyword: "required",
        minprice: {
            number: true,
            min: 0,
        },
        maxprice: {
            number: true,
            min: 0,
            myregex: true,
        },
        maxhandle: {
            digits: true,
            min: 1,
        },
	  },
	  // Specify the validation error messages
	  messages: {
	  	keyword: "Please enter a key word",
        minprice:{
            number: "Price should be a valid decimal number",
            min: "Minimum price cannot be below 0",
        },
        maxprice: {
            number: "Price should be a valid number",
            min: "Maximum price scannot be less than minimum price or below 0",
            myregex: "Maximum price scannot be less than minimum price or below 0",
        },
        maxhandle: {
            digits: "Max handling time should be a valid digit",
            min: "Max handling time should be greater than or equal to 1",
        },
	   },
	  // submitHandler: function(form) {
	  //   dataPass();
	  // }
	});


});

//fb post, together with part2&3
function fbPost(key){
    var itemGallery = "#" + key + "Gallery";
    var itemTitle = "#" + key + "Title";
    var itemPrice = "#" + key + "Price";
    var itemShipping = "#" + key + "ShipPrice";
    var itemLocation = "#" + key + "Location";
    FB.ui(
      {
        method: 'feed',
        name: $(itemTitle).html(),
        link: $(itemTitle).attr("href"),
        source: $(itemGallery).attr("src"),
        caption: 'Search Information from eBay.com',
        description: $(itemPrice).html() + $(itemShipping).html() + ", " + $(itemLocation).html(),
      },
      function(response) {
        if (response && !response.error_code) {
          alert('Posting Successfully');
        } else {
          alert('Not Posted');
        }
      }
    );
}
//fb post part2
window.fbAsyncInit = function() {
    FB.init({
      appId      : 'your-app-id',
      xfbml      : true,
      version    : 'v2.3'
    });
};
//fb post part3
(function(d, s, id){
 var js, fjs = d.getElementsByTagName(s)[0];
 if (d.getElementById(id)) {return;}
 js = d.createElement(s); js.id = id;
 js.src = "//connect.facebook.net/en_US/sdk.js";
 fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

//Initial pagination
function initialPage(){
    $('#resPage0').attr('class', 'disabled');
    $('#resPage0').attr('onclick', '');
    $('#resPage6').attr('class', '');
    $('#resPage6').attr('onclick', 'pagePc(6)');
    $('#resPage1').attr('class', 'active');
    $('#resPage1').attr('onclick', '');
    $('#resPage1').attr('value', '1');
    $('#resPage1').html('<a href="#updateArea">1<span class="sr-only">(current)</span></a>');
    for(i = 2; i < 6; i++){
        $('#resPage' + i).attr('class', '');
        $('#resPage' + i).attr('onclick', 'pagePc(' + i + ')');
        $('#resPage' + i).attr('value', i);
        $('#resPage' + i + ' a').html(i);
    }
    dataPass(1);
}

//page pagination
function pagePc(pageNum){
    var newPage = pageNum;

    //disactive current active page button get cur pagenum
    var curPage = 1;
    for(i = 1; i < 6; i++){
        var curID = '#resPage' + i;
        if($(curID).attr('class') === 'active'){
            $(curID).attr('class', '');
            $(curID).attr('onclick', 'pagePc(' + i + ')');
            $(curID + ' a').html($(curID).val());
            curPage = i;
            break;
        }
    }

    //next and previous button dealing, page button refresh if out of range
    if(pageNum === 6) {
        if(curPage === 5) {
            newPage = 1;
            for(i = 1; i < 6; i++){
                $('#resPage' + i).val($('#resPage' + i).val() + 5);
                $('#resPage' + i + ' a').html($('#resPage' + i).val());
            }
        }
        else newPage = curPage + 1;//.toString();
    }
    else if(pageNum === 0){
        if(curPage === 1) {
            newPage = 5;
            for(i = 1; i < 6; i++){
                $('#resPage' + i).val($('#resPage' + i).val() - 5);
                $('#resPage' + i + ' a').html($('#resPage' + i).val());
            }
        }
        else newPage = curPage - 1;//.toString();
    } 

    //active new page button
    $('#resPage' + newPage).attr('class', 'active');
    $('#resPage' + newPage).attr('onclick', '');
    $('#resPage' + newPage + ' a').html($('#resPage' + newPage).val() + 
        '<span class="sr-only">(current)</span>');        

    //first step disable next and previous botton, only disable previous here
    if($('#resPage' + newPage).val() === 1) {
        $('#resPage0').attr('class', 'disabled');
        $('#resPage0').attr('onclick', '');
    }
    else{
        $('#resPage0').attr('class', '');
        $('#resPage0').attr('onclick', 'pagePc(0)');
    }

    //go to .ajax
    dataPass($('#resPage' + newPage).val());
}

//2nd step disable next and previous botton, only disable previous here
function pageBtDisable(total, itemsPP){
    var tt = parseInt(total);
    var pp = parseInt(itemsPP);
    var totalPages;
    //cal total page number
    if(tt%pp == 0) totalPages = tt/pp;
    else totalPages = tt/pp + 1;
    // alert('tt and pp and ttp' + tt + '&' + pp + '&' + totalPages);

    //disable page buttons
    for(i = 5; i > 0; i--){
        if(parseInt($('#resPage' + i).val()) > totalPages){
            $('#resPage' + i).attr('class', 'disabled');
        }
        else if($('#resPage' + i).attr('class') === 'disabled'){
            $('#resPage' + i).attr('class', '');
        }
    }
    if(parseInt($('#resPage5').val()) >= totalPages){
        $('#resPage6').attr('class', 'disabled');
    }
    else $('#resPage6').attr('class', '');
}

//Setup ajax, generate ajax data object
function dataPass(pageNum) {
	var obj =	{};//new Object();
	obj.keyword = $('#keyword').val();
	obj.sort = $('#sort').val();
	obj.resnum = $('#resnum').val();
	if($('#maxhandle').val() != '') obj.maxhandle = $('#maxhandle').val();
	if($('#minprice').val() != '') obj.minprice = $('#minprice').val();
	if($('#maxprice').val() != '') obj.maxprice = $('#maxprice').val();
	if($('#cond1').prop("checked")) obj.cond1 = $('#cond1').val();
	if($('#cond2').prop("checked")) obj.cond2 = $('#cond2').val();
	if($('#cond3').prop("checked")) obj.cond3 = $('#cond3').val();
	if($('#cond4').prop("checked")) obj.cond4 = $('#cond4').val();
	if($('#cond5').prop("checked")) obj.cond5 = $('#cond5').val();
	if($('#buyf1').prop("checked")) obj.buyf1 = $('#buyf1').val();
	if($('#buyf2').prop("checked")) obj.buyf2 = $('#buyf2').val();
	if($('#buyf3').prop("checked")) obj.buyf3 = $('#buyf3').val();
	if($('#return').prop("checked")) obj['return'] = 'true';
	if($('#shipping1').prop("checked")) obj.shipping1 = 'true';
    if($('#shipping2').prop("checked")) obj.shipping2 = 'Expedited';
	obj.pagenum = pageNum;
	//****page number not included yet. use onclick to add it later!

	$.ajax({
		url: 'urlresult.php',
		data: obj,
		type: 'GET',
		success: function(jsonStr) {
			showContents(jsonStr);
		},
		error: function(){
			alert("failed ajax");}
	});
}

function showContents (jsonStr) {
	var res = '<hr>';
	var jsonObj = JSON.parse(jsonStr);//$.parseJSON(res);

    if(jsonObj.ack !== "Success") {
        res += "<h3>" + jsonObj.ack + "</h3>";
        $('#resultPagination').attr('style', 'display:none');
    }
    else{
        //Display range of items
    	var page = parseInt(jsonObj.pageNumber);
    	res += "<h3>"
        //items range
        if(page*jsonObj.itemCount >= jsonObj.resultCount){
            res += (page-1)*(jsonObj.itemCount)+1 + '-' + jsonObj.resultCount; }
        else{
    	   res += (page-1)*(jsonObj.itemCount)+1 + '-' + page*jsonObj.itemCount; }
        //need to consider num less than 5, and number per page
    	res += ' items out of ' + jsonObj.resultCount; //total items
    	res += "</h3>"

        //Display items list
        $.each(jsonObj, function(key, item) {
            if(typeof item == 'object') {
                var itemGallery = key + "Gallery";
                var itemTitle = key + "Title";
                var itemPrice = key + "Price";
                var itemLocation = key + "Location";
                var itemShipping = key + "ShipPrice";

                //media object
                res += "<div class=\"media\">";
                //media-left, Item image
                res += '<div class="media-left media-top" >';//style=\"width:20%;\"
                var modalID = key + "Img";
                var modalidLabel = key + "ImgLabel";
                res += '<img class="media-object" id="' + itemGallery + '" style=\"margin:0 auto;\" src="' + 
                    item.basicInfo.galleryURL + '" alt="Item Image" data-toggle="modal" data-target="#' + modalID + '">';//target to modal by id
                //Modal body
                res += '<div class="modal fade" id="' + modalID + '" tabindex="-1" role="dialog" aria-labelledby="' + modalidLabel + '" aria-hidden="true">';
                res += '<div class="modal-dialog">';
                res += '<div class="modal-content">';
                res += '<div class="modal-header">';
                res += '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>';
                res += '<b class="modal-title" id="' + modalidLabel + '">' + item.basicInfo['title'] + '</b></div>';//close modal-header
                res += '<div class="modal-body">';
                if(item.basicInfo.pictureURLSuperSize === "") //close modal-body
                    res += '<img src="' + item.basicInfo.galleryURL + '"class="img-responsive center-block"></div>';
                else res += '<img src="' + item.basicInfo.pictureURLSuperSize + '"class="img-responsive center-block"></div>';
                res += '<div class="modal-footer">';
                res += '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>';//close modal-footer
                //close modal-content, modal-dialog, mymodal
                res += '</div></div></div>';
                //close media-left
                res += "</div>";

                //media body
                res += '<div class="media-body">';
                //media-heading, Item title
                res += '<h4 class="media-heading"><a id="' + itemTitle + '" href="' + item.basicInfo.viewItemURL + '">';
                res += item.basicInfo['title'] + '</a></h4>';
                //price
                res += '<b id="' + itemPrice + '">Price: $' + item.basicInfo.convertedCurrentPrice + "</b>";
                //shipping Info.
                res += '<font id="' + itemShipping + '">';
                if(item.basicInfo.shippingServiceCost == "0.0"){ res += " (FREE Shipping)"; }
                else{ res += " (+ $" + item.basicInfo.shippingServiceCost + " for shipping)"; }
                res += "</font>&nbsp&nbsp&nbsp&nbsp"; //close font
                //item location
                res += '<i id="' + itemLocation + '">Location: ' + item.basicInfo['location'] + "</i>";
                //top rated icon
                if(item.basicInfo.topRatedListing == "true") { 
                    res += "<img src=\"itemTopRated.jpg\" style=\"width:30px;height:30px;\">";
                }

                //Collapse
                var detailsId = key + "Detail";
                res += '<a data-toggle="collapse" href="#' + detailsId + '">&nbsp&nbspView Details</a>';

                //Facebook post button
                var fbpostID = key + "FBpost";
                res += '&nbsp&nbsp<input id="' + fbpostID + '" class="fb-to-post" type="image" src="fb.png" alt="FBpost" onclick="fbPost(\'' + key + '\')">';// width="20" height="20"

                res += '<div class="collapse" id="' + detailsId + '">';
                //Tab, Nav tabs
                var basicID = key + 'Basic';
                var sellerID = key + 'Seller';
                var shippingID = key + 'Shipping';
                res += '<div role="tabpanel">';
                res += '<ul class="nav nav-tabs" role="tablist">';
                res += '  <li role="presentation" class="active"><a href="#' + basicID + '" aria-controls="' + basicID + '" role="tab" data-toggle="tab">Basic Info</a></li>';
                res += '  <li role="presentation"><a href="#' + sellerID + '" aria-controls="' + sellerID + '" role="tab" data-toggle="tab">Seller Info</a></li>';
                res += '  <li role="presentation"><a href="#' + shippingID + '" aria-controls="' + shippingID + '" role="tab" data-toggle="tab">Shipping Info</a></li>';
                res += '</ul>';

                //Tab, Tab panes
                res += '<div class="tab-content">';
                ///Basic Information tab
                res += '<div role="tabpanel" class="tab-pane active" id="' + basicID + '">';
                //row1
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Category name</b></div>';
                res += '<div class="col-sm-4">';
                if(item.basicInfo.categoryName === "") res += 'N/A</div></div>';
                else res += item.basicInfo.categoryName + '</div></div>';
                //BasicInfo row2
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Condition</b></div>';
                res += '<div class="col-sm-4">';
                if(item.basicInfo.conditionDisplayName === "") res += 'N/A</div></div>';
                else res += item.basicInfo.conditionDisplayName + '</div></div>';
                //BasicInfo row3
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Buying format</b></div>';
                res += '<div class="col-sm-4">';
                if(item.basicInfo.listingType === "") res += 'N/A</div></div>';
                else if(item.basicInfo.listingType === "FixedPrice" || item.basicInfo.listingType === "StoreInventory")
                    res += 'Buy it Now</div></div>';
                else if(item.basicInfo.listingType === "Auction")
                    res += 'Auction</div></div>';
                else if(item.basicInfo.listingType === "Classified")
                    res += 'Classified Ad</div></div>';
                else res += item.basicInfo.listingType + '</div></div>';
                res += '</div>';

                //Seller Information Tab
                res += '<div role="tabpanel" class="tab-pane" id="' + sellerID + '">';
                //row1
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>User name</b></div>';
                res += '<div class="col-sm-4">';
                if(item.sellerInfo.sellerUserName === "") res += 'N/A</div></div>';
                else res += item.sellerInfo.sellerUserName + '</div></div>';
                //row2
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Feedback score</b></div>';
                res += '<div class="col-sm-4">';
                if(item.sellerInfo.feedbackScore === "") res += 'N/A</div></div>';
                else res += item.sellerInfo.feedbackScore + '</div></div>';
                //row3
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Positive feedback</b></div>';
                res += '<div class="col-sm-4">';
                if(item.sellerInfo.positiveFeedbackPercent === "") res += 'N/A</div></div>';
                else res += item.sellerInfo.positiveFeedbackPercent + '%</div></div>';
                //row4
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Feedback rating</b></div>';
                res += '<div class="col-sm-4">';
                if(item.sellerInfo.feedbackRatingStar === "") res += 'N/A</div></div>';
                else res += item.sellerInfo.feedbackRatingStar + '</div></div>';
                //row5
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Top rated</b></div>';
                res += '<div class="col-sm-4">';
                if(item.sellerInfo.topRatedSeller === "true")
                    res += '<span class="glyphicon glyphicon-ok" aria-hidden="true" style="color:green"></span></div></div>';
                else if(item.sellerInfo.topRatedSeller === "false")
                    res += '<span class="glyphicon glyphicon-remove" aria-hidden="true" style="color:Maroon"></span></div></div>';
                else res += 'N/A</div></div>';
                //row6
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Store</b></div>';
                res += '<div class="col-sm-4">';
                if(item.sellerInfo.sellerStoreName === "") res += 'N/A</div></div>';
                else if(item.sellerInfo.sellerStoreURL === "")
                    res += item.sellerInfo.sellerStoreName + '</div></div>';
                else
                    res += '<a href="' + item.sellerInfo.sellerStoreURL + '">' + item.sellerInfo.sellerStoreName + '</a></div></div>';
                res += '</div>';

                //Shipping Information Tab
                res += '<div role="tabpanel" class="tab-pane" id="' + shippingID + '">';
                //row1
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Shipping type</b></div>';
                res += '<div class="col-sm-4">';
                if(item.shippingInfo.shippingType === "") res += 'N/A</div></div>';
                else {
                    var sppt = item.shippingInfo.shippingType.replace(/([a-z])([A-Z])/g, '$1 $2');
                    res += sppt + '</div></div>';
                }
                //row2
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Handling time</b></div>';
                res += '<div class="col-sm-4">';
                if(item.shippingInfo.handlingTime === "") res += 'N/A</div></div>';
                else res += item.shippingInfo.handlingTime + ' day(s)</div></div>';
                //row3
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Shipping location</b></div>';
                res += '<div class="col-sm-4">';
                if(item.shippingInfo.shipToLocations === "") res += 'N/A</div></div>';
                else res += item.shippingInfo.shipToLocations + '</div></div>';
                //row4
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Expedited shipping</b></div>';
                res += '<div class="col-sm-4">';
                if(item.shippingInfo.expeditedShipping === "") res += 'N/A</div></div>';
                else if(item.shippingInfo.expeditedShipping === "true") 
                    res += '<span class="glyphicon glyphicon-ok" aria-hidden="true" style="color:green"></span></div></div>';
                else 
                    res += '<span class="glyphicon glyphicon-remove" aria-hidden="true" style="color:Maroon"></span></div></div>';
                //row5
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>One day shipping</b></div>';
                res += '<div class="col-sm-4">';
                if(item.shippingInfo.oneDayShippingAvailable === "") res += 'N/A</div></div>';
                else if(item.shippingInfo.oneDayShippingAvailable === "true") 
                    res += '<span class="glyphicon glyphicon-ok" aria-hidden="true" style="color:green"></span></div></div>';
                else 
                    res += '<span class="glyphicon glyphicon-remove" aria-hidden="true" style="color:Maroon"></span></div></div>';
                //row6
                res += '<div class="row">';
                res += '<div class="col-sm-3"><b>Returns accepted</b></div>';
                res += '<div class="col-sm-4">';
                if(item.shippingInfo.returnsAccepted === "") res += 'N/A</div></div>';
                else if(item.shippingInfo.returnsAccepted === "true") 
                    res += '<span class="glyphicon glyphicon-ok" aria-hidden="true" style="color:green"></span></div></div>';
                else 
                    res += '<span class="glyphicon glyphicon-remove" aria-hidden="true" style="color:Maroon"></span></div></div>';
                res += '</div>';
                //close tabpanel, collapse
                res += '</div></div>';

                //close media-body, media
                res += '</div></div></div>';

            }
        });

        $('#resultPagination').attr('style', '');
        pageBtDisable(jsonObj.resultCount, jsonObj.itemCount);
    }

	$("#updateArea").html(res);


}





function formClear(){
  //clear text box and check box
  var allinputs = $("input");
  for (var i = 0; i < allinputs.length; i++) {
    if(allinputs[i].type ==  "text") allinputs[i].value =""; //clear text box
    if(allinputs[i].type == "checkbox") allinputs[i].checked = false; //clear checkbox
  };

  //set select default options
  var allselects = $("select");
  for (var i = 0; i < allselects.length; i++) {
    allselects[i].selectedIndex = 0;
  };
}

/*
{
    "ack": "Success",
    "resultCount": "260560",
    "pageNumber": "1",
    "itemCount": "5",
    "item0": {
        "basicInfo": {
            "title": "Fashion BOSS Big Brand New men's Short Sleeve POLO Shirts 2028140",
            "viewItemURL": "http://www.ebay.com/itm/Fashion-BOSS-Big-Brand-New-mens-Short-Sleeve-POLO-Shirts-2028140-/301562522265?pt=LH_DefaultDomain_0&var=600459443884",
            "galleryURL": "http://thumbs2.ebaystatic.com/pict/301562522265404000000001_1.jpg",
            "pictureURLSuperSize": "http://i.ebayimg.com/00/s/NzQ3WDYwNA==/z/nUUAAOSwqu9VA6H0/$_3.JPG",
            "convertedCurrentPrice": "12.49",
            "shippingServiceCost": "0.0",
            "conditionDisplayName": "New with tags",
            "listingType": "StoreInventory",
            "location": "Hong Kong",
            "categoryName": "T-Shirts",
            "topRatedListing": "false"
        },
        "sellerInfo": {
            "sellerUserName": "newbrand2018",
            "feedbackScore": "657",
            "positiveFeedbackPercent": "95.9",
            "feedbackRatingStar": "Purple",
            "topRatedSeller": "false",
            "sellerStoreName": "start2028",
            "sellerStoreURL": "http://stores.ebay.com/start2028"
        },
        "shippingInfo": {
            "shippingType": "Free",
            "shipToLocations": "Worldwide",
            "expeditedShipping": "false",
            "oneDayShippingAvailable": "false",
            "returnsAccepted": "true",
            "handlingTime": "2"
        }
    },
    "item1": {
        "basicInfo": {
            "title": "Hugo Boss jacket Retails $200+",
            "viewItemURL": "http://www.ebay.com/itm/Hugo-Boss-jacket-Retails-200-/121607943791?pt=LH_DefaultDomain_0",
            "galleryURL": "http://thumbs4.ebaystatic.com/m/m12IN_Nt-QBg-1Vs9knN27Q/140.jpg",
            "pictureURLSuperSize": "http://i.ebayimg.com/00/s/MTAwMFg3NTA=/z/EtgAAOSwstxU8RXS/$_3.JPG",
            "convertedCurrentPrice": "50.0",
            "shippingServiceCost": "",
            "conditionDisplayName": "New without tags",
            "listingType": "AuctionWithBIN",
            "location": "Los Angeles,CA,USA",
            "categoryName": "Coats & Jackets",
            "topRatedListing": "false"
        },
        "sellerInfo": {
            "sellerUserName": "edwibache",
            "feedbackScore": "21",
            "positiveFeedbackPercent": "100.0",
            "feedbackRatingStar": "Yellow",
            "topRatedSeller": "false",
            "sellerStoreName": "",
            "sellerStoreURL": ""
        },
        "shippingInfo": {
            "shippingType": "Calculated",
            "shipToLocations": "US",
            "expeditedShipping": "true",
            "oneDayShippingAvailable": "false",
            "returnsAccepted": "false",
            "handlingTime": "2"
        }
    },
    "item2": {
        "basicInfo": {
            "title": "Fashion BOSS Big Brand New men's Short Sleeve POLO Shirts 2028140",
            "viewItemURL": "http://www.ebay.com/itm/Fashion-BOSS-Big-Brand-New-mens-Short-Sleeve-POLO-Shirts-2028140-/301562522265?pt=LH_DefaultDomain_0&var=600459443894",
            "galleryURL": "http://thumbs2.ebaystatic.com/pict/301562522265404000000001_1.jpg",
            "pictureURLSuperSize": "http://i.ebayimg.com/00/s/NzQ3WDYwNA==/z/nUUAAOSwqu9VA6H0/$_3.JPG",
            "convertedCurrentPrice": "12.49",
            "shippingServiceCost": "0.0",
            "conditionDisplayName": "New with tags",
            "listingType": "StoreInventory",
            "location": "Hong Kong",
            "categoryName": "T-Shirts",
            "topRatedListing": "false"
        },
        "sellerInfo": {
            "sellerUserName": "newbrand2018",
            "feedbackScore": "657",
            "positiveFeedbackPercent": "95.9",
            "feedbackRatingStar": "Purple",
            "topRatedSeller": "false",
            "sellerStoreName": "start2028",
            "sellerStoreURL": "http://stores.ebay.com/start2028"
        },
        "shippingInfo": {
            "shippingType": "Free",
            "shipToLocations": "Worldwide",
            "expeditedShipping": "false",
            "oneDayShippingAvailable": "false",
            "returnsAccepted": "true",
            "handlingTime": "2"
        }
    },
    "item3": {
        "basicInfo": {
            "title": "HUGO BOSS regular fit Black Label SZ large POLO PIMA COTTON",
            "viewItemURL": "http://www.ebay.com/itm/HUGO-BOSS-regular-fit-Black-Label-SZ-large-POLO-PIMA-COTTON-/181701354387?pt=LH_DefaultDomain_0",
            "galleryURL": "http://thumbs4.ebaystatic.com/m/mwYRFw2wnEXqhQlCB87ZzUQ/140.jpg",
            "pictureURLSuperSize": "http://i.ebayimg.com/00/s/MTYwMFg5MDQ=/z/bN4AAOSwe-FU4l3u/$_3.JPG",
            "convertedCurrentPrice": "10.0",
            "shippingServiceCost": "5.0",
            "conditionDisplayName": "Pre-owned",
            "listingType": "AuctionWithBIN",
            "location": "Saginaw,MI,USA",
            "categoryName": "Casual Shirts",
            "topRatedListing": "false"
        },
        "sellerInfo": {
            "sellerUserName": "nmfoildrew",
            "feedbackScore": "468",
            "positiveFeedbackPercent": "100.0",
            "feedbackRatingStar": "Turquoise",
            "topRatedSeller": "false",
            "sellerStoreName": "",
            "sellerStoreURL": ""
        },
        "shippingInfo": {
            "shippingType": "FlatDomesticCalculatedInternational",
            "shipToLocations": "US,CA,GB,AU,AT,BE,FR,DE,IT,JP,ES,TW,NL,CN,HK",
            "expeditedShipping": "true",
            "oneDayShippingAvailable": "false",
            "returnsAccepted": "false",
            "handlingTime": "1"
        }
    },
    "item4": {
        "basicInfo": {
            "title": "Fashion BOSS Big Brand New men's Short Sleeve POLO Shirts 2028140",
            "viewItemURL": "http://www.ebay.com/itm/Fashion-BOSS-Big-Brand-New-mens-Short-Sleeve-POLO-Shirts-2028140-/301562522265?pt=LH_DefaultDomain_0&var=600459443889",
            "galleryURL": "http://thumbs2.ebaystatic.com/pict/301562522265404000000005_1.jpg",
            "pictureURLSuperSize": "http://i.ebayimg.com/00/s/NzQ5WDY0Mw==/z/RfEAAOSwEeFVA6Ij/$_3.JPG",
            "convertedCurrentPrice": "12.49",
            "shippingServiceCost": "0.0",
            "conditionDisplayName": "New with tags",
            "listingType": "StoreInventory",
            "location": "Hong Kong",
            "categoryName": "T-Shirts",
            "topRatedListing": "false"
        },
        "sellerInfo": {
            "sellerUserName": "newbrand2018",
            "feedbackScore": "657",
            "positiveFeedbackPercent": "95.9",
            "feedbackRatingStar": "Purple",
            "topRatedSeller": "false",
            "sellerStoreName": "start2028",
            "sellerStoreURL": "http://stores.ebay.com/start2028"
        },
        "shippingInfo": {
            "shippingType": "Free",
            "shipToLocations": "Worldwide",
            "expeditedShipping": "false",
            "oneDayShippingAvailable": "false",
            "returnsAccepted": "true",
            "handlingTime": "2"
        }
    }
}
*/
