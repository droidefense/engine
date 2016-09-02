// A $( document ).ready() block.

var cfgjson = undefined;

$(window).load(function() {
	//make ajax call and get sample information
	if(samplehash){
		if(samplehash.length == 64){
			console.log("This report belongs to sample with hash: "+samplehash);
		    //1. update document title
		    $('title').text('apkr report | '+samplehash);
		    //2. update report hash
		    $("#report-hash").html(samplehash);

		    if (!alreadyScanned) {
			    //notification start
			    showGoodNotification('Scanning...', 'Uploaded sample is being analyzed');
			}

			var callbackSuccess = function(data){
				injectReportData(data);
				$('#showDuringScan').hide();
				$('#hideDuringScan').show();
				//4. change progress-indicator
				$('progress-indicator').text("Scan done.");
				console.log("success");
			}
			var callbackError = function(data){
				$('#showDuringScan').hide();
				console.log("error");
			}
			ajax_getReportInformation(samplehash, callbackSuccess, callbackError);
		}
	}
});
function injectReportData(json) {

	//------- GLOBAL ----- //

	//change progress bar to done
	setProgressBar(100, "Scan completed");

	if(!alreadyScanned){
		showGoodNotification('Analysis completed', 'Sample scan is done');
	}

	//------- SCAN RESULT ----- //

	setBadgets(json);

	//------- FRIENDLY REPORT ----- //

	setFriendlyReport(json);

	//------- DETAILED REPORT ----- //

	//USED ANALYZERS

	setUsedAnalyzersAndTiming(json);

	//EXTERNAL SCANNER
	printExternalScannersInfo(json);

	//SOURCE FILE INFO

	//add file info to friendly table
	printSampleInfo(json);

	//FILE CONTENT INFORMATION

	printContentInfo(json);

	//DEX HEADER INFORMATION
	
	printDexHeaderInfo(json);

	//VIRUSTOTAL SECTION

	//ANDROID MANIFEST SECTION

	printAndroidManifestInfo(json);

	//add certificate info

	printCertificateInfo(json);

	//add statistics

	printStats(json);

	//add detailed information: juicy files

	//add app raw list
	var raw = json.staticInfo.rawFiles;
	var lib = json.staticInfo.libFiles;
	var assets = json.staticInfo.assetFiles;

	if(raw.length==0 && lib.length==0 | assets.length==0){
		$('detailed-info-juicy-files').hide();
	}
	else{
		printFilesDetailedInfo(raw, lib, assets);
		$('detailed-info-juicy-files').show();
	}

	//PRINT DEX FILE CLASS, METHODS,...

	printDexFileUnpacked(json);

	//PRINT STRING ANALYSIS INFORMATION

	printStringAnalysis(json);
	//generateStringChart(json);

	printPluginInformation(json);

	printCFGinfo();

	printML(json);
}

function getColoredBadget(number){
	if(number==undefined)
		return '';
	if(number>0){
		return '<span class="badge bg-blue">'+number+'</span>'
	}
	else{
		return '<span class="badge bg-green">'+number+'</span>'
	}
}

function printFilesDetailedInfo(raw, lib, assets){
	if(raw){
		var size = raw.length;
		if(size==0){
			//hide table
			$('#raw-info').html("This application has no declared raw inside");
			$('#raw-info').show();
		}
		else{
			//show data
			raw.forEach(function(item) {

				//add data to rows
		        var body = document.getElementById('rawfiles-table-body');
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name, main, meta, intent
		        insertRow(newRow, 0, item.filename);
		        insertRow(newRow, 1, item.filesize);
		        insertRow(newRow, 2, item.suspiciousFile);
			});
			$('#raw-info').show();
		}
	}

	//add app resources list
	
	if(lib){
		var size = lib.length;
		if(size==0){
			//hide table
			$('#lib-info').html("This application has no declared lib inside");
			$('#lib-info').show();
		}
		else{
			//show data
			lib.forEach(function(item) {

				//add data to rows
		        var body = document.getElementById('libfiles-table-body');
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name, main, meta, intent
		        insertRow(newRow, 0, item.filename);
		        insertRow(newRow, 1, item.filesize);
		        insertRow(newRow, 2, item.suspiciousFile);
			});
			$('#lib-info').show();
		}
	}

	//add app assets list
	
	if(assets){
		var size = assets.length;
		if(size==0){
			//hide table
			$('#assets-info').html("This application has no declared assets inside");
			$('#assets-info').show();
		}
		else{
			//show data
			assets.forEach(function(item) {

				//add data to rows
		        var body = document.getElementById('assetsfiles-table-body');
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name, main, meta, intent
		        insertRow(newRow, 0, item.filename);
		        insertRow(newRow, 1, item.filesize);
		        insertRow(newRow, 2, item.suspiciousFile);
			});
			$('#assets-info').show();
		}
	}

	$('#assetsTable').DataTable();
	$('#libTable').DataTable();
	$('#rawTable').DataTable();
}

