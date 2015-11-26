
/** 
	OutSystems - Mobility Experts
	João Gonçalves - 23/11/2015
*/

function CardioPlugin() {
}

exports.scanCard = function (successCallback, errorCallback, requireExpiry, requireCvv, requirePostalCode) {
    cordova.exec(successCallback, errorCallback, "CardioPlugin", "scanCard", [requireExpiry, requireCvv, requirePostalCode]);
};