#!/bin/bash
# Variables
activator_version="1.3.12"

# Install java
echo " => Installing Java 8"
echo " ==> Adding webupd8team/java repository"
sudo add-apt-repository ppa:webupd8team/java
echo " ==> Updating with the added repository"
sudo apt-get update
echo " ==> Auto-accepting Java 8 terms"
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
echo " ==> Installing Java 8 from repositories (This might take a few minutes)"
sudo apt-get install -y oracle-java8-installer 1>/dev/null || echo "Failed to install Java 8"

# Install nodejs for faster deployments
echo " => Installing nodejs"
sudo apt-get install -y nodejs

# Install activator
echo " => Installing Activator"
echo " ==> Installing zip to uncompress activator"
sudo apt-get install -y zip
echo " ==> Going to /opt/"
cd /opt/ || exit
echo " ==> Downloading activator from typesafe page"
wget --progress=bar https://downloads.typesafe.com/typesafe-activator/$activator_version/typesa\
fe-activator-$activator_version-minimal.zip
echo " ==> Unzipping downloaded file"
unzip typesafe-activator-$activator_version-minimal.zip
echo " ==> Creating a symbolic link to \"/opt/activator\""
ln -s activator-$activator_version-minimal activator
echo " ==> Adding execution permissions to activator"
sudo chmod +x /opt/activator/bin/activator
echo " ==> Adding activator's bin to PATH"
echo "export PATH=\$PATH:/opt/activator/bin" >> /home/ubuntu/.bashrc
echo " ==> Adding ivy2 patch"
echo "export SBT_OPTS=\"\${SBT_OPTS} -Dsbt.jse.engineType=Node -Dsbt.jse.command=\$(which nodejs)\"" >> /home/ubuntu/.bashrc
echo "alias activator=\"activator -Dsbt.ivy.home=/vagrant/.ivy2/ -Divy.home=/vagrant/.ivy2/\"" >> /home/ubuntu/.bashrc