function printPluginInformation(json){
	//add static plugin data
	if(json.staticInfoPlugins){
		json.staticInfoPlugins.forEach(function (plugin) {
			//add plugins results
			$('#static-plugin-result-holder').append(plugin.result);
		})
	}
	else{
		//hide data section
		$('#static-plugin-result-holder-row').hide();
	}

	//add dynamic plugin data
	if(json.dynamicInfoPlugins){
		json.dynamicInfoPlugins.forEach(function (plugin) {
			//add plugins results
			$('#dynamic-plugin-result-holder').append(plugin.result);
		})
	}
	else{
		//hide data section
		$('#dynamic-plugin-result-holder-row').hide();
	}
}

function printStats(json){
	var stats = json.statistics;

	var raw = json.staticInfo.rawFiles.length;
	var assets = json.staticInfo.assetFiles.length;
	var lib = json.staticInfo.libFiles.length;
	var app = json.staticInfo.appFiles.length;
	var thirdpartyPackages = 0;

	var thirds = stats.developerPackages;
	thirdpartyPackages = thirds.length -1;

	if(stats){
		$("#sample-package-count").text(stats.totalPackageCount);
		$("#sample-other-package-count").text(thirdpartyPackages);
		$("#sample-classes-count").text(stats.totalClassCount);
		$("#sample-total-count").text(app);

		$("#sample-developer-class-count").text(stats.developerClassCount);
		$("#sample-real-developer-class-count").text(stats.realDeveloperClassCount);
		$('#sample-real-developer-inner-class-count').text(stats.realDeveloperInnerClassCount);

		$("#sample-raw-count").text(raw);			
		$("#sample-assets-count").text(assets);
		$("#sample-lib-count").text(lib);
	}

	var total  = raw + assets + lib;

	//hide or show juicy files table
	if(total>0){
		$('#detailed-info-juicy-files').show();
		//inject in table files details
		if(raw){
			var body = document.getElementById('rawfiles-table-body');
			json.staticInfo.rawFiles.forEach(function (file) {
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name description, used
		        insertRow(newRow, 0, file.filename);
		        insertRow(newRow, 1, file.declaredExtension);
		        insertRow(newRow, 2, file.description+' or '+file.magicDescription);
		        insertRow(newRow, 3, file.md5);
			});
		}
		else{
			$('#raw-info').html("This application has no raw files inside");
			$('#raw-info').show();
		}

		if(assets){
			var body = document.getElementById('assetsfiles-table-body');
			json.staticInfo.assetFiles.forEach(function (file) {
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name description, used
		        insertRow(newRow, 0, file.filename);
		        insertRow(newRow, 1, file.declaredExtension);
		        insertRow(newRow, 2, file.description+' or '+file.magicDescription);
		        insertRow(newRow, 3, file.md5);
			});
		}
		else{
			$('#assets-info').html("This application has no asset files inside");
			$('#assets-info').show();
		}

		if(lib){
			var body = document.getElementById('libfiles-table-body');
			json.staticInfo.libFiles.forEach(function (file) {
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name description, used
		        insertRow(newRow, 0, file.filename);
		        insertRow(newRow, 1, file.declaredExtension);
		        insertRow(newRow, 2, file.description+' or '+file.magicDescription);
		        insertRow(newRow, 3, file.md5);
			});
		}
		else{
			$('#lib-info').html("This application has no lib files inside");
			$('#lib-info').show();
		}

		$('#rawfilesTable').DataTable();
		$('#libfilesTable').DataTable();
		$('#assetsfilesTable').DataTable();
	}
	else{
		$('#detailed-info-juicy-files').hide();
	}
}

