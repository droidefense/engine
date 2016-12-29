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
var address = server+":"+port;

var menuCreated = false;

var samplehash = "";

function getServerAddress (argument) {
	return "http://"+address+"/api/v1/"+argument;
}

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

function insertCssRow(row, idx, data, element, key, value) {
    var cellLeft = row.insertCell(idx);
    var el_span = document.createElement(element);
    el_span.setAttribute(key, value);
    var textNode = document.createTextNode(data);
    cellLeft.appendChild(el_span);
    el_span.appendChild(textNode);
}

function insertRow(row, idx, data) {
    var newCell = row.insertCell(idx);
    var newText = document.createTextNode(data);
    newCell.appendChild(newText);
}

function setProgressBar(idx, status){
    $('#progress-indicator').text(status);
    $('#scan-bar').css('width', idx+'%');
}

function setBadget(id, cls, title, message) {
    //set class
    $('#'+id).attr('class', cls);
    //set title
    $('#'+id+"-title").text(title);
    //set message
    $('#'+id+"-message").text(message);
}

function addTable(id, body, dataArray, columnNames, errorText) {
    var holder = $(id);
    if(dataArray){
        var body = document.getElementById(body);
        // Insert a row in the table at the last row
        //value

        if(dataArray.length>100){
            $(id).html("There are too many items to display. ("+dataArray.length+")");
        }
        else{
            dataArray.forEach(function(item) {
                var newRow = body.insertRow(body.rows.length);
                if(item && item.length>0)
                    insertRow(newRow, 0, item);
            });
        }

        //enable full features on the table
        /*$(id).DataTable({
          "paging": true,
          "lengthChange": false,
          "searching": true,
          "ordering": true,
          "info": true,
          "autoWidth": true
        });
        */
    }
    else{
        holder.text(errorText);
    }
}


function showInfoPopUp(title, cssCl, message, okBtn, clbck) {
    BootstrapDialog.show({
        title: title,
        cssClass: cssCl,
        message: message,
        type: BootstrapDialog.TYPE_DEFAULT, // <-- Default value is BootstrapDialog.TYPE_PRIMARY
        closable: false,
        draggable: false,
        buttons: [{
            label: okBtn,
            cssClass: 'btn-default',
            action: function(dialogRef) {
                dialogRef.close();
                if(clbck)
                    clbck();
            }
        }]
    });
}

function showDataPopUp(title, cssCl, message, okBtn, clbck) {

    BootstrapDialog.show({
        title: title,
        cssClass: cssCl,
        message: message,
        type: BootstrapDialog.TYPE_DEFAULT, // <-- Default value is BootstrapDialog.TYPE_PRIMARY
        closable: false,
        draggable: false,
        buttons: [{
            label: okBtn,
            cssClass: 'btn-default',
            action: function(dialogRef) {
                dialogRef.close();
                if(clbck)
                    clbck();
            }
        }]
    });
}

function betweenPreTags(data){
    return '<pre><code>'+data+'</code></pre>';
}

function getCurrentHash(){
    var current_title = $(document).attr('title');
    var hash = current_title.replace('apkr report | ', '');
    return hash;
}

function chooseFile(name, action) {
    var chooser = $(name);
    chooser.unbind('change');
    chooser.change(function(evt) {
        console.log($(this).val());
        action($(this).val());
    });
    chooser.trigger('click');  
}