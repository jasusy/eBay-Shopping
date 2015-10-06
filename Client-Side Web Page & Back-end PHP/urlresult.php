<?php
define('EOL',(PHP_SAPI == 'cli') ? PHP_EOL : '<br />');

	$keyword = ""; 
	$resnum = 0;
$i = 0;
$apicall = "http://svcs.eBay.com/services/search/FindingService/v1?siteid=0&OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&REST-PAYLOAD=true&SECURITY-APPNAME=USCe88a42-5100-40a7-87f5-a9f4e0ad7ea&RESPONSE-DATA-FORMAT=XML&outputSelector[0]=SellerInfo&outputSelector[1]=PictureURLSuperSize&outputSelector[2]=StoreInfo";
if(isset($_GET['keyword'])) {
	$keyword = $_GET['keyword'];
	$apicall = $apicall . "&keywords=" . urlencode($_GET['keyword']);
}
if(isset($_GET['sort'])) $apicall = $apicall . "&sortOrder=" . $_GET['sort'];
if(isset($_GET['resnum'])) {
	$resnum = $_GET['resnum'];
	$apicall = $apicall . "&paginationInput.entriesPerPage=" . $_GET['resnum'];
}
if(isset($_GET['minprice']) && !preg_match("/^(\s+)?$/", $_GET['minprice'])){
	$apicall = $apicall . "&itemFilter[" . $i . "].name=MinPrice";
	$apicall = $apicall . "&itemFilter[" . $i . "].value=" . floatval($_GET['minprice']);
	$i++;
}
if(isset($_GET['maxprice']) && !preg_match("/^(\s+)?$/", $_GET['maxprice'])){
	$apicall = $apicall . "&itemFilter[" . $i . "].name=MaxPrice";
	$apicall = $apicall . "&itemFilter[" . $i . "].value=" . floatval($_GET['maxprice']);
	$i++;
}
if (isset($_GET['cond1']) || isset($_GET['cond2']) || isset($_GET['cond3']) || isset($_GET['cond4']) || isset($_GET['cond5'])){
		$apicall = $apicall . "&itemFilter[" . $i . "].name=Condition";
		$j = 0;
	if (isset($_GET['cond1'])){
		$apicall = $apicall . "&itemFilter[" . $i . "].value[" . $j ."]=" . $_GET['cond1'];
		$j++;
	}
	if (isset($_GET['cond2'])){
		$apicall = $apicall . "&itemFilter[" . $i . "].value[" . $j ."]=" . $_GET['cond2'];
		$j++;
	}
	if (isset($_GET['cond3'])){
		$apicall = $apicall . "&itemFilter[" . $i . "].value[" . $j ."]=" . $_GET['cond3'];
		$j++;
	}
	if (isset($_GET['cond4'])){
		$apicall = $apicall . "&itemFilter[" . $i . "].value[" . $j ."]=" . $_GET['cond4'];
		$j++;
	}
	if (isset($_GET['cond5'])){
		$apicall = $apicall . "&itemFilter[" . $i . "].value[" . $j ."]=" . $_GET['cond5'];
		$j++;
	}
	$i++;
}				
if (isset($_GET['buyf1']) || isset($_GET['buyf2']) || isset($_GET['buyf3'])){
		$apicall = $apicall . "&itemFilter[" . $i . "].name=ListingType";
		$j = 0;
	if (isset($_GET['buyf1'])){
		$apicall = $apicall . "&itemFilter[" . $i . "].value[" . $j ."]=" . $_GET['buyf1'];
		$j++;
	}
	if (isset($_GET['buyf2'])){
		$apicall = $apicall . "&itemFilter[" . $i . "].value[" . $j ."]=" . $_GET['buyf2'];
		$j++;
	}
	if (isset($_GET['buyf3'])){
		$apicall = $apicall . "&itemFilter[" . $i . "].value[" . $j ."]=" . $_GET['buyf3'];
		$j++;
	}
	$i++;
}				
if(isset($_GET['return'])){
	$apicall = $apicall . "&itemFilter[" . $i . "].name=ReturnsAcceptedOnly";
	$apicall = $apicall . "&itemFilter[" . $i . "].value=" . $_GET['return'];//true
	$i++;		
}
if(isset($_GET['shipping1'])){
	$apicall = $apicall . "&itemFilter[" . $i . "].name=FreeShippingOnly";
	$apicall = $apicall . "&itemFilter[" . $i . "].value=" . $_GET['shipping1'];//true
	$i++;		
}
if(isset($_GET['shipping2'])){
	$apicall = $apicall . "&itemFilter[" . $i . "].name=ExpeditedShippingType";
	$apicall = $apicall . "&itemFilter[" . $i . "].value=" . $_GET['shipping2'];//Expedited
	$i++;		
}
if(isset($_GET['maxhandle']) && !preg_match("/^(\s+)?$/", $_GET['maxhandle'])){
	$apicall = $apicall . "&itemFilter[" . $i . "].name=MaxHandlingTime";
	$apicall = $apicall . "&itemFilter[" . $i . "].value=" . intval($_GET['maxhandle']);
	$i++;
}	
if(isset($_GET['pagenum'])){
	$apicall = $apicall . "&paginationInput.pageNumber=" . intval($_GET['pagenum']);
}


