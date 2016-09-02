// Mixing jQuery and Node.js code in the same file? Yes please!
//GLOBAL VARIABLES
var ngui = undefined;
var nwin = undefined;
var menu = undefined;
var tray = undefined;

var userConnected = false;

// Load native UI library
ngui = require('nw.gui');
// Get the current window
nwin = ngui.Window.get();

var fs = require("fs");
var crypt = require('crypto');
var async = require('async');
var os = require('os');
var CryptoJS = require("crypto-js");
var base64 = require('base-64');

var server = "127.0.0.1";
var port = 8080;
var address = server + ":" + port;

var menuCreated = false;

var samplehash = "";

ngui.App.clearCache();

function getServerAddress(argument) {
    return "http://" + address + "/api/v1/" + argument;
}

function checkRequirements() {
    if (window.localStorage) {
        //local storage exists
    } else {
        //local storage not available
        var callback = function(clicked) {
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

function init_mw() {
    if(getLocalStorage('menu_on_bar')){
        //true. do nothing
        console.log("Menu is already visible");
    }
    else{
        //show the damn fucking menu
        menu = new ngui.Menu({
            type: "menubar"
        });
        if (process.platform == "darwin") {
            menu.createMacBuiltin && menu.createMacBuiltin('apkr');
        }
        // Create a tray icon
        tray = new ngui.Tray({
            icon: 'mac-menu-icon.png'
        });

        var separator1 = new ngui.MenuItem({
            type: 'separator'
        });
        var separator2 = new ngui.MenuItem({
            type: 'separator'
        });

        var status = new ngui.MenuItem({
            label: 'Check server status'
        });
        var updates = new ngui.MenuItem({
            label: 'Check updates',
            key: "u",
            modifiers: "ctrl+u"
        });
        var exit = new ngui.MenuItem({
            label: 'Exit',
            click: function() {
                quitApp(false);
            },
            key: "q",
            modifiers: "ctrl+q"
        });

        // Create sub-menu
        var menuItems = new ngui.Menu();
        menuItems.append(status);
        menuItems.append(updates);

        //append items to menu
        menu.append(separator1);
        // Append MenuItem as a Submenu
        menu.append(
            new ngui.MenuItem({
                label: 'Check',
                submenu: menuItems // menu elements from menuItems object
            })
        );
        menu.append(separator2);
        menu.append(exit);
        tray.menu = menu;
        setLocalStorage('menu_on_bar', true);
    }
}

function requestPermissions() {
    // request permission on page load
    document.addEventListener('DOMContentLoaded', function() {
        if (Notification.permission !== "granted")
            Notification.requestPermission();
    });
}

//execute on each page load
// A $( document ).ready() block.
$(document).ready(function() {
    console.log("starting nw...");
    init_mw();
    requestPermissions();
    console.log("injecting template...");
    inject();
    loadNav();
    //hide latest upload table. by default
    $('#latest-uploads-row').hide();
    console.log("checking requirements");
    checkRequirements();
    console.log("checking server");

    var tableCallback = function(data) {
        $('#latest-uploads-row').hide();
        if (data != null) {
            var max = data['max'];
            console.log(max);
            var added = data['added'];
            if (added > 0) {
                //add data to rows
                var tableRef = document.getElementById('latest-uploads-table').getElementsByTagName('tbody')[0];
                for (var i = 0; i < added; i++) {
                    // Insert a row in the table at the last row
                    var newRow = tableRef.insertRow(tableRef.rows.length);
                    // Insert a cell in the row at index 0
                    var newCell = newRow.insertCell(0);
                    var newText = document.createTextNode(data['names'][i])
                    newCell.appendChild(newText);

                    newCell = newRow.insertCell(1);
                    newText = document.createTextNode(data['hash'][i])
                    newCell.appendChild(newText);

                    newCell = newRow.insertCell(2);
                    newText = document.createTextNode(data['lastMod'][i])
                    newCell.appendChild(newText);
                };
                $('#latest-uploads-row').show();
            }
        }
    }
    var pingCallback = function() {
        //notification example
        showGoodNotification('Status', 'Connected to server');
        console.log("profiling new user...");
        createProfile();
        console.log("Reading latest uploads...")
        //hide table
        $('#latest-uploads-row').hide();
        ajax_getLatestUploads(tableCallback);
        console.log("ready!");
        userConnected = true;
    }
    ajax_pingServer(pingCallback);
});

nwin.on('close', function(){
    setLocalStorage('menu_on_bar', false);
    appClosePopUp();
}
);