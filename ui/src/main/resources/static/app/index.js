(function (angular, _, name) {
    'use strict';

    angular.module(name, [
        'ngAnimate',
        'temp.storage.translate',
        'temp.storage.file'
    ]);

    angular.module(name).config(Config);

    Config.$inject = ['$stateProvider', '$urlRouterProvider'];

    function Config($stateProvider, $urlRouterProvider) {
        $stateProvider.state('error', {
            url: '/error',
            template: '<div>Sorry, Error</div>',
        });

        $urlRouterProvider.when('', '/files');
        $urlRouterProvider.otherwise('/error');
    }
})(angular, _, 'temp.storage');