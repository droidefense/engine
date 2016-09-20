var alreadyScanned = false;

function encapsulateData(data, encapsulate){
    if(encapsulate){
    	//cipher data
    	//return base64.encode(data);
        return data;
    }
    else{
    	//decode
    	return data;
        //return base64.decode(data);
    }
}

function showGoodNotification(title, message){
     //notification start
            var notification = new Notification(title, 
            {
              icon: '../../../common/img/goodNotif.png',
              body: message,
            });
}

function showBadNotification(title, message){
     //notification start
            var notification = new Notification(title, 
            {
              icon: '../../../common/img/badNotif.png',
              body: message,
            });
}

//Exit
function exitFullscreen() {
    document.webkitExitFullscreen();
}

//Launch
function launchIntoFullscreen(element) {
    element.webkitRequestFullscreen();
}


function redirect(url) {
    window.location.replace(url);
}

function humanFileSize(bytes, si) {
    var thresh = si ? 1000 : 1024;
    if (Math.abs(bytes) < thresh) {
        return bytes + ' B';
    }
    var units = si ? ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'] : ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
    var u = -1;
    do {
        bytes /= thresh;
        ++u;
    } while (Math.abs(bytes) >= thresh && u < units.length - 1);
    return bytes.toFixed(1) + ' ' + units[u];
}

function getLocalStorage(key){
    if(isLocalStorageAvailable())
        return localStorage.getItem(key);
}

function setLocalStorage(key, value){
    if(isLocalStorageAvailable())
        localStorage.setItem(key, value);
}

function getSessionStorage(key){
    return sessionStorage.getItem(key);
}

function setSessionStorage(key, value){
    sessionStorage.setItem(key, value);
}

function isLocalStorageAvailable(){
    /*if (typeof(Storage) !== "undefined") {
        // Code for localStorage/sessionStorage.
        return true;
    } else {
        // Sorry! No Web Storage support..
        return false;
    }*/
    return typeof(Storage) !== "undefined";
}

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