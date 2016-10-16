(function (angular, _, name) {
    'use strict';

    angular.module(name, [
        'temp.storage.input.file'
    ]);

    angular.module(name).component('tempStorageFileUpload', {
        templateUrl: 'file/upload/file.upload.template.html',
        bindings: {
            resolve: '<',
            close: '&',
            dismiss: '&'
        },
        controller: 'TempStorageFileUploadController as vm'
    });

    angular.module(name).controller('TempStorageFileUploadController', TempStorageFileUploadController);

    TempStorageFileUploadController.$inject = [];

    function TempStorageFileUploadController() {
        var vm = this;
        vm.file = {description: '', file: undefined};

        vm.send = send;
        vm.cancel = vm.dismiss;

        function send() {
            vm.close({$value: vm.file});
        }
    }
})(angular, _, 'temp.storage.file.upload.component');