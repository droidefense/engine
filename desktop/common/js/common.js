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