//utils.js
function canUserWorker() {
    if (window.FileReader && window.Worker) {
        var a = parseInt(jQuery.browser.version, 10);
        if (jQuery.browser.opera) {
            return false
        }
        if (jQuery.browser.mozilla && a >= 13) {
            return true
        }
        if (jQuery.browser.webkit && a >= 535) {
            return true
        }
    }
    return false
}

function loadjscssfile(filename, filetype) {
    if (filetype == "js") { //if filename is a external JavaScript file
        var fileref = document.createElement('script')
        fileref.setAttribute("type", "text/javascript")
        fileref.setAttribute("src", filename)
    } else if (filetype == "css") { //if filename is an external CSS file
        var fileref = document.createElement("link")
        fileref.setAttribute("rel", "stylesheet")
        fileref.setAttribute("type", "text/css")
        fileref.setAttribute("href", filename)
    }
    if (typeof fileref != "undefined")
        document.getElementsByTagName("body")[0].appendChild(fileref)
}

function cleanHtml(str) {
    return escape(str);
}