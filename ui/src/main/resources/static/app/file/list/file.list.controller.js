(function (angular, _, name) {
    'use strict';

    angular.module(name, [
        'temp.storage.common.size.readable',
        'temp.storage.common.highlight',
        'temp.storage.file.upload.service',
        'ui.bootstrap.datepickerPopup',
        'ui.bootstrap.tpls'
    ]);

    angular.module(name).controller('TempStorageFileListController', TempStorageFileListController);

    TempStorageFileListController.$inject = ['$scope', '$state', 'files', 'TempStorageFileUploadService'];

    function TempStorageFileListController($scope, $state, files, TempStorageFileUploadService) {
        var vm = this;
        vm.files = files;
        vm.orderProperty = '';
        vm.orderReverse = false;
        vm.isEmptyTable = false;
        vm.minUploadedDate = new Date(_.minBy(vm.files, 'uploaded').uploaded);
        vm.maxUploadedDate = new Date(_.maxBy(vm.files, 'uploaded').uploaded);
        vm.startDateOptions = {opened: false, minDate: vm.minUploadedDate};
        vm.endDateOptions = {opened: false, maxDate: vm.maxUploadedDate};
        vm.extensions = _.chain(files)
            .map('extension')
            .uniq()
            .filter(_.negate(_.isEmpty))
            .sortBy()
            .value();
        vm.filter = {
            filename: '',
            startDate: vm.minUploadedDate,
            endDate: vm.maxUploadedDate
        };

        vm.changeOrder = changeOrder;
        vm.filterFiles = filterFiles;
        vm.uploadFile = uploadFile;

        predefinedOneExtension();

        $scope.$watchGroup(['vm.filter.startDate', 'vm.filter.endDate'], setLimitOnUploaded);

        function changeOrder(property) {
            vm.orderReverse = vm.orderProperty == property ? !vm.orderReverse : false;
            vm.orderProperty = property;
        }

        function predefinedOneExtension() {
            if (_.size(vm.extensions) === 1) {
                vm.filter.extension = _.first(vm.extensions);
            }
        }

        function filterFiles(filter) {
            var iterator = _.chain(vm.files);

            var filename = filter.filename;
            if (!_.isEmpty(filename)) {
                iterator = iterator.filter(function (file) {
                    return _.includes(file.filename, filename);
                });
            }

            var extension = filter.extension;
            if (!_.isEmpty(extension)) {
                iterator = iterator.filter({extension: extension});
            }

            var startDate = filter.startDate;
            if (_.isDate(startDate)) {
                startDate.setHours(0, 0, 0, 0);
                startDate = startDate.getTime();
                iterator = iterator.filter(function (file) {
                    return _.gte(file.uploaded, startDate);
                });
            }

            var endDate = filter.endDate;
            if (_.isDate(endDate)) {
                endDate.setHours(23, 59, 59, 999);
                endDate = endDate.getTime();
                iterator = iterator.filter(function (file) {
                    return _.lte(file.uploaded, endDate);
                });
            }

            var result = iterator.value();
            vm.isEmptyTable = _.isEmpty(result);
            return result;
        }

        function setLimitOnUploaded() {
            vm.startDateOptions.maxDate = vm.filter.endDate;
            vm.endDateOptions.minDate = vm.filter.startDate;
        }

        function uploadFile() {
            return TempStorageFileUploadService.openUpload()
                .then(function (file) {
                    return $state.go('temp-storage-file-details', {fileId: file.fileId});
                });
        }
    }
})(angular, _, 'temp.storage.file.list.controller');