function printCertificateInfo(json){
	//add certificate information table data
	var certFile = json.staticInfo.certFile;

	var certList = json.staticInfo.certificates;

	if(certFile && certList.length>0){
		$('#certificate-block-inner').show();
		$('#no-cert-alert').hide();
	}
	else{
		$('#certificate-block-inner').hide();
		$('#no-cert-alert').show();
		return;
	}

	if(certFile){
		//show certificate information
		$('#cert-file-filename').html(certFile.filename);
		$('#cert-file-size').html(certFile.filesize+" bytes ("+certFile.beautyFilesize+")");
		$('#cert-file-crc32').html(certFile.crc32);
		$('#cert-file-md5').html(certFile.md5);
		$('#cert-file-sha1').html(certFile.sha1);
		$('#cert-file-sha256').html(certFile.sha256);
		$('#cert-file-sha512').html(certFile.sha512);
		$('#cert-file-ssdeep').html(certFile.ssdeep);
		$('#cert-file-type').html(certFile.declaredExtension);
		$('#cert-file-magic').html(certFile.description);
	}
	else{
		//hide table
	}
	//add certificate info
	
	if(certList){
		certList.forEach(function (cert) {
			$('#cert-version').text(cert.version);

			if(cert.subject){
				//subject
				$('#cert-commonName').text(cert.subject.commonName);
				$('#cert-organizationalUnit').text(cert.subject.organizationalUnit);
				$('#cert-organization').text(cert.subject.organization);
				$('#cert-locality').text(cert.subject.locality);
				$('#cert-stateOrProvinceName').text(cert.subject.stateOrProvinceName);
				$('#cert-countryName').text(cert.subject.countryName);
			}

			$('#cert-signatureAlgorithm').text(cert.signatureAlgorithm);
			$('#cert-oid').text(cert.oid);
			$('#cert-type').text(cert.type);
			$('#cert-publicKeyAlgorithm').text(cert.publicKeyAlgorithm);
			$('#cert-certType').text(cert.certType);
			$('#cert-exponent').text(cert.exponent);
			$('#cert-validity').text(cert.validity);
			$('#cert-issuer').text(cert.issuer.commonName);
			$('#cert-validfrom').text(cert.startDate);
			$('#cert-validuntil').text(cert.endDate);
			$('#cert-sn').text(cert.serialNumber);
			$('#certificate-bautifier').text(cert.beautifierString);

			//set title
			if(cert.subject.commonName)
				$('#cert-title').text(cert.subject.commonName);
			else
				$('#cert-title').text('Unknown');
			if(cert.subject.organization)
				$('#cert-subtitle').text('Organization: '+cert.subject.organization);
			else
				$('#cert-subtitle').text('Organization: unknown');

			//show or hide the banner
			if(cert.seemsDebugCertificate){
				$('#debug-cert-alert').show();
			}
			else{
				$('#debug-cert-alert').hide();
			}
			
		});
	}
	else{
		//hide cert info
		$('#certificate-bautifier').hide();
	}
}

