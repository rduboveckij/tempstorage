(function (angular, _, name) {
    'use strict';

    angular.module(name, [
        'ui.router',
        'temp.storage.template',
        'temp.storage.file.list.controller',
        'temp.storage.file.details.controller',
        'temp.storage.file.service'
    ]);

    angular.module(name).config(Config);

    Config.$inject = ['$stateProvider'];

    function Config($stateProvider) {
        $stateProvider.state('temp-storage-file-list', {
            url: '/files',
            templateUrl: 'file/list/file.list.template.html',
            controller: 'TempStorageFileListController as vm',
            resolve: {
                files: ['TempStorageFileService', _.method('getFiles')]
            }
        });

        $stateProvider.state('temp-storage-file-details', {
            url: '/files/:fileId',
            templateUrl: 'file/details/file.details.template.html',
            controller: 'TempStorageFileDetailsController as vm',
            resolve: {file: getFile},
            onEnter: checkRequiredParams
        });
    }

    getFile.$inject = ['$stateParams', 'TempStorageFileService'];

    function getFile($stateParams, TempStorageFileService) {
        return TempStorageFileService.getFile($stateParams.fileId);
    }

    checkRequiredParams.$inject = ['$state', '$stateParams'];

    function checkRequiredParams($state, $stateParams) {
        if (_.some($stateParams, _.isEmpty)) {
            return $state.go('temp-storage-file-list');
        }
    }
})(angular, _, 'temp.storage.file');