//GLOBAL VARIABLES
var serverList = null;
var mainServer = "localhost:1234";

var menuCreated = false;
var samplehash = null;
var userConnected = false;

//execute on each page load
// A $( document ).ready() block.
$(document).ready(function() {
    console.log("starting apkr");
    //getServerAddress();
    console.log("requesting permission for notifications...");
    requestPermissions();
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

function loadNav() {
    skel.breakpoints({
        wide: '(max-width: 1680px)',
        normal: '(max-width: 1280px)',
        narrow: '(max-width: 980px)',
        narrower: '(max-width: 840px)',
        mobile: '(max-width: 736px)',
        mobilep: '(max-width: 480px)'
    });

    var $window = $(window),
        $body = $('body'),
        $header = $('#header'),
        $banner = $('#banner');

    // Fix: Placeholder polyfill.
    $('form').placeholder();

    // Prioritize "important" elements on narrower.
    skel.on('+narrower -narrower', function() {
        $.prioritize(
            '.important\\28 narrower\\29',
            skel.breakpoint('narrower').active
        );
    });

    // Dropdowns.
    $('#nav > ul').dropotron({
        alignment: 'right'
    });

    // Off-Canvas Navigation.

    // Navigation Button.
    $(
            '<div id="navButton">' +
            '<a href="#navPanel" class="toggle"></a>' +
            '</div>'
        )
        .appendTo($body);

    // Navigation Panel.
    $(
            '<div id="navPanel">' +
            '<nav>' +
            $('#nav').navList() +
            '</nav>' +
            '</div>'
        )
        .appendTo($body)
        .panel({
            delay: 500,
            hideOnClick: true,
            hideOnSwipe: true,
            resetScroll: true,
            resetForms: true,
            side: 'left',
            target: $body,
            visibleClass: 'navPanel-visible'
        });

    // Fix: Remove navPanel transitions on WP<10 (poor/buggy performance).
    if (skel.vars.os == 'wp' && skel.vars.osVersion < 10)
        $('#navButton, #navPanel, #page-wrapper')
        .css('transition', 'none');

    // Header.
    // If the header is using "alt" styling and #banner is present, use scrollwatch
    // to revert it back to normal styling once the user scrolls past the banner.
    // Note: This is disabled on mobile devices.
    if (!skel.vars.mobile &&
        $header.hasClass('alt') &&
        $banner.length > 0) {

        $window.on('load', function() {

            $banner.scrollwatch({
                delay: 0,
                range: 0.5,
                anchor: 'top',
                on: function() {
                    $header.addClass('alt reveal');
                },
                off: function() {
                    $header.removeClass('alt');
                }
            });

        });

    }
}