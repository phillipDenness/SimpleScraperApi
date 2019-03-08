#! /bin/bash

curl -SL https://github.com/mozilla/geckodriver/releases/download/v0.24.0/geckodriver-v0.24.0-linux64.tar.gz > geckodriver.zip
tar -xvzf geckodriver.zip
sudo mv geckodriver ${GECKODRIVER_PATH}
curl http://ftp.mozilla.org/pub/firefox/releases/65.0.2/linux-x86_64/en-US/firefox-65.0.2.tar.bz2 > firefox-65.0.2.tar.bz2
tar -xvjf firefox-65.0.2.tar.bz2
sudo mv -v firefox /tmp/