function printAndroidManifestInfo(json){
	//add app permissions
	var permissions = json.staticInfo.manifestInfo.usesPermissionList;
	if(permissions){
		var size = permissions.length;
		if(size==0){
			//hide table
			$('#permission-info').html("This application has no declared permissions");
			$('#permission-info').show();
		}
		else{
			//show data
			permissions.forEach(function(item) {
				//add data to rows
		        var body = document.getElementById('permission-table-body');
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name description, used
		        insertRow(newRow, 0, item.name);
		        insertRow(newRow, 1, item.maxSdkVersion);
			});
			$('#permission-info').show();
		}
	}

	//add app activities
	var activities = json.staticInfo.manifestInfo.application.activities;
	if(activities){
		var size = activities.length;
		if(size==0){
			//hide table
			$('#activities-info').html("This application has no declared activities");
			$('#activities-info').show();
		}
		else{
			//show data
			activities.forEach(function(item) {

				//calculate intent filters list
				var isMain = false;
				var intentFilterList = "";

				var filters = item.intentFilter;
				if(filters){
					filters.forEach(function (filter) {
						intentFilterList+= filter.action.forEach(function (action) {
							intentFilterList= intentFilterList+" "+action.name;
						});
					});
				}

				//add data to rows
		        var body = document.getElementById('activities-table-body');
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name, main, meta, intent
		        insertRow(newRow, 0, item.name+'.java');
		        insertRow(newRow, 1, isMain);
		        insertRow(newRow, 2, intentFilterList);
		        insertRow(newRow, 3, item.metadata);
			});
			$('#activities-info').show();
		}
	}

	//add app services
	var services = json.staticInfo.manifestInfo.application.services;
	if(services){
		var size = services.length;
		if(size==0){
			//hide table
			$('#services-info').html("This application has no declared services");
			$('#services-info').show();
		}
		else{
			//show data
			services.forEach(function(item) {
				var triggers = "";
				var cats = "";
				var met = "";

				var filters = item.intentFilterList;
				if(filters && filters.length>0){
					filters.forEach(function (filter) {
						var actions = filter.action;
						if(actions && actions.length>0){
							actions.forEach(function (action) {
								triggers+=" "+action.name;
							});
						}
						else{
							triggers="-";
						}
						var categories = filter.categories;
						if(categories && categories.length>0){
							categories.forEach(function (category) {
								cats+=" "+category.name;
							});
						}
						else{
							cats="-";
						}
					});
				}
				else{
					cats="-";
					met = "-";
					triggers="-";
				}

				var metalist = item.metadataList;
				if(metalist && metalist.length>0){
					metalist.forEach(function (meta) {
						met+=" "+meta.name;
					});
				}
				else{
					met = "-";
				}
				//add data to rows
		        var body = document.getElementById('services-table-body');
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name, main, meta, intent
		        insertRow(newRow, 0, item.name+'.java');
		        insertRow(newRow, 1, item.enabled);
		        insertRow(newRow, 2, item.exported);
		        insertRow(newRow, 3, triggers);
		        insertRow(newRow, 4, cats);
		        insertRow(newRow, 5, met);
			});
			$('#services-info').show();
		}
	}

	//add app receivers
	var receivers = json.staticInfo.manifestInfo.application.receivers;
	if(receivers){
		var size = receivers.length;
		if(size==0){
			//hide table
			$('#receivers-info').html("This application has no declared receivers");
			$('#receivers-info').show();
		}
		else{
			//show data
			receivers.forEach(function(item) {
				var triggers = "";
				var cats = "";
				var met = "";

				var filters = item.intentFilterList;
				if(filters && filters.length>0){
					filters.forEach(function (filter) {
						var actions = filter.action;
						if(actions && actions.length>0){
							actions.forEach(function (action) {
								triggers+=" "+action.name;
							});
						}
						else{
							triggers="-";
						}
						var categories = filter.categories;
						if(categories && categories.length>0){
							categories.forEach(function (category) {
								cats+=" "+category.name;
							});
						}
						else{
							cats="-";
						}
					});
				}
				else{
					cats="-";
					met = "-";
				}

				var metalist = item.metadataList;
				if(metalist && metalist.length>0){
					metalist.forEach(function (meta) {
						met+=" "+meta.name;
					});
				}
				else{
					met = "-";
				}

				//add data to rows
		        var body = document.getElementById('receivers-table-body');
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name, main, meta, intent
		        insertRow(newRow, 0, item.name+'.java');
		        insertRow(newRow, 1, item.enabled);
		        insertRow(newRow, 2, item.exported);
		        insertRow(newRow, 3, triggers);
		        insertRow(newRow, 4, cats);
		        insertRow(newRow, 5, met);
			});
			$('#receivers-info').show();
		}
	}

	//add app intents
	var intents = json.staticInfo.manifestInfo.allFilters;
	if(intents){
		var size = intents.length;
		if(size==0){
			//hide table
			$('#intents-info').html("This application has no declared intent filters");
			$('#intents-info').show();
		}
		else{
			//show data
			intents.forEach(function(item) {

				var triggers = "";
				var cats = "";
				var met = "";

				var actions = item.action;
				if(actions && actions.length>0){
					actions.forEach(function (action) {
						triggers+=" "+action.name;
					});
				}
				else{
					triggers="-";
				}
				var categories = item.categories;
				if(categories && categories.length>0){
					categories.forEach(function (category) {
						cats+=" "+category.name;
					});
				}
				else{
					cats="-";
				}

				var metalist = item.data;
				if(metalist && metalist.length>0){
					metalist.forEach(function (meta) {
						met+=" "+meta.name;
					});
				}
				else{
					met = "-";
				}

				//add data to rows
		        var body = document.getElementById('intents-table-body');
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name, main, meta, intent
		        insertRow(newRow, 0, triggers);
		        insertRow(newRow, 1, cats);
		        insertRow(newRow, 2, met);
		        insertRow(newRow, 3, item.priority);
			});
			$('#intents-info').show();
		}
	}

	//add app metadata
	var metadata = json.staticInfo.manifestInfo.allMetadata;
	if(metadata){
		var size = metadata.length;
		if(size==0){
			//hide table
			$('#metadata-info').html("This application has no declared metadata");
			$('#metadata-info').show();
		}
		else{
			//show data
			metadata.forEach(function(item) {

				//add data to rows
		        var body = document.getElementById('metadata-table-body');
		        // Insert a row in the table at the last row
		        var newRow = body.insertRow(body.rows.length);
		        //name, main, meta, intent
		        insertRow(newRow, 0, item.name);
		        insertRow(newRow, 1, item.resource);
			});
			$('#metadata-info').show();
		}
	}

	$('#permissionTable').DataTable();
	$('#activitiesTable').DataTable();
	$('#servicesTable').DataTable();
	$('#receiversTable').DataTable();
	$('#intentsTable').DataTable();
	$('#metadataTable').DataTable();
}

