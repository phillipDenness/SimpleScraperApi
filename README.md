
Web scraper API


TODO - Dockerise chromedriver dependencies

Set WEBDRIVER_PATH env variable to chromedriver 

To deploy on AWS run following code on the box.

curl https://intoli.com/install-google-chrome.sh | bash

wget https://chromedriver.storage.googleapis.com/2.37/chromedriver_linux64.zip
unzip chromedriver_linux64.zip
sudo mv chromedriver /usr/bin/chromedriver
chromedriver --version


https://stackoverflow.com/questions/53211066/what-is-the-proper-way-to-create-ebextensions-for-aws-beanstalk-through-maven-f