(function (angular, _, name) {
    'use strict';

    angular.module(name, []);

    angular.module(name).value('highlightOptions', {
        regExpOptions: 'gi',
        markTemplate: '<mark>$&</mark>'
    });

    angular.module(name).filter('highlight', highlightFilter);

    highlightFilter.$inject = ['$sce', 'highlightOptions'];

    function highlightFilter($sce, highlightOptions) {
        return function (item, query, options) {
            if (_.isEmpty(item) || _.isEmpty(query)) {
                return item;
            }
            options = _.defaults(options, highlightOptions);
            var regExp = new RegExp(_.escapeRegExp(query), options.regExpOptions);
            return $sce.trustAsHtml(_.replace(item, regExp, options.markTemplate));
        };
    }
})(angular, _, 'temp.storage.common.highlight');