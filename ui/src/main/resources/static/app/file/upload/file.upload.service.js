(function (angular, _, name) {
    'use strict';

    angular.module(name, [
        'ui.bootstrap.modal',
        'ui.bootstrap.tpls',
        'temp.storage.file.service',
        'temp.storage.file.upload.component'
    ]);

    angular.module(name).factory('TempStorageFileUploadService', TempStorageFileUploadService);

    TempStorageFileUploadService.$inject = ['$uibModal', 'TempStorageFileService'];

    function TempStorageFileUploadService($uibModal, TempStorageFileService) {
        return {
            openUpload: openUpload
        };

        function openUpload() {
            return $uibModal
                .open({component: 'tempStorageFileUpload'})
                .result
                .then(TempStorageFileService.addFile);
        }
    }
})(angular, _, 'temp.storage.file.upload.service');