function setBadgets(json){
	//set "malware-result-badget" 
	var malwareResult = json['malwareResult'];

	if(malwareResult){
		if(malwareResult=='MALWARE'){
			setBadget(
				"malware-result-badget",
				'callout callout-danger',
				'Do not install thia app.',
				'Uploaded sample has a dangerous behaviour. We recommend you not to install.');
		}
		else if(malwareResult == 'GOODWARE'){
			setBadget(
				"malware-result-badget",
				'callout callout-success',
				'Congratulations',
				'This application is clean of malware.');
		}
	}
	else{
		//no result specified
		setBadget(
				"malware-result-badget",
				'callout callout-info',
				'NO CLASSIFIED',
				'This Application was not successfully classified because an internal error.');
	}

	//set "privacy-result-badget" 
	var privacyResult = json['privacyResult'];

	if(privacyResult){
		if(privacyResult=='DATA_LEAK'){
			setBadget(
				"privacy-result-badget",
				'callout callout-danger',
				'This app LEAKS personal details',
				'This application can use your personal information and send it to remote servers');
		}
		else if(privacyResult == 'SUSPICIOUS'){
			setBadget(
				"privacy-result-badget",
				'callout callout-warning',
				'This app may leak your personal information',
				'Uploaded sample has a suspicious activity, please check app permissions before install.');
		}
		else if(privacyResult == 'SAFE'){
			setBadget(
				"privacy-result-badget",
				'callout callout-success',
				'Congratulations',
				'This application is privacy safe.');
		}
	}
	else{
		//no result specified
		setBadget(
				"privacy-result-badget",
				'callout callout-info',
				'NO INSPECTED',
				'This application was not checked because internal failure.');
	}
}

function setFriendlyReport(json){
	//add file info to friendly table
	if(json['sourceFile']){
		$("#friendly-sample-info-filename").html(json['sourceFile'].filename);
		$("#friendly-sample-info-packagename").html('<tt>'+json.staticInfo.manifestInfo.packageName+'</tt>');
		$("#friendly-sample-info-size").html(json['sourceFile'].filesize+" bytes ("+json['sourceFile'].beautyFilesize+")");
		$("#friendly-sample-info-crc32").html(json['sourceFile'].crc32);
		$("#friendly-sample-info-md5").html(json['sourceFile'].md5);
		$("#friendly-sample-info-sha1").html(json['sourceFile'].sha1);
		$("#friendly-sample-info-sha256").html(json['sourceFile'].sha256);
		$("#friendly-sample-info-sha512").html(json['sourceFile'].sha512);
		$("#friendly-sample-info-ssdeep").html(json['sourceFile'].ssdeep);
		$("#friendly-sample-info-type").html(json['sourceFile'].ext);
		$("#friendly-sample-info-magic").html(json['sourceFile'].magicDescription);
	}

	//add summary info
	var rawSummary = json['summary'];
	if(rawSummary){
		var summary = base64.decode(rawSummary);
		$("#friendly-summary").html(summary);
		$("#friendly-summary").show();
		$("#friendly-summary-holder").show();
	}
	else{
		//hide summary section
		$("#friendly-summary").hide();
		$("#friendly-summary-holder").hide();
	}
}

function setUsedAnalyzersAndTiming(json){

	var used = json['usedAnalyzers'];

	if(used){
		used.forEach(function(item) {
			//add data to rows
	        var tableRef = document.getElementById('used-analyzers-body');
	        // Insert a row in the table at the last row
	        var newRow = tableRef.insertRow(tableRef.rows.length);

	        insertCssRow(newRow, 0, item.name , 'span', 'style', 'font-weight: bold;');
	        insertRow(newRow, 1, item.status);
	        if(item.positiveMatch){
	        	insertCssRow(newRow, 2, '' , 'span', 'class', 'fa fa-fw fa-check-circle');
	        }
	        else{
	        	insertCssRow(newRow, 2, '', 'span', 'class', 'fa fa-fw fa-close');
	        }
	        insertRow(newRow, 3, item.timeStamp.time+ " ("+item.timeStamp.duration +" ms)");
		});
	}

	//TIMING

	//add scan start date
	var dateStart = getTimeFromLong(json.timeStamp.start);
	$("#scan-start-time").html(dateStart);

	//add scan end date
	var dateEnd = getTimeFromLong(json.timeStamp.end);
	$("#scan-end-time").html(dateEnd);

	//add duration
	$("#scan-duration").html(json['timeStamp'].time+ " ("+json.timeStamp.duration +" ms)");
}

