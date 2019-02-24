
Web scraper API

Set WEBDRIVER_PATH env variable to chromedriver 

To deploy on AWS run following code on the box.

curl https://intoli.com/install-google-chrome.sh | bash

cd/tmp/
wget https://chromedriver.storage.googleapis.com/2.37/chromedriver_linux64.zip
unzip chromedriver_linux64.zip
sudo mv chromedriver /usr/bin/chromedriver
chromedriver --version