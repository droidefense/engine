// Create Base64 Object
var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9+/=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/rn/g,"n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}

function quitApp(deleteStorage) {
    if (deleteStorage == true) {
        console.log("Deleting local storage...");
        localStorage.clear();
    }
    //force quit
    nwin.hide(); // Pretend to be closed already
    console.log("We're closing...");
    nwin.close(true);
    setProperlyClosed(true);
    //RESET localstorage values
    resetSessionValues();
}

function cleanBackground() {
    $('#page-wrapper').hide();
    $('body').css("background-color", "black");
}

function restoreBackground() {
    $('#page-wrapper').show();
    $('body').css("background-color", "#F5F5F5");
}

function createProfile() {
    var data = 
        '{ "cpusInfo": ' + JSON.stringify(os.cpus()) +
        ', "vars": {' +
        '"tempdir": ' + JSON.stringify(os.tmpdir()) + ', ' +
        '"endianness": ' + JSON.stringify(os.endianness()) + ', ' +
        '"hostname": ' + JSON.stringify(os.hostname()) + ', ' +
        '"type": ' + JSON.stringify(os.type()) + ', ' +
        '"platform": ' + JSON.stringify(os.platform()) + ', ' +
        '"arch": ' + JSON.stringify(os.arch()) + ', ' +
        '"release": ' + JSON.stringify(os.release()) + ', ' +
        '"totalmem": ' + JSON.stringify(os.totalmem()) +
        '}, "networkInterfaces": ' +
        JSON.stringify(os.networkInterfaces()) +
        '}';
    var enc = encapsulateData(data, true);
    userProfile(enc);
}


function upload(filename, file, hash) {
    if (file && file.size > maxFileS * 1024 * 1024) {
        //show dile too big dialog
        filesizeTooBigPopUp();
        return;
    } else {
        var continueTimer = true;
        BootstrapDialog.show({
                title: 'Uploading sample',
                message: 'Uploading',
                cssClass: 'z-dialog-vertical-center',
                closable: true,
                draggable: false,
                type: BootstrapDialog.TYPE_PRIMARY,
                onshow: function(dialogRef){
                    dialogRef.enableButtons(false);
                    dialogRef.setClosable(false);
                    dialogRef.getModalBody().html('Uploading sample, please wait');
                    var counter = 0;
                    //change text. refresh. upload
                    setInterval(change, 1000);
                    function change() {
                        if(continueTimer){
                            var points = ".";
                            for(var i=0; i<counter%3; i++){
                                points+=".";
                            }
                            dialogRef.getModalBody().html('Uploading sample, please wait'+points);
                            counter++;
                    }
                }
                //function callback for action when upload finishes
                var callback = function(data) {
                        continueTimer = false;
                        if (data == undefined) {
                            //error
                            dialogRef.setClosable(true);
                        } else {
                            //success
                            dialogRef.setTitle('File uploaded. Analyzing...');
                            dialogRef.getModalBody().html('Your file is successfully uploaded to apkr.</br>We will start analyzing it! We hope to be fast, but if you are in a rush, hit \'View report\' button');
                            dialogRef.enableButtons(true);
                            dialogRef.setClosable(true);
                        }
                    }
                    //make upload jax request here
                    ajax_uploadFile(filename, file, hash, callback);
                },
                buttons: [{
                    label: 'View report',
                    cssClass: 'btn-default',
                    action: function(dialogRef){
                        console.log('view report clicked');
                        openNewReportWindow('../manager/report.html', hash);
                        //close dialog
                        dialogRef.close();
                    }
                }]
            });
    }
}

function openNewReportWindow(url, hash){
    console.log('Opening new window for sample with hash '+hash);
    localStorage.setItem("hash", hash);
    var newReportWindow = window.open(url, '_blank', 'width=800,height=400');
}

function exitApp(win){
    win.hide(); // Pretend to be closed already
    console.log("We're closing...");
    //reset default values
    setLocalStorage('menu_on_bar', false);
    setLocalStorage('safe_close', true);
    win.close(true);
}

function getServerAddress(argument) {
    if(serverList==null || serverList.length == 0){
        //try to get server list from repo
        /*
        $.ajax({
            url: 'https://raw.githubusercontent.com/zerjioang/apkr/master/resources/webapp/server.json',
            success: function (result) {
                if (result){
                    var json = jQuery.parseJSON(result);
                    serverList = json.datacenters;
                    console.log("Datacencers detecteds: "+serverList.length);
                    if(serverList.length>0){
                        mainserver = serverList[0];
                        console.log("Setting first datacenter as main server: "+mainServer);
                    }
                    else{
                        //no servers available
                        console.log("No servers available right now");
                    }
                }
            },
            error: function (result) {
                if (result){
                    console.log("Error: "+result);
                }
            }
        });
        */
        return [];
    }
    else{
        return "http://" + mainServer +"/api/v1/" + argument;
    }
}

