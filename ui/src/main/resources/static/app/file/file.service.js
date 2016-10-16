(function (angular, _, name) {
    'use strict';

    angular.module(name, []);

    angular.module(name).constant('EXTENSION_SPLITTER', '.');

    angular.module(name).factory('TempStorageFileService', TempStorageFileService);

    TempStorageFileService.$inject = ['$q', '$http', 'EXTENSION_SPLITTER'];

    function TempStorageFileService($q, $http, EXTENSION_SPLITTER) {
        return {
            getFiles: getFiles,
            getFile: getFile,
            editFile: editFile,
            addFile: addFile
        };

        function getFile(fileId) {
            return $http.get('api/files/' + fileId)
                .then(_.property('data'))
                .then(transform);
        }

        function getFiles() {
            return $http.get('api/files')

                .then(_.property('data'))
                .then(function (data) {
                    return _.map(_.uniq(data, 'fileId'), transform);
                });
        }

        function transform(file) {
            var filename = _.split(file.name, EXTENSION_SPLITTER);
            file.extension = filename.length === 1 ? '' : filename.pop();
            file.filename = filename.join(EXTENSION_SPLITTER);
            return file;
        }

        function addFile(file) {
            return $http
                .post('api/files', file, {
                    //IMPORTANT!!! You might think this should be set to 'multipart/form-data'
                    // but this is not true because when we are sending up files the request
                    // needs to include a 'boundary' parameter which identifies the boundary
                    // name between parts in this multi-part request and setting the Content-type
                    // manually will not set this boundary parameter. For whatever reason,
                    // setting the Content-type to 'false' will force the request to automatically
                    // populate the headers properly including the boundary parameter.
                    headers: {'Content-Type': undefined},
                    // This method will allow us to change how the data is sent up to the server
                    // for which we'll need to encapsulate the model data in 'FormData'
                    transformRequest: transformRequest,
                })
                .then(_.property('data'))
                .then(transform);

            function transformRequest(data) {
                var formData = new FormData();
                formData.append("file", data.file);
                // need to convert our json object to a string version of json otherwise
                // the browser will do a 'toString()' on the object which will result
                // in the value '[Object object]' on the server.
                formData.append("description", data.description);
                return formData;
            }
        }

        function editFile(file) {
            return $http.put('api/files/' + file.fileId, file)
                .then(_.property('data'))
                .then(transform);
        }
    }
})(angular, _, 'temp.storage.file.service');