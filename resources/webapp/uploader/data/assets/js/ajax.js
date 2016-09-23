function ajax_checkHash(hash, callback) {

    console.log('Checking hash (' + hash + ') via ajax');
    console.log(getServerAddress('status'));

    ajaxCall(
        getServerAddress('status'), {
            sha256: hash
        },
        'POST',
        'json',
        function(data) {
            data = encapsulateData(data, false);
            callback(data);
        },
        function(data) {
            data = encapsulateData(data, false);
            hashSignatureCheckFailedPopUp();
        },
        undefined
    );
}

function ajax_loadSearchInitialData(onSuccessCallback, onErrorCallback) {
    console.log('Getting initial search data');
    console.log(getServerAddress('search/'));

    ajaxCall(
        getServerAddress('latest/uploads'),
        undefined,
        'GET',
        'json',
        function(data) {
            data = encapsulateData(data, false);
            onSuccessCallback(data);
        },
        function(data) {
            data = encapsulateData(data, false);
            onErrorCallback(data);
        },
        undefined
    );
}

function ajax_doSampleSearch(criteriaStr, onSuccessCallback, onErrorCallback) {
    console.log('Getting samples that match with your criteria...');
    console.log(getServerAddress('search/'));

    ajaxCall(
        getServerAddress('search/sample'),
        {
            criteria: criteriaStr
        },
        'POST',
        'json',
        function(data) {
            data = encapsulateData(data, false);
            onSuccessCallback(data);
        },
        function(data) {
            data = encapsulateData(data, false);
            onErrorCallback(data);
        },
        undefined
    );
}

function ajax_getLatestUploads(callback) {

    console.log('Getting latest uploads...');
    console.log(getServerAddress('latest/uploads'));

    ajaxCall(
        getServerAddress('latest/uploads'),
        undefined,
        'GET',
        'json',
        function(data) {
            data = encapsulateData(data, false);
            callback(data);
        },
        function(data) {
            data = encapsulateData(data, false);
            //error. do nothing
        },
        undefined
    );
}

function ajax_betaAccess(callback) {

    console.log('Getting beta access...');
    console.log(getServerAddress('get/beta/'));

    ajaxCall(
        getServerAddress('get/beta/'),
        undefined,
        'POST',
        'json',
        function(data) {
            //success
            data = encapsulateData(data, false);
            callback(data);
        },
        function(data) {
            data = encapsulateData(data, false);
            //error. do nothing
            showBetaAccessErrorPopUp();
        },
        undefined
    );
}

function ajax_uploadFile(filename, file, hash, callback) {
    console.log("uploading file " + filename + " with hash " + hash + " and filesize " + file.size);
    console.log(getServerAddress('upload'));

    // Create a formdata object and add the files
    var formData = new FormData();

    //encode file data to base64
    var reader = new FileReader();
    reader.readAsBinaryString(file);
    reader.onload = function() {
        var fileBytes = this.result;
        var b64data = fromByteArray(fileBytes);
        formData.append("file", b64data);
        formData.append("filename", filename);
        formData.append("hash", hash);

        //make the call
        $.ajax({
            url: getServerAddress('upload'),
            type: "POST",
            data:formData,
            cache: false,
            contentType: false,
            processData: false
        })
        .error(function (xhr, status, error) {
            console.log('ERROR: ' + status);
            callback(undefined);
        })
        .success(function (data, status, xhr) {
            console.log('SUCCESS: ' + status);
            callback(data);
        });
    }
}

function ajaxCall(url, dataContent, requestType, responseData, successFunction, failFuncion, alwaysFunction) {
    $.ajax({
        // la URL para la petición
        url: url,

        // la información a enviar
        // (también es posible utilizar una cadena de datos)
        data: dataContent,

        // especifica si será una petición POST o GET
        type: requestType,

        // el tipo de información que se espera de respuesta
        dataType: responseData,

        // código a ejecutar si la petición es satisfactoria;
        // la respuesta es pasada como argumento a la función
        success: successFunction,

        // código a ejecutar si la petición falla;
        // son pasados como argumentos a la función
        // el objeto de la petición en crudo y código de estatus de la petición
        error: failFuncion,

        // código a ejecutar sin importar si la petición falló o no
        complete: alwaysFunction
    });
}

function ajax_pingServer(pinCallback) {
    ajaxCall(
        getServerAddress('verify/version'),
        undefined,
        'GET',
        'json',
        function(data, status) {
            data = encapsulateData(data, false);
            console.log('Connected to: ' + data['apkr.server-name']);
            pinCallback();
        },
        function(data, status) {
            noServerConnectionPopUp();
        },
        undefined
    );
}

function userProfile(data) {
    ajaxCall(
        getServerAddress('profile'), {
            payload: data,
            time: Date.now()
        },
        'POST',
        'json',
        function(data, status) {
            data = encapsulateData(data, false);
        },
        function(data, status) {
            data = encapsulateData(data, false);
        },
        undefined
    );
}