function checkRequirements() {
    if (window.localStorage) {
        //local storage exists
    } else {
        //local storage not available
        var callback = function() {
            localStorage.clear();
            quitApp(true);
        }

        showErrorPopUp(
            'ERROR',
            'dialog-vertical-center',
            'apkr can not work on this computer because it does not fullfil minimum requirements. </br> Please, check more information at <a href="https:www.google.com" target="_blank">minimum requirements</a>',
            'I understand',
            callback
        );
    }
}

function requestPermissions() {
    // request permission on page load
    document.addEventListener('DOMContentLoaded', function() {
        if (Notification.permission !== "granted")
            Notification.requestPermission();
    });
}


//base64 .js
var toByteArray;
var fromByteArray;

var lookup = []
var revLookup = []
var Arr = typeof Uint8Array !== 'undefined' ? Uint8Array : Array

function init () {
  var code = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'
  for (var i = 0, len = code.length; i < len; ++i) {
    lookup[i] = code[i]
    revLookup[code.charCodeAt(i)] = i
  }

  revLookup['-'.charCodeAt(0)] = 62
  revLookup['_'.charCodeAt(0)] = 63
}

init()

function toByteArray (b64) {
  var i, j, l, tmp, placeHolders, arr
  var len = b64.length

  if (len % 4 > 0) {
    throw new Error('Invalid string. Length must be a multiple of 4')
  }

  // the number of equal signs (place holders)
  // if there are two placeholders, than the two characters before it
  // represent one byte
  // if there is only one, then the three characters before it represent 2 bytes
  // this is just a cheap hack to not do indexOf twice
  placeHolders = b64[len - 2] === '=' ? 2 : b64[len - 1] === '=' ? 1 : 0

  // base64 is 4/3 + up to two characters of the original data
  arr = new Arr(len * 3 / 4 - placeHolders)

  // if there are placeholders, only get up to the last complete 4 chars
  l = placeHolders > 0 ? len - 4 : len

  var L = 0

  for (i = 0, j = 0; i < l; i += 4, j += 3) {
    tmp = (revLookup[b64.charCodeAt(i)] << 18) | (revLookup[b64.charCodeAt(i + 1)] << 12) | (revLookup[b64.charCodeAt(i + 2)] << 6) | revLookup[b64.charCodeAt(i + 3)]
    arr[L++] = (tmp >> 16) & 0xFF
    arr[L++] = (tmp >> 8) & 0xFF
    arr[L++] = tmp & 0xFF
  }

  if (placeHolders === 2) {
    tmp = (revLookup[b64.charCodeAt(i)] << 2) | (revLookup[b64.charCodeAt(i + 1)] >> 4)
    arr[L++] = tmp & 0xFF
  } else if (placeHolders === 1) {
    tmp = (revLookup[b64.charCodeAt(i)] << 10) | (revLookup[b64.charCodeAt(i + 1)] << 4) | (revLookup[b64.charCodeAt(i + 2)] >> 2)
    arr[L++] = (tmp >> 8) & 0xFF
    arr[L++] = tmp & 0xFF
  }

  return arr
}

function tripletToBase64 (num) {
  return lookup[num >> 18 & 0x3F] + lookup[num >> 12 & 0x3F] + lookup[num >> 6 & 0x3F] + lookup[num & 0x3F]
}

function encodeChunk (uint8, start, end) {
  var tmp
  var output = []
  for (var i = start; i < end; i += 3) {
    tmp = (uint8[i] << 16) + (uint8[i + 1] << 8) + (uint8[i + 2])
    output.push(tripletToBase64(tmp))
  }
  return output.join('')
}

function fromByteArray (uint8) {
  var tmp
  var len = uint8.length
  var extraBytes = len % 3 // if we have 1 byte left, pad 2 bytes
  var output = ''
  var parts = []
  var maxChunkLength = 16383 // must be multiple of 3

  // go through the array every three bytes, we'll deal with trailing stuff later
  for (var i = 0, len2 = len - extraBytes; i < len2; i += maxChunkLength) {
    parts.push(encodeChunk(uint8, i, (i + maxChunkLength) > len2 ? len2 : (i + maxChunkLength)))
  }

  // pad the end with zeros, but make sure to not forget the extra bytes
  if (extraBytes === 1) {
    tmp = uint8[len - 1]
    output += lookup[tmp >> 2]
    output += lookup[(tmp << 4) & 0x3F]
    output += '=='
  } else if (extraBytes === 2) {
    tmp = (uint8[len - 2] << 8) + (uint8[len - 1])
    output += lookup[tmp >> 10]
    output += lookup[(tmp >> 4) & 0x3F]
    output += lookup[(tmp << 2) & 0x3F]
    output += '='
  }

  parts.push(output)

  return parts.join('')
}