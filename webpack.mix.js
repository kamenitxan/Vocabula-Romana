let mix = require('laravel-mix');

mix.js([
	'src/frontend/vocabularomana/js/FR.js'
], 'js/app.js')
	.css('src/frontend/vocabularomana/css/main.css', 'css/main.css')
	.extract()
	.setPublicPath('modules/backend/static');