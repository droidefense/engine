//upload.js
var myDropzone;
var maxFileS = 12; //max mb size
Dropzone.options.apkdropzone = {
    paramName: "file", // The name that will be used to transfer the file
    maxFilesize: maxFileS, // MB
    maxFiles: 1,
    maxfilesexceeded: function(file) {
        this.removeAllFiles();
        this.addFile(file);
    },

    // Prevents Dropzone from uploading dropped files immediately
    autoProcessQueue: false,

    init: function() {
        var submitButton = document.querySelector("#scan-all")
        myDropzone = this; // closure

        // You might want to show the submit button only when 
        // files are dropped here:
        this.on("addedfile", function() {
            if (myDropzone.getQueuedFiles().length == 0)
                $('#scan-all').html('Scan sample');
            else
                $('#scan-all').html('Scan samples');
            $('#scan-all').show();
        });

    }
};