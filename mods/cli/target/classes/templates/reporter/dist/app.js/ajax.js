function ajaxCall(url, dataContent, requestType, responseData, successFunction, failFuncion, alwaysFunction){
    $.ajax({
        // la URL para la petición
        url : url,
     
        // la información a enviar
        // (también es posible utilizar una cadena de datos)
        data : dataContent,
     
        // especifica si será una petición POST o GET
        type : requestType,
     
        // el tipo de información que se espera de respuesta
        dataType : responseData,
     
        // código a ejecutar si la petición es satisfactoria;
        // la respuesta es pasada como argumento a la función
        success : successFunction,
     
        // código a ejecutar si la petición falla;
        // son pasados como argumentos a la función
        // el objeto de la petición en crudo y código de estatus de la petición
        error : failFuncion,
     
        // código a ejecutar sin importar si la petición falló o no
        complete : alwaysFunction
    });
}

function ajax_getReportInformation(hash, callbackSuccess, callbackError){
    console.log('Retrieving project  ('+hash+') information via ajax');
    console.log(getServerAddress('report'));

    ajaxCall(
        getServerAddress('report'),
        {
            id: hash
        },
        'POST',
        'json',
        function(data) {
            //success
            data = encapsulateData(data, false);
            callbackSuccess(data);
        },
        function(data) {
            //error
            data = encapsulateData(data, false);
            callbackError(data);
        },
        undefined
    );
}

function ajax_getCFGasJson(hash, callbackSuccess, callbackError){
    console.log('Retrieving project  ('+hash+') cfg as json via ajax');
    console.log(getServerAddress('report'));

    ajaxCall(
        getServerAddress('report/cfg/json'),
        {
            id: hash
        },
        'POST',
        'json',
        function(data) {
            //success
            data = encapsulateData(data, false);
            callbackSuccess(data);
        },
        function(data) {
            //error
            data = encapsulateData(data, false);
            callbackError(data);
        },
        undefined
    );
}

function ajax_getCFGasGraphViz(hash, callbackSuccess, callbackError){
    console.log('Retrieving project  ('+hash+') cfg as graphviz via ajax');
    console.log(getServerAddress('report'));

    ajaxCall(
        getServerAddress('report/cfg/graph'),
        {
            id: hash
        },
        'POST',
        'json',
        function(data) {
            //success
            data = encapsulateData(data, false);
            callbackSuccess(data);
        },
        function(data) {
            //error
            data = encapsulateData(data, false);
            callbackError(data);
        },
        undefined
    );
}