function printStringAnalysis(json){

	//get string classification
	var strData = json.dynamicInfo.stringAnalysisResult;

	if(strData){
		//show or hide ofuscation alert box
		if(strData.ofuscated){
			$('#ofuscated-strings').show();
		}
		else{
			$('#ofuscated-strings').hide();
		}

		//populate boxes
		var total = strData.initialLength;
		var juicy = total - strData.unknown - strData.irrelevant - strData.classname - strData.javaName - strData.innerclass - strData.accessor;
		var url = strData.url;
		var numeric = strData.numeric
		var emails = strData.email;
		var ips = strData.ipv4 + strData.ipv6
		var dns = strData.dns;
		var unknown = strData.unknown;

		//update boxes
		updateBox('#box-total', 'Hardcoded strings: ', total, total);
		updateBox('#box-juicy', 'Juicy strings: ', juicy, total);
		updateBox('#box-url', 'URL strings: ', url, total);
		updateBox('#box-numeric', 'Numeric strings: ', numeric, total);
		updateBox('#box-email', 'Email strings: ', emails, total);
		updateBox('#box-ip', 'IP: ', ips, total);
		updateBox('#box-dns', 'DNS: ', dns, total);
		updateBox('#box-unknown', 'Unknown strings: ', unknown, total);

		//load string table
		var list = strData.classified;
		//list object comes from a hashmap
		if(list){
			for (var i in list){
				//add data to rows
		        var tableRef = document.getElementById('string-table-body');
		        // Insert a row in the table at the last row
		        var newRow = tableRef.insertRow(tableRef.rows.length);
		        insertRow(newRow, 0, i); //word
		        insertRow(newRow, 1, list[i]); //class
			}
			$('#stringTable').DataTable();
		}
	}
}

function updateBox(id, text, value, total){
	var porcentaje = (value/total)*100;
	//set value
	$(id).text(text+value);
	//set porcentaje value
	$(id+'-pb').css('width', porcentaje+'%');
	//set porcentaje text
	$(id+'-pb-text').text(porcentaje+'% strings');
}

function printSampleInfo(json){
	if(json['sourceFile']){
		$("#detailed-sample-info-filename").html(json['sourceFile'].filename);
		$("#detailed-sample-info-size").html(json['sourceFile'].filesize+" bytes ("+json['sourceFile'].beautyFilesize+")");
		$("#detailed-sample-info-crc32").html(json['sourceFile'].crc32);
		$("#detailed-sample-info-md5").html(json['sourceFile'].md5);
		$("#detailed-sample-info-sha1").html(json['sourceFile'].sha1);
		$("#detailed-sample-info-sha256").html(json['sourceFile'].sha256);
		$("#detailed-sample-info-sha512").html(json['sourceFile'].sha512);
		$("#detailed-sample-info-ssdeep").html(json['sourceFile'].ssdeep);
		$("#detailed-sample-info-type").html(json['sourceFile'].ext);
		$("#detailed-sample-info-magic").html(json['sourceFile'].magicDescription);
	}
	else{
		
	}
}

function printContentInfo(json){
	//add info to app info table
	$('#detailed-sample-app-info-packagename').html(
		json.staticInfo.manifestInfo.packageName
	);
	$('#detailed-sample-app-info-versioname').html(
		json.staticInfo.manifestInfo.versionName
	);
	$('#detailed-sample-app-info-versioncode').html(
		json.staticInfo.manifestInfo.versionCode
	);
	$('#detailed-sample-app-info-min-sdk').html(
		json.staticInfo.manifestInfo.minSDK
	);
	$('#detailed-sample-app-info-target-sdk').html(
		json.staticInfo.manifestInfo.targetSDK
	);
	$('#detailed-sample-app-info-max-sdk').html(
		json.staticInfo.manifestInfo.maxSDK
	);
	$('#detailed-sample-app-info-files').html(
		getColoredBadget(json.staticInfo.appFiles.length)
	);
	$('#detailed-sample-app-info-assets').html(
		getColoredBadget(json.staticInfo.assetFiles.length)
	);
	$('#detailed-sample-app-info-raw').html(
		getColoredBadget(json.staticInfo.rawFiles.length)
	);
	$('#detailed-sample-app-info-lib').html(
		getColoredBadget(json.staticInfo.libFiles.length)
	);
	$('#detailed-sample-app-files').html(
		getColoredBadget(json.staticInfo.filesNumber)
	);
	$('#detailed-sample-app-folders').html(
		getColoredBadget(json.staticInfo.foldersNumber)
	);
}

