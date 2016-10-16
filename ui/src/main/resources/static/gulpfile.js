'use strict';

const gulp = require('gulp');
const browserSync = require('browser-sync');
const mergeStream = require('merge-stream');
const _ = require('lodash');
const bodyParser = require('body-parser');

const plugins = require('gulp-load-plugins')({pattern: ['gulp-*']});

const MOCK = {
    "58028b506c3fa529c8224fd7": {
        id: "58028b506c3fa529c8224fd7",
        name: "13880131_1266858566677776_1483345925737807710_n.jpg",
        contentType: "image/jpeg",
        description: "It is cool file",
        size: 21057,
        comments: [
            {
                text: "Comment 1",
                created: 1476561744416,
                updated: 1476561744416
            },
            {
                text: "Comment 2",
                created: 1476561744417
            }
        ],
        uploaded: 1476561744416
    },
    "58028b506c3ca529c8224fd7": {
        id: "58028b506c3ca529c8224fd7",
        name: "foto.png",
        contentType: "image/png",
        size: 21054547,
        comments: [
            {
                text: "Comment 3",
                created: 1476561744416,
                updated: 1476561744416
            },
            {
                text: "Comment 4",
                created: 1476561744417
            }
        ],
        uploaded: Date.now()
    }
};

gulp.task('serve', ['build'], serve);
gulp.task('clean', clean);
gulp.task('compile-js', compileJs);
gulp.task('compile-less', compileLess);
gulp.task('build', ['compile-js', 'compile-less']);

gulp.task('default', ['clean'], () => gulp.start('build'));

function clean() {
    return gulp.src('./dist', {read: false})
        .pipe(plugins.debug({title: 'clean:'}))
        .pipe(plugins.rimraf());
}

function serve() {
    browserSync({
        server: {baseDir: "./"},
        middleware: [
            {route: "/api", handle: bodyParser.json()},
            {route: "/api", handle: middlewareFiles}
        ]
    });

    gulp.watch(['app/**/*.html', 'app/**/!(*.spec).js'], ['compile-js'], reload);
    gulp.watch('app/**/*.less', ['compile-less'], reload);
}

function middlewareFiles(req, res, next) {
    if (req.method === 'GET' && req.url === '/files') {
        return res.end(JSON.stringify(_.values(MOCK)));
    } else if (req.method === 'GET' && _.startsWith(req.url, '/files/')) {
        return res.end(JSON.stringify(MOCK[_.replace(req.url, '/files/', '')]));
    } else if (req.method === 'PUT' && _.startsWith(req.url, '/files/')) {
        let id = _.replace(req.url, '/files/', '');
        MOCK[id] = req.body;
        MOCK[id].id = id;
        return res.end(JSON.stringify(MOCK[id]));
    } else if (req.method === 'POST') {
        let id = _.uniqueId();
        MOCK[id] = {
            id: id, name: 'Name ' + id, description: '',
            size: _.uniqueId() + 43242, uploaded: Date.now(),
            contentType: "image/png"
        };
        return res.end(JSON.stringify(MOCK[id]));
    }

    next();
}

function reload() {
    browserSync.reload();
}

function compileJs() {
    return mergeStream(
        gulp.src('app/**/!(*.spec).js'),
        gulp.src('app/**/*.html')
            .pipe(plugins.ngHtml2js({moduleName: 'temp.storage.template'}))
    )
        .pipe(plugins.uglify())
        .pipe(plugins.concat('scripts.js'))
        .pipe(gulp.dest('./dist/'));
}

function compileLess() {
    return gulp.src('app/**/*.less')
        .pipe(plugins.less())
        .pipe(plugins.csso())
        .pipe(plugins.concat('styles.css'))
        .pipe(gulp.dest('./dist/'));
}