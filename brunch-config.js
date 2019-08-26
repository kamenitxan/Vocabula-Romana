// See http://brunch.io for documentation.
exports.paths = {
    watched: ['src/frontend'],
    public: 'static'
};

exports.files = {
    javascripts: {
        joinTo: {
            'js/vendor.js': /^node_modules/, // Files that are not in `app` dir.
            'js/app.js': /^src\/frontend\/vocabularomana\/js/
        }
    },
    stylesheets: {
        joinTo: {
            'css/vendor.css': /^node_modules/,
            'css/main.css': /^src\/frontend\/vocabularomana\/css/
        }
    },


};

exports.plugins = {
    babel: {presets: ['latest']}
};

exports.npm = {
    enabled: true,
    styles: {
        bootstrap: ['dist/css/bootstrap.css']
    }
};

exports.modules = {
    nameCleaner(path) {
        return path
        // Strip app/ and app/externals/ prefixes
            .replace(/^app\/(?:externals\/)?/, '')
            // Allow -x.y[.z…] version suffixes in mantisses
            .replace("src/frontend/vocabularomana/js/", '')
    }
};