function printDexHeaderInfo(json){
	//add dex info to table
	$('#detailed-sample-app-magic-number').html(json.internalInfo.magicNumber);
	$('#detailed-sample-app-header-checksum').html(json.internalInfo.headerChecksum);
	$('#detailed-sample-app-signature').html(json.internalInfo.fileSignature);
	$('#detailed-sample-app-header-size').html(json.internalInfo.headerSize+' (bytes)');
	$('#detailed-sample-app-header-filesize').html(json.internalInfo.headerFileSize+' (bytes)');
	$('#detailed-sample-app-endian-tag').html(json.internalInfo.endianTag);
	$('#detailed-sample-app-endian-string-tag').html(json.internalInfo.endianString);
	$('#detailed-sample-app-link-size').html(json.internalInfo.linkSize+' (bytes)');
	$('#detailed-sample-app-link-offset').html(json.internalInfo.linkOffset+' (bytes)');
}

function printDexFileUnpacked(json){
	var jsonFieldNames = ['dexFieldNames', 'dexFieldTypes', 'dexFieldClasses', 'dexMethodTypes', 'dexMethodNames', 'dexMethodClasses', 'dexTypes', 'dexDescriptors'];
	var tableIds = ['fieldNames', 'fieldTypes', 'fieldClasses', 'methodTypes', 'methodNames', 'methodClass', 'classType', 'callDesc'];
	for (var i in tableIds){
		var dataset = json.internalInfo[jsonFieldNames[i]];
		if(dataset){
			dataset.forEach(function(name) {
				//add data to rows
		        var tableRef = document.getElementById(tableIds[i]+'-table-body');
		        // Insert a row in the table at the last row
		        var newRow = tableRef.insertRow(tableRef.rows.length);
		        insertRow(newRow, 0, name); //name
			});
		}
		//call datatable
		$('#'+tableIds[i]+'Table').DataTable();
	}
}

function generateStringChart(json){
	//-------------
  //- PIE CHART -
  //-------------
  // Get context with jQuery - using jQuery's .get() method.
  var stringChartCanvas = $("#stringChart").get(0).getContext("2d");
  var stringChart = new Chart(stringChartCanvas);
  var PieData = [
    {
      value: 700,
      color: "#f56954",
      highlight: "#f56954",
      label: "Chrome"
    },
    {
      value: 500,
      color: "#00a65a",
      highlight: "#00a65a",
      label: "IE"
    },
    {
      value: 400,
      color: "#f39c12",
      highlight: "#f39c12",
      label: "FireFox"
    },
    {
      value: 600,
      color: "#00c0ef",
      highlight: "#00c0ef",
      label: "Safari"
    },
    {
      value: 300,
      color: "#3c8dbc",
      highlight: "#3c8dbc",
      label: "Opera"
    },
    {
      value: 100,
      color: "#d2d6de",
      highlight: "#d2d6de",
      label: "Navigator"
    }
  ];
  var pieOptions = {
    //Boolean - Whether we should show a stroke on each segment
    segmentShowStroke: true,
    //String - The colour of each segment stroke
    segmentStrokeColor: "#fff",
    //Number - The width of each segment stroke
    segmentStrokeWidth: 1,
    //Number - The percentage of the chart that we cut out of the middle
    percentageInnerCutout: 50, // This is 0 for Pie charts
    //Number - Amount of animation steps
    animationSteps: 100,
    //String - Animation easing effect
    animationEasing: "easeOutBounce",
    //Boolean - Whether we animate the rotation of the Doughnut
    animateRotate: true,
    //Boolean - Whether we animate scaling the Doughnut from the centre
    animateScale: false,
    //Boolean - whether to make the chart responsive to window resizing
    responsive: true,
    // Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
    maintainAspectRatio: false,
    //String - A legend template
    legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<segments.length; i++){%><li><span style=\"background-color:<%=segments[i].fillColor%>\"></span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>",
    //String - A tooltip template
    tooltipTemplate: "<%=value %> <%=label%> users"
  };
  //Create pie or douhnut chart
  // You can switch between pie and douhnut using the method below.
  stringChart.Doughnut(PieData, pieOptions);
  //-----------------
  //- END PIE CHART -
  //-----------------
}

