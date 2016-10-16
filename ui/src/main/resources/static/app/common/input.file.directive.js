(function (angular, _, name) {
    'use strict';

    angular.module(name, []);

    angular.module(name).directive('inputFile', _.constant({
        restrict: 'A',
        require: 'ngModel',
        link: inputFileLink
    }));

    function inputFileLink($scope, $element, $attributes, ngModel) {
        $element.bind('change', function (event) {
            ngModel.$setViewValue(event.target.files[0]);
        });
    }

})(angular, _, 'temp.storage.input.file');