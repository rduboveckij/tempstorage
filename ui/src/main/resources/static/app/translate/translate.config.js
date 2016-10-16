(function (angular, _, name) {
    'use strict';

    angular.module(name, [
        'pascalprecht.translate',
        'ngSanitize'
    ]);

    angular.module(name).config(Config);

    Config.$inject = ['$translateProvider'];

    function Config($translateProvider) {
        $translateProvider.translations('en', {
            'title': 'TempStorage application',
            'file.list': 'List of files',
            'file.name': 'File name',
            'content.type': 'Type of content',
            'size': 'Size',
            'uploaded.date': 'Uploaded',
            'extension': 'Extension',
            'no.data': 'The data is not available',
            'file.details': 'The details of file {{filename}}',
            'search.file.name': 'Search by file name',
            'no.data.found': 'The data did not find',
            'all': 'All',
            'start.uploaded': 'Start uploaded',
            'end.uploaded': 'End uploaded',
            'created': 'Created',
            'updated': 'Updated',
            'save': 'Save',
            'edit': 'Edit',
            'comment': 'Comment',
            'fill.comment': 'Fill comment',
            'upload.file': 'Upload file',
            'send': 'Send',
            'cancel': 'Cancel',
            'description': 'Description',
            'file': 'File',
            'select.file': 'Please select file',
            'download': 'Download'
        });

        $translateProvider
            .useSanitizeValueStrategy('sanitize')
            .preferredLanguage('en');
    }
})(angular, _, 'temp.storage.translate');