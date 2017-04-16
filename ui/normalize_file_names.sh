#!/usr/bin/env bash
curr_dir=$(pwd)
cd ..
# rm public/ui/main.bundle.js || true
mv public/ui/main.*.js public/ui/main.js
# rm public/ui/index.html
rm public/ui/favicon.ico
cd $curr_dir
