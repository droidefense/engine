var KEY_F12 = 123;

$(document).keydown(function(event){
    if(event.keyCode==KEY_F12){
    	nwin.toggleFullscreen();
   		return false;
   }
	else if(event.ctrlKey && event.shiftKey && event.keyCode==73){        
	      return false;  //Prevent from ctrl+shift+i
	  }
});

$("#view-manifest-dropdown").click(function() {
	console.log("view manifest");
});

$("#export-manifest-json").click(function() {
	console.log("export manifest as json");
});

$("#cfg-graphviz-download").click(function() {
	console.log("cfg graphviz download selected");

	var hash = getCurrentHash();
	var successCallback = function(data){

		var callback =  function(path){
			fs.writeFile(path, data, function(err) {
				if(err) {
				    return console.log(err);
				}

				console.log("The file was saved as cfg.dot!");
			});
		};

		chooseFile('#dotfileDialog', callback);
	};

	var failCallback = function(data){
		showInfoPopUp("Download failed", undefined, "An error ocurred while downloading GraphViz cfg file", "Close", undefined) 
	}
	ajax_getCFGasGraphViz(hash, successCallback, failCallback)
});

$("#cfg-json-download").click(function() {

	var successCallback = function(data){

		var callback =  function(path){
			fs.writeFile(path, data, function(err) {
				if(err) {
				    return console.log(err);
				}

				console.log("The file was saved as cfg.dot!");
			});
		};

		chooseFile('#jsonfileDialog', callback);
	};

	if(cfgjson == undefined){
		console.log("cfg json download selected");
		var hash = getCurrentHash();
	
		var failCallback = function(data){
			showInfoPopUp("Download failed", undefined, "An error ocurred while downloading JSON cfg file", "Close", undefined) 
		}
		ajax_getCFGasJson(hash, successCallback, failCallback);
	}
	else{
		successCallback(cfgjson);
	}
});