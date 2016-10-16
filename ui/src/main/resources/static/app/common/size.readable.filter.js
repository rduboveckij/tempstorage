(function (angular, _, name) {
    'use strict';

    angular.module(name, []);

    angular.module(name).constant('SIZE_READABLE_GRADATION_STANDARD', 1000);

    angular.module(name).constant('SIZE_READABLE_GRADATION_BINARY', 1024);

    angular.module(name).value('sizeReadableMeasurement', ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']);

    angular.module(name).filter('sizeReadable', sizeReadableFilter);

    sizeReadableFilter.$inject = ['sizeReadableMeasurement', 'SIZE_READABLE_GRADATION_STANDARD'];

    function sizeReadableFilter(sizeReadableMeasurement, SIZE_READABLE_GRADATION) {
        return function (size, decimals) {
            decimals = decimals || 2;
            var index = Math.floor(Math.log(size) / Math.log(SIZE_READABLE_GRADATION));
            var value = parseFloat((size / Math.pow(SIZE_READABLE_GRADATION, index)).toFixed(decimals + 1));
            if (!_.isFinite(value)) {
                return '';
            }
            return [value, sizeReadableMeasurement[index]].join(' ');
        };
    }
})(angular, _, 'temp.storage.common.size.readable');