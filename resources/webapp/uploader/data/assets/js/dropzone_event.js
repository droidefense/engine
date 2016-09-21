//upload.js
var myDropzone;
var maxFileS = 2; //max mb size
Dropzone.options.apkdropzone = {
    paramName: "file", // The name that will be used to transfer the file
    maxFilesize: maxFileS, // MB
    maxFiles: 1,
    maxfilesexceeded: function(file) {
        this.removeAllFiles();
    },

    // Prevents Dropzone from uploading dropped files immediately
    autoProcessQueue: false,

    init: function() {
        
        var disclaimer = $('#disclaimer');
        var submitButton = $("#scan-all");
        myDropzone = this; // closure

        // You might want to show the submit button only when 
        // files are dropped here:
        this.on("addedfile", function() {
            if (myDropzone.getQueuedFiles().length == 0)
                submitButton.html('Scan sample');
            else
                submitButton.html('Scan samples');
            disclaimer.show();
        });

    }
};