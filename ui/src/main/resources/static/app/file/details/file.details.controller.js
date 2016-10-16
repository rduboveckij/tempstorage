(function (angular, _, name) {
    'use strict';

    var EMPTY_COMMENT = {text: ''};

    angular.module(name, [
        'temp.storage.common.size.readable',
        'temp.storage.file.service'
    ]);

    angular.module(name).controller('TempStorageFileDetailsController', TempStorageFileDetailsController);

    TempStorageFileDetailsController.$inject = ['TempStorageFileService', 'file'];

    function TempStorageFileDetailsController(TempStorageFileService, file) {
        var vm = this;
        vm.file = file;
        vm.comment = _.clone(EMPTY_COMMENT);
        vm.saveComment = saveComment;

        function saveComment() {
            vm.file.comments = vm.file.comments || [];

            if (_.has(vm.comment, 'created')) {
                vm.comment.updated = Date.now();
            } else {
                vm.comment.created = Date.now();
                vm.file.comments.push(vm.comment);
            }
            vm.comment = _.clone(EMPTY_COMMENT);
            return TempStorageFileService.editFile(vm.file);
        }
    }
})(angular, _, 'temp.storage.file.details.controller');