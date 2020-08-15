const Bundler = require('parcel-bundler');

const entryFiles = ['./UI/javascripts/*.js', './UI/stylesheets/*.css'];

const options = {
    outDir: './public',
    contentHash: false,
    minify: true,
    target: 'browser',
    sourceMaps: false,
    autoInstall: true,
    hmr: false
};

(async () => {
    const bundler = new Bundler(entryFiles, options);

    bundler.on('bundled', (bundle) => {
        if(process.env.ENVIRONMENT === 'prod') {
            process.exit();
        }
    });

    const bundle = await bundler.bundle();
})();