// echo $apicall , EOL, EOL;
// $apicall = "http://svcs.ebay.com/services/search/FindingService/v1?siteid=0&SECURITY-APPNAME=USCe88a42-5100-40a7-87f5-a9f4e0ad7ea&OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&RESPONSE-DATA-FORMAT=XML&keywords=diablo+III&paginationInput.entriesPerPage=10&sortOrder=PricePlusShippingHighest&itemFilter[0].name=MinPrice&itemFilter[0].value=30&itemFilter[1].name=Condition&itemFilter[1].value[0]=1000&itemFilter[1].value[1]=4000&itemFilter[1].value[2]=6000&itemFilter[2].name=ListingType&itemFilter[2].value[0]=FixedPrice&itemFilter[2].value[1]=Auction&itemFilter[2].value[2]=Classified&itemFilter[3].name=ReturnsAcceptedOnly&itemFilter[3].value=true&itemFilter[4].name=FreeShippingOnly&itemFilter[4].value=true&itemFilter[5].name=ExpeditedShippingType&itemFilter[5].value=Expedited&itemFilter[6].name=MaxHandlingTime&itemFilter[6].value=3&outputSelector[0]=SellerInfo&outputSelector[1]=PictureURLSuperSize&outputSelector[2]=StoreInfo";
//	$sXml = simplexml_load_file($apicall) or die("Error: Cannot create object");
$sXml = new SimpleXMLElement(file_get_contents($apicall));
// if ($sXml === false) { //don't use double equals, have to check type&value
// 	echo "Failed get response XML from eBay: <br>\n";
// 	foreach (libxml_get_errors() as $error) {
// 		echo "<br>", $error->message;
// 	}
// }
//	echo "totalEntries: " . $sXml->paginationOutput->totalEntries . " for " . $keyword . "<br>";
//	echo $sXml->asXML();
// $dom = new DOMDocument('1.0');
// $dom->preserveWhiteSpace = false;
// $dom->formatOutput = true;
// $dom->loadXML($sXml->asXML());
// echo $dom->saveXML();
$obj = new stdClass();
if($sXml->paginationOutput->totalEntries == 0) {
	$obj->ack = 'No results found';
}
else{
	$obj->ack = (string)$sXml->ack;
	$obj->resultCount = (string)$sXml->paginationOutput->totalEntries;
	$obj->pageNumber = (string)$sXml->paginationOutput->pageNumber;
	$obj->itemCount = (string)$sXml->paginationOutput->entriesPerPage;

	$i = 0;
	foreach ($sXml->searchResult->item as $oneItem) {
		$itemName = 'item' . $i++;
	 	$obj->$itemName = new stdClass();
	 	$obj->$itemName->basicInfo = new stdClass();
	 	//basic information
	 	$bi = $obj->$itemName->basicInfo;
	 	$bi->title = (string)$oneItem->title;
	 	$bi->viewItemURL = (string)$oneItem->viewItemURL;
	 	$bi->galleryURL = (string)$oneItem->galleryURL;
	 	$bi->pictureURLSuperSize = (string)$oneItem->pictureURLSuperSize;//**
	 	$bi->convertedCurrentPrice = (string)$oneItem->sellingStatus->convertedCurrentPrice;
	 	$bi->shippingServiceCost = (string)$oneItem->shippingInfo->shippingServiceCost;
	 	$bi->conditionDisplayName = (string)$oneItem->condition->conditionDisplayName;
	 	$bi->listingType = (string)$oneItem->listingInfo->listingType;
	 	$bi->location = (string)$oneItem->location;
	 	$bi->categoryName = (string)$oneItem->primaryCategory->categoryName;
	 	$bi->topRatedListing = (string)$oneItem->topRatedListing;
	 	//seller information
	 	$obj->$itemName->sellerInfo = new stdClass();
	 	$sli = $obj->$itemName->sellerInfo;
	 	$sli->sellerUserName = (string)$oneItem->sellerInfo->sellerUserName;
	 	$sli->feedbackScore = (string)$oneItem->sellerInfo->feedbackScore;
	 	$sli->positiveFeedbackPercent = (string)$oneItem->sellerInfo->positiveFeedbackPercent;
	 	$sli->feedbackRatingStar = (string)$oneItem->sellerInfo->feedbackRatingStar;
	 	$sli->topRatedSeller = (string)$oneItem->sellerInfo->topRatedSeller;
	 	$sli->sellerStoreName = (string)$oneItem->storeInfo->storeName;
	 	$sli->sellerStoreURL = (string)$oneItem->storeInfo->storeURL;
	 	//Shipping information
	 	$obj->$itemName->shippingInfo = new stdClass();
	 	$spi = $obj->$itemName->shippingInfo;
	 	$spi->shippingType = (string)$oneItem->shippingInfo->shippingType;
	 	//combine shiptolocations separated with comma
	 	$temp = "";
	 	$firstTemp = true;
	 	foreach ($oneItem->shippingInfo->shipToLocations as $spl) {
	 		if($firstTemp) {$temp = $temp . (string)$spl; $firstTemp = false;}
	 		else $temp = $temp . ',' . (string)$spl;
	 	}
	 	$spi->shipToLocations = $temp;
	 	$spi->expeditedShipping = (string)$oneItem->shippingInfo->expeditedShipping;
	 	$spi->oneDayShippingAvailable = (string)$oneItem->shippingInfo->oneDayShippingAvailable;
	 	$spi->returnsAccepted = (string)$oneItem->returnsAccepted;
	 	$spi->handlingTime = (string)$oneItem->shippingInfo->handlingTime;

	}
}
//header("content-type:application/json");
echo json_encode($obj);

/*$filename = "ebayresult.json";
$file = fopen( $filename, "w" );
fwrite( $file, json_encode($obj));
fclose( $file );*/