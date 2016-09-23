var KEY_F12 = 123;

$('#scan-all').click(function() {
    
    //change button text to scanning
    var oldText = $('#scan-all').text();
    $('#scan-all').text('Scanning');
    
    //check if it was already analyzed
    var file = myDropzone.getQueuedFiles()[0];
    var calculatedHash = "";

    //check if there is a file there
    if (file != null && file.size > 0) {
        //1 check file extension
        var ext = file.name.split('.').pop();
        ext = ext.toLowerCase();
        if (ext != 'apk') {
            showFileNotApkPopUp(file.name);
        } else {
            var callback = function(data) {
                $('#scan-all').text(oldText);
                if (data != null) {
                    var analyzed = data['analyzed'];
                    if (analyzed == true) {
                        //TODO get file info: hash, lastDate, daysAgo
                        fileAlreadyAnalyzedPopUp(data, file);
                    } else {
                        upload(file.name, file, calculatedHash);
                    }
                    return;
                }
            };
            //calcula sha and when its done execute callback
            worker = new Worker("uploader/data/assets/js/sha256.min.js");
            worker.onmessage = function(b) {
                if (b.data.progress) {
                    console.log(b.data.progress);
                } else {
                    calculatedHash = b.data.sha256;
                    ajax_checkHash(calculatedHash, callback);
                }
            };
            worker.postMessage({
                file: file
            });
        }
    } else if (file != null && file.size == 0) {
        fileIsEmptyPopUp();
    }
    $('#scan-all').text(oldText);
});

// this is the id of the form
$('#betazone').submit(function(e) {
    e.preventDefault();
    // avoid to execute the actual submit of the form.
    //get email
    var email = $('#user-input-email');
    //log
    console.log("Granting BETA access to: " + email.html());
    //send

    var callback = function() {
        console.log("done");
    };
    if (userConnected) {
        ajax_betaAccess(email, callback);
    } else {
        showBetaAccessErrorPopUp();
        callback();
    }
});

// user clicks on view report results href link
$('#reportResultDialog').click(function(e) {
    e.preventDefault();
    //open new nw js window with the report results
    // Create a new window and get it
    var url = $('#reportResultDialog').attr('href');
    openNewReportWindow(url, samplehash);
});


$('toggle-screen').click(function(e) {
    nwin.toggleFullscreen();
});

$(document).keydown(function(event){
    if(event.keyCode==KEY_F12){
        nwin.toggleFullscreen();
        return false;
   }
    else if(event.ctrlKey && event.shiftKey && event.keyCode==73){        
          return false;  //Prevent from ctrl+shift+i
      }
});

$('#terms').click(function(e) {
    e.preventDefault();
    console.log("terms show");
    termsOfUsePopup();
});

$('#privacy').click(function(e) {
    e.preventDefault();
    console.log("privacy show");
    privacyPopUp();
});