function printExternalScannersInfo(json){
	var file = json['sourceFile'];
	if(file){
		var md5 = file.md5;
		var sha256 = file.sha256;

		var scanners = [
			"AndroidObservatory",
			"CopperDroid",
			"ForeSafe",
			"Heldroid",
			"SansDroid",
			"Virustotal",
			"VisualThread"
		];

		var id =[
			'https://androidobservatory.org/apk/'+md5,
			'http://copperdroid.isg.rhul.ac.uk/copperdroid/search.php?md5='+md5,
			'http://foresafe.com/report/'+md5,
			'http://heldroid.andrototal.org/fetchscan?hash='+sha256,
			'http://sanddroid.xjtu.edu.cn/report?apk_md5='+md5,
			'https://www.virustotal.com/en/file/'+sha256+'/analysis/',
			'http://www.visualthreat.com/md5/'+md5
		];

		//add data to rows
		var tableRef = document.getElementById('external-analysis-body');

		for(var i in scanners){
			// Insert a row in the table at the last row
		        var newRow = tableRef.insertRow(tableRef.rows.length);
		        insertRow(newRow, 0, scanners[i]); //name
		        insertRow(newRow, 1, id[i]); //link
		}
	}
}

function printCFGinfo(){
	if(cfgjson == undefined){
		console.log("cfg json download selected");
		var hash = getCurrentHash();

		var failCallback = function(data){
			console.log("error downloading cfg data");
		}
		var successCallback = function(data){
			printCFGdetails(data);
		}

		ajax_getCFGasJson(hash, successCallback, failCallback);
	}
	else{
		printCFGdetails(cfgjson);
	}
}

function printCFGdetails(json){
	//data already download. convert it to json object and print it
	var object = JSON.parse(json);
	var nodes = object.nodes;
	var connections = object.connections;
	var opt = object.optimized;
	$('#node-count').text(nodes);
	$('#connection-count').text(connections);
	$('#node-optimized').text(opt);
}

function printML(json){

	//print matched rules

	var rules = json.dynamicInfo.matchedRules;
	var baseBlock = "<div class='info-box'><span class='info-box-icon bg-green'><i class='fa fa-code'></i></span><div class='info-box-content'><span class='info-box-text'>NAME</span></div></div>";
	if(rules && rules.length > 0){
		for(var i in rules){
			var block = baseBlock;
			block = block.replace('NAME', rules[i].name);
			//append to holding div
			var old = $('#rules-block').html();
			$('#rules-block').html(old+block);
		}
	}
	else{
		//set no matches
		$('#rules-block').html('<h4>Dynamic rules engine</h4><p>There are no matching rules for this application.</p>');
	}

	//print machine learning algorithm results and information

	var ml = json.machineLearningResult.results;
	var baseCard = '<div class="info-box bg-COLOR"><span class="info-box-icon"><i class="fa fa-thumbs-o-up"></i></span><div class="info-box-content"><span class="info-box-text">TITLE</span><span class="info-box-number">VALUE</span><div class="progress"><div class="progress-bar" style="width: MATCHING_AT%"></div></div><span class="progress-description">DESC</span></div></div>';
	
	if(ml && ml.length > 0){
		for(var i in ml){
			var card = baseCard;
			card = card.replace('TITLE', ml[i].name);
			card = card.replace('DESC', ml[i].result);
			if(ml[i].value==0){
				card = card.replace('COLOR','red');
			}
			else{
				card = card.replace('COLOR','green');
			}
			card = card.replace('VALUE', ml[i].value*100);
			card = card.replace('MATCHING_AT', ml[i].value*100);
			//append to holding div
			var old = $('#ml-block').html();
			$('#ml-block').html(old+card);
		}
	}
	else{
		//set no matches
		$('#ml-block').html('<h4>Machine Learning prediction</h4><p>There are no Machine Learning algorithm results available right now.</p>');
	}
}

function getTimeFromLong(t){
	//t is nanosecs
	var millis = Math.floor(t/1000);
	//t is millis now
	var dt = new Date(millis*1000);
	return dt;
}